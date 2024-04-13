package com.example.finalproject.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalproject.Models.Review;
import com.example.finalproject.R;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder>{

    private Context context;

    private ArrayList<Review> reviews;


    public ReviewAdapter(Context context, ArrayList<Review> reviews) {
        this.context = context;
        this.reviews = reviews;
    }



    @NonNull
    @Override
    public ReviewAdapter.ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.horisontal_review_item, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewAdapter.ReviewViewHolder holder, int position) {
        Review review = getItem(position);
        holder.review_name.setText(review.getName());
        holder.review_overview.setText(review.getOverview());
        holder.review_date.setText(review.getDate());
        holder.review_rating.setRating(review.getRating());
    }

    @Override
    public int getItemCount() {
        if (reviews == null)
            return 0;
        return reviews.size();
    }

public Review getItem(int position) {
        return reviews.get(position);
    }

    public class ReviewViewHolder extends RecyclerView.ViewHolder {

        private MaterialTextView review_name;
        private MaterialTextView review_overview;
        private MaterialTextView review_date;
        private RatingBar review_rating;

        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            review_name = itemView.findViewById(R.id.reviewerNameTextView);
            review_overview = itemView.findViewById(R.id.reviewTextView);
            review_date = itemView.findViewById(R.id.dateTextView);
            review_rating = itemView.findViewById(R.id.reviewRatingBar);
        }
    }
}
