package com.example.asapelectrocountingapplicationproj;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class ElectroEstimator extends AppCompatActivity {

    private EditText usageEditText;
    private Spinner typeSpinner, seasonSpinner;
    private Button calculateButton, saveButton, backButton;
    private TextView resultTextView;

    private static final String RESIDENTIAL = "住宅用";
    private static final String NON_RESIDENTIAL = "住宅以外非營業用";
    private static final String COMMERCIAL = "營業用";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_electro_estimator);

        initViews();
        setupSpinners();
        setupButtonListeners();
    }

    private void initViews() {
        usageEditText = findViewById(R.id.usageEditText);
        typeSpinner = findViewById(R.id.typeSpinner);
        seasonSpinner = findViewById(R.id.seasonSpinner);
        calculateButton = findViewById(R.id.calculateButton);
        saveButton = findViewById(R.id.saveButton);
        resultTextView = findViewById(R.id.resultTextView);
        backButton = findViewById(R.id.backButton);
    }

    private void setupSpinners() {
        ArrayAdapter<CharSequence> typeAdapter = ArrayAdapter.createFromResource(this,
                R.array.type_array, android.R.layout.simple_spinner_item);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSpinner.setAdapter(typeAdapter);

        ArrayAdapter<CharSequence> seasonAdapter = ArrayAdapter.createFromResource(this,
                R.array.season_array, android.R.layout.simple_spinner_item);
        seasonAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        seasonSpinner.setAdapter(seasonAdapter);
    }

    private void setupButtonListeners() {
        calculateButton.setOnClickListener(v -> calculateElectricity());
        saveButton.setOnClickListener(v -> saveResult());
        backButton.setOnClickListener(v -> handleBackButton());
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

        // 考慮2個月抄表、收費一次的情況
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

        // 如果是公用路燈且非營業用,電費減半
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
        if (result.equals("估算結果將顯示在這裡")) {
            Toast.makeText(this, "請先計算電費", Toast.LENGTH_SHORT).show();
            return;
        }

        // 這裡應該實現保存結果的邏輯,例如保存到數據庫或文件
        Toast.makeText(this, "結果已保存", Toast.LENGTH_SHORT).show();
    }

    private void handleBackButton() {
        if (!usageEditText.getText().toString().isEmpty()) {
            showConfirmDialog();
        } else {
            navigateToMainActivity();
        }
    }

    private void showConfirmDialog() {
        new AlertDialog.Builder(this)
                .setTitle("確定返回?")
                .setMessage("尚未儲存的資料將會被移除")
                .setPositiveButton("確定", (dialog, which) -> navigateToMainActivity())
                .setNegativeButton("取消", null)
                .show();
    }

    private void navigateToMainActivity() {
        Intent intent = new Intent(ElectroEstimator.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}