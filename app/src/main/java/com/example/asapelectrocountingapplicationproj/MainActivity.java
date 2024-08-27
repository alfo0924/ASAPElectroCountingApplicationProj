package com.example.asapelectrocountingapplicationproj;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private TextView countdownText;
    private FrameLayout adContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // 設置邊界，保持內容不會被系統狀態欄擠壓
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 初始化倒數計時相關的 View
        countdownText = findViewById(R.id.countdownText);
        adContainer = findViewById(R.id.adContainer);

        // 開始倒數計時
        startCountdown();

        // 找到電費估算器按鈕
        Button estimatorButton = findViewById(R.id.estimatorButton);
        estimatorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ElectroEstimatorPlanChoose.class);
                startActivity(intent);
            }
        });

        // 找到電費紀錄按鈕
        Button recordsButton = findViewById(R.id.recordsButton);
        recordsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ElectroBillRecords.class);
                startActivity(intent);
            }
        });

        //找到deviceButton
        Button selectElectroDeviceButton = findViewById(R.id.deviceButton);
        selectElectroDeviceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, activity_electro_device.class);
                startActivity(intent);
            }
        });
    }

    private void startCountdown() {
        // 設定倒數計時 5 秒，每秒更新一次 開啟時看到會減 1 秒 故加一秒
        new CountDownTimer(6000, 1000) {
            public void onTick(long millisUntilFinished) {
                // 更新 TextView 顯示剩餘秒數
                countdownText.setText(String.valueOf(millisUntilFinished / 1000));
            }

            public void onFinish() {
                // 倒數結束後隱藏廣告容器
                adContainer.setVisibility(View.GONE);
            }
        }.start();
    }
}
