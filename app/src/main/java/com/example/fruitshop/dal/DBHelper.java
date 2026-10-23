package com.example.fruitshop.dal;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.fruitshop.models.Account;
import com.example.fruitshop.models.Customer;

public class DBHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "fruitshop.db";
    private static final int DB_VERSION = 1;

    // "Order" is an SQL keyword, so the table name must be quoted
    private static final String T_ORDER = "\"Order\"";

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE Account(id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "username TEXT UNIQUE NOT NULL, password TEXT NOT NULL, role TEXT NOT NULL)");
        db.execSQL("CREATE TABLE Fruit(id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "name TEXT NOT NULL, category TEXT, price REAL, quantity INTEGER, "
                + "expiryDate TEXT, description TEXT)");
        db.execSQL("CREATE TABLE Customer(id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "fullname TEXT, phone TEXT, email TEXT, accountId INTEGER)");
        db.execSQL("CREATE TABLE " + T_ORDER + "(id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "customerId INTEGER, orderDate TEXT, totalAmount REAL, status TEXT)");

        // Sample data
        db.execSQL("INSERT INTO Account(username, password, role) VALUES "
                + "('admin', '123', 'ADMIN'), ('customer', '123', 'CUSTOMER'), ('customer2', '123', 'CUSTOMER')");
        db.execSQL("INSERT INTO Customer(fullname, phone, email, accountId) VALUES "
                + "('Nguyen Van A', '0901234567', 'a@gmail.com', 2), "
                + "('Tran Thi B', '0912345678', 'b@gmail.com', 3)");
        db.execSQL("INSERT INTO Fruit(name, category, price, quantity, expiryDate, description) VALUES "
                + "('Tao My', 'Nhap khau', 80000, 50, '2027-06-30', 'Tao do gion ngot'), "
                + "('Chuoi Tieu', 'Trong nuoc', 25000, 100, '2027-01-15', 'Chuoi chin vang'), "
                + "('Cam Sanh', 'Trong nuoc', 40000, 30, '2025-12-01', 'Cam nhieu nuoc'), "
                + "('Nho Xanh', 'Nhap khau', 150000, 20, '2027-03-10', 'Nho khong hat'), "
                + "('Xoai Cat', 'Trong nuoc', 60000, 0, '2027-02-20', 'Xoai cat Hoa Loc')");
        db.execSQL("INSERT INTO " + T_ORDER + "(customerId, orderDate, totalAmount, status) VALUES "
                + "(1, '2026-10-20 09:30', 160000, 'Completed'), (1, '2026-10-22 14:00', 50000, 'Pending')");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + T_ORDER);
        db.execSQL("DROP TABLE IF EXISTS Customer");
        db.execSQL("DROP TABLE IF EXISTS Fruit");
        db.execSQL("DROP TABLE IF EXISTS Account");
        onCreate(db);
    }

    // ===== Account =====
    public Account login(String username, String password) {
        Cursor c = getReadableDatabase().rawQuery(
                "SELECT id, username, password, role FROM Account WHERE username = ? AND password = ?",
                new String[]{username, password});
        Account account = null;
        if (c.moveToFirst()) {
            account = new Account(c.getInt(0), c.getString(1), c.getString(2), c.getString(3));
        }
        c.close();
        return account;
    }

    // ===== Customer =====
    public Customer getCustomerByAccountId(int accountId) {
        Cursor c = getReadableDatabase().rawQuery(
                "SELECT id, fullname, phone, email, accountId FROM Customer WHERE accountId = ?",
                new String[]{String.valueOf(accountId)});
        Customer customer = null;
        if (c.moveToFirst()) {
            customer = new Customer(c.getInt(0), c.getString(1), c.getString(2), c.getString(3), c.getInt(4));
        }
        c.close();
        return customer;
    }
}
