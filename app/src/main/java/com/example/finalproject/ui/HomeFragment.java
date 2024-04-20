package com.example.finalproject.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalproject.Adapters.ReviewAdapter;
import com.example.finalproject.Listeners.AppointmentLoadListener;
import com.example.finalproject.Listeners.BarberListener;
import com.example.finalproject.Listeners.ReviewLoadListener;
import com.example.finalproject.Listeners.UserLoadListener;
import com.example.finalproject.Models.Appointment;
import com.example.finalproject.Models.FireBaseManager;
import com.example.finalproject.Models.Review;
import com.example.finalproject.Models.User;
import com.example.finalproject.R;
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
    private MaterialButton appointment_btn_manage;
    private MaterialButton view_btn_appointments;
    private ArrayList<Review> allReviews;
    private FragmentHomeBinding binding;
    private FirebaseAuth auth;
    private FireBaseManager fbm;
    private boolean isBarber;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        fbm = new FireBaseManager();
        auth = FirebaseAuth.getInstance();
        allReviews = new ArrayList<>();
        findViews();
        checkIfBarber();
        initName();
        initReviews(); // Load reviews
        setupButtonListeners(); // Set up button listeners

        return root;
    }


    private void checkIfBarber() { // Check if user is a barber
        fbm.checkIsBarber(auth.getCurrentUser().getUid(), new BarberListener() {
            @Override
            public void isBarberLoaded(boolean isBarberLoaded) {
                isBarber = isBarberLoaded;
                initNextAppointment();
                updateButton();
            }
        });
    }

    private void initNextAppointment() {  // get the next appointment
        fbm.getFirstFutureAppointment(auth.getCurrentUser().getUid(), isBarber, new AppointmentLoadListener() {
            @Override
            public void onAppointmentLoaded(ArrayList<Appointment> appointments) { // Display the next appointment if available
                if (appointments.size() > 0) {
                    Appointment appointment = appointments.get(0);
                    textViewNextAppointment.setText("Your next appointment is on " + appointment.getDate() + " at " + appointment.getTime() + ".");
                } else {
                    textViewNextAppointment.setText("You have no upcoming appointments.");
                }
            }
        });
    }

    private void updateButton() { // Update button visibility based on user type
        if (isBarber) {
            make_btn_appointment.setVisibility(View.GONE);
            view_btn_appointments.setVisibility(View.VISIBLE);
            appointment_btn_manage.setVisibility(View.VISIBLE);
        } else {
            make_btn_appointment.setVisibility(View.VISIBLE);
            view_btn_appointments.setVisibility(View.VISIBLE);
            appointment_btn_manage.setVisibility(View.GONE);
        }
    }

    private void setupButtonListeners() { // display the appropriate fragment when a button is clicked, show only the relevant buttons based on user type

        make_btn_appointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.action_nav_home_to_nested_appointment);
            }
        });

        view_btn_appointments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.action_nav_home_to_nested_myAppointments);
            }
        });

        appointment_btn_manage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.action_nav_home_to_nested_management);
            }
        });
    }


    private void initReviews() { // Load reviews
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

    private void updateReviewsUI() { // make sure to display only 3 reviews, the first 3 reviews

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


    private void initName() { // get the user name from the database
        fbm.getUserByUID(auth.getCurrentUser().getUid(), new UserLoadListener() {
            @Override
            public void onUserLoaded(User user) {
                textViewGreeting.setText("Hello, " + user.getName() + "!");

            }

            @Override
            public void onUserLoadFailed(String errorMessage) {
                // Handle error, update UI
                System.err.println("Failed to load user: " + errorMessage);
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
        appointment_btn_manage = binding.appointmentBtnManage;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}