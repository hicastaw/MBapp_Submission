package com.example.fruitshop.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.fruitshop.R;
import com.example.fruitshop.dal.DBHelper;
import com.example.fruitshop.models.Account;
import com.example.fruitshop.models.Order;

import java.util.ArrayList;
import java.util.List;

public class OrderActivity extends AppCompatActivity {
    private DBHelper db;
    private boolean isAdmin;
    private int customerId;   // set when role = CUSTOMER

    private TextView tvSummary;
    private ListView lvOrders;
    private List<Order> orders = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        db = new DBHelper(this);

        SharedPreferences prefs = getSharedPreferences(LoginActivity.PREFS, MODE_PRIVATE);
        int accountId = prefs.getInt(LoginActivity.KEY_ACCOUNT_ID, -1);
        isAdmin = Account.ROLE_ADMIN.equals(prefs.getString(LoginActivity.KEY_ROLE, ""));

        tvSummary = findViewById(R.id.tvSummary);
        lvOrders = findViewById(R.id.lvOrders);
        TextView tvHint = findViewById(R.id.tvHint);

        if (isAdmin) {
            setTitle("Quản lý đơn hàng");
            tvHint.setText("Chạm vào một đơn để cập nhật trạng thái");
            lvOrders.setOnItemClickListener((p, view, pos, id) -> showStatusDialog(orders.get(pos)));
        } else {
            setTitle("Lịch sử đơn hàng");
            customerId = db.getCustomerByAccountId(accountId).getId();
        }

        loadOrders();
    }

    private void loadOrders() {
        orders = isAdmin ? db.getAllOrders() : db.getOrdersByCustomer(customerId);

        List<String> rows = new ArrayList<>();
        double total = 0;
        for (Order o : orders) {
            String customer = isAdmin ? " | " + o.getCustomerName() : "";
            rows.add("#" + o.getId() + customer + "\n" + o.getOrderDate()
                    + " | " + FruitActivity.formatMoney(o.getTotalAmount()) + " | " + o.getStatus());
            total += o.getTotalAmount();
        }
        lvOrders.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, rows));
        tvSummary.setText("Số đơn: " + orders.size() + " - Tổng tiền: " + FruitActivity.formatMoney(total));
    }

    private void showStatusDialog(Order o) {
        new AlertDialog.Builder(this)
                .setTitle("Đơn #" + o.getId() + " (" + o.getStatus() + ")")
                .setItems(Order.STATUSES, (d, which) -> {
                    db.updateOrderStatus(o.getId(), Order.STATUSES[which]);
                    Toast.makeText(this, "Đã cập nhật: " + Order.STATUSES[which], Toast.LENGTH_SHORT).show();
                    loadOrders();
                })
                .show();
    }
}
