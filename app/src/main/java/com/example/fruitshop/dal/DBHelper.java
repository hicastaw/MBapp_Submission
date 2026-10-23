package com.example.fruitshop.dal;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

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
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + T_ORDER);
        db.execSQL("DROP TABLE IF EXISTS Customer");
        db.execSQL("DROP TABLE IF EXISTS Fruit");
        db.execSQL("DROP TABLE IF EXISTS Account");
        onCreate(db);
    }
}
