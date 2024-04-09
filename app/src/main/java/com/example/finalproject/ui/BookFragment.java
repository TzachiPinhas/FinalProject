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

import com.example.finalproject.Adapters.ServiceAdapter;
import com.example.finalproject.Interfaces.ServiceCallback;
import com.example.finalproject.Models.Service;
import com.example.finalproject.R;
import com.example.finalproject.Utilities.ServiceManager;
import com.example.finalproject.databinding.FragmentBookBinding;
public class BookFragment extends Fragment {
    private RecyclerView main_LST_service;
    private FragmentBookBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentBookBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        findViews();
        initViews(root);

        return root;
    }

    private void initViews(View view) {
        ServiceAdapter serviceAdapter = new ServiceAdapter(view.getContext(), ServiceManager.getServices());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        main_LST_service.setLayoutManager(linearLayoutManager);
        main_LST_service.setAdapter(serviceAdapter);

        // Handle item click events
        serviceAdapter.setServiceCallBack(new ServiceCallback() {
            @Override
            public void ServiceClicked(Service service) {
                Bundle bundle = new Bundle();
                bundle.putString("service", service.getName());
                bundle.putString("price", service.getPrice());
                Navigation.findNavController(view).navigate(R.id.action_nav_book_to_dateFragment, bundle);
            }
        });
    }


    private void findViews() {
        main_LST_service=binding.mainLSTService;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}