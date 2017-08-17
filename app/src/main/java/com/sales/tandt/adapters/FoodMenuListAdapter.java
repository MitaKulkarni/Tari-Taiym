package com.sales.tandt.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.sales.tandt.R;
import com.sales.tandt.activity.GenerateInvoiceActivity;
import com.sales.tandt.listener.OnFoodItemAdded;
import com.sales.tandt.models.FoodItems;

import java.util.ArrayList;


/**
 * Created by Mita on 8/3/2017.
 */

public class FoodMenuListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<FoodItems> mFoodsList;
    private OnFoodItemAdded mListener;

    public FoodMenuListAdapter(ArrayList<FoodItems> foodList) {
        mFoodsList = foodList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.menu_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final ViewHolder viewHolder = (ViewHolder) holder;
        final FoodItems foodItems = mFoodsList.get(position);

        viewHolder.mFoodItemTv.setText(foodItems.getDishName());
        viewHolder.mPriceTv.setText(String.valueOf(foodItems.getPrice()));
        viewHolder.mMinusBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int quantity = Integer.parseInt(viewHolder.mQuantityTv.getText().toString());
                if (quantity > 0) {
                    quantity = quantity - 1;
                }
                viewHolder.mQuantityTv.setText(String.valueOf(quantity));

                if (quantity > 0) {
                    mListener.onItemRemoved(foodItems);
                    foodItems.setQuantity(quantity);
                    mListener.onItemAdded(foodItems);
                } else if (quantity == 0) {
                    foodItems.setQuantity(quantity);
                    mListener.onItemRemoved(foodItems);
                }
            }
        });

        viewHolder.mAddBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int quantity = Integer.parseInt(viewHolder.mQuantityTv.getText().toString());
                quantity = quantity + 1;
                viewHolder.mQuantityTv.setText(String.valueOf(quantity));
                if (quantity > 1) {
                    mListener.onItemRemoved(foodItems);
                    foodItems.setQuantity(quantity);
                    mListener.onItemAdded(foodItems);
                } else if (quantity == 1) {
                    foodItems.setQuantity(quantity);
                    mListener.onItemAdded(foodItems);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return mFoodsList != null ? mFoodsList.size() : 0;
    }

   public void setOnItemAddedListener(OnFoodItemAdded listener) {
        mListener = listener;
    }

    private class ViewHolder extends RecyclerView.ViewHolder {
        TextView mFoodItemTv, mPriceTv, mQuantityTv;
        Button mMinusBt, mAddBt;

        ViewHolder(View itemView) {
            super(itemView);
            mFoodItemTv = (TextView) itemView.findViewById(R.id.menu_list_item_food_item_tv);
            mPriceTv = (TextView) itemView.findViewById(R.id.menu_list_item_price_tv);
            mQuantityTv = (TextView) itemView.findViewById(R.id.menu_list_item_quantity_tv);
            mQuantityTv.setText("0");
            mMinusBt = (Button) itemView.findViewById(R.id.menu_list_item_minus_bt);
            mAddBt = (Button) itemView.findViewById(R.id.menu_list_item_add_bt);
        }
    }
}
