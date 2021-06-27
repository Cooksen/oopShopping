package com.oopFinalSelling.oopFinalShopping;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;

import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.snackbar.Snackbar;
import com.oopFinalSelling.oopFinalShopping.database.DB_Handler;
import com.oopFinalSelling.oopFinalShopping.fragments.SignIn;
import com.oopFinalSelling.oopFinalShopping.fragments.SignUp;
import com.oopFinalSelling.oopFinalShopping.interfaces.FinishActivity;
import com.oopFinalSelling.oopFinalShopping.dbUpdater.dbUpdateAdapter;

public class SplashActivity extends AppCompatActivity implements FinishActivity {

    DB_Handler db_handler;
    Button signIn, signUp;
    Handler handler;
    TableLayout bottomLay;
    Snackbar snackbar = null;
    CoordinatorLayout coordinatorLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Service To Fetch Data From URL
        setHandler();
        startIntentService();

        // Initialize DB Handler
        db_handler = new DB_Handler(this);

        setIds();
        setClickListeners();
    }

    // Set Ids
    private void setIds() {
        signIn = findViewById(R.id.signin);
        signUp = findViewById(R.id.signup);
        bottomLay = findViewById(R.id.bottomLay);
        coordinatorLayout = findViewById(R.id.coordinatorLay);
    }

    // Set Click Listeners
    private void setClickListeners() {
        // Sign In
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ft.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left);
                ft.replace(R.id.fragment, new SignIn());
                ft.addToBackStack(null);
                ft.commit();
            }
        });

        // Sign Up
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ft.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left);
                ft.replace(R.id.fragment, new SignUp());
                ft.addToBackStack(null);
                ft.commit();
            }
        });
    }

    // Start Intent Service To Fetch Data
    private void startIntentService() {
        Intent intent = new Intent(getApplicationContext(), dbUpdateAdapter.class);
        intent.putExtra("messenger", new Messenger(handler));
        startService(intent);
    }

    // Load Next Activity
    private void loadNextActivity() {
        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(i);
        overridePendingTransition(0, 0);
        finish();
    }

    // Handler To Receive Data From Service
    @SuppressLint("HandlerLeak")
    private void setHandler() {
        try {
            handler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    Bundle reply = msg.getData();
                    if (reply.getString("message").equals("success")) {
                        bottomLay.setVisibility(View.VISIBLE);
                    } else {
                        // Show Error In Snack Bar
                        try {
                            String message = reply.getString("message");
                            assert message != null;
                            snackbar = Snackbar
                                    .make(coordinatorLayout, message, Snackbar.LENGTH_INDEFINITE)
                                    .setAction(R.string.retry, new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            snackbar.dismiss();
                                            startIntentService();
                                        }
                                    });

                            // Changing message text color
                            snackbar.setActionTextColor(Color.RED);
                            snackbar.show();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            };
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void finishActivity() {
        overridePendingTransition(0, 0);
        finish();
    }
}
