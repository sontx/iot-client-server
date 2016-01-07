package com.blogspot.sontx.myapp.ui;

import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.blogspot.sontx.iot.shared.model.bean.Device;
import com.blogspot.sontx.iot.shared.model.bean.RealTime;
import com.blogspot.sontx.myapp.R;
import com.blogspot.sontx.myapp.lib.ConnectionServer;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.text.DecimalFormat;

public class RealtimeActivity extends TaskActivity implements Handler.Callback, Runnable {
    private static final int VISIBLE_RANGE = 10;
    private static final int UPDATE_AFTER = 1000;// 1s
    private static final int WHAT_OK = 1;
    private static final int WHAT_WARNING = 0;
    private static final int WHAT_ERROR = 2;
    private Device mDevice;
    private LineChart mPowerChart;
    private LineChart mAmperageChart;
    private LineChart mVoltageChart;
    private boolean pendingStop = false;
    private Handler mUpdaterHandler = new Handler(this);
    private ValueFormatter formatter = new ValueFormatter() {
        private DecimalFormat format = new DecimalFormat(".##");
        @Override
        public String getFormattedValue(float v, Entry entry, int i, ViewPortHandler viewPortHandler) {
            return format.format(v);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_realtime);

        mPowerChart = (LineChart) findViewById(R.id.realtime_lc_power);
        mAmperageChart = (LineChart) findViewById(R.id.realtime_lc_amperage);
        mVoltageChart = (LineChart) findViewById(R.id.realtime_lc_voltage);

        int[] colors = ColorTemplate.COLORFUL_COLORS;
        setupChart(mPowerChart, "Power(kWh)", colors[0], 15000.0f / 1000.0f);
        setupChart(mAmperageChart, "Amperage(A)", colors[1], 50000.0f / 1000.0f);
        setupChart(mVoltageChart, "Voltage(V)", colors[2], 300);

        mDevice = getDevice();

        new Thread(this).start();
    }

    private void setupChart(LineChart chart, String description, int color, float max) {
        chart.setDescription(description);
        chart.setNoDataText("");
        chart.setNoDataTextDescription("");
        chart.setDrawGridBackground(false);
        chart.setScaleEnabled(false);
        chart.setDrawBorders(false);

        chart.getXAxis().setDrawLabels(false);
        chart.getXAxis().setDrawGridLines(false);
        chart.getXAxis().setDrawAxisLine(false);
        chart.getAxisLeft().setDrawLabels(false);
        chart.getAxisLeft().setDrawGridLines(false);
        chart.getAxisLeft().setDrawAxisLine(false);
        chart.getAxisRight().setDrawLabels(false);
        chart.getAxisRight().setDrawGridLines(false);
        chart.getAxisRight().setDrawAxisLine(false);

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setAxisMaxValue(max * 1.2f);
        leftAxis.setStartAtZero(true);

        chart.getLegend().setEnabled(false);

        LineData data = new LineData();
        data.addDataSet(createSet(color));
        for (int i = 0; i < VISIBLE_RANGE; i++) {
            data.addXValue("");
        }
        data.setValueFormatter(formatter);
        data.setValueTextColor(Color.BLACK);
        chart.setData(data);

        chart.invalidate();
    }

    @Override
    protected void onDestroy() {
        pendingStop = true;
        super.onDestroy();
    }

    private LineDataSet createSet(int color) {
        LineDataSet set = new LineDataSet(null, "");

        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setColor(color);
        set.setLineWidth(1f);
        set.setDrawCircles(false);
        set.setHighLightColor(Color.rgb(244, 117, 117));
        set.setDrawValues(true);
        set.setDrawCubic(true);

        return set;
    }

    @Override
    public boolean handleMessage(Message msg) {
        if (msg.what == WHAT_OK) {
            updateRealtime((RealTime) msg.obj);
        } else if (msg.what == WHAT_WARNING) {
            displayWarning(mPowerChart);
            displayWarning(mAmperageChart);
            displayWarning(mVoltageChart);
        } else if (msg.what == WHAT_ERROR) {
            displayError(mPowerChart);
            displayError(mAmperageChart);
            displayError(mVoltageChart);
        }
        return true;
    }

    private void updateRealtime(RealTime realTime) {
        if (realTime == null)
            return;
        float power = realTime.getPower() / 1000.0f;// convert to kWh
        float amperage = realTime.getAmperage() / 1000.0f;// convert to A
        float voltage = realTime.getVoltage();
        updateForChart(power, mPowerChart);
        updateForChart(amperage, mAmperageChart);
        updateForChart(voltage, mVoltageChart);
    }

    private void updateForChart(float value, LineChart chart) {
        // clear last warning info
        chart.setNoDataText("");
        chart.setNoDataTextDescription("");

        LineData data = chart.getData();
        DataSet dataSet = data.getDataSetByIndex(0);

        data.addXValue("");
        int xIndex = dataSet.getEntryCount() + VISIBLE_RANGE;
        dataSet.addEntry(new Entry(value, xIndex));

        chart.notifyDataSetChanged();

        // limit visible count
        chart.setVisibleXRangeMaximum(VISIBLE_RANGE);
        chart.moveViewToX(data.getXValCount() - VISIBLE_RANGE - 1);
    }

    private void displayWarning(LineChart chart) {
        chart.setNoDataText("Warning!");
        chart.setNoDataTextDescription("Missing data, try again...");
    }

    private void displayError(LineChart chart) {
        chart.setNoDataText("Error!");
        chart.setNoDataTextDescription("Connection broken down!");
    }

    @Override
    public void run() {
        int lastError = 0;// count of try :|
        int deviceId = mDevice.getId();
        while (!pendingStop) {
            RealTime realTime = ConnectionServer.getInstance().getRealtime(deviceId);
            if (realTime != null) {
                Message msg = mUpdaterHandler.obtainMessage(WHAT_OK, realTime);// everything OK
                mUpdaterHandler.sendMessage(msg);
                lastError = 0;
            } else if (lastError < 5) {
                mUpdaterHandler.sendEmptyMessage(WHAT_WARNING);// something wrong! try again...
                lastError++;
            } else {
                mUpdaterHandler.sendEmptyMessage(WHAT_ERROR);// connection broken down so we display message and exit thread
                break;
            }

            long start = System.currentTimeMillis();
            while ((System.currentTimeMillis() - start < UPDATE_AFTER) && (!pendingStop)) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    break;
                }
            }
        }
    }
}
