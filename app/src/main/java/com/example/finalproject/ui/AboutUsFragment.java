package com.example.finalproject.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.finalproject.Utilities.AboutUsManager;
import com.example.finalproject.databinding.FragmentAboutUsBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textview.MaterialTextView;

public class AboutUsFragment extends Fragment {
    private FragmentAboutUsBinding binding;

    private  MaterialTextView textViewIntroduction;
    private MaterialTextView dayHours00;
    private MaterialTextView dayHours01;
    private MaterialTextView dayHours10;
    private MaterialTextView dayHours11;
    private FloatingActionButton buttonOpenMaps;
    private FloatingActionButton buttonPhone;

    private MaterialTextView textViewAddress;



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentAboutUsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        findViews();
        initViews();

        return root;
    }

    public void navigateToMaps(View view) {
        Uri gmmIntentUri = Uri.parse("google.navigation:q=20+Trumpeldor+St,+Petah+Tikva");
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);
    }
    private void initViews() {
        textViewIntroduction.setText(AboutUsManager.getDescription());
        String hours[][] = AboutUsManager.getInfoArray();
        dayHours00.setText(hours[0][0]);
        dayHours01.setText(hours[0][1]);
        dayHours10.setText(hours[1][0]);
        dayHours11.setText(hours[1][1]);
        textViewAddress.setText(AboutUsManager.getAdress());
        buttonOpenMaps.setOnClickListener(this::navigateToMaps);
        buttonPhone.setOnClickListener(this::callToPhone);
    }

    private void callToPhone(View view) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:+972544409035"));
        startActivity(intent);
    }



    private void findViews() {
         textViewIntroduction = binding.textViewIntroduction;
         dayHours00 =binding.dayHours00;
         dayHours01= binding.dayHours01;
         dayHours10= binding.dayHours10;
         dayHours11= binding.dayHours11;
         buttonOpenMaps = binding.buttonOpenMaps;
         buttonPhone = binding.buttonPhone;
         textViewAddress = binding.textViewAddress;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
