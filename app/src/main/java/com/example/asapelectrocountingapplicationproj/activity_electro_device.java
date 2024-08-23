package com.example.asapelectrocountingapplicationproj;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import androidx.appcompat.app.AppCompatActivity;

public class activity_electro_device extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_electro_device); // 使用 activity_electro_device 佈局

        // 取得 LinearLayout 容器
        LinearLayout spinnerContainer = findViewById(R.id.spinnerContainer);

        // 設置電器名稱資料
        String[] appliances = {"冷氣機", "吹風機", "電暖爐", "除濕機", "電扇(傳統)", "電扇(DC)", "抽風機",
                "燈泡(60W)", "日光燈(20W)", "省電燈泡", "微波爐", "電磁爐", "開飲機",
                "電鍋", "電烤箱", "抽油煙機", "烘碗機", "冰箱", "乾衣機", "電熨斗",
                "洗衣機", "電視機", "音響", "桌電", "筆電"};

        // 設置消耗電力資料
        String[] powerConsumption = {"2800", "800", "700", "285", "66", "27", "30", "60", "20", "17", "1200",
                "1200", "800", "800", "800", "350", "200", "130", "1200", "800", "420",
                "220", "50", "400", "100"};

        // 設置時數資料
        String[] hours = new String[49];
        for (int i = 0; i <= 48; i++) {
            hours[i] = String.valueOf(i * 0.5);
        }

        // 創建並設置電器名稱 Spinner
        Spinner applianceSpinner = new Spinner(this);
        ArrayAdapter<String> applianceAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, appliances);
        applianceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        applianceSpinner.setAdapter(applianceAdapter);
        spinnerContainer.addView(applianceSpinner);

        // 創建並設置消耗電力 Spinner
        Spinner powerSpinner = new Spinner(this);
        ArrayAdapter<String> powerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, powerConsumption);
        powerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        powerSpinner.setAdapter(powerAdapter);
        spinnerContainer.addView(powerSpinner);

        // 創建並設置時數 Spinner
        Spinner hoursSpinner = new Spinner(this);
        ArrayAdapter<String> hoursAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, hours);
        hoursAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        hoursSpinner.setAdapter(hoursAdapter);
        spinnerContainer.addView(hoursSpinner);
    }
}
