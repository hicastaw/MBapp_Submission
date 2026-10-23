package com.example.fruitshop.models;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Fruit {
    public static final String DATE_FORMAT = "yyyy-MM-dd";

    private int id;
    private String name;
    private String category;
    private double price;
    private int quantity;
    private String expiryDate;   // yyyy-MM-dd
    private String description;

    public Fruit(int id, String name, String category, double price, int quantity,
                 String expiryDate, String description) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.price = price;
        this.quantity = quantity;
        this.expiryDate = expiryDate;
        this.description = description;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public double getPrice() { return price; }
    public int getQuantity() { return quantity; }
    public String getExpiryDate() { return expiryDate; }
    public String getDescription() { return description; }

    public static String today() {
        return new SimpleDateFormat(DATE_FORMAT, Locale.US).format(new Date());
    }

    /** yyyy-MM-dd strings compare correctly as text. */
    public boolean isExpired() {
        return expiryDate.compareTo(today()) < 0;
    }
}
