package com.example.asapelectrocountingapplicationproj;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class ThemeManager {
    private static final String PREF_NAME = "AppTheme";
    private static final String KEY_TEXT_SIZE = "text_size";
    private static final String KEY_BACKGROUND_COLOR = "background_color";
    private static final String KEY_TEXT_COLOR = "text_color";
    private static final String KEY_BUTTON_COLOR = "button_color";

    public static void applyTheme(Activity activity) {
        SharedPreferences prefs = activity.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        int backgroundColor = prefs.getInt(KEY_BACKGROUND_COLOR, Color.WHITE);
        int textColor = prefs.getInt(KEY_TEXT_COLOR, Color.BLACK);
        float textSize = prefs.getFloat(KEY_TEXT_SIZE, 18);
        int buttonColor = prefs.getInt(KEY_BUTTON_COLOR, Color.LTGRAY);

        activity.getWindow().getDecorView().setBackgroundColor(backgroundColor);
        applyThemeToViewHierarchy(activity.getWindow().getDecorView(), textColor, textSize, buttonColor, backgroundColor);
    }

    private static void applyThemeToViewHierarchy(View view, int textColor, float textSize, int buttonColor, int backgroundColor) {
        if (view instanceof TextView) {
            TextView textView = (TextView) view;
            if (textView.getId() == R.id.titleTextView) {
                textView.setTextColor(getContrastColor(backgroundColor));
            } else if (!(textView instanceof Button)) {
                textView.setTextColor(textColor);
                textView.setTextSize(textSize);
            }
        }
        if (view instanceof Button) {
            ((Button) view).setBackgroundColor(buttonColor);
            ((Button) view).setTextColor(textColor);
        }

        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                applyThemeToViewHierarchy(viewGroup.getChildAt(i), textColor, textSize, buttonColor, backgroundColor);
            }
        }
    }

    public static void saveTheme(Context context, float textSize, int backgroundColor, int textColor, int buttonColor) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit();
        editor.putFloat(KEY_TEXT_SIZE, textSize);
        editor.putInt(KEY_BACKGROUND_COLOR, backgroundColor);
        editor.putInt(KEY_TEXT_COLOR, textColor);
        editor.putInt(KEY_BUTTON_COLOR, buttonColor);
        editor.apply();
    }

    public static int getContrastColor(int color) {
        double luminance = (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)) / 255;
        return luminance > 0.5 ? Color.BLACK : Color.WHITE;
    }
}