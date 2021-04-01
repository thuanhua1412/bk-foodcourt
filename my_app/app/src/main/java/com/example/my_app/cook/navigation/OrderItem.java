package com.example.my_app.cook.navigation;

public class OrderItem {
    private String imageUrl;
    private String order;
    private String customer;
    private String vendor;

    public OrderItem() {}

    public OrderItem(String url, String Order, String Customer, String Vendor) {
        imageUrl = url;
        order = Order;
        customer = Customer;
        vendor = Vendor;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getOrder() {
        return order;
    }

    public String getCustomer() {
        return customer;
    }

    public String getVendor() {
        return vendor;
    }
}
