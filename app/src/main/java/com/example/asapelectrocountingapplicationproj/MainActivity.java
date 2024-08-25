package com.example.asapelectrocountingapplicationproj;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private TextView titleTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        titleTextView = findViewById(R.id.titleTextView);
        setTitleTextSize();

        ThemeManager.applyTheme(this);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setupButtons();
    }

    private void setTitleTextSize() {
        // 設置固定的字體大小，不受主題影響
        titleTextView.setTextSize(50); // 您可以根據需要調整這個值
    }

    private void setupButtons() {
        setupButton(R.id.estimatorButton, ElectroEstimatorPlanChoose.class);
        setupButton(R.id.recordsButton, ElectroBillRecords.class);
        setupButton(R.id.analyzeButton, analyze.class);
        setupButton(R.id.settingsButton, settings.class);
    }

    private void setupButton(int buttonId, final Class<?> destinationClass) {
        Button button = findViewById(buttonId);
        button.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, destinationClass);
            startActivity(intent);
        });
    }

    private final BroadcastReceiver themeChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if ("com.example.asapelectrocountingapplicationproj.THEME_CHANGED".equals(intent.getAction())) {
                ThemeManager.applyTheme(MainActivity.this);
                setTitleTextSize(); // 確保標題字體大小不變
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(themeChangeReceiver, new IntentFilter("com.example.asapelectrocountingapplicationproj.THEME_CHANGED"));
        ThemeManager.applyTheme(this);
        setTitleTextSize(); // 確保標題字體大小不變
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(themeChangeReceiver);
    }
}