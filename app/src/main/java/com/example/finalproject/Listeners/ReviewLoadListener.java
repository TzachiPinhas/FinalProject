package com.example.finalproject.Listeners;

import com.example.finalproject.Models.Review;

import java.util.ArrayList;

public interface ReviewLoadListener { // This interface is used to load reviews
    void onReviewLoaded(ArrayList<Review> reviews);
}
