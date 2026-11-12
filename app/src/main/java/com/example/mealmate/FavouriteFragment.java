package com.example.mealmate;

import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;


public class FavouriteFragment extends Fragment {
    private RecyclerView recyclerView;
    private MealAdapter mealAdapter;
    private List<Meal> favoriteMeals;
    private DBHelper dbHelper;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favourite, container, false);

        recyclerView = view.findViewById(R.id.recyclerFavorite);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        dbHelper = new DBHelper(getContext());
        loadFavoriteMeals();

        return view;
    }

    private void loadFavoriteMeals() {
        favoriteMeals = new ArrayList<>();
        Cursor cursor = dbHelper.getFavoriteMeals();

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String name = cursor.getString(1);
                String category = cursor.getString(2);
                String ingredients = cursor.getString(3);
                String instructions = cursor.getString(4);
                String time = cursor.getString(5);
                byte[] image = cursor.getBlob(6);
                boolean isFavorite = cursor.getInt(7) == 1; // Fetch is_favorite column

                favoriteMeals.add(new Meal(id, name, category, ingredients, instructions, time, image, isFavorite));
            } while (cursor.moveToNext());
        }

        cursor.close();
        mealAdapter = new MealAdapter(getContext(), favoriteMeals, true);
        recyclerView.setAdapter(mealAdapter);

    }

    public void refreshFavoriteMeals() {
        loadFavoriteMeals();
        mealAdapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadFavoriteMeals(); // Refresh the list when returning
    }


}