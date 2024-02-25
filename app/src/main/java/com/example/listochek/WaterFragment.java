package com.example.listochek;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.listochek.utils.FirebaseUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class WaterFragment extends Fragment {

    String userId;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public WaterFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_water, container, false);
        WaterViewModel viewModel = new ViewModelProvider(requireActivity()).get(WaterViewModel.class);

        viewModel.getWaterGoal().observe(getViewLifecycleOwner(), waterGoalLiters -> {
            TextView goalView = view.findViewById(R.id.goalView);
            goalView.setText("Ваша цель: " + waterGoalLiters + " литра в день ");
        });

        return view;

    }
}