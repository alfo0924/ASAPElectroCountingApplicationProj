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

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 找到電費估算器按鈕
        Button estimatorButton = findViewById(R.id.estimatorButton);

        // 設置按鈕點擊監聽器
        estimatorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 創建一個Intent來啟動ElectroEstimator活動
                Intent intent = new Intent(MainActivity.this, ElectroEstimator.class);
                startActivity(intent);
            }
        });
    }
}