package com.example.asapelectrocountingapplicationproj;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class ElectroTimeEstimator extends AppCompatActivity {

    private RadioGroup rateTypeGroup, seasonGroup, dayTypeGroup;
    private EditText peakUsageInput, halfPeakUsageInput, offPeakUsageInput;
    private Button calculateButton;
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
                calculateElectricityBill();
            }
        });
    }

    private void calculateElectricityBill() {
        boolean isTwoStageRate = rateTypeGroup.getCheckedRadioButtonId() == R.id.twoStageRate;
        boolean isSummerSeason = seasonGroup.getCheckedRadioButtonId() == R.id.summerSeason;
        boolean isWeekday = dayTypeGroup.getCheckedRadioButtonId() == R.id.weekday;

        double peakUsage = Double.parseDouble(peakUsageInput.getText().toString());
        double halfPeakUsage = Double.parseDouble(halfPeakUsageInput.getText().toString());
        double offPeakUsage = Double.parseDouble(offPeakUsageInput.getText().toString());
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
}