package com.example.listochek.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.listochek.R;
import com.example.listochek.model.MealModel;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class MealRecyclerAdapter extends FirestoreRecyclerAdapter<MealModel, MealRecyclerAdapter.MealViewHolder> {
    private Context context;
    private OnMealListener listener;

    public MealRecyclerAdapter(@NonNull FirestoreRecyclerOptions<MealModel> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull MealViewHolder holder, int position, @NonNull MealModel model) {
        holder.mealNameText.setText(model.getName());
        holder.mealWeightText.setText(model.getWeight() + " г");
        holder.mealCaloriesText.setText(model.getCalories() + " ккал");

        holder.itemView.setOnClickListener(v -> {
            int adapterPosition = holder.getAdapterPosition();
            if (listener != null && adapterPosition != RecyclerView.NO_POSITION) {
                listener.onMealClick(getItem(adapterPosition));
            }
        });
    }

    @NonNull
    @Override
    public MealViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.meal_recycler_row, parent, false);
        return new MealViewHolder(view);
    }

    public void setOnMealListener(OnMealListener listener) {
        this.listener = listener;
    }

    class MealViewHolder extends RecyclerView.ViewHolder {
        TextView mealNameText;
        TextView mealWeightText;
        TextView mealCaloriesText;

        public MealViewHolder(@NonNull View itemView) {
            super(itemView);
            mealNameText = itemView.findViewById(R.id.nameOfDish);
            mealWeightText = itemView.findViewById(R.id.weightOfDish);
            mealCaloriesText = itemView.findViewById(R.id.caloriesOfDish);
        }
    }

    public interface OnMealListener {
        void onMealClick(MealModel meal);
    }
}
