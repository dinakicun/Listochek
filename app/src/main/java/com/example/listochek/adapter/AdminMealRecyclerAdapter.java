package com.example.listochek.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import com.example.listochek.R;
import com.example.listochek.model.MealModel;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import android.util.Log;

public class AdminMealRecyclerAdapter extends FirestoreRecyclerAdapter<MealModel, AdminMealRecyclerAdapter.MealViewHolder> {
    private Context context;
    private OnMealListener listener;
    private static final String TAG = "AdminMealRecyclerAdapter";

    public AdminMealRecyclerAdapter(@NonNull FirestoreRecyclerOptions<MealModel> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull MealViewHolder holder, int position, @NonNull MealModel model) {
        Log.d(TAG, "Binding view holder at position " + position);
        holder.mealNameText.setText(model.getName() != null ? model.getName() : "");
        holder.mealWeightText.setText(model.getWeight() != null ? model.getWeight() + " г" : "");
        holder.mealCaloriesText.setText(model.getCalories() != null ? model.getCalories() + " ккал" : "");

        holder.itemView.setOnClickListener(v -> {
            int adapterPosition = holder.getAdapterPosition();
            if (listener != null && adapterPosition != RecyclerView.NO_POSITION) {
                listener.onMealClick(getItem(adapterPosition));
            }
        });

        holder.deleteButton.setOnClickListener(v -> new AlertDialog.Builder(context)
                .setTitle("Удалить блюдо")
                .setMessage("Вы уверены, что хотите удалить это блюдо?")
                .setPositiveButton("Да", (dialog, which) -> FirebaseFirestore.getInstance().collection("meal")
                        .document(model.getId())
                        .delete()
                        .addOnSuccessListener(aVoid -> Log.d(TAG, "Блюдо удалено"))
                        .addOnFailureListener(e -> Log.w(TAG, "Ошибка при удалении блюда", e)))
                .setNegativeButton("Нет", null)
                .show());
    }

    @NonNull
    @Override
    public MealViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.selected_dish_layout, parent, false);
        return new MealViewHolder(view);
    }

    public void setOnMealListener(OnMealListener listener) {
        this.listener = listener;
    }

    class MealViewHolder extends RecyclerView.ViewHolder {
        TextView mealNameText;
        TextView mealWeightText;
        TextView mealCaloriesText;
        ImageButton deleteButton;

        public MealViewHolder(@NonNull View itemView) {
            super(itemView);
            mealNameText = itemView.findViewById(R.id.dish_name);
            mealWeightText = itemView.findViewById(R.id.dish_weight);
            mealCaloriesText = itemView.findViewById(R.id.dish_calories);
            deleteButton = itemView.findViewById(R.id.delete_button);
        }
    }

    public interface OnMealListener {
        void onMealClick(MealModel meal);
    }
}
