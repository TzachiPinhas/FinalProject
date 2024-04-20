package com.example.finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.bumptech.glide.Glide;
import com.example.finalproject.Listeners.BarberListener;
import com.example.finalproject.Listeners.UserLoadListener;
import com.example.finalproject.Models.FireBaseManager;
import com.example.finalproject.Models.User;
import com.example.finalproject.databinding.ActivityMainBinding;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    private FirebaseAuth auth;
    private FireBaseManager fbm;
    private boolean isBarber;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
       setSupportActionBar(binding.appBarMain.toolbar);
        fbm = new FireBaseManager();
        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        checkIfBarber();


        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nested_appointment,R.id.nested_management, R.id.nav_about, R.id.nav_logout, R.id.nested_myAppointments, R.id.nav_review)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        View header = navigationView.getHeaderView(0);
        changeNavHeader(header, user);

        navigationView.getMenu().findItem(R.id.nav_logout).setOnMenuItemClickListener(menuItem -> {
            logout();
            return true;
        });
    }

    private void checkIfBarber() {
        fbm.checkIsBarber(auth.getCurrentUser().getUid(), new BarberListener() {
            @Override
            public void isBarberLoaded(boolean isBarberLoaded) {
                isBarber = isBarberLoaded;
                updateMenuItems();
            }
        });
    }

    private void updateMenuItems() {
        NavigationView navigationView = binding.navView;
        Menu menu = navigationView.getMenu();
        MenuItem bookAppointmentItem = menu.findItem(R.id.nested_appointment);
        MenuItem managementAppointmentItem = menu.findItem(R.id.nested_management);
        if (isBarber) {
            // Hide booking option for barbers
            bookAppointmentItem.setVisible(false);
            managementAppointmentItem.setVisible(true);

        } else {
            // Show booking option for clients
            bookAppointmentItem.setVisible(true);
            managementAppointmentItem.setVisible(false);
        }
    }


    private void changeNavHeader(View header, FirebaseUser user) {
        TextView textTitleLabel = header.findViewById(R.id.name_LBL);
        TextView emailTitleLabel = header.findViewById(R.id.email_LBL);
        ImageView imageTitleLabel = header.findViewById(R.id.main_image);

        fbm.getUserByUID(user.getUid(), new UserLoadListener() {
            @Override
            public void onUserLoaded(User userDetails) {
                textTitleLabel.setText(userDetails.getName());
                emailTitleLabel.setText(userDetails.getEmail());
            }
            @Override
            public void onUserLoadFailed(String message) {
                System.err.println("Failed to load user: " + message);
            }
        });
        Glide.with(header.getContext()) // Load the image from the URL
                .load(user.getPhotoUrl())
                .into(imageTitleLabel);

        // Using Glide to load the image with resizing
        Glide.with(header.getContext())
                .load(user.getPhotoUrl() != null ? user.getPhotoUrl() : R.drawable.user) //if the user has no image, load the default image
                .placeholder(R.drawable.user)
                .error(R.drawable.user)
                .override(200, 200) // Resize the image
                .into(imageTitleLabel);

    }


    private void logout() {
        AuthUI.getInstance().signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {

                        // Redirect the user to the LoginActivity
                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }
                });
    }


    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }


}