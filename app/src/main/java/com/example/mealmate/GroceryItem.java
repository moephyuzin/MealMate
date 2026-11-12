package com.example.mealmate;

public class GroceryItem {

        private int groceryId;
        private int mealId;
        private String groceryName;
        private String groceryCategory;
        private byte[]groceryImage;
        private boolean isChecked;

        // Constructor
        public GroceryItem(int groceryId, int mealId, String groceryName, String groceryCategory, byte[] groceryImage, boolean isChecked) {
            this.groceryId = groceryId;
            this.mealId = mealId;
            this.groceryName = groceryName;
            this.groceryCategory = groceryCategory;
            this.groceryImage = groceryImage;
            this.isChecked = isChecked;
        }

        // Getters and setters
        public int getGroceryId() {
            return groceryId;
        }

        public void setGroceryId(int groceryId) {
            this.groceryId = groceryId;
        }

        public int getMealId() {
            return mealId;
        }

        public void setMealId(int mealId) {
            this.mealId = mealId;
        }

        public String getGroceryName() {
            return groceryName;
        }

        public void setGroceryName(String groceryName) {
            this.groceryName = groceryName;
        }

        public String getGroceryCategory() {
            return groceryCategory;
        }

        public void setGroceryCategory(String groceryCategory) {
            this.groceryCategory = groceryCategory;
        }

        public byte[] getGroceryImage() {
            return groceryImage;
        }

        public void setGroceryImage(byte[] groceryImage) {
            this.groceryImage = groceryImage;
        }

        public boolean isChecked() {
                return isChecked;
         }

        public void setChecked(boolean checked) {
            isChecked = checked;
        }
    }


