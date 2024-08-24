package com.example.asapelectrocountingapplicationproj;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.HashMap;

public class activity_electro_device extends AppCompatActivity {

    private HashMap<String, String> appliancePowerMap;
    private Spinner applianceSpinner, hoursSpinner;
    private TextView powerTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_electro_device);

        // 初始化電器名稱與消耗電力綁定資料
        appliancePowerMap = new HashMap<>();
        appliancePowerMap.put("冷氣機", "2800");
        appliancePowerMap.put("吹風機", "800");
        appliancePowerMap.put("電暖爐", "700");
        appliancePowerMap.put("除濕機", "285");
        appliancePowerMap.put("電扇(傳統)", "66");
        appliancePowerMap.put("電扇(DC)", "27");
        appliancePowerMap.put("抽風機", "30");
        appliancePowerMap.put("燈泡(60W)", "60");
        appliancePowerMap.put("日光燈(20W)", "20");
        appliancePowerMap.put("省電燈泡", "17");
        appliancePowerMap.put("微波爐", "1200");
        appliancePowerMap.put("電磁爐", "1200");
        appliancePowerMap.put("開飲機", "800");
        appliancePowerMap.put("電鍋", "800");
        appliancePowerMap.put("電烤箱", "800");
        appliancePowerMap.put("抽油煙機", "350");
        appliancePowerMap.put("烘碗機", "200");
        appliancePowerMap.put("冰箱", "130");
        appliancePowerMap.put("乾衣機", "1200");
        appliancePowerMap.put("電熨斗", "800");
        appliancePowerMap.put("洗衣機", "420");
        appliancePowerMap.put("電視機", "220");
        appliancePowerMap.put("音響", "50");
        appliancePowerMap.put("桌電", "400");
        appliancePowerMap.put("筆電", "100");

        // 初始化 TextView 和 Spinner
        powerTextView = findViewById(R.id.power_text_view);
        applianceSpinner = findViewById(R.id.appliance_spinner);
        hoursSpinner = findViewById(R.id.hours_spinner);

        // 設置電器名稱下拉選單
        ArrayAdapter<String> applianceAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, appliancePowerMap.keySet().toArray(new String[0]));
        applianceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        applianceSpinner.setAdapter(applianceAdapter);

        // 當選擇電器名稱時更新顯示
        applianceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedAppliance = parent.getItemAtPosition(position).toString();
                String power = appliancePowerMap.get(selectedAppliance);
                powerTextView.setText(power + " W");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                powerTextView.setText("");
            }
        });

        // 設置小時數下拉選單
        String[] hours = new String[49];
        for (int i = 0; i <= 48; i++) {
            hours[i] = String.valueOf(i * 0.5); // 時數以 0.5 為間隔
        }
        ArrayAdapter<String> hoursAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, hours);
        hoursAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        hoursSpinner.setAdapter(hoursAdapter);
    }
}
