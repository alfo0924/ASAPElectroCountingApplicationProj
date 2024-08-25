package com.example.asapelectrocountingapplicationproj;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;

public class activity_electro_device extends AppCompatActivity {

    private HashMap<String, String> appliancePowerMap;
    private Spinner applianceSpinner, hoursSpinner, daysSpinner, summerSpinner, businessSpinner;
    private EditText powerTextView;
    private EditText totalTextView;
    private EditText estimateExpenseTextView;
    private ListView deviceListView;
    private ArrayList<String> deviceList;
    private ArrayAdapter<String> deviceListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_electro_device);

        // 初始化電器名稱與消耗電力綁定資料
        initAppliancePowerMap();

        // 初始化 UI 元素
        initUIElements();

        // 設置電器名稱下拉選單
        setupApplianceSpinner();

        // 設置小時數下拉選單
        setupHoursSpinner();

        // 設置天數下拉選單
        setupDaysSpinner();

        // 設置夏季/非夏季下拉選單
        setupSummerSpinner();

        // 設置電力用途下拉選單
        setupBusinessSpinner();

        // 設置顯示用的 ListView
        setupDeviceListView();

        // 設置按鈕事件
        setupAddDataDeviceButton();

        // 設置長按刪除功能
        setupLongClickDelete();
    }

    private void initAppliancePowerMap() {
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
    }

    private void initUIElements() {
        powerTextView = findViewById(R.id.power_text_view);
        totalTextView = findViewById(R.id.totalkWh);
        estimateExpenseTextView = findViewById(R.id.estimateExpense);
        applianceSpinner = findViewById(R.id.appliance_spinner);
        hoursSpinner = findViewById(R.id.hours_spinner);
        daysSpinner = findViewById(R.id.days_spinner);
        summerSpinner = findViewById(R.id.summerSpinner);
        businessSpinner = findViewById(R.id.businessSpinner);
        deviceListView = findViewById(R.id.deviceListView);
    }

    private void setupApplianceSpinner() {
        ArrayAdapter<String> applianceAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, appliancePowerMap.keySet().toArray(new String[0]));
        applianceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        applianceSpinner.setAdapter(applianceAdapter);

        applianceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedAppliance = applianceSpinner.getSelectedItem().toString();
                String powerStr = appliancePowerMap.get(selectedAppliance);
                powerTextView.setText(String.format("%s W", powerStr));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }

    private void setupHoursSpinner() {
        String[] hours = new String[49];
        for (int i = 0; i <= 48; i++) {
            hours[i] = String.valueOf(i * 0.5);
        }
        ArrayAdapter<String> hoursAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, hours);
        hoursAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        hoursSpinner.setAdapter(hoursAdapter);
    }

    private void setupDaysSpinner() {
        String[] days = new String[31];
        for (int i = 1; i <= 31; i++) {
            days[i - 1] = String.valueOf(i);
        }
        ArrayAdapter<String> daysAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, days);
        daysAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        daysSpinner.setAdapter(daysAdapter);
    }

    private void setupSummerSpinner() {
        String[] summerOptions = {"夏季", "非夏季"};
        ArrayAdapter<String> summerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, summerOptions);
        summerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        summerSpinner.setAdapter(summerAdapter);
    }

    private void setupBusinessSpinner() {
        String[] businessOptions = {"住宅用", "住宅以外非營業用", "營業用"};
        ArrayAdapter<String> businessAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, businessOptions);
        businessAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        businessSpinner.setAdapter(businessAdapter);
    }

    private void setupDeviceListView() {
        deviceList = new ArrayList<>();
        deviceListAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, deviceList);
        deviceListView.setAdapter(deviceListAdapter);
    }

    private void setupAddDataDeviceButton() {
        Button addDataDeviceButton = findViewById(R.id.addDataDevice);
        addDataDeviceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addDevice();
            }
        });
    }

    private void setupLongClickDelete() {
        deviceListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                new AlertDialog.Builder(activity_electro_device.this)
                        .setTitle("刪除資料")
                        .setMessage("確定要刪除此筆資料？")
                        .setPositiveButton("確定", (dialog, which) -> {
                            deviceList.remove(position);
                            deviceListAdapter.notifyDataSetChanged();
                            updateTotalAndEstimate();
                        })
                        .setNegativeButton("取消", null)
                        .show();
                return true;
            }
        });
    }

    private void addDevice() {
        String selectedAppliance = applianceSpinner.getSelectedItem().toString();
        String powerStr = appliancePowerMap.get(selectedAppliance);
        double power = Double.parseDouble(powerStr);

        String hoursStr = hoursSpinner.getSelectedItem().toString();
        double hours = Double.parseDouble(hoursStr);

        String daysStr = daysSpinner.getSelectedItem().toString();
        int days = Integer.parseInt(daysStr);

        if (hours == 0) {
            new AlertDialog.Builder(activity_electro_device.this)
                    .setTitle("提示")
                    .setMessage("請選擇小時數")
                    .setPositiveButton("確定", null)
                    .show();
            return;
        }

        double kWh = power * hours * days / 1000.0;

        String businessType = businessSpinner.getSelectedItem().toString();
        boolean isSummer = summerSpinner.getSelectedItem().toString().equals("夏季");
        double bill = calculateBill((int) Math.round(kWh), isSummer, businessType);

        // 新增標記
        String businessMark;
        if (businessType.equals("住宅用")) {
            businessMark = "住";
        } else if (businessType.equals("住宅以外非營業用")) {
            businessMark = "住非";
        } else {
            businessMark = "營";
        }

        String summerMark = isSummer ? "夏" : "非";

        // 使用標記來生成結果字串
        String result = String.format("%s (%s, %s): %.2f 度, 電費: %.2f 元",
                selectedAppliance, businessMark, summerMark, kWh, bill);
        deviceList.add(result);
        deviceListAdapter.notifyDataSetChanged();


        updateTotalAndEstimate();
    }

    private void updateTotalAndEstimate() {
        double totalKWh = 0.0;
        double totalBill = 0.0;
        for (String item : deviceList) {
            String[] parts = item.split(":");
            if (parts.length >= 3) {
                // 提取度數和電費
                String[] kWhPart = parts[1].split(" 度");
                String[] billPart = parts[2].split(" 元");

                // 確保在分割後有值
                if (kWhPart.length > 0 && billPart.length > 0) {
                    try {
                        totalKWh += Double.parseDouble(kWhPart[0].trim());
                        totalBill += Double.parseDouble(billPart[0].trim());
                    } catch (NumberFormatException e) {
                        // 處理解析錯誤
                        e.printStackTrace();
                    }
                }
            }
        }

        // 更新顯示
        totalTextView.setText(String.format("%.2f", totalKWh));
        estimateExpenseTextView.setText(String.format("%.2f", totalBill));
    }

    private double calculateBill(int kWh, boolean isSummer, String businessType) {
        if (businessType.equals("住宅用") || businessType.equals("住宅以外非營業用")) {
            return calculateFamilyBill(kWh, isSummer);
        } else if (businessType.equals("營業用")) {
            return calculateBusinessBill(kWh, isSummer);
        }
        return 0;
    }

    private double calculateFamilyBill(int kWh, boolean isSummer) {
        double bill = 0;
        int[] thresholds = {120, 330, 500, 700, 1000};
        double[] summerRates = {1.68, 2.45, 3.70, 5.04, 6.24, 8.46};
        double[] nonSummerRates = {1.68, 2.16, 3.03, 4.14, 5.07, 6.63};
        int remainingKWh = kWh;

        for (int i = 0; i < thresholds.length && remainingKWh > 0; i++) {
            int lowerBound = (i == 0) ? 1 : thresholds[i-1] + 1;
            int upperBound = thresholds[i];
            int usageInThisBracket = Math.min(remainingKWh, upperBound - lowerBound + 1);
            bill += usageInThisBracket * (isSummer ? summerRates[i] : nonSummerRates[i]);
            remainingKWh -= usageInThisBracket;
        }

        if (remainingKWh > 0) {
            bill += remainingKWh * (isSummer ? summerRates[5] : nonSummerRates[5]);
        }

        return bill;
    }

    private double calculateBusinessBill(int kWh, boolean isSummer) {
        double bill = 0;
        int[] thresholds = {330, 700, 1500, 3000};
        double[] summerRates = {2.61, 3.66, 4.46, 7.08, 7.43};
        double[] nonSummerRates = {2.18, 3.00, 3.61, 5.56, 5.83};
        int remainingKWh = kWh;

        for (int i = 0; i < thresholds.length && remainingKWh > 0; i++) {
            int lowerBound = (i == 0) ? 1 : thresholds[i-1] + 1;
            int upperBound = thresholds[i];
            int usageInThisBracket = Math.min(remainingKWh, upperBound - lowerBound + 1);
            bill += usageInThisBracket * (isSummer ? summerRates[i] : nonSummerRates[i]);
            remainingKWh -= usageInThisBracket;
        }

        if (remainingKWh > 0) {
            bill += remainingKWh * (isSummer ? summerRates[4] : nonSummerRates[4]);
        }

        return bill;
    }
}