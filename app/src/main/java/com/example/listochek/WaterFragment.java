package com.example.listochek;

import android.content.Context;
import android.content.SharedPreferences;
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
import com.example.listochek.utils.UserPointsViewModel;
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
    private static final String PREFS_NAME = "WaterPrefs";
    private static final String LAST_UPDATE_KEY = "lastWaterPointsUpdate";
    private UserPointsViewModel userPointsViewModel;

    public WaterFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_water, container, false);
        CombinedWaterViewModel combinedWaterViewModel = new ViewModelProvider(requireActivity()).get(CombinedWaterViewModel.class);
        userPointsViewModel = new ViewModelProvider(requireActivity()).get(UserPointsViewModel.class);

        TextView finishedNumberOfWaterText = view.findViewById(R.id.finishedNumberOfWaterText);
        TextView leftNumberOfWaterText = view.findViewById(R.id.leftNumberOfWaterText);
        TextView goalView = view.findViewById(R.id.goalView);

        userId = FirebaseUtil.currentUserId();
        combinedWaterViewModel.loadData(userId);
        userPointsViewModel.loadUserPoints(userId);

        combinedWaterViewModel.getWaterGoal().observe(getViewLifecycleOwner(), waterGoalLiters -> {
            if (waterGoalLiters != null) {
                goalView.setText(String.format(Locale.getDefault(), "Ваша цель: %.2f литра в день", waterGoalLiters));
            }
        });

        combinedWaterViewModel.getCurrentVolume().observe(getViewLifecycleOwner(), volume -> {
            if (volume != null) {
                finishedNumberOfWaterText.setText(String.format(Locale.getDefault(), "%.2f л", volume));
            }
        });

        combinedWaterViewModel.getRemainingWaterGoal().observe(getViewLifecycleOwner(), remaining -> {
            if (remaining != null) {
                leftNumberOfWaterText.setText(String.format(Locale.getDefault(), "%.2f л", remaining));
            }
        });

        configureWaterButtons(view, combinedWaterViewModel);

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
        CombinedWaterViewModel combinedWaterViewModel = new ViewModelProvider(requireActivity()).get(CombinedWaterViewModel.class);

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
                    Log.d("WaterFragment", "Успешно обновлено!");
                    combinedWaterViewModel.updateWaterIntakeData(userId);
                    checkAndUpdateWaterPoints(combinedWaterViewModel);
                })
                .addOnFailureListener(e -> Log.w("WaterFragment", "Ошибка обновления.", e));
    }

    private void checkAndUpdateWaterPoints(CombinedWaterViewModel viewModel) {
        viewModel.getRemainingWaterGoal().observe(getViewLifecycleOwner(), remaining -> {
            if (remaining != null && remaining <= 0) {
                updateWaterPointsIfNotAlreadyUpdated();
            }
        });
    }

    private void updateWaterPointsIfNotAlreadyUpdated() {
        String todayDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        SharedPreferences prefs = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String lastUpdateDate = prefs.getString(LAST_UPDATE_KEY, "");

        if (!todayDate.equals(lastUpdateDate) || lastUpdateDate == null ) {
            DocumentReference userDocRef = db.collection("users").document(userId);
            db.runTransaction((Transaction.Function<Void>) transaction -> {
                        DocumentSnapshot snapshot = transaction.get(userDocRef);
                        Long currentWaterPoints = snapshot.getLong("waterPoints");
                        if (currentWaterPoints != null) {
                            transaction.update(userDocRef, "waterPoints", currentWaterPoints + 1);
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putString(LAST_UPDATE_KEY, todayDate);
                            editor.apply();
                        }
                        return null;
                    }).addOnSuccessListener(aVoid -> {
                        Log.d("WaterFragment", "Очки за воду успешно обновлены");
                        userPointsViewModel.loadUserPoints(userId);
                    })
                    .addOnFailureListener(e -> Log.w("WaterFragment", "Не удалось обновить очки за воду", e));
        }
    }
}
