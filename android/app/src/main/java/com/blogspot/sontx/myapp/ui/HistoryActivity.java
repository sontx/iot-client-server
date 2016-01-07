package com.blogspot.sontx.myapp.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.blogspot.sontx.iot.shared.model.bean.Energy;
import com.blogspot.sontx.iot.shared.utils.Convert;
import com.blogspot.sontx.iot.shared.utils.DateTime;
import com.blogspot.sontx.myapp.R;
import com.blogspot.sontx.myapp.lib.ConnectionServer;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.util.ArrayList;
import java.util.List;

public class HistoryActivity extends TaskActivity implements View.OnClickListener, OnChartValueSelectedListener {
    public static final String INTENT_CHART_TYPE = "chart_type";
    public static final int CHART_DAY = 1;
    public static final int CHART_MONTH = 2;
    public static final int CHART_YEAR = 3;
    public static final int CHART_ALL = 4;

    private int mDeviceId;
    private int mType;
    private BarChart mBarChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        mBarChart = (BarChart) findViewById(R.id.history_bc_chart);
        setupChart(mBarChart);
        Button btnNext = (Button) findViewById(R.id.history_btn_next);
        btnNext.setOnClickListener(this);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        mType = bundle.getInt(INTENT_CHART_TYPE);
        mDeviceId = getDeviceId();

        if (mType == CHART_DAY) {
            btnNext.setText("this month");
            loadDay();
        } else if (mType == CHART_MONTH) {
            btnNext.setText("this month");
            loadMonth();
        }
    }

    private void loadMonth() {
        final ProcessingBox box = new ProcessingBox(this);
        box.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                final DateTime now = DateTime.now();
                final float[] energies = ConnectionServer.getInstance().getEnergies(
                        mDeviceId, now.getMonth(), now.getYear());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (energies == null) {
                            Toast.makeText(HistoryActivity.this, "Error!", Toast.LENGTH_LONG).show();
                            finish();
                        } else {
                            mBarChart.getXAxis().setLabelsToSkip(1);
                            float[] data = new float[DateTime.getMaxDay(now.getMonth(), now.getYear())];
                            for (int i = 0; i < energies.length; i++) {
                                data[i] = energies[i] / 1000.0f;
                            }
                            loadData(data);
                        }
                        box.dismiss();
                    }
                });
            }
        }).start();
    }

    protected void setupChart(BarChart chart) {
        chart.setDescription("Energy(kWh)");
        chart.setDrawBarShadow(false);
        chart.setDrawGridBackground(false);
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setSpaceBetweenLabels(0);
        xAxis.setDrawGridLines(false);
        chart.getAxisRight().setDrawLabels(false);
        chart.getLegend().setEnabled(false);
        chart.animateY(1500);
        chart.setOnChartValueSelectedListener(this);
    }

    private void loadData(float[] data) {
        ArrayList<BarEntry> yVals = new ArrayList<>(data.length);
        ArrayList<String> labels = new ArrayList<>(data.length);
        for (int i = 0; i < data.length; i++) {
            yVals.add(new BarEntry(data[i], i));
            labels.add(String.format("%d", i + 1));
        }
        BarDataSet dataSet = new BarDataSet(yVals, "");
        dataSet.setDrawValues(false);
        BarData barData = new BarData(labels, dataSet);
        mBarChart.setData(barData);
    }

    private void loadDay() {
        final ProcessingBox box = new ProcessingBox(this);
        box.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                final DateTime now = DateTime.now();
                final float[] energies = ConnectionServer.getInstance().getEnergies(
                        mDeviceId, now.getDay(), now.getMonth(), now.getYear());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (energies == null) {
                            Toast.makeText(HistoryActivity.this, "Error!", Toast.LENGTH_LONG).show();
                            finish();
                        } else {
                            mBarChart.getXAxis().setLabelsToSkip(1);
                            float[] data = new float[24];
                            for (int i = 0; i < energies.length; i++) {
                                data[i] = energies[i] / 1000.0f;
                            }
                            loadData(data);
                        }
                        box.dismiss();
                    }
                });
            }
        }).start();
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putInt(INTENT_DEVICE_ID, mDeviceId);
        if (mType == CHART_DAY) {
            bundle.putInt(INTENT_CHART_TYPE, CHART_MONTH);
        }
        intent.putExtras(bundle);
        intent.setClass(this, getClass());
        startActivity(intent);
        finish();
    }

    @Override
    public void onValueSelected(Entry entry, int i, Highlight highlight) {
        Toast.makeText(HistoryActivity.this, String.format("%f kWh", entry.getVal()), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected() {

    }
}
