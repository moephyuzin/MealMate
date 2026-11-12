package com.example.mealmate;

import android.database.Cursor;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.material.snackbar.Snackbar;
import java.util.ArrayList;
import java.util.List;

public class GroceryListFragment extends Fragment {

    private RecyclerView recyclerView;
    private GroceryAdapter groceryAdapter;
    private List<GroceryItem> groceryMeals;
    private DBHelper dbHelper;
    private GroceryItem deletedItem; // Store deleted item temporarily
    private int deletedItemPosition; // Store position of deleted item

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_grocery_list, container, false);

        recyclerView = view.findViewById(R.id.recyclerGrocery);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        dbHelper = new DBHelper(getContext());
        groceryMeals = new ArrayList<>();
        loadGroceryItems();  // Load the data into the list and adapter

        enableSwipeToDelete(view); // Pass the view to show snackbar

        return view;
    }

    // Update this method to properly assign data to groceryMeals
    private void loadGroceryItems() {
        groceryMeals.clear();  // Clear previous data to avoid duplication
        Cursor cursor = dbHelper.getGroceryMeals();

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                int mealId = cursor.getInt(1);
                String name = cursor.getString(2);
                String category = cursor.getString(3);
                byte[] image = cursor.getBlob(4);
                boolean isChecked = cursor.getInt(5) == 1;

                groceryMeals.add(new GroceryItem(id, mealId, name, category, image, isChecked));
            } while (cursor.moveToNext());
        }
        cursor.close();

        // Notify adapter with updated list
        if (groceryAdapter == null) {
            groceryAdapter = new GroceryAdapter(getContext(), groceryMeals);
            recyclerView.setAdapter(groceryAdapter);
        } else {
            groceryAdapter.notifyDataSetChanged();
        }
    }

    private void enableSwipeToDelete(final View view) {
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                                  RecyclerView.ViewHolder target) {
                return false; // No move operation needed
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                if (groceryMeals == null || groceryMeals.isEmpty()) {
                    return; // Prevent crash if list is empty
                }

                int position = viewHolder.getAdapterPosition();
                if (position >= 0 && position < groceryMeals.size()) {
                    // Save the deleted item and its position
                    deletedItem = groceryMeals.get(position);
                    deletedItemPosition = position;

                    // Remove item from database
                    dbHelper.deleteGroceryItem(deletedItem.getGroceryId());

                    // Remove item from the list
                    groceryMeals.remove(position);

                    // Notify adapter that an item has been removed
                    groceryAdapter.notifyItemRemoved(position);

                    // Show Snackbar with Undo option
                    Snackbar snackbar = Snackbar.make(view, "Item deleted", Snackbar.LENGTH_LONG);
                    snackbar.setAction("UNDO", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            undoDelete(); // Restore the deleted item if UNDO is clicked
                        }
                    });
                    snackbar.show();
                }
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    // Undo the deletion and restore the item
    private void undoDelete() {
        if (deletedItem != null && deletedItemPosition >= 0) {
            // Restore item to the database
            dbHelper.addGroceryItem(deletedItem);

            // Add the item back to the list at the original position
            groceryMeals.add(deletedItemPosition, deletedItem);

            // Notify adapter that an item has been inserted
            groceryAdapter.notifyItemInserted(deletedItemPosition);
        }
    }

    public void refreshGroceryMeals() {
        loadGroceryItems();  // Reload the grocery items
        groceryAdapter.notifyDataSetChanged();  // Notify adapter to refresh
    }

    @Override
    public void onResume() {
        super.onResume();
        loadGroceryItems();  // Refresh the list when returning
    }
}
