package com.example.asapelectrocountingapplicationproj;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ElectroEstimatorPlanChoose extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ThemeManager.applyTheme(this);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_electro_estimator_plan_choose);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 添加返回按鈕
        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // 這會關閉當前活動並返回到MainActivity
            }
        });

        // 添加一般電費估算按鈕
        Button normalEstimatorButton = findViewById(R.id.normalEstimatorButton);
        normalEstimatorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ElectroEstimatorPlanChoose.this, ElectroEstimator.class);
                startActivity(intent);
            }
        });

        // 添加時間電價估算按鈕
        Button timeEstimatorButton = findViewById(R.id.timeEstimatorButton);
        timeEstimatorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ElectroEstimatorPlanChoose.this, ElectroTimeEstimator.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish(); // 直接關閉當前活動，返回到MainActivity
    }
    @Override
    protected void onResume() {
        super.onResume();
        ThemeManager.applyTheme(this);
    }
}