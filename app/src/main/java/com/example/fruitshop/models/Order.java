package com.example.fruitshop.models;

public class Order {
    public static final String STATUS_PENDING = "Pending";
    public static final String STATUS_CONFIRMED = "Confirmed";
    public static final String STATUS_COMPLETED = "Completed";
    public static final String[] STATUSES = {STATUS_PENDING, STATUS_CONFIRMED, STATUS_COMPLETED};

    private int id;
    private int customerId;
    private String orderDate;
    private double totalAmount;
    private String status;

    // Display only (joined from Customer), not a column of Order
    private String customerName;

    public Order(int id, int customerId, String orderDate, double totalAmount, String status) {
        this.id = id;
        this.customerId = customerId;
        this.orderDate = orderDate;
        this.totalAmount = totalAmount;
        this.status = status;
    }

    public int getId() { return id; }
    public int getCustomerId() { return customerId; }
    public String getOrderDate() { return orderDate; }
    public double getTotalAmount() { return totalAmount; }
    public String getStatus() { return status; }
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
}
