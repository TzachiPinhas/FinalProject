package com.example.finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;

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
import com.firebase.ui.auth.IdpResponse;
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
    private EditText dobEditText;

    private DatabaseReference customerBookRef;
    private ValueEventListener customerBookListener;

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
        }
        else {
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
                            // ...
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
        dobEditText = findViewById(R.id.dobEditText);
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
                    onSignInResult(result);
                }
            }
    );

    private void onSignInResult(FirebaseAuthUIAuthenticationResult result) {
        IdpResponse response = result.getIdpResponse();
        if (result.getResultCode() == RESULT_OK) {
            // Successfully signed in
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            checkUserExistence(user);
            // ...
        } else {
            // Sign in failed. If response is null the user canceled the
            // sign-in flow using the back button. Otherwise check
            // response.getError().getErrorCode() and handle the error.
            // ...
        }
    }

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

        // Set user details
        Customer customer = new Customer()
                .setCustomerId(user.getUid())
                .setName(nameEditText.getText().toString())
                .setPhone(phoneEditText.getText().toString())
                .setEmail(emailEditText.getText().toString())
                .setAppointments(null);


        // Save user details to the database
        customerBookRef.child(user.getUid()).setValue(customer)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.putExtra("user",user);
                            startActivity(intent);
                            finish();
                        } else {
                            // Failed to save user details, show error message or retry
                        }
                    }
                });
    }



}