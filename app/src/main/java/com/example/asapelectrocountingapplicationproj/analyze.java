package com.example.asapelectrocountingapplicationproj;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class analyze extends AppCompatActivity {

    private Spinner analysisTypeSpinner;
    private LineChart chart;
    private Button backButton;
    private Button downloadButton;
    private SQLiteDatabase db;
    private boolean hasData = false;
    private SharedPreferences sharedPreferences;
    private static final String PREF_NAME = "AppTheme";

    private BroadcastReceiver themeChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if ("com.example.asapelectrocountingapplicationproj.THEME_CHANGED".equals(intent.getAction())) {
                applyTheme();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analyze);

        sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        analysisTypeSpinner = findViewById(R.id.analysisTypeSpinner);
        chart = findViewById(R.id.chart);
        backButton = findViewById(R.id.backButton);
        downloadButton = findViewById(R.id.downloadButton);

        setupSpinner();
        setupChart();
        setupDatabase();

        backButton.setOnClickListener(v -> finish());
        downloadButton.setOnClickListener(v -> handleDownload());

        updateChart(0); // 默認顯示用電分析

        applyTheme();

        IntentFilter filter = new IntentFilter("com.example.asapelectrocountingapplicationproj.THEME_CHANGED");
        registerReceiver(themeChangeReceiver, filter);
    }

    private void applyTheme() {
        int backgroundColor = sharedPreferences.getInt("background_color", Color.WHITE);
        int textColor = sharedPreferences.getInt("text_color", Color.BLACK);
        float textSize = sharedPreferences.getFloat("text_size", 18);
        int buttonColor = sharedPreferences.getInt("button_color", Color.LTGRAY);

        getWindow().getDecorView().setBackgroundColor(backgroundColor);

        // 設置 Spinner 的背景為透明
        analysisTypeSpinner.setBackgroundColor(Color.TRANSPARENT);

        // 更新 Spinner 的文字顏色
        ArrayAdapter adapter = (ArrayAdapter) analysisTypeSpinner.getAdapter();
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        analysisTypeSpinner.setAdapter(adapter);

        // 設置 Spinner 選中項的文字顏色
        analysisTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (view instanceof TextView) {
                    ((TextView) view).setTextColor(ThemeManager.getContrastColor(backgroundColor));
                }
                updateChart(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        backButton.setBackgroundColor(buttonColor);
        backButton.setTextColor(textColor);
        backButton.setTextSize(textSize);

        downloadButton.setBackgroundColor(buttonColor);
        downloadButton.setTextColor(textColor);
        downloadButton.setTextSize(textSize);

        // 更新圖表的顏色
        updateChartAppearance(backgroundColor, textColor);
    }

    private void updateChartAppearance(int backgroundColor, int textColor) {
        chart.setBackgroundColor(backgroundColor);
        chart.getDescription().setTextColor(textColor);
        chart.getLegend().setTextColor(textColor);
        chart.getXAxis().setTextColor(textColor);
        chart.getAxisLeft().setTextColor(textColor);
        chart.getAxisRight().setTextColor(textColor);
        chart.invalidate();
    }

    private void setupSpinner() {
        String[] analysisTypes = {"用電分析", "電費分析", "總分析"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, analysisTypes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        analysisTypeSpinner.setAdapter(adapter);
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
        xAxis.setLabelRotationAngle(45);

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setDrawGridLines(false);
        chart.getAxisRight().setEnabled(false);
    }

    private void setupDatabase() {
        db = openOrCreateDatabase("ElectricityBills", MODE_PRIVATE, null);
    }

    private void updateChart(int analysisType) {
        List<Entry> entries = new ArrayList<>();
        List<String> dates = new ArrayList<>();
        String query = "SELECT date, amount, usage FROM bills ORDER BY date ASC";
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.getCount() == 0) {
            showNoDataMessage();
            hasData = false;
        } else {
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

            if (entries.isEmpty()) {
                showNoDataMessage();
                hasData = false;
            } else {
                hasData = true;
                drawChart(entries, dates, analysisType);
            }
        }
    }

    private void showNoDataMessage() {
        chart.clear();
        chart.setNoDataText("無資料");
        chart.setNoDataTextColor(Color.BLACK);
        chart.invalidate();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示")
                .setMessage("目前沒有可分析的資料")
                .setPositiveButton("確定", (dialog, which) -> {
                    // 用戶點擊確定按鈕後的操作（如果需要）
                })
                .setCancelable(false);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void drawChart(List<Entry> entries, List<String> dates, int analysisType) {
        LineDataSet dataSet;
        String yAxisLabel;

        switch (analysisType) {
            case 0:
                dataSet = new LineDataSet(entries, "用電量");
                yAxisLabel = "度";
                break;
            case 1:
                dataSet = new LineDataSet(entries, "電費");
                yAxisLabel = "元";
                break;
            default:
                dataSet = new LineDataSet(entries, "平均電費");
                yAxisLabel = "元/度";
                break;
        }

        dataSet.setColor(Color.BLUE);
        dataSet.setLineWidth(2f);
        dataSet.setCircleColor(Color.BLUE);
        dataSet.setCircleRadius(4f);
        dataSet.setDrawValues(true);
        dataSet.setValueTextSize(10f);

        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);

        XAxis xAxis = chart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(dates));
        xAxis.setLabelCount(dates.size(), true);

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.format("%.1f %s", value, yAxisLabel);
            }
        });
        leftAxis.setAxisMinimum(0f);

        float maxValue = Collections.max(entries, (e1, e2) -> Float.compare(e1.getY(), e2.getY())).getY();
        leftAxis.setAxisMaximum(maxValue * 1.1f);

        chart.getDescription().setEnabled(true);
        chart.getDescription().setText(dataSet.getLabel());
        chart.getDescription().setTextSize(12f);
        chart.animateX(1000);
        chart.invalidate();
    }

    private void handleDownload() {
        if (!hasData) {
            showNoDataMessage();
        } else {
            showDownloadDialog();
        }
    }

    private void showDownloadDialog() {
        final CharSequence[] items = {"JPG", "PDF"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("選擇下載格式");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                if (item == 0) {
                    downloadChart("jpg");
                } else {
                    downloadChart("pdf");
                }
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void downloadChart(String format) {
        chart.invalidate();
        int width = chart.getWidth();
        int height = chart.getHeight();
        Bitmap chartBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(chartBitmap);
        chart.draw(canvas);

        String fileName = "chart_" + System.currentTimeMillis();
        File file;

        try {
            File directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            if (format.equals("jpg")) {
                file = new File(directory, fileName + ".jpg");
                FileOutputStream fos = new FileOutputStream(file);
                chartBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.close();
            } else {
                file = new File(directory, fileName + ".pdf");
                PdfDocument pdfDocument = new PdfDocument();
                PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(width, height, 1).create();
                PdfDocument.Page page = pdfDocument.startPage(pageInfo);
                Canvas pdfCanvas = page.getCanvas();
                pdfCanvas.drawBitmap(chartBitmap, 0, 0, null);
                pdfDocument.finishPage(page);
                pdfDocument.writeTo(new FileOutputStream(file));
                pdfDocument.close();
            }

            runOnUiThread(() -> Toast.makeText(analyze.this, "圖表已下載", Toast.LENGTH_SHORT).show());
        } catch (IOException e) {
            e.printStackTrace();
            runOnUiThread(() -> Toast.makeText(analyze.this, "下載失敗", Toast.LENGTH_SHORT).show());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (db != null) {
            db.close();
        }
        unregisterReceiver(themeChangeReceiver);
    }
}