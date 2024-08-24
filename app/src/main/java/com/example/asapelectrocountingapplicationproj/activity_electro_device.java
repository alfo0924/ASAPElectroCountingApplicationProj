package com.example.asapelectrocountingapplicationproj;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.HashMap;

public class activity_electro_device extends AppCompatActivity {

    private static final String RESIDENTIAL = "住宅用";
    private static final String NON_RESIDENTIAL = "住宅以外非營業用";
    private static final String COMMERCIAL = "營業用";

    private HashMap<String, String> appliancePowerMap;
    private Spinner applianceSpinner, hoursSpinner, daysSpinner, summerSpinner, businessSpinner;
    private TextView powerTextView;
    private ListView deviceListView;
    private ArrayList<String> deviceList;
    private ArrayAdapter<String> deviceListAdapter;

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
        daysSpinner = findViewById(R.id.days_spinner);
        deviceListView = findViewById(R.id.deviceListView);
        summerSpinner = findViewById(R.id.summerSpinner);
        businessSpinner = findViewById(R.id.businessSpinner);

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

        // 設置 1 到 31 天的下拉選單
        String[] days = new String[31];
        for (int i = 1; i <= 31; i++) {
            days[i - 1] = String.valueOf(i); // 1 到 31 天
        }
        ArrayAdapter<String> daysAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, days);
        daysAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        daysSpinner.setAdapter(daysAdapter);

        // 設置夏季與非夏季下拉選單
        String[] summerOptions = {"夏季", "非夏季"};
        ArrayAdapter<String> summerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, summerOptions);
        summerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        summerSpinner.setAdapter(summerAdapter);

        // 設置電力用途下拉選單
        String[] businessOptions = {RESIDENTIAL, NON_RESIDENTIAL, COMMERCIAL};
        ArrayAdapter<String> businessAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, businessOptions);
        businessAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        businessSpinner.setAdapter(businessAdapter);

        // 設置顯示用的 ListView
        deviceList = new ArrayList<>();
        deviceListAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, deviceList);
        deviceListView.setAdapter(deviceListAdapter);

        // 設置按鈕事件
        Button addDataDeviceButton = findViewById(R.id.addDataDevice);
        addDataDeviceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 取得選取的電器資料
                String selectedAppliance = applianceSpinner.getSelectedItem().toString();
                String powerStr = appliancePowerMap.get(selectedAppliance);
                double power = Double.parseDouble(powerStr);

                // 取得選取的使用小時數與天數
                String hoursStr = hoursSpinner.getSelectedItem().toString();
                double hours = Double.parseDouble(hoursStr);

                String daysStr = daysSpinner.getSelectedItem().toString();
                int days = Integer.parseInt(daysStr);

                // 取得選取的季節與用途
                String summerOption = summerSpinner.getSelectedItem().toString();
                String businessOption = businessSpinner.getSelectedItem().toString();

                // 檢查小時數是否為 0
                if (hours == 0) {
                    // 顯示提示對話框
                    new AlertDialog.Builder(activity_electro_device.this)
                            .setTitle("提示")
                            .setMessage("請選擇小時數")
                            .setPositiveButton("確定", null)
                            .show();
                    return;
                }

                // 計算度數
                double kWh = power * hours * days / 1000.0;

                // 根據用途類型和季節來調整電費計算 (假設有額外費用需求)
                double additionalCost = 0.0;
                if (summerOption.equals("夏季")) {
                    additionalCost += 0.2; // 假設夏季加收 0.2 單位
                }
                if (businessOption.equals(COMMERCIAL)) {
                    additionalCost += 0.5; // 假設營業用加收 0.5 單位
                }

                double totalCost = kWh + additionalCost;

                // 建立顯示的字符串，只包含電器名稱和計算出來的度數
                String result = String.format("%s: %.2f 度 (總電費: %.2f 單位)", selectedAppliance, kWh, totalCost);

                // 將結果添加到列表並更新顯示
                deviceList.add(result);
                deviceListAdapter.notifyDataSetChanged();
            }
        });
    }
}
