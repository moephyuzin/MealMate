package com.example.mealmate;

public class Meal {
    private int id;
    private String mealName, category, ingredients, instructions, cookingTime;
    private byte[] image;
    private boolean isFavorite; // Added isFavorite field

    private boolean isChecked;

    // Constructor with ID and isFavorite (for fetching from DB)
    public Meal(int id, String mealName, String category, String ingredients, String instructions, String cookingTime, byte[] image, boolean isFavorite) {
        this.id = id;
        this.mealName = mealName;
        this.category = category;
        this.ingredients = ingredients;
        this.instructions = instructions;
        this.cookingTime = cookingTime;
        this.image = image;
        this.isFavorite = isFavorite;
    }

    // Constructor without ID and isFavorite (for adding new meals)
    public Meal(String mealName, String category, String ingredients, String instructions, String cookingTime, byte[] image) {
        this.id = -1; // Default ID
        this.mealName = mealName;
        this.category = category;
        this.ingredients = ingredients;
        this.instructions = instructions;
        this.cookingTime = cookingTime;
        this.image = image;
        this.isFavorite = false; // Default to false
    }

    // Getters
    public int getId() { return id; }
    public String getMealName() { return mealName; }
    public String getCategory() { return category; }
    public String getIngredients() { return ingredients; }
    public String getInstructions() { return instructions; }
    public String getCookingTime() { return cookingTime; }
    public byte[] getImage() { return image; }
    public boolean isFavorite() { return isFavorite; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setMealName(String mealName) { this.mealName = mealName; }
    public void setCategory(String category) { this.category = category; }
    public void setIngredients(String ingredients) { this.ingredients = ingredients; }
    public void setInstructions(String instructions) { this.instructions = instructions; }
    public void setCookingTime(String cookingTime) { this.cookingTime = cookingTime; }
    public void setImage(byte[] image) { this.image = image; }
    public void setFavorite(boolean favorite) { isFavorite = favorite; } // Added setter
}
