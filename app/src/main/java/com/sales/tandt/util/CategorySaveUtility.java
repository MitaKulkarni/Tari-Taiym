package com.sales.tandt.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import com.sales.tandt.models.FoodCategory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.gson.Gson;

/**
 * Created by Mita on 8/4/2017.
 */

public class CategorySaveUtility {
    private static final String TAG = "CategorySaveUtility";
    private static final String AddMenu = "add_menu";
    private static final String CATEGORY_PREFS_NAME = "Categories";

    public CategorySaveUtility() {
        super();
    }

    private static void saveFoodItem(Context context, List<FoodCategory> foodCategoryList) {
        SharedPreferences settings;
        SharedPreferences.Editor editor;

        settings = context.getSharedPreferences(CATEGORY_PREFS_NAME,
                Context.MODE_PRIVATE);
        editor = settings.edit();

        if (foodCategoryList == null || foodCategoryList.size() == 0) {
            editor.putString(AddMenu, null);
            editor.apply();
            return;
        }

        Gson gson = new Gson();
        String jsonFoodCategory = gson.toJson(foodCategoryList);
        Log.d(TAG, "saveCategories : " + jsonFoodCategory);
        editor.putString(AddMenu, jsonFoodCategory);

        editor.apply();
    }

    public static void addFoodItem(Context context, FoodCategory foodCategory) {
            if (null == foodCategory) {
                return;
            }
            List<FoodCategory> foodCategories = getFoodItemList(context);
            if (foodCategories == null)
                foodCategories = new ArrayList<>();
            else if (foodCategories.contains(foodCategory)) {
                Log.d(TAG, "Already contains the food category");
                return;
            }
        foodCategories.add(foodCategory);
            saveFoodItem(context, foodCategories);
    }

    public static void removeFoodItem(Context context, FoodCategory foodCategory) {
            List<FoodCategory> foodCategoryList = getFoodItemList(context);
            if (foodCategoryList == null)
                foodCategoryList = new ArrayList<>();
            foodCategoryList.remove(foodCategory);
            saveFoodItem(context, foodCategoryList);

    }

    private static ArrayList<FoodCategory> getFoodItemList(Context context) {
        SharedPreferences settings;
        ArrayList<FoodCategory> foodCategoryArrayList = new ArrayList<>();

        settings = context.getSharedPreferences(CATEGORY_PREFS_NAME, Context.MODE_PRIVATE);
        String jsonFoodCategories = settings.getString(AddMenu, null);
        if (!TextUtils.isEmpty(jsonFoodCategories)) {
            Log.d(TAG, "getFoodCategoryList : " + jsonFoodCategories);
            Gson gson = new Gson();
            FoodCategory[] foodCategories = gson.fromJson(jsonFoodCategories,
                    FoodCategory[].class);

            foodCategoryArrayList = new ArrayList<>(Arrays.asList(foodCategories));
        }

        return foodCategoryArrayList;
    }

    public static void clearFoodItem(Context context) {
        SharedPreferences prefs;
        SharedPreferences.Editor editor;

        prefs = context.getSharedPreferences(CATEGORY_PREFS_NAME,
                Context.MODE_PRIVATE);
        editor = prefs.edit();
        editor.remove(AddMenu);
        editor.apply();
    }
}
