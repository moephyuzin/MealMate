package com.example.mealmate;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.mealmate.UserData;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {

    // Database and Table Names
    private static final String DB_NAME = "MealMate.db";
    private static final int DB_VERSION = 1;
    private static final String USER_TABLE = "User_Table";

    private static final String MEAL_TABLE = "Meal_Table";

    // User Table Columns
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_PASSWORD = "password";

    // Meal Table Columns
    private static final String COLUMN_MEAL_ID = "meal_id";
    private static final String COLUMN_MEAL_NAME = "meal_name";
    private static final String COLUMN_CATEGORY = "category";
    private static final String COLUMN_INGREDIENTS = "ingredients";
    private static final String COLUMN_INSTRUCTIONS = "instructions";
    private static final String COLUMN_COOKING_TIME = "cooking_time";
    private static final String COLUMN_IMAGE = "image"; // Store image as a BLOB
    static final String COLUMN_FAVOURITE = "is_favorite";

    // Grocery List Table
    public static final String TABLE_GROCERY = "grocery_list";
    public static final String COLUMN_GROCERY_ID = "grocery_id";
    private static final String COLUMN_GROCERY_MEAL_ID = "meal_id"; // Foreign key reference

    public static final String COLUMN_GROCERY_NAME = "grocery_name";
    public static final String COLUMN_GROCERY_CATEGORY = "grocery_category";
    public static final String COLUMN_GROCERY_IMAGE = "grocery_image";
    public static final String COLUMN_CHECKED = "checked"; // 0 = not checked, 1 = checked

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Creating User Table
        String createUserTable = "CREATE TABLE " + USER_TABLE + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_USERNAME + " TEXT UNIQUE, "
                + COLUMN_EMAIL + " TEXT, "
                + COLUMN_PASSWORD + " TEXT)";
        db.execSQL(createUserTable);

        // Creating Meal Table
        String CREATE_MEAL_TABLE = "CREATE TABLE " + MEAL_TABLE + " (" +
                COLUMN_MEAL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + // Corrected
                COLUMN_MEAL_NAME + " TEXT, " +
                COLUMN_CATEGORY + " TEXT, " +
                COLUMN_INGREDIENTS + " TEXT, " +
                COLUMN_INSTRUCTIONS + " TEXT, " +
                COLUMN_COOKING_TIME + " TEXT, " +
                COLUMN_IMAGE + " BLOB, " +
                COLUMN_FAVOURITE + " INTEGER DEFAULT 0)"; // Corrected
        db.execSQL(CREATE_MEAL_TABLE);

        String createGroceryListTable = "CREATE TABLE " + TABLE_GROCERY + " (" +
                COLUMN_GROCERY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_GROCERY_MEAL_ID + " INTEGER, " +
                COLUMN_GROCERY_NAME + " TEXT, " +
                COLUMN_GROCERY_CATEGORY + " TEXT, " +
                COLUMN_GROCERY_IMAGE + " BLOB, " +
                COLUMN_CHECKED + " INTEGER DEFAULT 0)";

        db.execSQL(createGroceryListTable);
    }




    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GROCERY);
        db.execSQL("DROP TABLE IF EXISTS " + MEAL_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + USER_TABLE);
        onCreate(db);
    }

    // Insert new user into the database
    public void addUser(String username, String password, String email) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(COLUMN_USERNAME, username);
        contentValues.put(COLUMN_EMAIL, email);
        contentValues.put(COLUMN_PASSWORD, password);

        database.insert(USER_TABLE, null, contentValues);
        database.close();
    }

    // Read all users from the database
    public ArrayList<UserData> readUser() {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor userCursor = database.rawQuery("SELECT * FROM " + USER_TABLE, null);

        ArrayList<UserData> userModelArrayList = new ArrayList<>();

        if (userCursor.moveToFirst()) {
            do {
                userModelArrayList.add(new UserData(
                        userCursor.getInt(0),
                        userCursor.getString(1),
                        userCursor.getString(2),
                        userCursor.getString(3)
                ));
            } while (userCursor.moveToNext());
        }
        userCursor.close();
        database.close();
        return userModelArrayList;
    }

    // Check if user exists based on username and password
    public boolean checkUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + USER_TABLE + " WHERE " +
                        COLUMN_USERNAME + " = ? AND " + COLUMN_PASSWORD + " = ?",
                new String[]{username, password});

        boolean exists = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return exists;
    }


    // Check if username already exists
    public boolean isUsernameExists(String username) {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM " + USER_TABLE + " WHERE " +
                COLUMN_USERNAME + " = ?", new String[]{username});

        boolean exists = cursor.moveToFirst();
        cursor.close();
        database.close();
        return exists;
    }

    // Insert meal data
    public boolean addMeal(String mealName, String category, String ingredients, String instructions, String cookingTime, byte[] image, boolean isFavorite) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(COLUMN_MEAL_NAME, mealName);
        contentValues.put(COLUMN_CATEGORY, category);
        contentValues.put(COLUMN_INGREDIENTS, ingredients);
        contentValues.put(COLUMN_INSTRUCTIONS, instructions);
        contentValues.put(COLUMN_COOKING_TIME, cookingTime);
        contentValues.put(COLUMN_IMAGE, image);
        contentValues.put(COLUMN_FAVOURITE, isFavorite ? 1 : 0); // Store as integer

        long result = db.insert(MEAL_TABLE, null, contentValues);
        db.close();
        return result != -1;
    }


    public Cursor getAllMeals() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + MEAL_TABLE, null);  // Ensure it selects all columns
    }

    // Retrieve meal image by meal ID
    public byte[] getMealImage(int mealId) {
        SQLiteDatabase db = this.getReadableDatabase();
        byte[] image = null;

        Cursor cursor = db.rawQuery("SELECT " + COLUMN_IMAGE + " FROM " + MEAL_TABLE +
                " WHERE " + COLUMN_MEAL_ID + " = ?", new String[]{String.valueOf(mealId)});

        if (cursor.moveToFirst()) {
            image = cursor.getBlob(0);
        }
        cursor.close();
        db.close();
        return image;
    }

    // Update meal
    public boolean updateMeal(int mealId, String mealName, String category, String ingredients,
                              String instructions, String cookingTime, byte[] mealImage) {
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_MEAL_NAME, mealName);
            values.put(COLUMN_CATEGORY, category);
            values.put(COLUMN_INGREDIENTS, ingredients);
            values.put(COLUMN_INSTRUCTIONS, instructions);
            values.put(COLUMN_COOKING_TIME, cookingTime);
            values.put(COLUMN_IMAGE, mealImage);

            int rowsUpdated = db.update(MEAL_TABLE, values, COLUMN_MEAL_ID + " = ?",
                    new String[]{String.valueOf(mealId)});

            return rowsUpdated > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    // Delete meal
    public boolean deleteMeal(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(MEAL_TABLE, COLUMN_MEAL_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
        return result > 0;
    }

    public Cursor getFavoriteMeals() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + MEAL_TABLE + " WHERE is_favorite = 1", null);
    }

    public void updateFavoriteStatus(int mealId, boolean isFavorite) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_FAVOURITE, isFavorite ? 1 : 0);
        db.update(MEAL_TABLE, values, COLUMN_MEAL_ID + "=?", new String[]{String.valueOf(mealId)});
        db.close();
    }

    public Cursor getGroceryMeals() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_GROCERY,  null);
    }

    public void addMealToGroceryList(Meal meal) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_GROCERY_MEAL_ID, meal.getId());
        values.put(COLUMN_GROCERY_NAME, meal.getMealName());
        values.put(COLUMN_GROCERY_CATEGORY, meal.getCategory());
        values.put(COLUMN_GROCERY_IMAGE, meal.getImage());
        values.put(COLUMN_CHECKED, 0); // Default unchecked

        long result = db.insert(TABLE_GROCERY, null, values);
        if (result == -1) {
            Log.e("DBHelper", "Failed to insert meal into grocery list.");
        } else {
            Log.d("DBHelper", "Meal added to grocery list successfully.");
        }
        db.close();
    }


    public void updateGroceryCheckedState(int groceryId, boolean isChecked) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_CHECKED, isChecked ? 1 : 0); // Convert boolean to integer (1 = checked, 0 = unchecked)

        db.update(TABLE_GROCERY, values, COLUMN_GROCERY_ID + "=?", new String[]{String.valueOf(groceryId)});
        db.close();
    }

    public void deleteGroceryItem(int groceryId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_GROCERY, COLUMN_GROCERY_ID + "=?", new String[]{String.valueOf(groceryId)});
        db.close();
    }

    public void addGroceryItem(GroceryItem item) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_GROCERY_NAME, item.getGroceryName());
        values.put(COLUMN_GROCERY_CATEGORY, item.getGroceryCategory());  // Ensure this column is used correctly
        values.put(COLUMN_GROCERY_IMAGE, item.getGroceryImage());
        values.put(COLUMN_CHECKED, item.isChecked() ? 1 : 0);
        values.put(COLUMN_GROCERY_MEAL_ID, item.getMealId());

        db.insert(TABLE_GROCERY, null, values);
    }

    public Meal getMealDetailsById(int mealId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(MEAL_TABLE,
                new String[]{COLUMN_MEAL_ID, COLUMN_MEAL_NAME, COLUMN_CATEGORY, COLUMN_INGREDIENTS,
                        COLUMN_INSTRUCTIONS, COLUMN_COOKING_TIME, COLUMN_IMAGE, COLUMN_FAVOURITE},
                COLUMN_MEAL_ID + "=?", new String[]{String.valueOf(mealId)}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            int mealIdIndex = cursor.getColumnIndex(COLUMN_MEAL_ID);
            int mealNameIndex = cursor.getColumnIndex(COLUMN_MEAL_NAME);
            int categoryIndex = cursor.getColumnIndex(COLUMN_CATEGORY);
            int ingredientsIndex = cursor.getColumnIndex(COLUMN_INGREDIENTS);
            int instructionsIndex = cursor.getColumnIndex(COLUMN_INSTRUCTIONS);
            int cookingTimeIndex = cursor.getColumnIndex(COLUMN_COOKING_TIME);
            int imageIndex = cursor.getColumnIndex(COLUMN_IMAGE);
            int favouriteIndex = cursor.getColumnIndex(COLUMN_FAVOURITE);

            // Check if any column index is invalid (-1)
            if (mealIdIndex != -1 && mealNameIndex != -1 && categoryIndex != -1 && ingredientsIndex != -1 &&
                    instructionsIndex != -1 && cookingTimeIndex != -1 && imageIndex != -1 && favouriteIndex != -1) {

                Meal meal = new Meal(
                        cursor.getInt(mealIdIndex),
                        cursor.getString(mealNameIndex),
                        cursor.getString(categoryIndex),
                        cursor.getString(ingredientsIndex),
                        cursor.getString(instructionsIndex),
                        cursor.getString(cookingTimeIndex),
                        cursor.getBlob(imageIndex),
                        cursor.getInt(favouriteIndex) == 1
                );
                cursor.close();
                return meal;
            } else {
                // Handle missing column(s)
                cursor.close();
                return null;
            }
        } else {
            cursor.close();
            return null;
        }
    }







}