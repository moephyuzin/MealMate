package com.example.mealmate;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MealAdapter extends RecyclerView.Adapter<MealAdapter.MealViewHolder> {
    private Context context;
    private List<Meal> mealList;
    private DBHelper dbHelper;
    private boolean isFavoriteFragment;

    // Add isFavoriteFragment as a parameter to distinguish between Favorite Fragment and others
    public MealAdapter(Context context, List<Meal> mealList, boolean isFavoriteFragment) {
        this.context = context;
        this.mealList = mealList;
        this.dbHelper = new DBHelper(context);
        this.isFavoriteFragment = isFavoriteFragment;
    }

    @NonNull
    @Override
    public MealViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_meals, parent, false);
        return new MealViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MealViewHolder holder, int position) {
        Meal meal = mealList.get(position);
        holder.txtMealName.setText(meal.getMealName());
        holder.txtCategory.setText("Category: " + meal.getCategory());

        Bitmap bitmap = BitmapFactory.decodeByteArray(meal.getImage(), 0, meal.getImage().length);
        holder.imgMeal.setImageBitmap(bitmap);

        // Set the favorite icon
        holder.imgFavorite.setImageResource(meal.isFavorite() ? R.drawable.fav_filled : R.drawable.favorite);

        holder.imgFavorite.setOnClickListener(v -> {
            boolean newFavoriteStatus = !meal.isFavorite();
            meal.setFavorite(newFavoriteStatus);
            dbHelper.updateFavoriteStatus(meal.getId(), newFavoriteStatus);

            // If it's the Favorite Fragment, remove the item from the list immediately when deselected
            if (!newFavoriteStatus && isFavoriteFragment) {
                // Remove the meal from the favorite list
                int removedPosition = holder.getAdapterPosition();
                if (removedPosition != RecyclerView.NO_POSITION) {
                    mealList.remove(removedPosition);
                    notifyItemRemoved(removedPosition);
                }
            } else {
                // If it's not the Favorite Fragment, just update the item
                notifyItemChanged(position);
            }
        });

        holder.imgAdd.setOnClickListener(v -> {
            // Add the meal to the grocery list
            dbHelper.addMealToGroceryList(meal);

            // Show a "Added to Grocery List" message
            Toast.makeText(context, "Added to Grocery List", Toast.LENGTH_SHORT).show();
        });

        // Handle item click to view meal details
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, MealDetails.class);
            intent.putExtra("MEAL_ID", meal.getId());
            intent.putExtra("MEAL_NAME", meal.getMealName());
            intent.putExtra("CATEGORY", meal.getCategory());
            intent.putExtra("INGREDIENTS", meal.getIngredients());
            intent.putExtra("INSTRUCTIONS", meal.getInstructions());
            intent.putExtra("TIME", meal.getCookingTime());
            intent.putExtra("IMAGE", meal.getImage()); // Pass image
            context.startActivity(intent);
        });


    }

    @Override
    public int getItemCount() {
        return mealList.size();
    }

    public static class MealViewHolder extends RecyclerView.ViewHolder {
        TextView txtMealName, txtCategory;
        ImageView imgMeal, imgFavorite, imgAdd;

        public MealViewHolder(@NonNull View itemView) {
            super(itemView);
            txtMealName = itemView.findViewById(R.id.txtTitle);
            txtCategory = itemView.findViewById(R.id.txtCategory);
            imgMeal = itemView.findViewById(R.id.imageMeal);
            imgFavorite = itemView.findViewById(R.id.favourite);
            imgAdd = itemView.findViewById(R.id.add);
        }
    }
}
