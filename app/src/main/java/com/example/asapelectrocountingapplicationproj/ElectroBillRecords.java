package com.example.asapelectrocountingapplicationproj;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class ElectroBillRecords extends AppCompatActivity {

    private EditText etDate, etAmount;
    private Button btnAdd, btnBack;
    private ListView lvBills;
    private ArrayList<String> billsList;
    private ArrayAdapter<String> adapter;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_electro_bill_records);

        etDate = findViewById(R.id.etDate);
        etAmount = findViewById(R.id.etAmount);
        btnAdd = findViewById(R.id.btnAdd);
        btnBack = findViewById(R.id.btnBack);
        lvBills = findViewById(R.id.lvBills);

        billsList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, billsList);
        lvBills.setAdapter(adapter);

        db = openOrCreateDatabase("ElectricityBills", MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS bills(id INTEGER PRIMARY KEY AUTOINCREMENT, date TEXT, amount REAL)");

        loadBills();

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addBill();
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        lvBills.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                showEditDeleteDialog(position);
                return true;
            }
        });
    }

    private void loadBills() {
        billsList.clear();
        Cursor cursor = db.rawQuery("SELECT * FROM bills ORDER BY date DESC", null);
        if (cursor.moveToFirst()) {
            do {
                String date = cursor.getString(1);
                double amount = cursor.getDouble(2);
                billsList.add(date + " - $" + String.format("%.2f", amount));
            } while (cursor.moveToNext());
        }
        cursor.close();
        adapter.notifyDataSetChanged();
    }

    private void addBill() {
        String date = etDate.getText().toString().trim();
        String amountStr = etAmount.getText().toString().trim();

        if (date.isEmpty() || amountStr.isEmpty()) {
            Toast.makeText(this, "請填寫所有欄位", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount = Double.parseDouble(amountStr);

        ContentValues values = new ContentValues();
        values.put("date", date);
        values.put("amount", amount);

        db.insert("bills", null, values);

        etDate.setText("");
        etAmount.setText("");

        loadBills();
        Toast.makeText(this, "帳單已新增", Toast.LENGTH_SHORT).show();
    }

    private void showEditDeleteDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("編輯/刪除帳單");
        builder.setItems(new String[]{"編輯", "刪除"}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    editBill(position);
                } else {
                    deleteBill(position);
                }
            }
        });
        builder.show();
    }

    private void editBill(final int position) {
        String[] billInfo = billsList.get(position).split(" - \\$");
        final String oldDate = billInfo[0];
        final double oldAmount = Double.parseDouble(billInfo[1]);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_edit_bill, null);
        final EditText etEditDate = view.findViewById(R.id.etEditDate);
        final EditText etEditAmount = view.findViewById(R.id.etEditAmount);

        etEditDate.setText(oldDate);
        etEditAmount.setText(String.format("%.2f", oldAmount));

        builder.setView(view);
        builder.setTitle("編輯帳單");
        builder.setPositiveButton("保存", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newDate = etEditDate.getText().toString().trim();
                double newAmount = Double.parseDouble(etEditAmount.getText().toString().trim());

                ContentValues values = new ContentValues();
                values.put("date", newDate);
                values.put("amount", newAmount);

                db.update("bills", values, "date = ? AND amount = ?", new String[]{oldDate, String.valueOf(oldAmount)});

                loadBills();
                Toast.makeText(ElectroBillRecords.this, "帳單已更新", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("取消", null);
        builder.show();
    }

    private void deleteBill(int position) {
        String[] billInfo = billsList.get(position).split(" - \\$");
        String date = billInfo[0];
        double amount = Double.parseDouble(billInfo[1]);

        db.delete("bills", "date = ? AND amount = ?", new String[]{date, String.valueOf(amount)});

        loadBills();
        Toast.makeText(this, "帳單已刪除", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (db != null) {
            db.close();
        }
    }
}