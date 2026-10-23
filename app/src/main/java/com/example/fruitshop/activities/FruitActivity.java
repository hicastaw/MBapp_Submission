package com.example.fruitshop.activities;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fruitshop.R;
import com.example.fruitshop.dal.DBHelper;
import com.example.fruitshop.models.Account;
import com.example.fruitshop.models.Fruit;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FruitActivity extends AppCompatActivity {
    private DBHelper db;
    private boolean isAdmin;
    private int customerId;   // set when role = CUSTOMER

    private EditText edtName, edtCategory, edtPrice, edtQuantity, edtExpiry, edtDescription, edtSearch;
    private ListView lvFruits;
    private List<Fruit> fruits = new ArrayList<>();
    private Fruit selected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fruit);
        db = new DBHelper(this);

        SharedPreferences prefs = getSharedPreferences(LoginActivity.PREFS, MODE_PRIVATE);
        int accountId = prefs.getInt(LoginActivity.KEY_ACCOUNT_ID, -1);
        isAdmin = Account.ROLE_ADMIN.equals(prefs.getString(LoginActivity.KEY_ROLE, ""));

        edtName = findViewById(R.id.edtName);
        edtCategory = findViewById(R.id.edtCategory);
        edtPrice = findViewById(R.id.edtPrice);
        edtQuantity = findViewById(R.id.edtQuantity);
        edtExpiry = findViewById(R.id.edtExpiry);
        edtDescription = findViewById(R.id.edtDescription);
        edtSearch = findViewById(R.id.edtSearch);
        lvFruits = findViewById(R.id.lvFruits);

        loadFruits();
    }

    private void loadFruits() {
        List<Fruit> all = db.searchFruits("");
        fruits = new ArrayList<>();
        for (Fruit f : all) {
            if (isAdmin || f.getQuantity() > 0) fruits.add(f);
        }
        lvFruits.setAdapter(new FruitAdapter());
    }

    /** Two-line rows; expired fruits are shown in red. */
    private class FruitAdapter extends ArrayAdapter<Fruit> {
        FruitAdapter() {
            super(FruitActivity.this, android.R.layout.simple_list_item_2, android.R.id.text1, fruits);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row = super.getView(position, convertView, parent);
            Fruit f = getItem(position);
            TextView t1 = row.findViewById(android.R.id.text1);
            TextView t2 = row.findViewById(android.R.id.text2);
            t1.setText(f.getName() + " - " + formatMoney(f.getPrice()));
            t2.setText(f.getCategory() + " | SL: " + f.getQuantity() + " | HSD: " + f.getExpiryDate()
                    + (f.isExpired() ? " (HẾT HẠN)" : "")
                    + (f.getDescription() == null || f.getDescription().isEmpty() ? "" : "\n" + f.getDescription()));
            t2.setTextColor(f.isExpired() ? Color.RED : Color.GRAY);
            return row;
        }
    }

    static String formatMoney(double amount) {
        return String.format(Locale.US, "%,.0f đ", amount);
    }

    private void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
