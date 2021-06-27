package com.oopFinalSelling.oopFinalShopping.activities;

import android.os.Bundle;

import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.oopFinalSelling.oopFinalShopping.R;
import com.oopFinalSelling.oopFinalShopping.adapters.MyOrdersAdapter;
import com.oopFinalSelling.oopFinalShopping.database.DB_Handler;
import com.oopFinalSelling.oopFinalShopping.database.dataManager;
import com.oopFinalSelling.oopFinalShopping.objects.Cart;
import com.oopFinalSelling.oopFinalShopping.utils.Constants;

import java.util.List;

public class MyOrders extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.myorders);

        // Set Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Set Title
        TextView titleToolbar = findViewById(R.id.titleToolbar);
        titleToolbar.setText(R.string.my_orders);

        // Back Button
        ImageView backButton = findViewById(R.id.backButton);
        backButton.setVisibility(View.VISIBLE);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        // Hide Cart Icon
        ImageView cart = findViewById(R.id.cart);
        cart.setVisibility(View.GONE);

        // Get Orders From DB
        DB_Handler db_handler = new DB_Handler(this);
        dataManager dataManager = new dataManager(this);
        List<Cart> orderHistory = db_handler.getOrders(dataManager.getData(Constants.SESSION_EMAIL));

        // Fill ListView
        ListView listView = findViewById(R.id.listview);
        listView.setAdapter(new MyOrdersAdapter(this,orderHistory));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0,0);
    }
}
