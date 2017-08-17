package com.sales.tandt.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.sales.tandt.R;
import com.sales.tandt.controller.AppController;
import com.sales.tandt.util.RequestUrl;

import org.json.JSONException;
import org.json.JSONObject;

public class AddMenuItemsActivity extends BaseActivity {

    private static final String TAG = AddMenuItemsActivity.class.getName();
    private TextView mDishNameEditText, mPriceEditText;

    @Override
    protected String getScreenName() {
        return TAG;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_menu_items);
        setActionBar();
        setActionBarTitle(getString(R.string.add_food_to_menu_title));

        mDishNameEditText = (EditText) findViewById(R.id.activity_add_menu_dish_et);
        mPriceEditText = (EditText) findViewById(R.id.activity_add_menu_price_et);

        final Spinner categorySp = (Spinner) findViewById(R.id.activity_add_menu_category_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.food_category_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        categorySp.setAdapter(adapter);

        Button submitBtn = (Button) findViewById(R.id.activity_add_menu_submit_button);
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sentDataToServer(mDishNameEditText.getText().toString(), mPriceEditText.getText().toString(), categorySp.getSelectedItem().toString());
            }
        });

    }

    private void sentDataToServer(String dishName, String price, String categoryType) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("name", dishName);
            jsonObject.put("price", Integer.parseInt(price));
            jsonObject.put("type", categoryType);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        showProgressDialog();
        JsonObjectRequest addMenuTask = new JsonObjectRequest(Request.Method.POST, RequestUrl.addMenuUrl, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
               hideProgressDialogs();
                AppController.getInstance().sendToast(AddMenuItemsActivity.this, "Successfully added the food item!");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
               hideProgressDialogs();
                AppController.getInstance().sendToast(AddMenuItemsActivity.this, "Error in sending the network request. Please try again after some time.");
            }
        });

        addMenuTask.setTag(TAG);
        addMenuTask.setShouldCache(false);
        AppController.getInstance().setRetryPolicy(addMenuTask);
        AppController.getInstance().addToRequestQueue(addMenuTask);
    }
}
