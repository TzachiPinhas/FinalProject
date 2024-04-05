package com.example.finalproject.ui;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.finalproject.R;
import com.example.finalproject.databinding.FragmentEditingDetailsBinding;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wdullaer.materialdatetimepicker.date.DatePickerController;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

public class EditingDetailsFragment extends Fragment {
    private FragmentEditingDetailsBinding binding;

    private EditText nameEditText;
    private EditText emailEditText;
    private EditText phoneEditText;
    private EditText dobEditText;
    private MaterialButton login_BTN_save;
    private DatabaseReference customerBookRef;
    private ValueEventListener customerBookListener;
    private FirebaseAuth auth;

    private TimePickerDialog  timePickerDialog;
    private DatePickerController datePickerController;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentEditingDetailsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        findViews();

        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        login_BTN_save.setOnClickListener(v -> updateUser(user));
        return root;
    }

    private void updateUser(FirebaseUser user) {
        customerBookRef= FirebaseDatabase.getInstance().getReference("customerBook").child(user.getUid());
        if(!nameEditText.getText().toString().isEmpty()){
            customerBookRef.child("name").setValue(nameEditText.getText().toString());
        }
        if(!emailEditText.getText().toString().isEmpty()){
            customerBookRef.child("email").setValue(emailEditText.getText().toString());
        }
        if(!phoneEditText.getText().toString().isEmpty()){
            customerBookRef.child("phone").setValue(phoneEditText.getText().toString());
        }


    }

    private void findViews() {
        nameEditText = binding.nameEditText;
        emailEditText = binding.emailEditText;
        phoneEditText = binding.phoneEditText;
        dobEditText = binding.dobEditText;
        login_BTN_save=binding.loginBTNSave;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
