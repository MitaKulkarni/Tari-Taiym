package com.sales.tandt.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.gson.Gson;
import com.sales.tandt.R;
import com.sales.tandt.adapters.FoodCategoryListAdapter;
import com.sales.tandt.controller.AppController;
import com.sales.tandt.listener.OnFoodItemAdded;
import com.sales.tandt.models.FoodCategory;
import com.sales.tandt.models.FoodItems;
import com.sales.tandt.util.RequestUrl;
import org.json.JSONArray;
import java.util.ArrayList;

public class FoodMenuListActivity extends BaseActivity implements OnFoodItemAdded {

    private static final String TAG = FoodMenuListActivity.class.getName();
    private ArrayList<FoodCategory> mCategoryList;
    private RecyclerView mMenuListRv;
    private ArrayList<FoodItems> mFoodItemList;

    @Override
    protected String getScreenName() {
        return TAG;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_list);
        setActionBar();
        setActionBarTitle(getString(R.string.food_menu_list_title));

        mMenuListRv = (RecyclerView) findViewById(R.id.activity_menu_list_food_menu_rv);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mMenuListRv.setLayoutManager(linearLayoutManager);

        mCategoryList = new ArrayList<>();
        mFoodItemList =  new ArrayList<>();
        getDataToServer();

        Button submitBt = (Button) findViewById(R.id.activity_menu_list_submit_btn);
        submitBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FoodMenuListActivity.this, GenerateInvoiceActivity.class);
                intent.putExtra(getString(R.string.food_menu_list), mFoodItemList);
                startActivity(intent);
            }
        });
    }

    private void getDataToServer() {

        showProgressDialog();
        JsonArrayRequest getMenuTask = new JsonArrayRequest(Request.Method.GET, RequestUrl.addMenuUrl, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray jsonArray) {

                hideProgressDialogs();

                if (jsonArray != null && jsonArray.length() > 0) {
                    Gson gson = new Gson();
                    FoodItems[] foodItems = gson.fromJson(jsonArray.toString(), FoodItems[].class);
                    String[] typeArray = getResources().getStringArray(R.array.food_category_array);
                    for (int j = 0; j < typeArray.length; j++) {
                        ArrayList<FoodItems> foodList = new ArrayList<>();
                        for (int i = 0; i < foodItems.length; i++) {
                            if (foodItems[i].getType().equals(typeArray[j])) {
                                foodList.add(foodItems[i]);
                            }
                        }
                        FoodCategory foodCategory = new FoodCategory();
                        foodCategory.setType(typeArray[j]);
                        foodCategory.setList(foodList);
                        mCategoryList.add(foodCategory);
                    }
                    FoodCategoryListAdapter adapter = new FoodCategoryListAdapter(mCategoryList, FoodMenuListActivity.this);
                    mMenuListRv.setAdapter(adapter);
                }
            }
        }, new Response.ErrorListener()

        {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                hideProgressDialogs();
                AppController.getInstance().sendToast(FoodMenuListActivity.this, "Error in sending the network request. Please try again after some time.");
            }
        });

        getMenuTask.setTag(TAG);
        getMenuTask.setShouldCache(false);
        AppController.getInstance().setRetryPolicy(getMenuTask);
        AppController.getInstance().addToRequestQueue(getMenuTask);
    }

    @Override
    public void onItemAdded(FoodItems foodItems) {
        mFoodItemList.add(foodItems);
    }

    @Override
    public void onItemRemoved(FoodItems foodItems) {
        mFoodItemList.remove(foodItems);
    }
}
