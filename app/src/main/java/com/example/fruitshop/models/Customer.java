package com.example.fruitshop.models;

public class Customer {
    private int id;
    private String fullname;
    private String phone;
    private String email;
    private int accountId;

    public Customer(int id, String fullname, String phone, String email, int accountId) {
        this.id = id;
        this.fullname = fullname;
        this.phone = phone;
        this.email = email;
        this.accountId = accountId;
    }

    public int getId() { return id; }
    public String getFullname() { return fullname; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }
    public int getAccountId() { return accountId; }
}
