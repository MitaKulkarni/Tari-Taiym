package com.sales.tandt.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Parcelable;
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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.sales.tandt.R;
import com.sales.tandt.adapters.InvoiceGenerateListAdapter;
import com.sales.tandt.listener.OnFoodItemAdded;
import com.sales.tandt.models.FoodItems;
import com.sales.tandt.util.AppPermissionsConstants;

import android.Manifest;

import java.util.ArrayList;

public class GenerateInvoiceActivity extends BaseActivity {

    private static final String TAG = GenerateInvoiceActivity.class.getName();
    private String mPhoneNo;
    private String mInvoiceDetails;
    private TextView mTotalTv;
    private ArrayList<FoodItems> mFoodItemList;

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

        mTotalTv= (TextView) findViewById(R.id.activity_generate_invoice_total_amt_tv);
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

        mTotalTv.setText(getString(R.string.total_cart_amount, total));
    }

    private void openSmsDialog() {

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.sms_dialog_layout, null);
        Dialog dialog = new Dialog(this);
        dialog.setCanceledOnTouchOutside(false);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(dialogView);

        final EditText phoneEt = (EditText) dialogView.findViewById(R.id.sms_dialog_layout_phone_et);
        TextView invoiceDetailsTv = (TextView) dialogView.findViewById(R.id.sms_dialog_layout_invoice_text_tv);
        invoiceDetailsTv.setText(mInvoiceDetails);
        Button sendBt = (Button) dialogView.findViewById(R.id.sms_dialog_layout_send_bt);
        sendBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mPhoneNo = phoneEt.getText().toString();
                checkPermission();
            }
        });

        dialog.show();

    }

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.SEND_SMS)) {
                showRationaleSendSmsDialog();
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.SEND_SMS},
                        AppPermissionsConstants.PERMISSIONS_REQUEST_SEND_SMS);
            }
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
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(mPhoneNo, null, mInvoiceDetails, null, null);
                    Toast.makeText(getApplicationContext(), "SMS sent.",
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(),
                            "SMS failed, please try again.", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

}
