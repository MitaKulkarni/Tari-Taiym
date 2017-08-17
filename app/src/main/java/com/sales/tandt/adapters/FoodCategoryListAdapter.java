package com.sales.tandt.adapters;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sales.tandt.R;
import com.sales.tandt.listener.OnFoodItemAdded;
import com.sales.tandt.models.FoodCategory;

import java.util.ArrayList;


/**
 * Created by Mita on 8/4/2017.
 */

public class FoodCategoryListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<FoodCategory> mFoodCategoryList;
    private OnFoodItemAdded mListener;

    public FoodCategoryListAdapter(ArrayList<FoodCategory> foodList, OnFoodItemAdded onFoodItemAddedListener) {
        mFoodCategoryList = foodList;
        mListener = onFoodItemAddedListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        FoodCategory foodCategory = mFoodCategoryList.get(position);
        if (foodCategory.getList().size() > 0) {
            viewHolder.mCategoryNameTv.setText(foodCategory.getType());
            viewHolder.mHorizontalView.setVisibility(View.VISIBLE);

            FoodMenuListAdapter foodMenuListAdapter = new FoodMenuListAdapter(foodCategory.getList());
            viewHolder.mCategoryTypeRv.setAdapter(foodMenuListAdapter);
            foodMenuListAdapter.setOnItemAddedListener(mListener);

        } else {
            viewHolder.mHorizontalView.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mFoodCategoryList != null ? mFoodCategoryList.size() : 0;
    }

    private class ViewHolder extends RecyclerView.ViewHolder {
        TextView mCategoryNameTv;
        RecyclerView mCategoryTypeRv;
        View mHorizontalView;

        ViewHolder(View itemView) {
            super(itemView);
            mCategoryNameTv = (TextView) itemView.findViewById(R.id.category_item_layout_name_tv);
            mCategoryTypeRv = (RecyclerView) itemView.findViewById(R.id.category_item_layout_rv);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(itemView.getContext());
            mCategoryTypeRv.setLayoutManager(linearLayoutManager);
            mHorizontalView = itemView.findViewById(R.id.category_item_layout_view);

        }
    }
}

