package com.example.fruitshop.dal;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.fruitshop.models.Account;
import com.example.fruitshop.models.Customer;
import com.example.fruitshop.models.Fruit;
import com.example.fruitshop.models.Order;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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

    // ===== Fruit =====
    /** keyword matches name or category; empty keyword returns all. */
    public List<Fruit> searchFruits(String keyword) {
        List<Fruit> list = new ArrayList<>();
        String like = "%" + keyword + "%";
        Cursor c = getReadableDatabase().rawQuery(
                "SELECT id, name, category, price, quantity, expiryDate, description FROM Fruit "
                        + "WHERE name LIKE ? OR category LIKE ? ORDER BY name",
                new String[]{like, like});
        while (c.moveToNext()) {
            list.add(readFruit(c));
        }
        c.close();
        return list;
    }

    public Fruit getFruitById(int id) {
        Cursor c = getReadableDatabase().rawQuery(
                "SELECT id, name, category, price, quantity, expiryDate, description FROM Fruit WHERE id = ?",
                new String[]{String.valueOf(id)});
        Fruit fruit = c.moveToFirst() ? readFruit(c) : null;
        c.close();
        return fruit;
    }

    private Fruit readFruit(Cursor c) {
        return new Fruit(c.getInt(0), c.getString(1), c.getString(2), c.getDouble(3),
                c.getInt(4), c.getString(5), c.getString(6));
    }

    public long insertFruit(Fruit f) {
        return getWritableDatabase().insert("Fruit", null, toValues(f));
    }

    public int updateFruit(Fruit f) {
        return getWritableDatabase().update("Fruit", toValues(f), "id = ?",
                new String[]{String.valueOf(f.getId())});
    }

    public int deleteFruit(int id) {
        return getWritableDatabase().delete("Fruit", "id = ?", new String[]{String.valueOf(id)});
    }

    private ContentValues toValues(Fruit f) {
        ContentValues v = new ContentValues();
        v.put("name", f.getName());
        v.put("category", f.getCategory());
        v.put("price", f.getPrice());
        v.put("quantity", f.getQuantity());
        v.put("expiryDate", f.getExpiryDate());
        v.put("description", f.getDescription());
        return v;
    }

    // ===== Order =====
    /**
     * Places an order for one fruit: checks expiry and stock, reduces stock, inserts the order.
     * Returns null on success, otherwise an error message.
     */
    public String placeOrder(int customerId, int fruitId, int quantity) {
        Fruit fruit = getFruitById(fruitId);
        if (fruit == null) return "Không tìm thấy trái cây";
        if (fruit.isExpired()) return "Trái cây đã hết hạn, không thể đặt";
        if (quantity <= 0) return "Số lượng phải lớn hơn 0";
        if (quantity > fruit.getQuantity()) return "Không đủ hàng (còn " + fruit.getQuantity() + ")";

        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues stock = new ContentValues();
            stock.put("quantity", fruit.getQuantity() - quantity);
            db.update("Fruit", stock, "id = ?", new String[]{String.valueOf(fruitId)});

            ContentValues order = new ContentValues();
            order.put("customerId", customerId);
            order.put("orderDate", new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US).format(new Date()));
            order.put("totalAmount", fruit.getPrice() * quantity);
            order.put("status", Order.STATUS_PENDING);
            db.insert(T_ORDER, null, order);

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        return null;
    }

    public List<Order> getAllOrders() {
        return queryOrders("", null);
    }

    public List<Order> getOrdersByCustomer(int customerId) {
        return queryOrders("WHERE o.customerId = ? ", new String[]{String.valueOf(customerId)});
    }

    private List<Order> queryOrders(String where, String[] args) {
        List<Order> list = new ArrayList<>();
        Cursor c = getReadableDatabase().rawQuery(
                "SELECT o.id, o.customerId, o.orderDate, o.totalAmount, o.status, c.fullname "
                        + "FROM " + T_ORDER + " o LEFT JOIN Customer c ON o.customerId = c.id "
                        + where + "ORDER BY o.id DESC", args);
        while (c.moveToNext()) {
            Order o = new Order(c.getInt(0), c.getInt(1), c.getString(2), c.getDouble(3), c.getString(4));
            o.setCustomerName(c.getString(5));
            list.add(o);
        }
        c.close();
        return list;
    }

    public int updateOrderStatus(int orderId, String status) {
        ContentValues v = new ContentValues();
        v.put("status", status);
        return getWritableDatabase().update(T_ORDER, v, "id = ?", new String[]{String.valueOf(orderId)});
    }
}
