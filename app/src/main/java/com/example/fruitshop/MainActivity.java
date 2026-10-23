package com.example.fruitshop;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.example.fruitshop.activities.FruitActivity;
import com.example.fruitshop.activities.LoginActivity;
import com.example.fruitshop.activities.OrderActivity;
import com.example.fruitshop.dal.DBHelper;
import com.example.fruitshop.models.Account;
import com.example.fruitshop.models.Customer;

public class MainActivity extends AppCompatActivity {
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefs = getSharedPreferences(LoginActivity.PREFS, MODE_PRIVATE);
        int accountId = prefs.getInt(LoginActivity.KEY_ACCOUNT_ID, -1);
        String role = prefs.getString(LoginActivity.KEY_ROLE, "");
        if (accountId == -1) {
            logout();
            return;
        }

        TextView tvWelcome = findViewById(R.id.tvWelcome);
        TextView tvInfo = findViewById(R.id.tvInfo);
        Button btnFruits = findViewById(R.id.btnFruits);
        Button btnOrders = findViewById(R.id.btnOrders);
        Button btnLogout = findViewById(R.id.btnLogout);

        if (Account.ROLE_ADMIN.equals(role)) {
            tvWelcome.setText("Xin chào Admin");
            tvInfo.setText("Tài khoản: " + prefs.getString(LoginActivity.KEY_USERNAME, ""));
            btnFruits.setText("Quản lý trái cây");
            btnOrders.setText("Quản lý đơn hàng");
        } else {
            Customer c = new DBHelper(this).getCustomerByAccountId(accountId);
            tvWelcome.setText("Xin chào " + c.getFullname());
            tvInfo.setText("SĐT: " + c.getPhone() + "\nEmail: " + c.getEmail());
            btnFruits.setText("Xem & đặt trái cây");
            btnOrders.setText("Lịch sử đơn hàng");
        }

        btnFruits.setOnClickListener(v -> startActivity(new Intent(this, FruitActivity.class)));
        btnOrders.setOnClickListener(v -> startActivity(new Intent(this, OrderActivity.class)));
        btnLogout.setOnClickListener(v -> logout());
    }

    private void logout() {
        // Keep saved username/password for "remember me", only end the session
        prefs.edit()
                .remove(LoginActivity.KEY_ACCOUNT_ID)
                .remove(LoginActivity.KEY_ROLE)
                .apply();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}
