package com.example.asapelectrocountingapplicationproj;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import yuku.ambilwarna.AmbilWarnaDialog;

public class settings extends AppCompatActivity {

    private SeekBar seekBarTextSize;
    private TextView tvTextSizePreview;
    private Button btnBackgroundColor, btnTextColor, btnButtonColor, btnClearData, btnReturn, btnApply, btnReset;
    private SharedPreferences sharedPreferences;
    private static final String PREF_NAME = "AppTheme";
    private static final String KEY_TEXT_SIZE = "text_size";
    private static final String KEY_BACKGROUND_COLOR = "background_color";
    private static final String KEY_TEXT_COLOR = "text_color";
    private static final String KEY_BUTTON_COLOR = "button_color";

    private boolean settingsChanged = false;
    private float currentTextSize;
    private int currentBackgroundColor, currentTextColor, currentButtonColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);

        initializeViews();
        loadSettings();
        setupListeners();
    }

    private void initializeViews() {
        seekBarTextSize = findViewById(R.id.seekBarTextSize);
        tvTextSizePreview = findViewById(R.id.tvTextSizePreview);
        btnBackgroundColor = findViewById(R.id.btnBackgroundColor);
        btnTextColor = findViewById(R.id.btnTextColor);
        btnButtonColor = findViewById(R.id.btnButtonColor);
        btnClearData = findViewById(R.id.btnClearData);
        btnReturn = findViewById(R.id.btnReturn);
        btnApply = findViewById(R.id.btnApply);
        btnReset = findViewById(R.id.btnReset);
    }

    private void loadSettings() {
        currentTextSize = sharedPreferences.getFloat(KEY_TEXT_SIZE, 18);
        currentBackgroundColor = sharedPreferences.getInt(KEY_BACKGROUND_COLOR, Color.WHITE);
        currentTextColor = sharedPreferences.getInt(KEY_TEXT_COLOR, Color.BLACK);
        currentButtonColor = sharedPreferences.getInt(KEY_BUTTON_COLOR, Color.LTGRAY);

        seekBarTextSize.setProgress((int) currentTextSize - 14);
        tvTextSizePreview.setTextSize(currentTextSize);
        getWindow().getDecorView().setBackgroundColor(currentBackgroundColor);
        tvTextSizePreview.setTextColor(currentTextColor);
        updateButtonColors();
    }

    private void setupListeners() {
        seekBarTextSize.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                currentTextSize = 14 + progress;
                tvTextSizePreview.setTextSize(currentTextSize);
                settingsChanged = true;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        btnBackgroundColor.setOnClickListener(v -> showColorPicker("背景顏色", KEY_BACKGROUND_COLOR));
        btnTextColor.setOnClickListener(v -> showColorPicker("文字顏色", KEY_TEXT_COLOR));
        btnButtonColor.setOnClickListener(v -> showColorPicker("按鈕顏色", KEY_BUTTON_COLOR));
        btnClearData.setOnClickListener(v -> showClearDataConfirmDialog());
        btnReturn.setOnClickListener(v -> handleReturn());
        btnApply.setOnClickListener(v -> applySettings());
        btnReset.setOnClickListener(v -> resetSettings());
    }

    private void updateButtonColors() {
        btnBackgroundColor.setBackgroundColor(currentButtonColor);
        btnTextColor.setBackgroundColor(currentButtonColor);
        btnButtonColor.setBackgroundColor(currentButtonColor);
        btnClearData.setBackgroundColor(currentButtonColor);
        btnReturn.setBackgroundColor(currentButtonColor);
        btnApply.setBackgroundColor(currentButtonColor);
        btnReset.setBackgroundColor(currentButtonColor);

        btnBackgroundColor.setTextColor(currentTextColor);
        btnTextColor.setTextColor(currentTextColor);
        btnButtonColor.setTextColor(currentTextColor);
        btnClearData.setTextColor(currentTextColor);
        btnReturn.setTextColor(currentTextColor);
        btnApply.setTextColor(currentTextColor);
        btnReset.setTextColor(currentTextColor);
    }

    private void showColorPicker(String title, final String key) {
        int initialColor = sharedPreferences.getInt(key, Color.WHITE);
        AmbilWarnaDialog colorPicker = new AmbilWarnaDialog(this, initialColor, new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onCancel(AmbilWarnaDialog dialog) {}

            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {
                if (key.equals(KEY_BACKGROUND_COLOR)) {
                    currentBackgroundColor = color;
                    getWindow().getDecorView().setBackgroundColor(color);
                } else if (key.equals(KEY_TEXT_COLOR)) {
                    currentTextColor = color;
                    tvTextSizePreview.setTextColor(color);
                    updateButtonColors();
                } else if (key.equals(KEY_BUTTON_COLOR)) {
                    currentButtonColor = color;
                    updateButtonColors();
                }
                settingsChanged = true;
            }
        });
        colorPicker.show();
    }

    private void handleReturn() {
        if (settingsChanged) {
            new AlertDialog.Builder(this)
                    .setTitle("確定返回？")
                    .setMessage("當前所有設置將在按下確定後不會變更。")
                    .setPositiveButton("確定", (dialog, which) -> finish())
                    .setNegativeButton("取消", null)
                    .show();
        } else {
            finish();
        }
    }

    private void applySettings() {
        ThemeManager.saveTheme(this, currentTextSize, currentBackgroundColor, currentTextColor, currentButtonColor);
        settingsChanged = false;
        Toast.makeText(this, "設置已套用", Toast.LENGTH_SHORT).show();
        ThemeManager.applyTheme(this);
        // 通知其他活動更新主題
        sendBroadcast(new Intent("com.example.asapelectrocountingapplicationproj.THEME_CHANGED"));
    }

    private void resetSettings() {
        new AlertDialog.Builder(this)
                .setTitle("初始化設置")
                .setMessage("確定要將所有設置恢復到初始狀態嗎？")
                .setPositiveButton("確定", (dialog, which) -> {
                    clearAllData();
                    loadSettings();
                    ThemeManager.applyTheme(this);
                    Toast.makeText(settings.this, "所有設置已重置", Toast.LENGTH_SHORT).show();
                    // 通知其他活動更新主題
                    sendBroadcast(new Intent("com.example.asapelectrocountingapplicationproj.THEME_CHANGED"));
                })
                .setNegativeButton("取消", null)
                .show();
    }

    private void showClearDataConfirmDialog() {
        new AlertDialog.Builder(this)
                .setTitle("清除數據")
                .setMessage("確定要清除所有設置和數據嗎？此操作不可逆。")
                .setPositiveButton("確定", (dialog, which) -> {
                    clearAllData();
                    loadSettings();
                    ThemeManager.applyTheme(this);
                    Toast.makeText(settings.this, "所有數據已清除", Toast.LENGTH_SHORT).show();
                    // 通知其他活動更新主題
                    sendBroadcast(new Intent("com.example.asapelectrocountingapplicationproj.THEME_CHANGED"));
                    // 清除 ElectroBillRecords 中的所有帳單記錄
                    clearElectroBillRecords();
                })
                .setNegativeButton("取消", null)
                .show();
    }

    private void clearAllData() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        settingsChanged = false;
    }

    private void clearElectroBillRecords() {
        SQLiteDatabase db = openOrCreateDatabase("ElectricityBills", MODE_PRIVATE, null);
        db.execSQL("DELETE FROM bills");
        db.close();
    }

    @Override
    public void onBackPressed() {
        handleReturn();
    }
}