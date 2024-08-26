package com.example.asapelectrocountingapplicationproj;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;

public class Information extends AppCompatActivity {

    private TextView newsTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);

        newsTextView = findViewById(R.id.newsTextView);
        Button backButton = findViewById(R.id.backButton);
        Button updateButton = findViewById(R.id.updateButton);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchNews();
            }
        });

        fetchNews();
    }

    private void fetchNews() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final StringBuilder newsBuilder = new StringBuilder();
                try {
                    String url = "https://www.google.com/search?q=%E9%9B%BB%E5%83%B9%E5%8D%B3%E6%99%82%E6%96%B0%E8%81%9E&tbm=nws";
                    Document doc = Jsoup.connect(url).get();
                    Elements newsHeadlines = doc.select("div.mCBkyc");
                    for (Element headline : newsHeadlines) {
                        newsBuilder.append(headline.text()).append("\n\n");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    newsBuilder.append("無法獲取新聞，請檢查網絡連接。");
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        newsTextView.setText(newsBuilder.toString());
                    }
                });
            }
        }).start();
    }
}