package com.example.finalproject.Models;

import androidx.annotation.NonNull;

import com.example.finalproject.Listeners.AppointmentLoadListener;
import com.example.finalproject.Listeners.BarberListener;
import com.example.finalproject.Listeners.DisabledDaysLoadListener;
import com.example.finalproject.Listeners.ReviewLoadListener;
import com.example.finalproject.Listeners.UserLoadListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class FireBaseManager {
    private DatabaseReference databaseReferenceAp; // Appointments
    private DatabaseReference databaseReferenceRe; // Reviews
    private DatabaseReference databaseReferenceDays; // Disabled Days


    private FirebaseDatabase db;

    public FireBaseManager() {
        db = FirebaseDatabase.getInstance();
        databaseReferenceAp = FirebaseDatabase.getInstance().getReference("appointments");
        databaseReferenceRe = FirebaseDatabase.getInstance().getReference("reviews");
        databaseReferenceDays = FirebaseDatabase.getInstance().getReference("disabledDays");
    }


    public void saveAppointment(Appointment appointment) { // Save appointment
        DatabaseReference ref = db.getReference("appointments").child(appointment.getAppointmentId());
        ref.setValue(appointment);
    }

    public void removeAppointment(Appointment appointment) { // Remove appointment
        DatabaseReference ref = db.getReference("appointments").child(appointment.getAppointmentId());
        ref.removeValue();
    }

    public void saveAppointmentForUser(FirebaseAuth user, String appointmentId) { // Save appointment for user
        DatabaseReference ref = db.getReference("users").child(user.getUid()).child("appointments");
        ref.push().setValue(appointmentId);

    }

    public void removeAppointmentForUser(String customerId, String appointmentId) { // Remove appointment for user
        DatabaseReference userRef = db.getReference("users").child(customerId).child("appointments");
        userRef.orderByValue().equalTo(appointmentId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) { // onDataChange is called when the data is found
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


    public void getAllAppointments(AppointmentLoadListener listener) { // Get all appointments
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

    public void saveReview(Review review) { // Save review
        DatabaseReference ref = db.getReference("reviews").child(review.getReviewId());
        ref.setValue(review);
    }

    public void getAllReviews(ReviewLoadListener listener) { // Get all reviews
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

    public void getUserByUID(String uid, final UserLoadListener listener) { // Get user by UID
        DatabaseReference userRef = db.getReference("users").child(uid);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = new User().setUserId(uid)
                        .setEmail(dataSnapshot.child("email").getValue(String.class))
                        .setName(dataSnapshot.child("name").getValue(String.class))
                        .setPhone(dataSnapshot.child("phone").getValue(String.class))
                        .setBarber(dataSnapshot.child("barber").getValue(String.class));


                if (user != null) {
                    listener.onUserLoaded(user); // Notify the listener with the fetched user details
                } else {
                    listener.onUserLoadFailed("User not found"); // Handle user not found case
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                listener.onUserLoadFailed(databaseError.getMessage()); // Notify listener on cancellation
            }
        });
    }

    public void getFirstFutureAppointment(String userId, boolean isBarber, final AppointmentLoadListener listener) {
        this.getAllAppointments(new AppointmentLoadListener() {
            @Override
            public void onAppointmentLoaded(ArrayList<Appointment> appointments) {
                ArrayList<Appointment> myAppointments = new ArrayList<>();
                if (!isBarber) { // If user is not a barber, get only the user's appointments
                    for (Appointment appointment : appointments) {
                        if (appointment.getIdCustomer().equals(userId)) {
                            myAppointments.add(appointment);
                        }
                    }
                } else {
                    myAppointments.addAll(appointments);
                }
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()); // Date format for comparison
                Date currentDate = new Date();
                Appointment firstFutureAppointment = null; // Find the first future appointment
                for (Appointment appointment : myAppointments) {
                    try {
                        Date appointmentDateTime = sdf.parse(appointment.getDate() + " " + appointment.getTime()); // Parse the date and time
                        if (appointmentDateTime != null && appointmentDateTime.after(currentDate)) { // Check if the appointment is in the future
                            if (firstFutureAppointment == null || sdf.parse(firstFutureAppointment.getDate() + " " + firstFutureAppointment.getTime()).after(appointmentDateTime)) {
                                // If the appointment is the first future appointment or the appointment is earlier than the current first future appointment
                                firstFutureAppointment = appointment;
                            }
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                if (firstFutureAppointment != null) {
                    ArrayList<Appointment> futureAppointments = new ArrayList<>();
                    futureAppointments.add(firstFutureAppointment);
                    listener.onAppointmentLoaded(futureAppointments);
                } else {
                    listener.onAppointmentLoaded(new ArrayList<>());
                }
            }
        });
    }

    public void checkIsBarber(String uid, final BarberListener listener) { // Check if user is barber
        DatabaseReference userRef = db.getReference("users").child(uid);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String isBarber = dataSnapshot.child("barber").getValue(String.class);
                if (isBarber != null) {
                    listener.isBarberLoaded(isBarber.equals("true"));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void saveDisableDate(BlockDay blockDay) { // Save disabled date
        DatabaseReference ref = db.getReference("disabledDays").child(blockDay.getBlockDayId());
        ref.setValue(blockDay);
    }

    public void removeDisableDate(BlockDay blockDay) { // Remove disabled date
        DatabaseReference ref = db.getReference("disabledDays").child(blockDay.getBlockDayId());
        ref.removeValue();
    }

    public void getAllDisableDate(DisabledDaysLoadListener listener) { // Get all disabled dates
        ArrayList<BlockDay> disabledDays = new ArrayList<>();
        databaseReferenceDays.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    BlockDay blockDay = ds.getValue(BlockDay.class);
                    disabledDays.add(blockDay);
                }
                listener.onDaysLoad(disabledDays);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error case
            }
        });
    }


}


