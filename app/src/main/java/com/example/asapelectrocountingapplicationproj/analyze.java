package com.example.asapelectrocountingapplicationproj;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class analyze extends AppCompatActivity {

    private Spinner analysisTypeSpinner;
    private LineChart chart;
    private Button backButton;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analyze);

        analysisTypeSpinner = findViewById(R.id.analysisTypeSpinner);
        chart = findViewById(R.id.chart);
        backButton = findViewById(R.id.backButton);

        setupSpinner();
        setupChart();
        setupDatabase();

        backButton.setOnClickListener(v -> finish());
    }

    private void setupSpinner() {
        String[] analysisTypes = {"用電分析", "電費分析", "總分析"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, analysisTypes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        analysisTypeSpinner.setAdapter(adapter);

        analysisTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateChart(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void setupChart() {
        chart.getDescription().setEnabled(false);
        chart.setTouchEnabled(true);
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);
        chart.setPinchZoom(true);
        chart.setDrawGridBackground(false);

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(new IndexAxisValueFormatter());
    }

    private void setupDatabase() {
        db = openOrCreateDatabase("ElectricityBills", MODE_PRIVATE, null);
    }

    private void updateChart(int analysisType) {
        List<Entry> entries = new ArrayList<>();
        List<String> dates = new ArrayList<>();

        String query = "SELECT date, amount, usage FROM bills ORDER BY date ASC";
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                String dateStr = cursor.getString(0);
                float amount = cursor.getFloat(1);
                float usage = cursor.getFloat(2);

                dates.add(dateStr);
                switch (analysisType) {
                    case 0: // 用電分析
                        entries.add(new Entry(entries.size(), usage));
                        break;
                    case 1: // 電費分析
                        entries.add(new Entry(entries.size(), amount));
                        break;
                    case 2: // 總分析
                        entries.add(new Entry(entries.size(), amount / usage));
                        break;
                }
            } while (cursor.moveToNext());
        }
        cursor.close();

        LineDataSet dataSet;
        switch (analysisType) {
            case 0:
                dataSet = new LineDataSet(entries, "用電量 (度)");
                break;
            case 1:
                dataSet = new LineDataSet(entries, "電費 (元)");
                break;
            default:
                dataSet = new LineDataSet(entries, "平均電費 (元/度)");
                break;
        }

        dataSet.setColor(Color.BLUE);
        dataSet.setLineWidth(2f);
        dataSet.setCircleColor(Color.BLUE);
        dataSet.setCircleRadius(4f);
        dataSet.setDrawValues(false);

        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);

        XAxis xAxis = chart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(dates));
        xAxis.setLabelRotationAngle(45);

        chart.invalidate();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (db != null) {
            db.close();
        }
    }
}