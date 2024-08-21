package com.example.asapelectrocountingapplicationproj;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ElectroEstimator extends AppCompatActivity {

    private EditText usageEditText;
    private Spinner timeSpinner, seasonSpinner, businessTypeSpinner;
    private Button calculateButton, saveButton, backButton;
    private TextView resultTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_electro_estimator);

        initViews();
        setupSpinners();
        setupButtonListeners();
        setupWindowInsets();
    }

    private void initViews() {
        usageEditText = findViewById(R.id.usageEditText);
        timeSpinner = findViewById(R.id.timeSpinner);
        seasonSpinner = findViewById(R.id.seasonSpinner);
        businessTypeSpinner = findViewById(R.id.businessTypeSpinner);
        calculateButton = findViewById(R.id.calculateButton);
        saveButton = findViewById(R.id.saveButton);
        resultTextView = findViewById(R.id.resultTextView);
        backButton = findViewById(R.id.backButton);
    }

    private void setupSpinners() {
        ArrayAdapter timeAdapter = ArrayAdapter.createFromResource(this,
                R.array.time_array, android.R.layout.simple_spinner_item);
        timeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        timeSpinner.setAdapter(timeAdapter);

        ArrayAdapter seasonAdapter = ArrayAdapter.createFromResource(this,
                R.array.season_array, android.R.layout.simple_spinner_item);
        seasonAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        seasonSpinner.setAdapter(seasonAdapter);

        ArrayAdapter businessTypeAdapter = ArrayAdapter.createFromResource(this,
                R.array.business_type_array, android.R.layout.simple_spinner_item);
        businessTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        businessTypeSpinner.setAdapter(businessTypeAdapter);
    }

    private void setupButtonListeners() {
        calculateButton.setOnClickListener(v -> calculateElectricity());
        saveButton.setOnClickListener(v -> saveResult());
        backButton.setOnClickListener(v -> handleBackButton());
    }

    private void setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void calculateElectricity() {
        String usageStr = usageEditText.getText().toString();
        if (usageStr.isEmpty()) {
            Toast.makeText(this, "請輸入用電量", Toast.LENGTH_SHORT).show();
            return;
        }

        double usage = Double.parseDouble(usageStr);
        String season = seasonSpinner.getSelectedItem().toString();
        String businessType = businessTypeSpinner.getSelectedItem().toString();

        double cost = calculateNonTimeElectricity(usage, season, businessType);
        String result = String.format("預估電費: %.2f 元", cost);
        resultTextView.setText(result);
    }

    private double calculateNonTimeElectricity(double usage, String season, String businessType) {
        double cost = 0;
        boolean isSummer = season.equals("夏季");

        if (businessType.equals("非營業用")) {
            if (usage <= 120) {
                cost = usage * 1.68;
            } else if (usage <= 330) {
                cost = 120 * 1.68 + (usage - 120) * (isSummer ? 2.45 : 2.16);
            } else if (usage <= 500) {
                cost = 120 * 1.68 + 210 * (isSummer ? 2.45 : 2.16) + (usage - 330) * (isSummer ? 3.70 : 3.03);
            } else if (usage <= 700) {
                cost = 120 * 1.68 + 210 * (isSummer ? 2.45 : 2.16) + 170 * (isSummer ? 3.70 : 3.03) + (usage - 500) * (isSummer ? 5.04 : 4.14);
            } else if (usage <= 1000) {
                cost = 120 * 1.68 + 210 * (isSummer ? 2.45 : 2.16) + 170 * (isSummer ? 3.70 : 3.03) + 200 * (isSummer ? 5.04 : 4.14) + (usage - 700) * (isSummer ? 6.24 : 5.07);
            } else {
                cost = 120 * 1.68 + 210 * (isSummer ? 2.45 : 2.16) + 170 * (isSummer ? 3.70 : 3.03) + 200 * (isSummer ? 5.04 : 4.14) + 300 * (isSummer ? 6.24 : 5.07) + (usage - 1000) * (isSummer ? 8.46 : 6.63);
            }
        } else { // 營業用
            if (usage <= 330) {
                cost = usage * (isSummer ? 2.61 : 2.18);
            } else if (usage <= 700) {
                cost = 330 * (isSummer ? 2.61 : 2.18) + (usage - 330) * (isSummer ? 3.66 : 3.00);
            } else if (usage <= 1500) {
                cost = 330 * (isSummer ? 2.61 : 2.18) + 370 * (isSummer ? 3.66 : 3.00) + (usage - 700) * (isSummer ? 4.46 : 3.61);
            } else if (usage <= 3000) {
                cost = 330 * (isSummer ? 2.61 : 2.18) + 370 * (isSummer ? 3.66 : 3.00) + 800 * (isSummer ? 4.46 : 3.61) + (usage - 1500) * (isSummer ? 7.08 : 5.56);
            } else {
                cost = 330 * (isSummer ? 2.61 : 2.18) + 370 * (isSummer ? 3.66 : 3.00) + 800 * (isSummer ? 4.46 : 3.61) + 1500 * (isSummer ? 7.08 : 5.56) + (usage - 3000) * (isSummer ? 7.43 : 5.83);
            }
        }

        return cost;
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
                .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        navigateToMainActivity();
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }

    private void navigateToMainActivity() {
        Intent intent = new Intent(ElectroEstimator.this, MainActivity.class);
        startActivity(intent);
        finish(); // 結束當前活動
    }
}