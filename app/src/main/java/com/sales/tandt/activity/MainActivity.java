package com.sales.tandt.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.sales.tandt.R;


public class MainActivity extends BaseActivity {

    private static final String TAG = MainActivity.class.getName();

    @Override
    protected String getScreenName() {
        return TAG;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button menuBt = (Button) findViewById(R.id.activity_main_menu_bt);
        Button addMenuBt = (Button) findViewById(R.id.activity_main_add_menu_bt);

        menuBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, FoodMenuListActivity.class));
            }
        });

        addMenuBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, AddMenuItemsActivity.class));
            }
        });
    }
}
