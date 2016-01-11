package com.blogspot.sontx.myapp.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class HistoryActivity extends TaskActivity implements View.OnClickListener, OnChartValueSelectedListener {
    private int mDeviceId;
    private BarChart mBarChart;
    private EditText mYearView;
    private EditText mMonthView;
    private EditText mDayView;
    private DecimalFormat mFormat = new DecimalFormat("#.##");

    private int getYear() {
        return Convert.parseInt(mYearView.getText().toString(), DateTime.now().getYear());
    }

    private int getMonth() {
        return Convert.parseInt(mMonthView.getText().toString(), DateTime.now().getMonth());
    }

    private int getDay() {
        return Convert.parseInt(mDayView.getText().toString(), DateTime.now().getDay());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        mBarChart = (BarChart) findViewById(R.id.history_bc_chart);
        setupChart(mBarChart);

        mYearView = (EditText) findViewById(R.id.history_et_year);
        mMonthView = (EditText) findViewById(R.id.history_et_month);
        mDayView = (EditText) findViewById(R.id.history_et_day);
        DateTime now = DateTime.now();
        mYearView.setText(String.format("%d", now.getYear()));
        mMonthView.setText(String.format("%d", now.getMonth()));
        mDayView.setText(String.format("%d", now.getDay()));

        findViewById(R.id.history_btn_day).setOnClickListener(this);
        findViewById(R.id.history_btn_month).setOnClickListener(this);
        findViewById(R.id.history_btn_year).setOnClickListener(this);

        mDeviceId = getDeviceId();
    }

    private void loadYear() {
        final ProcessingBox box = new ProcessingBox(this);
        box.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                final float[] energies = ConnectionServer.getInstance().getEnergies(mDeviceId, getYear());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (energies == null) {
                            Toast.makeText(HistoryActivity.this, "Error!", Toast.LENGTH_LONG).show();
                        } else {
                            mBarChart.getXAxis().resetLabelsToSkip();
                            float[] data = new float[12];
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

    private void loadMonth() {
        final ProcessingBox box = new ProcessingBox(this);
        box.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                final float[] energies = ConnectionServer.getInstance().getEnergies(
                        mDeviceId, getMonth(), getYear());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (energies == null) {
                            Toast.makeText(HistoryActivity.this, "Error!", Toast.LENGTH_LONG).show();
                        } else {
                            mBarChart.getXAxis().setLabelsToSkip(1);
                            float[] data = new float[DateTime.getMaxDay(getMonth(), getYear())];
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

    private void loadDay() {
        final ProcessingBox box = new ProcessingBox(this);
        box.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                final float[] energies = ConnectionServer.getInstance().getEnergies(
                        mDeviceId, getDay(), getMonth(), getYear());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (energies == null) {
                            Toast.makeText(HistoryActivity.this, "Error!", Toast.LENGTH_LONG).show();
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
        mBarChart.invalidate();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.history_btn_day) {
            loadDay();
        } else if (v.getId() == R.id.history_btn_month) {
            loadMonth();
        } else if (v.getId() == R.id.history_btn_year) {
            loadYear();
        }
    }

    @Override
    public void onValueSelected(Entry entry, int i, Highlight highlight) {
        Toast.makeText(HistoryActivity.this, String.format("%s kWh", mFormat.format(entry.getVal())), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected() {

    }
}
