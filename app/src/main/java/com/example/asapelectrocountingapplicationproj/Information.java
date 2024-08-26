package com.example.asapelectrocountingapplicationproj;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;

public class Information extends AppCompatActivity {

    private LinearLayout newsContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);

        newsContainer = findViewById(R.id.newsContainer);
        Button backButton = findViewById(R.id.backButton);
        Button updateButton = findViewById(R.id.updateButton);

        backButton.setOnClickListener(v -> finish());
        updateButton.setOnClickListener(v -> fetchNews());

        fetchNews();
    }

    private void fetchNews() {
        new Thread(() -> {
            try {
                String url = "https://www.google.com/search?sca_esv=f2f12fe6c03236ec&sca_upv=1&sxsrf=ADLYWIJOaQbK7ZYHs05-BCfCfg-nmij09w:1724638776025&q=%E9%9B%BB%E5%83%B9%E5%8D%B3%E6%99%82%E6%96%B0%E8%81%9E&tbm=nws&source=lnms&fbs=AEQNm0AaBOazvTRM_Uafu9eNJJzC4WrwbrbA29xwRv1f3gcqcrwwQJc7RsYiWmmMk8q1vObddwDQFXnfN21mN8IiV05Vqf6TUTbv0Ku2aHG7u00-e3plMdZDebG37T1Qmz-pD_-bujmhZ33OvZIG-9F086Z5Vm_lkuB3XMPOH1y_kF-cwdEyyUl-iYUtzQLfE6E3PxYgDI4l&sa=X&ved=2ahUKEwiynPGMzJGIAxWiafUHHQRLAH8Q0pQJegQIEBAB&biw=1593&bih=756&dpr=2";
                Document doc = Jsoup.connect(url).get();
                Elements newsItems = doc.select("div.SoaBEf");

                runOnUiThread(() -> {
                    newsContainer.removeAllViews();
                    for (Element item : newsItems) {
                        Element titleElement = item.selectFirst("div.n0jPhd");
                        Element linkElement = item.selectFirst("a.WlydOe");
                        if (titleElement != null && linkElement != null) {
                            String title = titleElement.text();
                            String newsUrl = linkElement.attr("href");
                            addNewsItem(title, newsUrl);
                        }
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    TextView errorText = new TextView(Information.this);
                    errorText.setText("無法獲取新聞，請檢查網絡連接。");
                    newsContainer.addView(errorText);
                });
            }
        }).start();
    }

    private void addNewsItem(String title, String url) {
        TextView newsItem = new TextView(this);
        newsItem.setText(title);
        newsItem.setPadding(0, 16, 0, 16);
        newsItem.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        });
        newsContainer.addView(newsItem);
    }
}