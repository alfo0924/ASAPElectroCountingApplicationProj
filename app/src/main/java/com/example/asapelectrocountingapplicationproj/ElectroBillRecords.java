package com.example.asapelectrocountingapplicationproj;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class ElectroBillRecords extends AppCompatActivity {

    private SQLiteDatabase db;
    private EditText dateInput, amountInput, usageInput;
    private Button addButton, deleteButton, backButton;
    private ListView listView;
    private SimpleCursorAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_electro_bill_records);

        try {
            db = openOrCreateDatabase("ElectroBills.db", MODE_PRIVATE, null);
            db.execSQL("CREATE TABLE IF NOT EXISTS bills (id INTEGER PRIMARY KEY AUTOINCREMENT, date TEXT, amount REAL, usage REAL)");
        } catch (Exception e) {
            Toast.makeText(this, "資料庫創建失敗: " + e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        dateInput = findViewById(R.id.dateInput);
        amountInput = findViewById(R.id.amountInput);
        usageInput = findViewById(R.id.usageInput);
        addButton = findViewById(R.id.addButton);
        deleteButton = findViewById(R.id.deleteButton);
        backButton = findViewById(R.id.backButton);
        listView = findViewById(R.id.billListView);

        if (addButton == null || deleteButton == null || backButton == null || listView == null) {
            Toast.makeText(this, "界面元素初始化失敗", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        loadBills();

        addButton.setOnClickListener(v -> addOrUpdateBill());
        deleteButton.setOnClickListener(v -> deleteBill());
        backButton.setOnClickListener(v -> finish());

        listView.setOnItemClickListener((parent, view, position, id) -> {
            Cursor cursor = (Cursor) adapter.getItem(position);
            dateInput.setText(cursor.getString(cursor.getColumnIndexOrThrow("date")));
            amountInput.setText(cursor.getString(cursor.getColumnIndexOrThrow("amount")));
            usageInput.setText(cursor.getString(cursor.getColumnIndexOrThrow("usage")));
        });
    }

    private void loadBills() {
        try {
            Cursor cursor = db.rawQuery("SELECT * FROM bills ORDER BY date DESC", null);
            String[] from = {"date", "amount", "usage"};
            int[] to = {android.R.id.text1, android.R.id.text2};
            adapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_2, cursor, from, to, 0);
            listView.setAdapter(adapter);
        } catch (Exception e) {
            Toast.makeText(this, "載入資料失敗: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void addOrUpdateBill() {
        try {
            String date = dateInput.getText().toString().trim();
            String amount = amountInput.getText().toString().trim();
            String usage = usageInput.getText().toString().trim();

            if (date.isEmpty() || amount.isEmpty() || usage.isEmpty()) {
                Toast.makeText(this, "請填寫所有欄位", Toast.LENGTH_SHORT).show();
                return;
            }

            ContentValues values = new ContentValues();
            values.put("date", date);
            values.put("amount", Double.parseDouble(amount));
            values.put("usage", Double.parseDouble(usage));

            Cursor cursor = db.rawQuery("SELECT * FROM bills WHERE date = ?", new String[]{date});
            if (cursor.moveToFirst()) {
                db.update("bills", values, "date = ?", new String[]{date});
                Toast.makeText(this, "更新成功", Toast.LENGTH_SHORT).show();
            } else {
                db.insert("bills", null, values);
                Toast.makeText(this, "新增成功", Toast.LENGTH_SHORT).show();
            }
            cursor.close();
            loadBills();
            clearInputs();
        } catch (Exception e) {
            Toast.makeText(this, "操作失敗: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteBill() {
        try {
            String date = dateInput.getText().toString().trim();
            if (date.isEmpty()) {
                Toast.makeText(this, "請選擇要刪除的帳單", Toast.LENGTH_SHORT).show();
                return;
            }

            int deletedRows = db.delete("bills", "date = ?", new String[]{date});
            if (deletedRows > 0) {
                Toast.makeText(this, "刪除成功", Toast.LENGTH_SHORT).show();
                loadBills();
                clearInputs();
            } else {
                Toast.makeText(this, "找不到要刪除的帳單", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "刪除失敗: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void clearInputs() {
        dateInput.setText("");
        amountInput.setText("");
        usageInput.setText("");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (db != null) {
            db.close();
        }
    }
}