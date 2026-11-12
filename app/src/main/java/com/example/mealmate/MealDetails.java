package com.example.mealmate;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;

public class MealDetails extends AppCompatActivity {
    private TextView txtMealName, txtCategory, txtIngredients, txtInstructions, txtCookingTime;
    private ImageView imgMeal;
    private Button btnShare;
    private Bitmap mealBitmap;
    private String mealName, category, ingredients, instructions, cookingTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal_details);
        getWindow().setStatusBarColor(ContextCompat.getColor(MealDetails.this, R.color.blue));

        // Initialize views
        txtMealName = findViewById(R.id.meal_name);
        txtCategory = findViewById(R.id.meal_category);
        txtIngredients = findViewById(R.id.ingredients_text);
        txtInstructions = findViewById(R.id.instructions_text);
        txtCookingTime = findViewById(R.id.time_text);
        imgMeal = findViewById(R.id.meal_image);
        btnShare = findViewById(R.id.btnShare);

        // Get meal data from intent
        mealName = getIntent().getStringExtra("MEAL_NAME");
        category = getIntent().getStringExtra("CATEGORY");
        ingredients = getIntent().getStringExtra("INGREDIENTS");
        instructions = getIntent().getStringExtra("INSTRUCTIONS");
        cookingTime = getIntent().getStringExtra("TIME");
        byte[] image = getIntent().getByteArrayExtra("IMAGE");

        // Set values to views
        txtMealName.setText(mealName);
        txtCategory.setText("Category: " + category);
        txtIngredients.setText(ingredients);
        txtInstructions.setText(instructions);
        txtCookingTime.setText("Cooking Time: " + cookingTime);

        // Convert byte array image to Bitmap
        if (image != null) {
            mealBitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
            imgMeal.setImageBitmap(mealBitmap);
        }

        // Set click listener for sharing
        btnShare.setOnClickListener(v -> shareMeal());
    }

    private void shareMeal() {
        // Convert image to Uri
        Uri imageUri = getImageUri(mealBitmap);

        // Create the sharing text
        String shareText = "🍽 Meal: " + mealName +
                "\n📌 Category: " + category +
                "\n📝 Ingredients:\n" + ingredients +
                "\n📖 Instructions:\n" + instructions +
                "\n⏳ Cooking Time: " + cookingTime;

        // Share Intent
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);

        // Attach image if available
        if (imageUri != null) {
            shareIntent.setType("image/*");
            shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }

        // Start sharing
        startActivity(Intent.createChooser(shareIntent, "Share Meal Via"));
    }

    private Uri getImageUri(Bitmap bitmap) {
        try {
            File cachePath = new File(getCacheDir(), "images");
            cachePath.mkdirs();
            File file = new File(cachePath, "shared_meal.jpg");
            FileOutputStream stream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            stream.close();

            return FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", file);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
