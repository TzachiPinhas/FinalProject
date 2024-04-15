package com.example.finalproject.Interfaces;

import com.example.finalproject.Models.Customer;

public interface UserLoadListener {
    void onUserLoaded(Customer customerDetails);
    void onUserLoadFailed(String message);
}
