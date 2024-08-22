package com.example.asapelectrocountingapplicationproj;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ElectroTimeEstimator extends AppCompatActivity {

    private RadioGroup rateTypeGroup, seasonGroup, dayTypeGroup;
    private EditText peakUsageInput, halfPeakUsageInput, offPeakUsageInput, dateInput;
    private Button calculateButton, backButton, saveButton;
    private TextView resultText;
    private SQLiteDatabase db;
    private SimpleDateFormat dateFormat;

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
        dateInput = findViewById(R.id.dateInput);
        calculateButton = findViewById(R.id.calculateButton);
        saveButton = findViewById(R.id.saveButton);
        resultText = findViewById(R.id.resultText);

        dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        db = openOrCreateDatabase("ElectricityBills", MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS bills(id INTEGER PRIMARY KEY AUTOINCREMENT, date TEXT, amount REAL, usage REAL, remark TEXT)");

        calculateButton.setOnClickListener(v -> {
            if (validateInput()) {
                calculateElectricityBill();
            } else {
                Toast.makeText(ElectroTimeEstimator.this, "請輸入用電度數及勾選欄位", Toast.LENGTH_SHORT).show();
            }
        });

        saveButton.setOnClickListener(v -> {
            if (validateInput()) {
                saveBill();
            } else {
                Toast.makeText(ElectroTimeEstimator.this, "請輸入用電度數及勾選欄位", Toast.LENGTH_SHORT).show();
            }
        });

        backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> showExitConfirmDialog());
    }

    private boolean validateInput() {
        String date = dateInput.getText().toString().trim();
        if (date.isEmpty()) {
            Toast.makeText(this, "請輸入日期", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!isValidDate(date)) {
            Toast.makeText(this, "請輸入正確的日期格式 (YYYY-MM-DD)", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (peakUsageInput.getText().toString().isEmpty() &&
                halfPeakUsageInput.getText().toString().isEmpty() &&
                offPeakUsageInput.getText().toString().isEmpty()) {
            return false;
        }

        if (rateTypeGroup.getCheckedRadioButtonId() == -1 ||
                seasonGroup.getCheckedRadioButtonId() == -1 ||
                dayTypeGroup.getCheckedRadioButtonId() == -1) {
            return false;
        }

        return true;
    }

    private boolean isValidDate(String dateStr) {
        try {
            Date date = dateFormat.parse(dateStr);
            return dateFormat.format(date).equals(dateStr);
        } catch (Exception e) {
            return false;
        }
    }

    private void calculateElectricityBill() {
        double totalFee = calculateTotalFee();
        resultText.setText(String.format("估計電費: %.2f 元", totalFee));
    }

    private double calculateTotalFee() {
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

        return totalFee;
    }

    private double parseDoubleOrZero(String value) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    private void saveBill() {
        String date = dateInput.getText().toString().trim();
        double totalUsage = parseDoubleOrZero(peakUsageInput.getText().toString()) +
                parseDoubleOrZero(halfPeakUsageInput.getText().toString()) +
                parseDoubleOrZero(offPeakUsageInput.getText().toString());
        double totalFee = calculateTotalFee();
        String remark = generateRemark();

        ContentValues values = new ContentValues();
        values.put("date", date);
        values.put("amount", totalFee);
        values.put("usage", totalUsage);
        values.put("remark", remark);

        long result = db.insert("bills", null, values);
        if (result != -1) {
            Toast.makeText(this, "帳單已儲存", Toast.LENGTH_SHORT).show();
            clearInputs();
        } else {
            Toast.makeText(this, "儲存失敗", Toast.LENGTH_SHORT).show();
        }
    }

    private String generateRemark() {
        StringBuilder remark = new StringBuilder();
        remark.append("費率類型: ").append(rateTypeGroup.getCheckedRadioButtonId() == R.id.twoStageRate ? "兩段式" : "三段式").append(", ");
        remark.append("季節: ").append(seasonGroup.getCheckedRadioButtonId() == R.id.summerSeason ? "夏季" : "非夏季").append(", ");
        remark.append("日期類型: ").append(dayTypeGroup.getCheckedRadioButtonId() == R.id.weekday ? "平日" : "假日").append(", ");
        remark.append("尖峰用電: ").append(peakUsageInput.getText()).append(", ");
        remark.append("半尖峰用電: ").append(halfPeakUsageInput.getText()).append(", ");
        remark.append("離峰用電: ").append(offPeakUsageInput.getText());
        return remark.toString();
    }

    private void clearInputs() {
        dateInput.setText("");
        peakUsageInput.setText("");
        halfPeakUsageInput.setText("");
        offPeakUsageInput.setText("");
        rateTypeGroup.clearCheck();
        seasonGroup.clearCheck();
        dayTypeGroup.clearCheck();
        resultText.setText("");
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
        return !dateInput.getText().toString().isEmpty() ||
                !peakUsageInput.getText().toString().isEmpty() ||
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (db != null) {
            db.close();
        }
    }
}