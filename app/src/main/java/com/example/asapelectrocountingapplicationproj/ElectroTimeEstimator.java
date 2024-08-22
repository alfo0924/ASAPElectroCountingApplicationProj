package com.example.asapelectrocountingapplicationproj;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class ElectroTimeEstimator extends AppCompatActivity {

    private RadioGroup rateTypeGroup, seasonGroup, dayTypeGroup;
    private EditText peakUsageInput, halfPeakUsageInput, offPeakUsageInput;
    private Button calculateButton, backButton;
    private TextView resultText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_electro_time_estimator);

        rateTypeGroup = findViewById(R.id.rateTypeGroup);
        seasonGroup = findViewById(R.id.seasonGroup);
        dayTypeGroup = findViewById(R.id.dayTypeGroup);
        peakUsageInput = findViewById(R.id.peakUsageInput);
        halfPeakUsageInput = findViewById(R.id.halfPeakUsageInput);
        offPeakUsageInput = findViewById(R.id.offPeakUsageInput);
        calculateButton = findViewById(R.id.calculateButton);
        resultText = findViewById(R.id.resultText);

        calculateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateInput()) {
                    calculateElectricityBill();
                } else {
                    Toast.makeText(ElectroTimeEstimator.this, "請輸入用電度數及勾選欄位", Toast.LENGTH_SHORT).show();
                }
            }
        });

        backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showExitConfirmDialog();
            }
        });
    }

    private boolean validateInput() {
        boolean isValid = true;

        if (peakUsageInput.getText().toString().isEmpty() &&
                halfPeakUsageInput.getText().toString().isEmpty() &&
                offPeakUsageInput.getText().toString().isEmpty()) {
            isValid = false;
        }

        if (rateTypeGroup.getCheckedRadioButtonId() == -1 ||
                seasonGroup.getCheckedRadioButtonId() == -1 ||
                dayTypeGroup.getCheckedRadioButtonId() == -1) {
            isValid = false;
        }

        return isValid;
    }

    private void calculateElectricityBill() {
        boolean isTwoStageRate = rateTypeGroup.getCheckedRadioButtonId() == R.id.twoStageRate;
        boolean isSummerSeason = seasonGroup.getCheckedRadioButtonId() == R.id.summerSeason;
        boolean isWeekday = dayTypeGroup.getCheckedRadioButtonId() == R.id.weekday;

        double peakUsage = parseDoubleOrZero(peakUsageInput.getText().toString());
        double halfPeakUsage = parseDoubleOrZero(halfPeakUsageInput.getText().toString());
        double offPeakUsage = parseDoubleOrZero(offPeakUsageInput.getText().toString());
        double totalUsage = peakUsage + halfPeakUsage + offPeakUsage;

        double baseFee = 75.0;
        double totalFee = baseFee;

        if (isWeekday) {
            if (isTwoStageRate) {
                if (isSummerSeason) {
                    totalFee += peakUsage * 5.01 + offPeakUsage * 1.96;
                } else {
                    totalFee += (peakUsage + halfPeakUsage) * 4.78 + offPeakUsage * 1.89;
                }
            } else { // 三段式
                if (isSummerSeason) {
                    totalFee += peakUsage * 6.92 + halfPeakUsage * 4.54 + offPeakUsage * 1.96;
                } else {
                    totalFee += halfPeakUsage * 4.33 + offPeakUsage * 1.89;
                }
            }
        } else { // 週末或離峰日
            if (isSummerSeason) {
                totalFee += totalUsage * 1.96;
            } else {
                totalFee += totalUsage * 1.89;
            }
        }

        // 超過2000度的部分加收1.02元/度
        if (totalUsage > 2000) {
            totalFee += (totalUsage - 2000) * 1.02;
        }

        resultText.setText(String.format("估計電費: %.2f 元", totalFee));
    }

    private double parseDoubleOrZero(String value) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    private void showExitConfirmDialog() {
        if (hasUserInput()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("確定返回?");
            builder.setMessage("尚未儲存資料將會被移除");
            builder.setPositiveButton("確定", (dialog, which) -> finish());
            builder.setNegativeButton("取消", null);
            builder.show();
        } else {
            finish();
        }
    }

    private boolean hasUserInput() {
        return !peakUsageInput.getText().toString().isEmpty() ||
                !halfPeakUsageInput.getText().toString().isEmpty() ||
                !offPeakUsageInput.getText().toString().isEmpty() ||
                rateTypeGroup.getCheckedRadioButtonId() != -1 ||
                seasonGroup.getCheckedRadioButtonId() != -1 ||
                dayTypeGroup.getCheckedRadioButtonId() != -1;
    }

    @Override
    public void onBackPressed() {
        showExitConfirmDialog();
    }
}