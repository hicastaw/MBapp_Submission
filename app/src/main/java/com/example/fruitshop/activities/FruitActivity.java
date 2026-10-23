package com.example.fruitshop.activities;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.fruitshop.R;
import com.example.fruitshop.dal.DBHelper;
import com.example.fruitshop.models.Account;
import com.example.fruitshop.models.Fruit;

import java.util.ArrayList;
import java.util.Calendar;
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
        TextView tvHint = findViewById(R.id.tvHint);

        if (isAdmin) {
            setTitle("Quản lý trái cây");
            tvHint.setText("Chạm vào một dòng để sửa / xoá");
            edtExpiry.setOnClickListener(v -> pickDate());
            findViewById(R.id.btnAdd).setOnClickListener(v -> addFruit());
            findViewById(R.id.btnUpdate).setOnClickListener(v -> updateFruit());
            findViewById(R.id.btnDelete).setOnClickListener(v -> deleteFruit());
            findViewById(R.id.btnClear).setOnClickListener(v -> clearForm());
            lvFruits.setOnItemClickListener((p, view, pos, id) -> selectFruit(fruits.get(pos)));
        } else {
            setTitle("Trái cây");
            tvHint.setText("Chạm vào một trái cây để đặt hàng");
            findViewById(R.id.layoutForm).setVisibility(View.GONE);
            customerId = db.getCustomerByAccountId(accountId).getId();
            lvFruits.setOnItemClickListener((p, view, pos, id) -> showOrderDialog(fruits.get(pos)));
        }

        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int st, int c, int a) { }
            @Override public void onTextChanged(CharSequence s, int st, int b, int c) { loadFruits(); }
            @Override public void afterTextChanged(Editable s) { }
        });

        loadFruits();
    }

    private void loadFruits() {
        List<Fruit> all = db.searchFruits(edtSearch.getText().toString().trim());
        fruits = new ArrayList<>();
        for (Fruit f : all) {
            // Customers only see fruits that are in stock
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

    // ===== ADMIN =====
    private void pickDate() {
        Calendar cal = Calendar.getInstance();
        new DatePickerDialog(this, (view, y, m, d) ->
                edtExpiry.setText(String.format(Locale.US, "%04d-%02d-%02d", y, m + 1, d)),
                cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void selectFruit(Fruit f) {
        selected = f;
        edtName.setText(f.getName());
        edtCategory.setText(f.getCategory());
        edtPrice.setText(String.valueOf((long) f.getPrice()));
        edtQuantity.setText(String.valueOf(f.getQuantity()));
        edtExpiry.setText(f.getExpiryDate());
        edtDescription.setText(f.getDescription());
    }

    /** Reads the form; returns null and shows a message when input is invalid. */
    private Fruit readForm(int id) {
        String name = edtName.getText().toString().trim();
        String category = edtCategory.getText().toString().trim();
        String price = edtPrice.getText().toString().trim();
        String quantity = edtQuantity.getText().toString().trim();
        String expiry = edtExpiry.getText().toString().trim();
        String description = edtDescription.getText().toString().trim();
        if (name.isEmpty() || category.isEmpty() || price.isEmpty() || quantity.isEmpty() || expiry.isEmpty()) {
            toast("Vui lòng nhập đủ tên, loại, giá, số lượng, hạn sử dụng");
            return null;
        }
        try {
            double p = Double.parseDouble(price);
            int q = Integer.parseInt(quantity);
            if (p < 0 || q < 0) {
                toast("Giá và số lượng không được âm");
                return null;
            }
            return new Fruit(id, name, category, p, q, expiry, description);
        } catch (NumberFormatException e) {
            toast("Giá hoặc số lượng không hợp lệ");
            return null;
        }
    }

    private void addFruit() {
        Fruit f = readForm(0);
        if (f == null) return;
        db.insertFruit(f);
        toast("Đã thêm");
        clearForm();
    }

    private void updateFruit() {
        if (selected == null) {
            toast("Chọn một trái cây trong danh sách để sửa");
            return;
        }
        Fruit f = readForm(selected.getId());
        if (f == null) return;
        db.updateFruit(f);
        toast("Đã cập nhật");
        clearForm();
    }

    private void deleteFruit() {
        if (selected == null) {
            toast("Chọn một trái cây trong danh sách để xoá");
            return;
        }
        new AlertDialog.Builder(this)
                .setTitle("Xoá " + selected.getName() + "?")
                .setPositiveButton("Xoá", (d, w) -> {
                    db.deleteFruit(selected.getId());
                    toast("Đã xoá");
                    clearForm();
                })
                .setNegativeButton("Huỷ", null)
                .show();
    }

    private void clearForm() {
        selected = null;
        edtName.setText("");
        edtCategory.setText("");
        edtPrice.setText("");
        edtQuantity.setText("");
        edtExpiry.setText("");
        edtDescription.setText("");
        loadFruits();
    }

    // ===== CUSTOMER =====
    private void showOrderDialog(Fruit f) {
        if (f.isExpired()) {
            toast(f.getName() + " đã hết hạn (" + f.getExpiryDate() + "), không thể đặt");
            return;
        }
        EditText edtQty = new EditText(this);
        edtQty.setInputType(InputType.TYPE_CLASS_NUMBER);
        edtQty.setHint("Số lượng (còn " + f.getQuantity() + ")");

        new AlertDialog.Builder(this)
                .setTitle("Đặt " + f.getName())
                .setMessage("Giá: " + formatMoney(f.getPrice()) + "\nHạn sử dụng: " + f.getExpiryDate())
                .setView(edtQty)
                .setPositiveButton("Đặt hàng", (d, w) -> {
                    int qty;
                    try {
                        qty = Integer.parseInt(edtQty.getText().toString().trim());
                    } catch (NumberFormatException e) {
                        toast("Số lượng không hợp lệ");
                        return;
                    }
                    String error = db.placeOrder(customerId, f.getId(), qty);
                    if (error != null) {
                        toast(error);
                    } else {
                        toast("Đặt hàng thành công: " + formatMoney(f.getPrice() * qty));
                        loadFruits();
                    }
                })
                .setNegativeButton("Huỷ", null)
                .show();
    }

    static String formatMoney(double amount) {
        return String.format(Locale.US, "%,.0f đ", amount);
    }

    private void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
