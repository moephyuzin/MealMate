package com.example.mealmate;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import java.io.ByteArrayOutputStream;
import java.io.IOException;


 public class CreateFragment extends Fragment {
         private EditText edtMealName, edtCategory, edtIngredients, edtInstructions, edtTime;
         private ImageView mealImageView;
         private MaterialButton btnUploadImage, btnSaveMeal;
         private DBHelper dbHelper;
         private Bitmap selectedImageBitmap;

         private static final int PICK_IMAGE_REQUEST = 1;

         @Nullable
         @Override
         public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
             View view = inflater.inflate(R.layout.fragment_create, container, false);

             dbHelper = new DBHelper(getContext());

             edtMealName = view.findViewById(R.id.edtUsername);
             edtCategory = view.findViewById(R.id.edtCategory);
             edtIngredients = view.findViewById(R.id.edtIngredients);
             edtInstructions = view.findViewById(R.id.edtInstruction);
             edtTime = view.findViewById(R.id.edtTime);
             mealImageView = view.findViewById(R.id.image);
             btnUploadImage = view.findViewById(R.id.btnUploadImage);
             btnSaveMeal = view.findViewById(R.id.btnSaveMeal);

             btnUploadImage.setOnClickListener(v -> selectImage());

             btnSaveMeal.setOnClickListener(v -> saveMealToDatabase());

             return view;
         }

         private void selectImage() {
             Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
             startActivityForResult(intent, PICK_IMAGE_REQUEST);
         }

         @Override
         public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
             super.onActivityResult(requestCode, resultCode, data);
             if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK
                     && data != null && data.getData() != null) {
                 Uri selectedImageUri = data.getData();
                 try {
                     selectedImageBitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(),
                             selectedImageUri);
                     mealImageView.setImageBitmap(selectedImageBitmap);
                 } catch (IOException e) {
                     e.printStackTrace();
                 }
             }
         }

     private void saveMealToDatabase() {
         String mealName = edtMealName.getText().toString().trim();
         String category = edtCategory.getText().toString().trim();
         String ingredients = edtIngredients.getText().toString().trim();
         String instructions = edtInstructions.getText().toString().trim();
         String cookingTime = edtTime.getText().toString().trim();

         if (mealName.isEmpty() || category.isEmpty() || ingredients.isEmpty() || instructions.isEmpty()
                 || cookingTime.isEmpty() || selectedImageBitmap == null) {
             Toast.makeText(getContext(), "Please fill all fields and select an image!",
                     Toast.LENGTH_SHORT).show();
             return;
         }

         byte[] imageByteArray = convertBitmapToByteArray(selectedImageBitmap);
         boolean isInserted = dbHelper.addMeal(mealName, category, ingredients,
                 instructions, cookingTime, imageByteArray, false);

         if (isInserted) {
             Toast.makeText(getContext(), "Meal Added Successfully!",
                     Toast.LENGTH_SHORT).show();
             // Clear input fields
             edtMealName.setText("");
             edtCategory.setText("");
             edtIngredients.setText("");
             edtInstructions.setText("");
             edtTime.setText("");
             mealImageView.setImageDrawable(null);
             selectedImageBitmap = null;

             // Refresh ManageFragment
             if (getActivity() != null) {
                 ManageFragment manageFragment = (ManageFragment) getActivity()
                         .getSupportFragmentManager()
                         .findFragmentByTag("MANAGE_FRAGMENT");

                 if (manageFragment != null) {
                     manageFragment.refreshData();
                 }
             }
         } else {
             Toast.makeText(getContext(), "Failed to Add Meal!",
                     Toast.LENGTH_SHORT).show();
         }
     }

     private byte[] convertBitmapToByteArray(Bitmap bitmap) {
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
             return outputStream.toByteArray();
         }
     }


