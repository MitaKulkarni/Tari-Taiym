package com.sales.tandt.listener;

import com.sales.tandt.models.FoodItems;

/**
 * Created by Mita on 8/11/2017.
 */

public interface OnFoodItemAdded {
    void onItemAdded(FoodItems foodItems);
    void onItemRemoved(FoodItems foodItems);
}
