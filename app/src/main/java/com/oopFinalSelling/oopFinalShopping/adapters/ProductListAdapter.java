package com.oopFinalSelling.oopFinalShopping.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.oopFinalSelling.oopFinalShopping.R;
import com.oopFinalSelling.oopFinalShopping.activities.ProductDetails;
import com.oopFinalSelling.oopFinalShopping.database.dataManager;
import com.oopFinalSelling.oopFinalShopping.objects.Product;

import java.util.List;

public class ProductListAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;
    private List<Product> productList;
    dataManager dataManager;

    public ProductListAdapter(Context context, List<Product> productList) {
        this.context = context;
        this.productList = productList;
        inflater = (LayoutInflater) context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return productList.size();
    }

    @Override
    public Object getItem(int i) {
        return productList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @SuppressLint({"ViewHolder", "InflateParams"})
    @Override
    public View getView(final int position, View view, ViewGroup viewGroup) {
        // TODO Auto-generated method stub
        final Holder holder = new Holder();
        View rowView;

        rowView = inflater.inflate(R.layout.product_grid_item, null);
        holder.name = rowView.findViewById(R.id.name);
        holder.price = rowView.findViewById(R.id.price);
        holder.image = rowView.findViewById(R.id.image);

        holder.name.setText(productList.get(position).getName());
        holder.price.setText(productList.get(position).getPrice_range());

//        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
//        StorageReference photoReference= storageReference.child("uploads/1623441052256.jpg");
//
//        final long ONE_MEGABYTE = 1024 * 1024;
//        photoReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
//            @Override
//            public void onSuccess(byte[] bytes) {
//                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
//                holder.image.setImageBitmap(bmp);
//
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception exception) {
//
//            }
//        });



        // Product Item Click
        holder.itemLay = rowView.findViewById(R.id.itemLay);
        holder.itemLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ProductDetails.class);
                intent.putExtra("ProductId", productList.get(position).getId());
                context.startActivity(intent);
                Activity activity = (Activity) context;
                activity.overridePendingTransition(0,0);
            }
        });

        return rowView;
    }

    public class Holder {
        RelativeLayout itemLay;
        TextView name, price;
        ImageView image;
    }
}
