package com.example.fruitshop.activities;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.fruitshop.MainActivity;
import com.example.fruitshop.R;
import com.example.fruitshop.dal.DBHelper;
import com.example.fruitshop.models.Account;

public class LoginActivity extends AppCompatActivity {
    public static final String PREFS = "login_prefs";
    public static final String KEY_ACCOUNT_ID = "accountId";
    public static final String KEY_USERNAME = "username";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_ROLE = "role";
    public static final String KEY_REMEMBER = "remember";

    private EditText edtUsername, edtPassword;
    private CheckBox chkRemember;
    private DBHelper db;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = getSharedPreferences(PREFS, MODE_PRIVATE);

        // Already logged in with "remember me" -> skip login screen
        if (prefs.getBoolean(KEY_REMEMBER, false) && prefs.getInt(KEY_ACCOUNT_ID, -1) != -1) {
            openMain();
            return;
        }

        setContentView(R.layout.activity_login);
        db = new DBHelper(this);
        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        chkRemember = findViewById(R.id.chkRemember);
        Button btnLogin = findViewById(R.id.btnLogin);

        edtUsername.setText(prefs.getString(KEY_USERNAME, ""));
        edtPassword.setText(prefs.getString(KEY_PASSWORD, ""));
        chkRemember.setChecked(prefs.getBoolean(KEY_REMEMBER, false));

        btnLogin.setOnClickListener(v -> login());
    }

    private void login() {
        String username = edtUsername.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();
        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập username và password", Toast.LENGTH_SHORT).show();
            return;
        }

        Account account = db.login(username, password);
        if (account == null) {
            Toast.makeText(this, "Sai username hoặc password", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean remember = chkRemember.isChecked();
        prefs.edit()
                .putInt(KEY_ACCOUNT_ID, account.getId())
                .putString(KEY_ROLE, account.getRole())
                .putString(KEY_USERNAME, account.getUsername())
                .putString(KEY_PASSWORD, remember ? account.getPassword() : "")
                .putBoolean(KEY_REMEMBER, remember)
                .apply();

        Toast.makeText(this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
        openMain();
    }

    private void openMain() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
