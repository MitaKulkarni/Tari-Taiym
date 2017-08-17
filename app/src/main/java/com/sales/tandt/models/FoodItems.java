package com.sales.tandt.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by admin on 8/3/2017.
 */

public class FoodItems implements Parcelable {

    @SerializedName("name")
    @Expose
    private String dishName;
    @SerializedName("price")
    @Expose
    private int price;
    private int quantity;
    @SerializedName("type")
    @Expose
    private String type;


    protected FoodItems(Parcel in) {
        dishName = in.readString();
        price = in.readInt();
        quantity = in.readInt();
        type = in.readString();
    }

    public static final Creator<FoodItems> CREATOR = new Creator<FoodItems>() {
        @Override
        public FoodItems createFromParcel(Parcel in) {
            return new FoodItems(in);
        }

        @Override
        public FoodItems[] newArray(int size) {
            return new FoodItems[size];
        }
    };

    public void setDishName(String dishName) {
        this.dishName = dishName;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getQuantity() { return quantity; }

    public String getDishName() {
        return dishName;
    }

    public int getPrice() {
        return price;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.dishName);
        dest.writeInt(this.price);
        dest.writeInt(this.quantity);
        dest.writeString(this.type);
    }

}
