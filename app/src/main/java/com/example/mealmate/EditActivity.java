package com.example.mealmate;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class EditActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;

    private EditText edtMealName, edtCategory, edtIngredients, edtInstructions, edtTime;
    private ImageView imgMeal;
    private Button btnUpdateMeal, btnUploadImage;
    private DBHelper dbHelper;
    private int mealId;
    private byte[] mealImage;
    private boolean isNewImageSelected = false; // Flag to track image selection

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        getWindow().setStatusBarColor(ContextCompat.getColor(EditActivity.this, R.color.blue));

        dbHelper = new DBHelper(this);

        edtMealName = findViewById(R.id.edtMealName);
        edtCategory = findViewById(R.id.edtCategory);
        edtIngredients = findViewById(R.id.edtIngredients);
        edtInstructions = findViewById(R.id.edtInstruction);
        edtTime = findViewById(R.id.edtTime);
        imgMeal = findViewById(R.id.imageMeal);
        btnUpdateMeal = findViewById(R.id.btnUpdateMeal);
        btnUploadImage = findViewById(R.id.btnUploadImage);

        // Retrieve meal data from intent
        Intent intent = getIntent();
        mealId = intent.getIntExtra("MEAL_ID", -1);
        edtMealName.setText(intent.getStringExtra("MEAL_NAME"));
        edtCategory.setText(intent.getStringExtra("CATEGORY"));
        edtIngredients.setText(intent.getStringExtra("INGREDIENTS"));
        edtInstructions.setText(intent.getStringExtra("INSTRUCTIONS"));
        edtTime.setText(intent.getStringExtra("TIME"));
        mealImage = intent.getByteArrayExtra("IMAGE");

        if (mealImage != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(mealImage, 0, mealImage.length);
            imgMeal.setImageBitmap(bitmap);
        }

        // Image upload button click
        btnUploadImage.setOnClickListener(view -> openImagePicker());

        // Update meal button click
        btnUpdateMeal.setOnClickListener(view -> updateMeal());
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                imgMeal.setImageBitmap(bitmap);
                mealImage = convertBitmapToByteArray(bitmap); // Convert to byte array
                isNewImageSelected = true; // Mark new image as selected
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private byte[] convertBitmapToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

    private void updateMeal() {
        String mealName = edtMealName.getText().toString().trim();
        String category = edtCategory.getText().toString().trim();
        String ingredients = edtIngredients.getText().toString().trim();
        String instructions = edtInstructions.getText().toString().trim();
        String time = edtTime.getText().toString().trim();

        if (mealName.isEmpty() || category.isEmpty() || ingredients.isEmpty() ||
                instructions.isEmpty() || time.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // If no new image is selected, keep the old image
        byte[] imageToUpdate = isNewImageSelected ? mealImage : dbHelper.getMealImage(mealId);

        boolean updated = dbHelper.updateMeal(mealId, mealName, category, ingredients,
                instructions, time, imageToUpdate);
        if (updated) {
            Toast.makeText(this, "Meal updated successfully", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Update failed", Toast.LENGTH_SHORT).show();
        }
    }

}
