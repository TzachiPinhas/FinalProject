package com.example.finalproject.Models;

import androidx.annotation.NonNull;

import com.example.finalproject.Interfaces.AppointmentLoadListener;
import com.example.finalproject.Interfaces.ReviewLoadListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FireBaseManager {
    private DatabaseReference databaseReferenceAp;

    private DatabaseReference databaseReferenceRe;


    private FirebaseDatabase db;

    public FireBaseManager() {
        db = FirebaseDatabase.getInstance();
        databaseReferenceAp = FirebaseDatabase.getInstance().getReference("appointments");
        databaseReferenceRe= FirebaseDatabase.getInstance().getReference("reviews");
    }

    public void saveAppointmentForUser(FirebaseAuth user, String appointmentId) {
        DatabaseReference ref = db.getReference("customerBook").child(user.getUid()).child("appointments");
        ref.push().setValue(appointmentId);

    }

    public void saveAppointment(Appointment appointment) {
        DatabaseReference ref = db.getReference("appointments").child(appointment.getAppointmentId());
        ref.setValue(appointment);
    }

    public void removeAppointment(Appointment appointment) {
        DatabaseReference ref = db.getReference("appointments").child(appointment.getAppointmentId());
        ref.removeValue();
    }

    public void removeAppointmentForUser(String customerId, String appointmentId) {
        DatabaseReference customerRef = db.getReference("customerBook").child(customerId).child("appointments");
        customerRef.orderByValue().equalTo(appointmentId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    childSnapshot.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle potential error
            }
        });
    }


    public void getAllAppointments(AppointmentLoadListener listener) {
        ArrayList<Appointment> appointments = new ArrayList<>();
        databaseReferenceAp.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Appointment appointment = ds.getValue(Appointment.class);
                    appointments.add(appointment);
                }
                listener.onAppointmentLoaded(appointments); // Call the callback with the loaded appointments
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error case
            }
        });
    }

    public void saveReview(Review review) {
        DatabaseReference ref = db.getReference("reviews").child(review.getReviewId());
        ref.setValue(review);
    }

    public void getAllReviews(ReviewLoadListener listener) {
        ArrayList<Review> reviews = new ArrayList<>();
        databaseReferenceRe.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Review review = ds.getValue(Review.class);
                    reviews.add(review);
                }
                listener.onReviewLoaded(reviews); // Call the callback with the loaded reviews
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error case
            }
        });
    }
}
