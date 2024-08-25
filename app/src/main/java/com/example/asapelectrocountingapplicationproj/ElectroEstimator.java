package com.example.asapelectrocountingapplicationproj;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ElectroEstimator extends AppCompatActivity {

    private EditText usageEditText, dateEditText;
    private Spinner typeSpinner, seasonSpinner;
    private Button calculateButton, saveButton, backButton;
    private TextView resultTextView, editTextTextMultiLine;
    private static final String RESIDENTIAL = "住宅用";
    private static final String NON_RESIDENTIAL = "住宅以外非營業用";
    private static final String COMMERCIAL = "營業用";
    private SimpleDateFormat dateFormat;
    private SQLiteDatabase db;
    private boolean isCalculated = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_electro_estimator);

        initViews();
        setupSpinners();
        setupButtonListeners();
        dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        setupDatabase();

        ThemeManager.applyTheme(this);

    }

    private void initViews() {
        usageEditText = findViewById(R.id.usageEditText);
        dateEditText = findViewById(R.id.dateEditText);
        typeSpinner = findViewById(R.id.typeSpinner);
        seasonSpinner = findViewById(R.id.seasonSpinner);
        calculateButton = findViewById(R.id.calculateButton);
        saveButton = findViewById(R.id.saveButton);
        resultTextView = findViewById(R.id.resultTextView);
        backButton = findViewById(R.id.backButton);
        editTextTextMultiLine = findViewById(R.id.editTextTextMultiLine);
    }

    private void setupSpinners() {
        ArrayAdapter typeAdapter = ArrayAdapter.createFromResource(this,
                R.array.type_array, android.R.layout.simple_spinner_item);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSpinner.setAdapter(typeAdapter);

        ArrayAdapter seasonAdapter = ArrayAdapter.createFromResource(this,
                R.array.season_array, android.R.layout.simple_spinner_item);
        seasonAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        seasonSpinner.setAdapter(seasonAdapter);
    }

    private void setupButtonListeners() {
        calculateButton.setOnClickListener(v -> {
            calculateElectricity();
            isCalculated = true;
        });
        saveButton.setOnClickListener(v -> saveResult());
        backButton.setOnClickListener(v -> handleBackButton());
    }

    private void setupDatabase() {
        db = openOrCreateDatabase("ElectricityBills", MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS bills(id INTEGER PRIMARY KEY AUTOINCREMENT, date TEXT, amount REAL, usage REAL, remark TEXT, type TEXT)");
    }

    private void calculateElectricity() {
        String usageStr = usageEditText.getText().toString();
        if (usageStr.isEmpty()) {
            Toast.makeText(this, "請輸入用電量", Toast.LENGTH_SHORT).show();
            return;
        }

        double usage = Double.parseDouble(usageStr);
        String type = typeSpinner.getSelectedItem().toString();
        String season = seasonSpinner.getSelectedItem().toString();
        boolean isSummer = season.equals("夏月");
        double cost = calculateCost(usage, type, isSummer);
        String result = String.format("預估電費: %.2f 元", cost);
        resultTextView.setText(result);
    }

    private double calculateCost(double usage, String type, boolean isSummer) {
        double[][] rates = getRates(type);
        double totalCost = 0;
        double remainingUsage = usage;
        double multiplier = 2;

        for (double[] rate : rates) {
            double limit = rate[0] * multiplier;
            double summerRate = rate[1];
            double nonSummerRate = rate[2];
            if (remainingUsage <= 0) break;
            double usageInThisTier = Math.min(remainingUsage, limit);
            double costInThisTier = usageInThisTier * (isSummer ? summerRate : nonSummerRate);
            totalCost += costInThisTier;
            remainingUsage -= usageInThisTier;
            if (limit == Double.MAX_VALUE) break;
        }

        if (type.equals(NON_RESIDENTIAL) && usage > 0) {
            totalCost *= 0.5;
        }

        return totalCost;
    }

    private double[][] getRates(String type) {
        if (type.equals(RESIDENTIAL) || type.equals(NON_RESIDENTIAL)) {
            return new double[][]{
                    {120, 1.68, 1.68},
                    {330, 2.45, 2.16},
                    {500, 3.70, 3.03},
                    {700, 5.04, 4.14},
                    {1000, 6.24, 5.07},
                    {Double.MAX_VALUE, 8.46, 6.63}
            };
        } else { // 營業用
            return new double[][]{
                    {330, 2.61, 2.18},
                    {700, 3.66, 3.00},
                    {1500, 4.46, 3.61},
                    {3000, 7.08, 5.56},
                    {Double.MAX_VALUE, 7.43, 5.83}
            };
        }
    }

    private void saveResult() {
        String result = resultTextView.getText().toString();
        String dateStr = dateEditText.getText().toString().trim();
        String usageStr = usageEditText.getText().toString().trim();
        String remarkStr = editTextTextMultiLine.getText().toString().trim();

        if (dateStr.isEmpty()) {
            Toast.makeText(this, "請輸入日期", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isValidDate(dateStr)) {
            Toast.makeText(this, "請輸入正確的日期格式 (YYYY-MM-DD)", Toast.LENGTH_SHORT).show();
            return;
        }

        if (usageStr.isEmpty()) {
            Toast.makeText(this, "請輸入用電度數", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isCalculated) {
            Toast.makeText(this, "請點選計算電費", Toast.LENGTH_SHORT).show();
            return;
        }

        double usage = Double.parseDouble(usageStr);
        double amount = Double.parseDouble(result.split(": ")[1].replace(" 元", ""));
        String type = typeSpinner.getSelectedItem().toString();
        String season = seasonSpinner.getSelectedItem().toString();
        String remark = type + ", " + season + " - " + usageStr + "度" + " / " + remarkStr;

        ContentValues values = new ContentValues();
        values.put("date", dateStr);
        values.put("type", "est");
        values.put("amount", amount);
        values.put("usage", usage);
        values.put("remark", remark);

        long newRowId = db.insert("bills", null, values);
        if (newRowId != -1) {
            Toast.makeText(this, "結果已保存", Toast.LENGTH_SHORT).show();
            clearInputs();
        } else {
            Toast.makeText(this, "保存失敗", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isValidDate(String dateStr) {
        try {
            Date date = dateFormat.parse(dateStr);
            return dateFormat.format(date).equals(dateStr);
        } catch (Exception e) {
            return false;
        }
    }

    private void clearInputs() {
        usageEditText.setText("");
        dateEditText.setText("");
        resultTextView.setText("估算結果將顯示在這裡");
        typeSpinner.setSelection(0);
        seasonSpinner.setSelection(0);
        isCalculated = false;
    }

    private void handleBackButton() {
        if (!usageEditText.getText().toString().isEmpty() || !dateEditText.getText().toString().isEmpty()) {
            showConfirmDialog();
        } else {
            finish();
        }
    }

    private void showConfirmDialog() {
        new AlertDialog.Builder(this)
                .setTitle("確定返回?")
                .setMessage("尚未儲存的資料將會被移除")
                .setPositiveButton("確定", (dialog, which) -> finish())
                .setNegativeButton("取消", null)
                .show();
    }

    @Override
    public void onBackPressed() {
        handleBackButton();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (db != null) {
            db.close();
        }
    }
    private final BroadcastReceiver themeChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if ("com.example.asapelectrocountingapplicationproj.THEME_CHANGED".equals(intent.getAction())) {
                ThemeManager.applyTheme(ElectroEstimator.this);
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(themeChangeReceiver, new IntentFilter("com.example.asapelectrocountingapplicationproj.THEME_CHANGED"), Context.RECEIVER_NOT_EXPORTED);
        ThemeManager.applyTheme(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(themeChangeReceiver);
    }
}