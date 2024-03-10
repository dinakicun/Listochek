package com.example.listochek.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
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
    Context context;
    public MealRecyclerAdapter(@NonNull FirestoreRecyclerOptions<MealModel> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull MealViewHolder holder, int position, @NonNull MealModel model) {
        if (model.getName() != null) {
            holder.mealNameText.setText(model.getName());
        } else {
            holder.mealNameText.setText(""); // или какое-то значение по умолчанию
        }

        if (model.getWeight() != null) {
            holder.mealWeightText.setText(String.valueOf(model.getWeight()) + " г");
        } else {
            holder.mealWeightText.setText(""); // или какое-то значение по умолчанию
        }

        if (model.getCalories() != null) {
            holder.mealCaloriesText.setText(String.valueOf(model.getCalories()) + " ккал");
        } else {
            holder.mealCaloriesText.setText(""); // или какое-то значение по умолчанию
        }
    }


    @Override
    @NonNull
    public MealViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d("MealRecyclerAdapter", "onCreateViewHolder called");
        View view = null;
        try {
            view = LayoutInflater.from(context).inflate(R.layout.meal_recycler_row, parent, false);
            Log.d("MealRecyclerAdapter", "View inflated successfully");
        } catch (Exception e) {
            Log.e("MealRecyclerAdapter", "Error inflating view", e);
        }
        return new MealViewHolder(view);
    }


    class MealViewHolder extends RecyclerView.ViewHolder{

        TextView mealNameText;
        TextView mealWeightText;
        TextView mealCaloriesText;
        ImageView addBtn;
        public MealViewHolder(@NonNull View itemView) {
            super(itemView);
            mealNameText = itemView.findViewById(R.id.nameOfDish);
            mealWeightText = itemView.findViewById(R.id.weightOfDish);
            mealCaloriesText = itemView.findViewById(R.id.caloriesOfDish);
//            addBtn = itemView.findViewById(R.id.add_btn_view_image);
        }
    }
}
