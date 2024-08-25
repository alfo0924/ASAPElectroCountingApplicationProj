package com.example.asapelectrocountingapplicationproj;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.drawable.DrawableCompat;

import yuku.ambilwarna.AmbilWarnaDialog;

public class settings extends AppCompatActivity {

    private SeekBar seekBarTextSize;
    private TextView tvTextSizePreview;
    private Button btnBackgroundColor, btnTextColor, btnButtonColor, btnClearData;
    private Button btnReturn, btnApply, btnReset;
    private SharedPreferences sharedPreferences;
    private static final String PREF_NAME = "AppSettings";
    private static final String KEY_TEXT_SIZE = "text_size";
    private static final String KEY_BACKGROUND_COLOR = "background_color";
    private static final String KEY_TEXT_COLOR = "text_color";
    private static final String KEY_BUTTON_COLOR = "button_color";

    private boolean settingsChanged = false;

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

    private void setupListeners() {
        seekBarTextSize.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float textSize = 14 + progress;
                tvTextSizePreview.setTextSize(textSize);
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

    private void loadSettings() {
        float textSize = sharedPreferences.getFloat(KEY_TEXT_SIZE, 18);
        int backgroundColor = sharedPreferences.getInt(KEY_BACKGROUND_COLOR, Color.WHITE);
        int textColor = sharedPreferences.getInt(KEY_TEXT_COLOR, Color.BLACK);
        int buttonColor = sharedPreferences.getInt(KEY_BUTTON_COLOR, Color.BLUE);

        seekBarTextSize.setProgress((int) textSize - 14);
        tvTextSizePreview.setTextSize(textSize);
        getWindow().getDecorView().setBackgroundColor(backgroundColor);
        tvTextSizePreview.setTextColor(textColor);
        updateButtonColors(buttonColor);
    }

    private void updateButtonColors(int color) {
        setButtonColor(btnBackgroundColor, color);
        setButtonColor(btnTextColor, color);
        setButtonColor(btnButtonColor, color);
        setButtonColor(btnClearData, color);
        setButtonColor(btnReturn, color);
        setButtonColor(btnApply, color);
        setButtonColor(btnReset, color);
    }

    private void setButtonColor(Button button, int color) {
        Drawable buttonDrawable = button.getBackground();
        buttonDrawable = DrawableCompat.wrap(buttonDrawable);
        DrawableCompat.setTint(buttonDrawable, color);
        button.setBackground(buttonDrawable);
    }

    private void showColorPicker(String title, final String key) {
        int initialColor = sharedPreferences.getInt(key, Color.WHITE);
        AmbilWarnaDialog colorPicker = new AmbilWarnaDialog(this, initialColor, new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onCancel(AmbilWarnaDialog dialog) {}

            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {
                if (key.equals(KEY_BACKGROUND_COLOR)) {
                    getWindow().getDecorView().setBackgroundColor(color);
                } else if (key.equals(KEY_TEXT_COLOR)) {
                    tvTextSizePreview.setTextColor(color);
                } else if (key.equals(KEY_BUTTON_COLOR)) {
                    updateButtonColors(color);
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
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat(KEY_TEXT_SIZE, tvTextSizePreview.getTextSize() / getResources().getDisplayMetrics().scaledDensity);
        editor.putInt(KEY_BACKGROUND_COLOR, ((ColorDrawable) getWindow().getDecorView().getBackground()).getColor());
        editor.putInt(KEY_TEXT_COLOR, tvTextSizePreview.getCurrentTextColor());
        editor.putInt(KEY_BUTTON_COLOR, getButtonColor(btnBackgroundColor));
        editor.apply();

        settingsChanged = false;
        Toast.makeText(this, "設置已套用", Toast.LENGTH_SHORT).show();
    }

    private int getButtonColor(Button button) {
        Drawable buttonDrawable = button.getBackground();
        if (buttonDrawable instanceof ColorDrawable) {
            return ((ColorDrawable) buttonDrawable).getColor();
        } else {
            return Color.BLUE; // 默認顏色
        }
    }

    private void resetSettings() {
        new AlertDialog.Builder(this)
                .setTitle("初始化設置")
                .setMessage("確定要將所有設置恢復到初始狀態嗎？")
                .setPositiveButton("確定", (dialog, which) -> {
                    clearAllData();
                    loadSettings();
                    Toast.makeText(settings.this, "所有設置已重置", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(settings.this, "所有數據已清除", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("取消", null)
                .show();
    }

    private void clearAllData() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        loadSettings();
        settingsChanged = false;
    }

    @Override
    public void onBackPressed() {
        handleReturn();
    }
}