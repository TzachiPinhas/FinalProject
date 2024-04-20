package com.example.finalproject.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalproject.Adapters.ReviewAdapter;
import com.example.finalproject.Listeners.ReviewLoadListener;
import com.example.finalproject.Models.FireBaseManager;
import com.example.finalproject.Models.Review;
import com.example.finalproject.databinding.FragmentWriteReviewBinding;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.UUID;

public class WriteReviewFragment extends Fragment {

    private FragmentWriteReviewBinding binding;

    private RecyclerView reviewsRecyclerView;
    private EditText nameEditText;
    private RatingBar ratingBar;
    private EditText reviewEditText;
    private MaterialTextView overallRatingTextView;
    private MaterialButton submitReviewButton;
    private FireBaseManager fbm;
    private ArrayList<Review> allReviews;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentWriteReviewBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        allReviews = new ArrayList<>();
        fbm = new FireBaseManager();
        findViews();
        getReviews();
        initViews();

        return root;
    }

    private void initViews() {
        submitReviewButton.setOnClickListener(new View.OnClickListener() { // if the submit button is clicked then the review is saved to the database
            @Override
            public void onClick(View v) {
                String userName = nameEditText.getText().toString();
                String reviewText = reviewEditText.getText().toString();
                float rating = ratingBar.getRating();

                if (userName.isEmpty() || reviewText.isEmpty() || rating == 0) { // Validate the input
                    Toast.makeText(getContext(), "Please fill all fields and provide a rating.", Toast.LENGTH_LONG).show();
                    return; // Stop the submission if validation fails
                }

                String DateKey = String.format("%d/%d/%d", Calendar.getInstance().get(Calendar.DAY_OF_MONTH), Calendar.getInstance().get(Calendar.MONTH) + 1, Calendar.getInstance().get(Calendar.YEAR));
                Review review = new Review()
                        .setReviewId(UUID.randomUUID().toString())
                        .setName(userName)
                        .setOverview(reviewText)
                        .setRating(rating)
                        .setDate(DateKey);
                fbm.saveReview(review); // Save the review to the database

                getReviews();
                nameEditText.setText("");
                reviewEditText.setText("");
                ratingBar.setRating(0);
            }
        });
    }


    private void getReviews() {  // Fetch all reviews from the database
        fbm.getAllReviews(new ReviewLoadListener() {
            @Override
            public void onReviewLoaded(ArrayList<Review> reviews) {
                if (reviews != null) {
                    processReviews(reviews);
                }
                else
                    processReviews(new ArrayList<>());
            }
        });
    }

    private void processReviews(ArrayList<Review> reviews) {
        allReviews.clear();  // Clear existing data
        allReviews.addAll(reviews);  // Add all fetched reviews
        updateAdapter();
        calculateAverageRating();
    }

    private void calculateAverageRating() {
        float totalRating = 0;
        for (Review review : allReviews) {
            totalRating += review.getRating();
        }
        float averageRating = totalRating / allReviews.size();
        overallRatingTextView.setText("Overall Rating: " + String.format("%.1f", averageRating));
    }




    private void updateAdapter() { // Update the adapter with the fetched reviews
        ReviewAdapter adapter = new ReviewAdapter(getContext(), allReviews);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        reviewsRecyclerView.setLayoutManager(linearLayoutManager);
        reviewsRecyclerView.setAdapter(adapter);

    }

    private void findViews() {
        nameEditText = binding.nameEditText;
        ratingBar = binding.ratingBar;
        reviewEditText = binding.reviewEditText;
        submitReviewButton = binding.submitReviewButton;
        overallRatingTextView = binding.overallRatingTextView;
        reviewsRecyclerView = binding.reviewsRecyclerView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}