package com.example.listochek;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.listochek.model.WaterIntakeModel;
import com.example.listochek.utils.CombinedWaterViewModel;
import com.example.listochek.utils.FirebaseUtil;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Transaction;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class WaterFragment extends Fragment {

    String userId;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public WaterFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_water, container, false);
        CombinedWaterViewModel viewModel = new ViewModelProvider(requireActivity()).get(CombinedWaterViewModel.class);

        TextView finishedNumberOfWaterText = view.findViewById(R.id.finishedNumberOfWaterText);
        TextView leftNumberOfWaterText = view.findViewById(R.id.leftNumberOfWaterText);
        TextView goalView = view.findViewById(R.id.goalView);

        userId = FirebaseUtil.currentUserId();
        viewModel.loadData(userId);

        viewModel.getWaterGoal().observe(getViewLifecycleOwner(), waterGoalLiters -> {
            if (waterGoalLiters != null) {
                goalView.setText(String.format(Locale.getDefault(), "Ваша цель: %.2f литра в день", waterGoalLiters));
            }
        });

        viewModel.getCurrentVolume().observe(getViewLifecycleOwner(), volume -> {
            if (volume != null) {
                finishedNumberOfWaterText.setText(String.format(Locale.getDefault(), "%.2f л", volume));
            }
        });

        viewModel.getRemainingWaterGoal().observe(getViewLifecycleOwner(), remaining -> {
            if (remaining != null) {
                leftNumberOfWaterText.setText(String.format(Locale.getDefault(), "%.2f л", remaining));
            }
        });
        configureWaterButtons(view, viewModel);

        return view;
    }

    private void configureWaterButtons(View view, CombinedWaterViewModel viewModel) {
        ImageButton[] buttons = new ImageButton[]{
                view.findViewById(R.id.addWaterBtn1),
                view.findViewById(R.id.addWaterBtn2),
                view.findViewById(R.id.addWaterBtn3),
                view.findViewById(R.id.addWaterBtn4),
                view.findViewById(R.id.addWaterBtn5),
                view.findViewById(R.id.addWaterBtn6),
                view.findViewById(R.id.addWaterBtn7),
                view.findViewById(R.id.addWaterBtn8),
                view.findViewById(R.id.addWaterBtn9),
                view.findViewById(R.id.addWaterBtn10)
        };
        viewModel.getGlassesOfWater().observe(getViewLifecycleOwner(), glasses -> {
            for (int i = 0; i < buttons.length; i++) {
                if (i < glasses) {
                    buttons[i].setImageResource(R.drawable.full_glass);
                    buttons[i].setTag("full");
                } else {
                    buttons[i].setImageResource(R.drawable.empty_glass);
                    buttons[i].setTag("empty");
                }
            }
        });

        for (ImageButton button : buttons) {
            button.setOnClickListener(v -> {
                ImageButton imgButton = (ImageButton) v;
                if ("empty".equals(imgButton.getTag().toString())) {
                    imgButton.setImageResource(R.drawable.full_glass);
                    imgButton.setTag("full");
                    updateWaterIntake(0.25);

                } else {
                    imgButton.setImageResource(R.drawable.empty_glass);
                    imgButton.setTag("empty");
                    updateWaterIntake(-0.25);

                }
            });
        }

    }

    private void updateWaterIntake(double volumeChange) {
        CombinedWaterViewModel viewModel = new ViewModelProvider(requireActivity()).get(CombinedWaterViewModel.class);

        String dateString = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        DocumentReference dateDocRef = db.collection("waterIntake")
                .document(userId)
                .collection("Intakes")
                .document(dateString);

        db.runTransaction((Transaction.Function<Void>) transaction -> {
                    DocumentSnapshot snapshot = transaction.get(dateDocRef);
                    double newVolume = volumeChange;
                    if (snapshot.exists()) {
                        Double currentVolume = snapshot.getDouble("volume");
                        if (currentVolume != null) {
                            newVolume += currentVolume;
                        }
                    }
                    transaction.set(dateDocRef, new WaterIntakeModel(new Date(), newVolume));
                    return null;
                }).addOnSuccessListener(aVoid -> {
                    Log.d("WaterFragment", "Получилось!");
                    viewModel.updateWaterIntakeData(userId);
                })
                .addOnFailureListener(e -> Log.w("WaterFragment", "Не получилось.", e));
    }

}