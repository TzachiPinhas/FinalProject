package com.example.finalproject.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalproject.Adapters.ReviewAdapter;
import com.example.finalproject.Interfaces.AppointmentLoadListener;
import com.example.finalproject.Interfaces.ReviewLoadListener;
import com.example.finalproject.Interfaces.UserLoadListener;
import com.example.finalproject.Models.Appointment;
import com.example.finalproject.Models.Customer;
import com.example.finalproject.Models.FireBaseManager;
import com.example.finalproject.Models.Review;
import com.example.finalproject.databinding.FragmentHomeBinding;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class HomeFragment extends Fragment {


    private MaterialTextView textViewGreeting;
    private MaterialTextView textViewNextAppointment;
    private MaterialTextView textViewRating;
    private RecyclerView recyclerViewReviews;
    private MaterialButton make_btn_appointment;
    private MaterialButton view_btn_appointments;
    private ArrayList<Review> allReviews;
    private FragmentHomeBinding binding;
    private FirebaseAuth auth;
    private FireBaseManager fbm;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        fbm = new FireBaseManager();
        auth = FirebaseAuth.getInstance();
        allReviews = new ArrayList<>();
        findViews();
        initName();
        initNextAppointment();
        initReviews();
        setupButtonListeners();


        return root;
    }

    private void setupButtonListeners() {

    }


    private void initReviews() {
        fbm.getAllReviews(new ReviewLoadListener() {
            @Override
            public void onReviewLoaded(ArrayList<Review> reviews) {
                if (reviews != null) {
                    processReviews(reviews);
                }
            }
        });
    }

    private void processReviews(ArrayList<Review> reviews) {
        allReviews.addAll(reviews);  // Add all fetched reviews
        updateReviewsUI();
        calculateAverageRating();
    }

    private void updateReviewsUI() {

        if (allReviews.size() > 0) {
            ArrayList<Review> reviewsToDisplay = new ArrayList<>(allReviews.subList(0, Math.min(3, allReviews.size())));
            ReviewAdapter adapter = new ReviewAdapter(getContext(), reviewsToDisplay);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
            linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerViewReviews.setLayoutManager(linearLayoutManager);
            recyclerViewReviews.setAdapter(adapter);
        }
    }

    private void calculateAverageRating() {
        float totalRating = 0;
        for (Review review : allReviews) {
            totalRating += review.getRating();
        }
        float averageRating = totalRating / allReviews.size();
        if (averageRating > 0)
            textViewRating.setText("Overall Rating: " + String.format("%.1f", averageRating) + "/5.0");
        else
            textViewRating.setText("No reviews yet.");
    }


    private void initNextAppointment() {
        fbm.getFirstFutureAppointment(auth.getCurrentUser().getUid(), new AppointmentLoadListener() {
            @Override
            public void onAppointmentLoaded(ArrayList<Appointment> appointments) {
                if (appointments.size() > 0) {
                    Appointment appointment = appointments.get(0);
                    textViewNextAppointment.setText("Your next appointment is on " + appointment.getDate() + " at " + appointment.getTime() + ".");
                } else {
                    textViewNextAppointment.setText("You have no upcoming appointments.");
                }
            }
        });
    }

    private void initName() {
        fbm.getCustomerByUID(auth.getCurrentUser().getUid(), new UserLoadListener() {
            @Override
            public void onUserLoaded(Customer customer) {
                textViewGreeting.setText("Hello, " + customer.getName() + "!");

            }
            @Override
            public void onUserLoadFailed(String errorMessage) {
                // Handle error, update UI
                System.err.println("Failed to load customer: " + errorMessage);
            }
        });

    }

    private void findViews() {
        textViewGreeting = binding.textViewGreeting;
        textViewNextAppointment = binding.textViewNextAppointment;
        textViewRating = binding.textViewRating;
        recyclerViewReviews = binding.recyclerViewReviews;
        view_btn_appointments = binding.viewBtnAppointments;
        make_btn_appointment = binding.makeBtnAppointment;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}