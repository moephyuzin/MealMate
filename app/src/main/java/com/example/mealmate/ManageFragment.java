package com.example.mealmate;

import static com.example.mealmate.DBHelper.COLUMN_FAVOURITE;

import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class ManageFragment extends Fragment {

    public ManageFragment() {
        // Required empty public constructor
    }

    private RecyclerView recyclerView;
    private MealAdapter mealAdapter;
    private List<Meal> mealList;
    private DBHelper dbHelper;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manage, container, false);

        recyclerView = view.findViewById(R.id.recyclerMeals);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        dbHelper = new DBHelper(getContext());
        loadMeals();
        attachSwipeGestures(); // Enable swipe gestures

        return view;
    }

    private void loadMeals() {
        mealList = new ArrayList<>();
        Cursor cursor = dbHelper.getAllMeals();

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String name = cursor.getString(1);
                String category = cursor.getString(2);
                String ingredients = cursor.getString(3);
                String instructions = cursor.getString(4);
                String time = cursor.getString(5);
                byte[] image = cursor.getBlob(6);

                int favIndex = cursor.getColumnIndex(COLUMN_FAVOURITE);
                boolean isFavorite = (favIndex != -1) ? cursor.getInt(favIndex) == 1 : false;

                mealList.add(new Meal(id, name, category, ingredients, instructions, time, image, isFavorite));
            } while (cursor.moveToNext());
        }

        if (cursor != null) {
            cursor.close();
        }

        mealAdapter = new MealAdapter(getContext(), mealList, false);
        recyclerView.setAdapter(mealAdapter);

    }


    private void attachSwipeGestures() {
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                Meal meal = mealList.get(position);

                if (direction == ItemTouchHelper.LEFT) {
                    // Open EditMealActivity with meal details
                    Intent intent = new Intent(getContext(), EditActivity.class);
                    intent.putExtra("MEAL_ID", meal.getId());
                    intent.putExtra("MEAL_NAME", meal.getMealName());
                    intent.putExtra("CATEGORY", meal.getCategory());
                    intent.putExtra("INGREDIENTS", meal.getIngredients());
                    intent.putExtra("INSTRUCTIONS", meal.getInstructions());
                    intent.putExtra("TIME", meal.getCookingTime());
                    intent.putExtra("IMAGE", meal.getImage()); // Pass image
                    startActivity(intent);
                    mealAdapter.notifyItemChanged(position);
                } else if (direction == ItemTouchHelper.RIGHT) {
                    // Delete meal from database
                    dbHelper.deleteMeal(meal.getId());
                    mealList.remove(position);
                    mealAdapter.notifyItemRemoved(position);

                    Snackbar.make(recyclerView, "Meal deleted", Snackbar.LENGTH_LONG)
                            .setAction("Undo", v -> {
                                dbHelper.addMeal(meal.getMealName(), meal.getCategory(), meal.getIngredients(), meal.getInstructions(), meal.getCookingTime(), meal.getImage(), meal.isFavorite());
                                mealList.add(position, meal);
                                mealAdapter.notifyItemInserted(position);
                            }).show();
                }
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };

        new ItemTouchHelper(simpleCallback).attachToRecyclerView(recyclerView);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadMeals(); // Refresh meals and favorite status when returning to fragment
    }


    public void refreshData() {
        loadMeals();
        if (mealAdapter != null) {
            mealAdapter.notifyDataSetChanged();
        }
    }


}
