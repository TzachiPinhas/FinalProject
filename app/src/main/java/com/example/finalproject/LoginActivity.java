package com.example.finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.finalproject.Models.Customer;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.List;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private MaterialButton login_BTN_signOut;
    private MaterialButton login_BTN_save;
    private EditText nameEditText;
    private EditText emailEditText;
    private EditText phoneEditText;
    private DatabaseReference customerBookRef;
    private ValueEventListener customerBookListener;

    private String name;
    private String email;
    private String phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        findViews();
        initViews();

        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        if (user == null) {
            login();
        } else {
            prepopulateUserDetails(user);
            checkUserExistence(user);
        }
        login_BTN_save.setOnClickListener(v -> {
            // Check if the user is signed in before saving details
            FirebaseUser currentUser = auth.getCurrentUser();
            if (currentUser != null) {
                // Save user details
                saveUserDetails(currentUser);
            }
        });
    }

    private void initViews() {
        login_BTN_signOut.setOnClickListener(v -> {
            AuthUI.getInstance()
                    .signOut(this)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Intent intent = new Intent(LoginActivity.this, LoginActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(LoginActivity.this, "Failed to sign out", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        });
    }

    private void findViews() {
        login_BTN_signOut = findViewById(R.id.login_BTN_signOut);
        login_BTN_save = findViewById(R.id.login_BTN_save);
        nameEditText = findViewById(R.id.nameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
    }

    private void login() {
        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.PhoneBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build()
        );

// Create and launch sign-in intent
        Intent signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setLogo(R.drawable.barber_shop)
                .build();


        signInLauncher.launch(signInIntent);
    }

    // See: https://developer.android.com/training/basics/intents/result
    private final ActivityResultLauncher<Intent> signInLauncher = registerForActivityResult(
            new FirebaseAuthUIActivityResultContract(),
            new ActivityResultCallback<FirebaseAuthUIAuthenticationResult>() {
                @Override
                public void onActivityResult(FirebaseAuthUIAuthenticationResult result) {
                    if (result.getResultCode() == RESULT_OK && FirebaseAuth.getInstance().getCurrentUser() != null) {
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        prepopulateUserDetails(user);
                        checkUserExistence(user);
                    } else if (result.getIdpResponse() != null && result.getIdpResponse().getError() != null) {
                        Toast.makeText(LoginActivity.this, "Authentication failed: " + result.getIdpResponse().getError().getErrorCode(), Toast.LENGTH_SHORT).show();
                    }
                }
            });



    private void checkUserExistence(FirebaseUser user) {
        // Get a reference to the customer book in the database
        customerBookRef = FirebaseDatabase.getInstance().getReference("customerBook");

        // Listen for changes in the customer book
        customerBookListener = customerBookRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(user.getUid()).exists()) {
                    // User exists in the database, transfer to main page
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    // User does not exist in the database, do nothing
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error
            }
        });
    }


    private void saveUserDetails(FirebaseUser user) {
        String name = nameEditText.isEnabled() ? nameEditText.getText().toString().trim() : nameEditText.getText().toString();
        String email = emailEditText.isEnabled() ? emailEditText.getText().toString().trim() : emailEditText.getText().toString();
        String phone = phoneEditText.isEnabled() ? phoneEditText.getText().toString().trim() : phoneEditText.getText().toString();

        if (!isValidInput()) {
            Toast.makeText(this, "Please enter valid details", Toast.LENGTH_SHORT).show();
            return;
        }

        Customer customer = new Customer()
                .setCustomerId(user.getUid())
                .setName(name)
                .setPhone(phone)
                .setEmail(email)
                .setAppointments(null);

        DatabaseReference customerBookRef = FirebaseDatabase.getInstance().getReference("customerBook");

        customerBookRef.child(user.getUid()).setValue(customer)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.putExtra("user", user);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, "Failed to save user details", Toast.LENGTH_SHORT).show();
                    }
                });
    }



    private boolean isValidInput() {
        if (nameEditText.isEnabled() && nameEditText.getText().toString().trim().isEmpty()) {
            nameEditText.setError("Please enter your name");
            return false;
        }
        if (emailEditText.isEnabled() && (emailEditText.getText().toString().trim().isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(emailEditText.getText().toString().trim()).matches())) {
            emailEditText.setError("Invalid email format");
            return false;
        }
        if (phoneEditText.isEnabled() && (phoneEditText.getText().toString().trim().isEmpty() || !android.util.Patterns.PHONE.matcher(phoneEditText.getText().toString().trim()).matches())) {
            phoneEditText.setError("Invalid phone number");
            return false;
        }
        return true;
    }


    private void prepopulateUserDetails(FirebaseUser user) {
        if (user.getDisplayName() != null && !user.getDisplayName().isEmpty()) {
            nameEditText.setText(user.getDisplayName());
            nameEditText.setEnabled(false);
        } else {
            nameEditText.setEnabled(true);
        }

        if (user.getEmail() != null && !user.getEmail().isEmpty()) {
            emailEditText.setText(user.getEmail());
            emailEditText.setEnabled(false);
        } else {
            emailEditText.setEnabled(true);
        }

        if (user.getPhoneNumber() != null && !user.getPhoneNumber().isEmpty()) {
            phoneEditText.setText(user.getPhoneNumber());
            phoneEditText.setEnabled(false);
        } else {
            phoneEditText.setEnabled(true);
        }
    }



}