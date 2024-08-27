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

        backButton.setBackgroundColor(buttonColor);
        backButton.setTextColor(textColor);
        backButton.setTextSize(textSize);

        downloadButton.setBackgroundColor(buttonColor);
        downloadButton.setTextColor(textColor);
        downloadButton.setTextSize(textSize);

        updateSpinnerAppearance(backgroundColor);
        updateChartAppearance(backgroundColor);
    }

    private void updateSpinnerAppearance(int backgroundColor) {
        analysisTypeSpinner.setBackgroundColor(Color.TRANSPARENT);
        int textColor = ThemeManager.getContrastColor(backgroundColor);

        ArrayAdapter<CharSequence> adapter = (ArrayAdapter<CharSequence>) analysisTypeSpinner.getAdapter();
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        analysisTypeSpinner.setAdapter(adapter);

        analysisTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (view instanceof TextView) {
                    ((TextView) view).setTextColor(textColor);
                }
                updateChart(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void updateChartAppearance(int backgroundColor) {
        int textColor = ThemeManager.getContrastColor(backgroundColor);

        chart.setBackgroundColor(backgroundColor);
        chart.getDescription().setTextColor(textColor);
        chart.getLegend().setTextColor(textColor);
        chart.getXAxis().setTextColor(textColor);
        chart.getAxisLeft().setTextColor(textColor);
        chart.getAxisRight().setTextColor(textColor);

        if (chart.getData() != null && chart.getData().getDataSetCount() > 0) {
            LineDataSet dataSet = (LineDataSet) chart.getData().getDataSetByIndex(0);
            dataSet.setValueTextColor(textColor);
        }

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


        String query = "SELECT date, amount, usage FROM bills WHERE type = 'real' ORDER BY date ASC";

        Cursor cursor = db.rawQuery(query, null);

        List<Entry> entries2 = new ArrayList<>();
        List<String> dates2 = new ArrayList<>();
        String query2 = "SELECT date, amount, usage FROM bills WHERE type = 'est' ORDER BY date ASC";
        Cursor cursor2 = db.rawQuery(query2, null);

        if (cursor.getCount() == 0 && cursor2.getCount() == 0) {
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


            if (cursor2.moveToFirst()) {
                do {
                    String dateStr = cursor2.getString(0);
                    float amount = cursor2.getFloat(1);
                    float usage = cursor2.getFloat(2);

                    dates2.add(dateStr);
                    switch (analysisType) {
                        case 0: // 用電分析
                            entries2.add(new Entry(entries2.size(), usage));
                            break;
                        case 1: // 電費分析
                            entries2.add(new Entry(entries2.size(), amount));
                            break;
                        case 2: // 總分析
                            entries2.add(new Entry(entries2.size(), amount / usage));
                            break;
                    }
                } while (cursor2.moveToNext());
            }
            cursor2.close();

            if (entries.isEmpty() && entries2.isEmpty()) {
                showNoDataMessage();
                hasData = false;
            } else {
                hasData = true;
                drawChart(entries, dates, entries2, dates2, analysisType);
            }
        }
    }

    private void showNoDataMessage() {
        chart.clear();
        chart.setNoDataText("無資料");
        chart.setNoDataTextColor(ThemeManager.getContrastColor(sharedPreferences.getInt("background_color", Color.WHITE)));
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

    private void drawChart(List<Entry> entries, List<String> dates, List<Entry> entries2, List<String> dates2, int analysisType) {
        // 合併日期列表
        List<String> allDates = new ArrayList<>(dates);
        for (String date : dates2) {
            if (!allDates.contains(date)) {
                allDates.add(date);
            }
        }
        // 將日期列表進行排序
        Collections.sort(allDates);

        // 根據合併後的日期列表調整 entries 和 entries2
        List<Entry> adjustedEntries1 = new ArrayList<>();
        List<Entry> adjustedEntries2 = new ArrayList<>();

        for (int i = 0; i < allDates.size(); i++) {
            String currentDate = allDates.get(i);

            // 對齊 entries
            if (dates.contains(currentDate)) {
                int index = dates.indexOf(currentDate);
                adjustedEntries1.add(new Entry(i, entries.get(index).getY()));
            } else {
                adjustedEntries1.add(new Entry(i, 0)); // 或者用 Float.NaN 代表空值
            }

            // 對齊 entries2
            if (dates2.contains(currentDate)) {
                int index = dates2.indexOf(currentDate);
                adjustedEntries2.add(new Entry(i, entries2.get(index).getY()));
            } else {
                adjustedEntries2.add(new Entry(i, 0)); // 或者用 Float.NaN 代表空值
            }
        }

        LineDataSet dataSet, dataSet2;
        String yAxisLabel;

        String typeLabel;

        switch (analysisType) {
            case 0:
                typeLabel = "用電量";
                dataSet = new LineDataSet(adjustedEntries1, "真實用電量");
                dataSet2 = new LineDataSet(adjustedEntries2, "估算用電量");
                yAxisLabel = "度";
                break;
            case 1:
                typeLabel = "電費";
                dataSet = new LineDataSet(adjustedEntries1, "真實電費");
                dataSet2 = new LineDataSet(adjustedEntries2, "估算電費");
                yAxisLabel = "元";
                break;
            default:
                typeLabel = "平均電費";
                dataSet = new LineDataSet(adjustedEntries1, "真實平均電費");
                dataSet2 = new LineDataSet(adjustedEntries2, "估算平均電費");
                yAxisLabel = "元/度";
                break;
        }

        int textColor = ThemeManager.getContrastColor(sharedPreferences.getInt("background_color", Color.WHITE));

        dataSet.setColor(Color.BLUE);
        dataSet.setLineWidth(2f);
        dataSet.setCircleColor(Color.BLUE);
        dataSet.setCircleRadius(4f);
        dataSet.setDrawValues(true);
        dataSet.setValueTextSize(10f);
        dataSet.setValueTextColor(textColor);

        dataSet2.setColor(Color.RED);
        dataSet2.setLineWidth(2f);
        dataSet2.setCircleColor(Color.RED);
        dataSet2.setCircleRadius(4f);
        dataSet2.setDrawValues(true);
        dataSet2.setValueTextSize(10f);

        LineData lineData = new LineData(dataSet, dataSet2);
        chart.setData(lineData);

        XAxis xAxis = chart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(allDates));
        xAxis.setLabelCount(allDates.size(), true);

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.format("%.1f %s", value, yAxisLabel);
            }
        });


        float max1 = Collections.max(adjustedEntries1, (e1, e2) -> Float.compare(e1.getY(), e2.getY())).getY();
        float max2 = Collections.max(adjustedEntries2, (e1, e2) -> Float.compare(e1.getY(), e2.getY())).getY();

        float maxValue = Math.max(max1, max2);
        leftAxis.setAxisMinimum(0f);

        leftAxis.setAxisMaximum(maxValue * 1.1f);

        chart.getDescription().setEnabled(true);
        chart.getDescription().setText(typeLabel);
        chart.getDescription().setTextSize(12f);
        chart.getDescription().setTextColor(textColor);
        chart.animateX(1000);
        chart.invalidate();

        updateChartAppearance(sharedPreferences.getInt("background_color", Color.WHITE));
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