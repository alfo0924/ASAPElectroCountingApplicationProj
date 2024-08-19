package com.example.asapelectrocountingapplicationproj;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ElectroEstimator extends AppCompatActivity {

    private EditText usageEditText;
    private Spinner timeSpinner, seasonSpinner;
    private Button calculateButton, saveButton;
    private TextView resultTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_electro_estimator);

        // 初始化視圖
        initViews();

        // 設置Spinner適配器
        setupSpinners();

        // 設置按鈕點擊監聽器
        setupButtonListeners();

        // 設置窗口插入監聽器
        setupWindowInsets();
    }

    private void initViews() {
        usageEditText = findViewById(R.id.usageEditText);
        timeSpinner = findViewById(R.id.timeSpinner);
        seasonSpinner = findViewById(R.id.seasonSpinner);
        calculateButton = findViewById(R.id.calculateButton);
        saveButton = findViewById(R.id.saveButton);
        resultTextView = findViewById(R.id.resultTextView);
    }

    private void setupSpinners() {
        ArrayAdapter<CharSequence> timeAdapter = ArrayAdapter.createFromResource(this,
                R.array.time_array, android.R.layout.simple_spinner_item);
        timeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        timeSpinner.setAdapter(timeAdapter);

        ArrayAdapter<CharSequence> seasonAdapter = ArrayAdapter.createFromResource(this,
                R.array.season_array, android.R.layout.simple_spinner_item);
        seasonAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        seasonSpinner.setAdapter(seasonAdapter);
    }

    private void setupButtonListeners() {
        calculateButton.setOnClickListener(v -> calculateElectricity());
        saveButton.setOnClickListener(v -> saveResult());
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
        String time = timeSpinner.getSelectedItem().toString();
        String season = seasonSpinner.getSelectedItem().toString();

        // 這裡是一個簡單的電費計算邏輯,您需要根據實際電價政策進行調整
        double rate = (season.equals("夏季")) ? 3.70 : 3.03;
        if (time.equals("尖峰時段")) {
            rate *= 1.2;
        }

        double cost = usage * rate;
        String result = String.format("預估電費: %.2f 元", cost);
        resultTextView.setText(result);
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
}