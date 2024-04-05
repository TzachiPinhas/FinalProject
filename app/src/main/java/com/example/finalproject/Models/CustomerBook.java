package com.example.finalproject.Models;

import java.util.HashMap;

public class CustomerBook {

    private HashMap<String,Customer> allCustomer = new HashMap<>();

    public CustomerBook() {
    }

    public HashMap<String, Customer> getAllCustomer() {
        return allCustomer;
    }

    public CustomerBook setAllCustomer(HashMap<String, Customer> allCustomer) {
        this.allCustomer = allCustomer;
        return this;
    }
}
