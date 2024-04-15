package com.example.finalproject.Models;

public class Review {
    private String reviewId = "";
    private String name = "";
    private String overview = "";
    private String date = "";
    private float rating = 0.0f;

    public Review() {
    }

    public String getName() {
        return name;
    }

    public Review setName(String name) {
        this.name = name;
        return this;
    }

    public String getReviewId() {
        return reviewId;
    }

    public Review setReviewId(String reviewId) {
        this.reviewId = reviewId;
        return this;
    }

    public String getOverview() {
        return overview;
    }

    public Review setOverview(String overview) {
        this.overview = overview;
        return this;
    }

    public String getDate() {
        return date;
    }

    public Review setDate(String date) {
        this.date = date;
        return this;
    }

    public float getRating() {
        return rating;
    }

    public Review setRating(float rating) {
        this.rating = rating;
        return this;
    }
}
