package com.oopFinalSelling.oopFinalShopping.dbUpdater;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import android.util.Log;

import com.oopFinalSelling.oopFinalShopping.R;
import com.oopFinalSelling.oopFinalShopping.database.DB_Handler;
import com.oopFinalSelling.oopFinalShopping.objects.Category;
import com.oopFinalSelling.oopFinalShopping.objects.Product;
import com.oopFinalSelling.oopFinalShopping.objects.ProductRank;
import com.oopFinalSelling.oopFinalShopping.objects.Ranking;
import com.oopFinalSelling.oopFinalShopping.objects.ResponseJSON;
import com.oopFinalSelling.oopFinalShopping.objects.Variant;
import com.oopFinalSelling.oopFinalShopping.utils.Util;
import com.oopFinalSelling.oopFinalShopping.localServ.RetrofitBuilder;
import com.oopFinalSelling.oopFinalShopping.localServ.RetrofitInterface;

import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class dbUpdateAdapter extends IntentService {

    DB_Handler db_handler;
    Intent intent;

    public dbUpdateAdapter(String name) {
        super(name);
    }

    public dbUpdateAdapter() {
        super("SyncDBService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        db_handler = new DB_Handler(this);
        fetchData();
        this.intent = intent;
    }

    // Fetch Data From URL
    private void fetchData() {
        // Initialize Retrofit
        RetrofitBuilder retrofitBuilder = new RetrofitBuilder(this);
        OkHttpClient httpClient = retrofitBuilder.setClient();
        Retrofit retrofit = retrofitBuilder.retrofitBuilder(httpClient);
        RetrofitInterface retrofitInterface = retrofit.create(RetrofitInterface.class);

        // Call Web Service
        Call<ResponseJSON> call = retrofitInterface.fetchData();
        call.enqueue(new Callback<ResponseJSON>() {
            @Override
            public void onResponse(Call<ResponseJSON> call, Response<ResponseJSON> response) {
                try {
                    if (response.body() != null) {
                        processData(response.body());
                    }
                } catch (Exception ignore) {
                    reply(getResources().getString(R.string.Error500));
                }
            }

            @Override
            public void onFailure(Call<ResponseJSON> call, Throwable t) {
                reply(Util.getErrorMessage(t, dbUpdateAdapter.this));
            }
        });
    }

    // Process JSON and Save In Local DB
    private void processData(ResponseJSON responseJSON) {
        try {

            // Get Categories
            List<Category> categoryList = responseJSON.getCategories();
            for (int i = 0; i < categoryList.size(); i++) {

                int CategoryID = responseJSON.getCategories().get(i).getId();
                String CategoryName = responseJSON.getCategories().get(i).getName();

                // insert category into local DB
                db_handler.insertCategories(CategoryID, CategoryName);

                // Get Products
                List<Product> productList = responseJSON.getCategories().get(i).getProducts();
                for (int j = 0; j < productList.size(); j++) {
                    int ProductID = productList.get(j).getId();
                    String ProductName = productList.get(j).getName();
                    String Date = productList.get(j).getDateAdded();

                    // insert products into local DB
                    db_handler.insertProducts(ProductID, CategoryID, ProductName, Date);

                    // Get Variants
                    List<Variant> variantList = productList.get(j).getVariants();
                    for (int p = 0; p < variantList.size(); p++) {
                        int VariantID = variantList.get(p).getId();
                        String Size = null;
                        String Color = variantList.get(p).getColor();
                        String Price = String.valueOf(variantList.get(p).getPrice());

                        try {
                            // Size May Produce NullPointerException
                            Size = variantList.get(p).getSize().toString();
                        } catch (NullPointerException ignore) {
                        }

                        // insert variants into local DB
                        db_handler.insertVariants(VariantID, Size, Color, Price, ProductID);
                    }
                }

                // Get Child Categories
                List<Integer> childCategories = categoryList.get(i).getChildCategories();
                for (int k = 0; k < childCategories.size(); k++) {
                    int SubcategoryID = childCategories.get(k);

                    // insert childs into subcategory mapping
                    db_handler.insertChildCategoryMapping(CategoryID, SubcategoryID);
                }
            }

            // Get Rankings
            List<Ranking> rankingList = responseJSON.getRankings();
            for (int i = 0; i < rankingList.size(); i++) {
                // Get Products Rank List
                List<ProductRank> productRankList = rankingList.get(i).getProducts();
                for (int j = 0; j < productRankList.size(); j++) {
                    try {
                        int id = productRankList.get(j).getId();
                        switch (i) {
                            case 0: // Most Viewed Products
                                int viewCount = productRankList.get(j).getViewCount();

                                // update product table
                                db_handler.updateCounts(DB_Handler.VIEW_COUNT, viewCount, id);
                                break;

                            case 1: // Most Ordered Products
                                int orderCount = productRankList.get(j).getOrderCount();

                                // update product table
                                db_handler.updateCounts(DB_Handler.ORDER_COUNT, orderCount, id);
                                break;

                            case 2: // Most Shared Products
                                int shareCount = productRankList.get(j).getShares();

                                // update product table
                                db_handler.updateCounts(DB_Handler.SHARE_COUNT, shareCount, id);
                                break;
                        }
                    } catch (Exception ignore) {
                    }
                }
            }

            reply("success");
            Log.i("DB Sync","success");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Reply Message
    private void reply(String value)
    {
        Bundle bundle = intent.getExtras();
        bundle.putString("message",value);
        if (bundle != null) {
            Messenger messenger = (Messenger) bundle.get("messenger");
            Message msg = Message.obtain();
            msg.setData(bundle); //put the data here
            try {
                messenger.send(msg);
            } catch (RemoteException e) {
                Log.i("error", "error");
            }
        }
    }
}
