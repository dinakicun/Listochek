package com.example.listochek.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.listochek.MealDetailsActivity;
import com.example.listochek.R;
import com.example.listochek.model.MealModel;
import java.util.List;

public class MealConsumptionRecyclerAdapter extends RecyclerView.Adapter<MealConsumptionRecyclerAdapter.MealViewHolder> {
    private List<MealModel> meals;
    private Context context;

    public MealConsumptionRecyclerAdapter(List<MealModel> meals, Context context) {
        this.meals = meals;
        this.context = context;
    }

    @NonNull
    @Override
    public MealViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.selected_dish_layout, parent, false);
        return new MealViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MealViewHolder holder, int position) {
        MealModel meal = meals.get(position);
        holder.mealNameText.setText(meal.getName());
        holder.mealWeightText.setText(meal.getWeight() + " г");
        holder.mealCaloriesText.setText(meal.getCalories() + " ккал");

        holder.deleteButton.setOnClickListener(v -> {
            if (context instanceof MealDetailsActivity) {
                ((MealDetailsActivity) context).removeMeal(meal);
            }
        });
    }

    @Override
    public int getItemCount() {
        return meals.size();
    }

    public static class MealViewHolder extends RecyclerView.ViewHolder {
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
}
