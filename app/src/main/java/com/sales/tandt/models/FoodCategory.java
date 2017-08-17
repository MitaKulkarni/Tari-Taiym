package com.sales.tandt.models;

import java.util.ArrayList;

/**
 * Created by Mita on 8/4/2017.
 */

public class FoodCategory {
    private String type;
    private ArrayList<FoodItems> foodItemsArrayList;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ArrayList<FoodItems> getList() {
        return foodItemsArrayList;
    }

    public void setList(ArrayList<FoodItems> list) {
        this.foodItemsArrayList = list;
    }
}
