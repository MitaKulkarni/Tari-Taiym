package com.sales.tandt.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.sales.tandt.AppConstants;
import com.sales.tandt.R;
import com.sales.tandt.adapters.InvoiceGenerateListAdapter;
import com.sales.tandt.controller.AppController;
import com.sales.tandt.listener.OnFoodItemAdded;
import com.sales.tandt.models.FoodItems;
import com.sales.tandt.util.AppPermissionsConstants;
import com.sales.tandt.util.RequestUrl;
import com.sales.tandt.util.Utilities;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class GenerateInvoiceActivity extends BaseActivity {

    private static final String TAG = GenerateInvoiceActivity.class.getName();
    private String mPhoneNo;
    private String mInvoiceDetails;
    private TextView mTotalTv;
    private ArrayList<FoodItems> mFoodItemList;
    private Dialog mSmsDialog;
    private int mTotalAmount;

    @Override
    protected String getScreenName() {
        return TAG;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_invoice);
        setActionBar();
        setActionBarTitle(getString(R.string.food_cart_title));

        RecyclerView menuListRv = (RecyclerView) findViewById(R.id.activity_generate_invoice_rv);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        menuListRv.setLayoutManager(linearLayoutManager);

        mFoodItemList = getIntent().getParcelableArrayListExtra(getString(R.string.food_menu_list));

        InvoiceGenerateListAdapter adapter = new InvoiceGenerateListAdapter(mFoodItemList);
        menuListRv.setAdapter(adapter);

        mTotalTv = (TextView) findViewById(R.id.activity_generate_invoice_total_amt_tv);
        calculateTotal();

        adapter.setOnItemAddedListener(new OnFoodItemAdded() {
            @Override
            public void onItemAdded(FoodItems foodItems) {
                mFoodItemList.add(foodItems);
                calculateTotal();
            }

            @Override
            public void onItemRemoved(FoodItems foodItems) {
                mFoodItemList.remove(foodItems);
                calculateTotal();
            }
        });

        Button sendSms = (Button) findViewById(R.id.activity_generate_invoice_send_sms_btn);
        sendSms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSmsDialog();
            }
        });
    }

    private void sentDataToServer(String phone, String invoice, int total, boolean subscribe) {

        if (Utilities.isNetworkAvailable(this)) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put(AppConstants.JsonConstants.PHONE, phone);
                jsonObject.put(AppConstants.JsonConstants.INVOICE, invoice);
                jsonObject.put(AppConstants.JsonConstants.TOTAL_AMOUNT, total);
                jsonObject.put(AppConstants.JsonConstants.SUBSCRIBE, subscribe);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            showProgressDialog();
            JsonObjectRequest addInvoiceTask = new JsonObjectRequest(Request.Method.POST, RequestUrl.addInvoiceUrl, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObject) {
                    hideProgressDialogs();
                    Toast.makeText(GenerateInvoiceActivity.this, getString(R.string.add_food_item_success_msg), Toast.LENGTH_LONG).show();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    hideProgressDialogs();
                    Toast.makeText(GenerateInvoiceActivity.this, getString(R.string.error_connecting), Toast.LENGTH_LONG).show();
                }
            });

            addInvoiceTask.setTag(TAG);
            addInvoiceTask.setShouldCache(false);
            AppController.getInstance().setRetryPolicy(addInvoiceTask);
            AppController.getInstance().addToRequestQueue(addInvoiceTask);
        } else {
            Utilities.showToast(this, getString(R.string.no_internet_connection_error));
        }
    }

    private void calculateTotal() {
        mInvoiceDetails = "";
        int total = 0;
        for (int i = 0; i < mFoodItemList.size(); i++) {
            FoodItems foodItems = mFoodItemList.get(i);
            int price = foodItems.getPrice() * foodItems.getQuantity();
            total = total + price;
            mInvoiceDetails = mInvoiceDetails + foodItems.getDishName() + " quantity " + foodItems.getQuantity() + " Price: Rs. " + price + "\n";
        }
        mInvoiceDetails = mInvoiceDetails + "Total: Rs. " + total;
        mTotalAmount = total;

        mTotalTv.setText(getString(R.string.total_cart_amount, total));
    }

    private void openSmsDialog() {

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.sms_dialog_layout, null);
        mSmsDialog = new Dialog(this);
        mSmsDialog.setCanceledOnTouchOutside(false);
        mSmsDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mSmsDialog.setContentView(dialogView);

        final EditText phoneEt = (EditText) dialogView.findViewById(R.id.sms_dialog_layout_phone_et);
        final CheckBox subscribeCb = (CheckBox) dialogView.findViewById(R.id.sms_dialog_layout_susbcribe_cb);
        TextView invoiceDetailsTv = (TextView) dialogView.findViewById(R.id.sms_dialog_layout_invoice_text_tv);
        invoiceDetailsTv.setText(mInvoiceDetails);
        Button sendBt = (Button) dialogView.findViewById(R.id.sms_dialog_layout_send_bt);
        sendBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mPhoneNo = phoneEt.getText().toString();
                sentDataToServer(mPhoneNo, mInvoiceDetails, mTotalAmount, subscribeCb.isChecked());
                checkPermission();
            }
        });

        mSmsDialog.show();

    }

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.SEND_SMS)) {
                showRationaleSendSmsDialog();
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.SEND_SMS},
                        AppPermissionsConstants.PERMISSIONS_REQUEST_SEND_SMS);
            }
        } else {
            sendSms();
        }
    }

    private void showRationaleSendSmsDialog() {
        Dialog dialog;
        final AlertDialog.Builder builder =
                new AlertDialog.Builder(this);
        builder.setTitle(R.string.sned_sms_request_title);
        builder.setMessage(R.string.send_sms_access_request)
                .setCancelable(false)
                .setPositiveButton(getResources().getString(R.string.dialog_ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int id) {
                        dialogInterface.dismiss();
                        ActivityCompat.requestPermissions(GenerateInvoiceActivity.this,
                                new String[]{Manifest.permission.SEND_SMS},
                                AppPermissionsConstants.PERMISSIONS_REQUEST_SEND_SMS);
                    }
                })
                .setNegativeButton(getResources().getString(R.string.dialog_cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case AppPermissionsConstants.PERMISSIONS_REQUEST_SEND_SMS: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    sendSms();
                } else {
                    Toast.makeText(getApplicationContext(),
                            "SMS failed, please try again.", Toast.LENGTH_LONG).show();
                }
            }
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void sendSms() {
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(mPhoneNo, null, mInvoiceDetails, null, null);
        Toast.makeText(getApplicationContext(), "SMS sent.",
                Toast.LENGTH_LONG).show();
        mSmsDialog.hide();
    }

}
