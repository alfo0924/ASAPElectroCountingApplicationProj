package com.example.asapelectrocountingapplicationproj;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ElectroBillRecords extends AppCompatActivity {

    private SQLiteDatabase db;
    private SimpleCursorAdapter adapter;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_electro_bill_records);

        db = openOrCreateDatabase("ElectroBills.db", MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS bills (id INTEGER PRIMARY KEY AUTOINCREMENT, date TEXT, amount REAL, usage REAL)");

        listView = findViewById(R.id.billListView);
        Button addButton = findViewById(R.id.addButton);

        loadBills();

        addButton.setOnClickListener(v -> showAddDialog());

        listView.setOnItemLongClickListener((parent, view, position, id) -> {
            showEditDeleteDialog(id);
            return true;
        });
    }

    private void loadBills() {
        Cursor cursor = db.rawQuery("SELECT * FROM bills ORDER BY date DESC", null);
        String[] from = {"date", "amount", "usage"};
        int[] to = {R.id.dateText, R.id.amountText, R.id.usageText};
        adapter = new SimpleCursorAdapter(this, R.layout.bill_item, cursor, from, to, 0);
        listView.setAdapter(adapter);
    }

    private void showAddDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_bill, null);
        final EditText amountInput = dialogView.findViewById(R.id.amountInput);
        final EditText usageInput = dialogView.findViewById(R.id.usageInput);

        builder.setView(dialogView)
                .setTitle("新增帳單")
                .setPositiveButton("確定", (dialog, id) -> {
                    String amount = amountInput.getText().toString();
                    String usage = usageInput.getText().toString();
                    if (!amount.isEmpty() && !usage.isEmpty()) {
                        addBill(Double.parseDouble(amount), Double.parseDouble(usage));
                    }
                })
                .setNegativeButton("取消", null);
        builder.create().show();
    }

    private void addBill(double amount, double usage) {
        ContentValues values = new ContentValues();
        values.put("date", new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date()));
        values.put("amount", amount);
        values.put("usage", usage);
        db.insert("bills", null, values);
        loadBills();
    }

    private void showEditDeleteDialog(final long id) {
        new AlertDialog.Builder(this)
                .setTitle("編輯或刪除")
                .setItems(new CharSequence[]{"編輯", "刪除"}, (dialog, which) -> {
                    if (which == 0) {
                        showEditDialog(id);
                    } else {
                        deleteBill(id);
                    }
                })
                .show();
    }

    private void showEditDialog(final long id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_bill, null);
        final EditText amountInput = dialogView.findViewById(R.id.amountInput);
        final EditText usageInput = dialogView.findViewById(R.id.usageInput);

        Cursor cursor = db.rawQuery("SELECT * FROM bills WHERE id = ?", new String[]{String.valueOf(id)});
        if (cursor.moveToFirst()) {
            amountInput.setText(cursor.getString(cursor.getColumnIndex("amount")));
            usageInput.setText(cursor.getString(cursor.getColumnIndex("usage")));
        }
        cursor.close();

        builder.setView(dialogView)
                .setTitle("編輯帳單")
                .setPositiveButton("確定", (dialog, which) -> {
                    String amount = amountInput.getText().toString();
                    String usage = usageInput.getText().toString();
                    if (!amount.isEmpty() && !usage.isEmpty()) {
                        updateBill(id, Double.parseDouble(amount), Double.parseDouble(usage));
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }

    private void updateBill(long id, double amount, double usage) {
        ContentValues values = new ContentValues();
        values.put("amount", amount);
        values.put("usage", usage);
        db.update("bills", values, "id = ?", new String[]{String.valueOf(id)});
        loadBills();
    }

    private void deleteBill(long id) {
        db.delete("bills", "id = ?", new String[]{String.valueOf(id)});
        loadBills();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (db != null) {
            db.close();
        }
    }
}