package com.oopFinalSelling.oopFinalShopping.database;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class dataManager {

    private SharedPreferences SharedPref;
    private SharedPreferences.Editor SharedPref_Editor;

    @SuppressLint("CommitPrefEdits")
    public dataManager(Context context) {
        this.SharedPref = context.getSharedPreferences("com.preethzcodez.ecommerceexample.database.PREFERENCE.", Activity.MODE_PRIVATE);
        this.SharedPref_Editor = SharedPref.edit();
    }

    public void saveData(String Key, String Value) {
        SharedPref_Editor.putString(Key, Value);
        SharedPref_Editor.commit();
    }

    public String getData(String Key) {
        return SharedPref.getString(Key, "");
    }

    public void clearPreferences() {
        SharedPref_Editor.clear();
        SharedPref_Editor.commit();
    }
}
