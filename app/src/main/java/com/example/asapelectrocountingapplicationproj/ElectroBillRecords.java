package com.example.asapelectrocountingapplicationproj;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class ElectroBillRecords extends AppCompatActivity {

    private SQLiteDatabase db;
    private EditText dateInput, amountInput, usageInput;
    private Button addButton, deleteButton;
    private ListView listView;
    private SimpleCursorAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_electro_bill_records);

        db = openOrCreateDatabase("ElectroBills.db", MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS bills (id INTEGER PRIMARY KEY AUTOINCREMENT, date TEXT, amount REAL, usage REAL)");

        dateInput = findViewById(R.id.dateInput);
        amountInput = findViewById(R.id.amountInput);
        usageInput = findViewById(R.id.usageInput);
        addButton = findViewById(R.id.addButton);
        deleteButton = findViewById(R.id.deleteButton);
        listView = findViewById(R.id.billListView);

        loadBills();

        addButton.setOnClickListener(v -> addOrUpdateBill());
        deleteButton.setOnClickListener(v -> deleteBill());

        listView.setOnItemClickListener((parent, view, position, id) -> {
            Cursor cursor = (Cursor) adapter.getItem(position);
            dateInput.setText(cursor.getString(cursor.getColumnIndex("date")));
            amountInput.setText(cursor.getString(cursor.getColumnIndex("amount")));
            usageInput.setText(cursor.getString(cursor.getColumnIndex("usage")));
        });
    }

    private void loadBills() {
        Cursor cursor = db.rawQuery("SELECT * FROM bills ORDER BY date DESC", null);
        String[] from = {"date", "amount", "usage"};
        int[] to = {android.R.id.text1, android.R.id.text2};
        adapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_2, cursor, from, to, 0);
        listView.setAdapter(adapter);
    }

    private void addOrUpdateBill() {
        String date = dateInput.getText().toString();
        String amount = amountInput.getText().toString();
        String usage = usageInput.getText().toString();

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
    }

    private void deleteBill() {
        String date = dateInput.getText().toString();
        if (date.isEmpty()) {
            Toast.makeText(this, "請選擇要刪除的帳單", Toast.LENGTH_SHORT).show();
            return;
        }

        db.delete("bills", "date = ?", new String[]{date});
        Toast.makeText(this, "刪除成功", Toast.LENGTH_SHORT).show();
        loadBills();
        clearInputs();
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