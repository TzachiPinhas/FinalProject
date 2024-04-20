package com.example.finalproject.Listeners;

import com.example.finalproject.Models.User;

public interface UserLoadListener { // This interface is used to load user details
    void onUserLoaded(User customerDetails);
    void onUserLoadFailed(String message);
}
