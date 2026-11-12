package com.example.mealmate;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class GroceryAdapter extends RecyclerView.Adapter<GroceryAdapter.GroceryViewHolder> {
    private Context context;
    private List<GroceryItem> groceryList;
    private DBHelper dbHelper;

    public GroceryAdapter(Context context, List<GroceryItem> groceryList) {
        this.context = context;
        this.groceryList = groceryList;
        this.dbHelper = new DBHelper(context);
    }

    @NonNull
    @Override
    public GroceryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_grocery, parent, false);
        return new GroceryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GroceryViewHolder holder, int position) {
        GroceryItem item = groceryList.get(position);
        holder.txtGroceryName.setText(item.getGroceryName());
        holder.txtCategory.setText("Category: " + item.getGroceryCategory());

        Bitmap bitmap = BitmapFactory.decodeByteArray(item.getGroceryImage(), 0, item.getGroceryImage().length);
        holder.imgGrocery.setImageBitmap(bitmap);


            // Set checked or unchecked icon based on the current state
            holder.imgUnchecked.setImageResource(item.isChecked() ? R.drawable.checked : R.drawable.unchecked);

            // Toggle check state when clicked
            holder.imgUnchecked.setOnClickListener(v -> {
                boolean newCheckedState = !item.isChecked(); // Toggle checked state
                item.setChecked(newCheckedState);
                dbHelper.updateGroceryCheckedState(item.getGroceryId(), newCheckedState); // Update database

                // Update UI
                holder.imgUnchecked.setImageResource(newCheckedState ? R.drawable.checked : R.drawable.unchecked);
            });

        // Set click listener to view meal details
        holder.itemView.setOnClickListener(v -> {
            // Get the meal ID associated with the grocery item
            int mealId = item.getMealId();

            // Query the database or use any other method to get the meal details using the mealId
            // For example, assuming you have a method in DBHelper to fetch meal details
            Meal meal = dbHelper.getMealDetailsById(mealId);

            if (meal != null) {
                // Pass the meal details to MealDetailsActivity
                Intent intent = new Intent(context, MealDetails.class);
                intent.putExtra("MEAL_ID", meal.getId());
                intent.putExtra("MEAL_NAME", meal.getMealName());
                intent.putExtra("CATEGORY", meal.getCategory());
                intent.putExtra("INGREDIENTS", meal.getIngredients());
                intent.putExtra("INSTRUCTIONS", meal.getInstructions());
                intent.putExtra("TIME", meal.getCookingTime());
                intent.putExtra("IMAGE", meal.getImage());
                context.startActivity(intent);
            }
        });


        }



    @Override
    public int getItemCount() {
        return groceryList.size();
    }

    public static class GroceryViewHolder extends RecyclerView.ViewHolder {
        TextView txtGroceryName, txtCategory;
        ImageView imgGrocery, imgUnchecked;


        public GroceryViewHolder(@NonNull View itemView) {
            super(itemView);
            txtGroceryName = itemView.findViewById(R.id.txtTitle);
            txtCategory = itemView.findViewById(R.id.txtCategory);
            imgGrocery = itemView.findViewById(R.id.imageMeal);
            imgUnchecked = itemView.findViewById(R.id.unchecked);
        }
    }
}

