package com.fpoly.project1.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.fpoly.project1.R;
import com.fpoly.project1.firebase.controller.ControllerProductCategory;
import com.fpoly.project1.firebase.model.Product;
import com.fpoly.project1.firebase.model.ProductCategory;

import java.util.ArrayList;
import java.util.List;

public class HomeMenuAdapter extends RecyclerView.Adapter<HomeMenuAdapter.ViewHolder> {
    private final Context context;
    private final LayoutInflater layoutInflater;
    private final List<Product> products;
    private final ArrayList<ProductCategory> categories = new ControllerProductCategory().getAllSync();

    public HomeMenuAdapter(Context context, List<Product> products) {
        this.context = context;
        this.products = products;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public HomeMenuAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(layoutInflater.inflate(R.layout.item_recycler_menu, parent));
    }

    @Override
    public void onBindViewHolder(@NonNull HomeMenuAdapter.ViewHolder holder, int position) {
        Product product = products.get(position);

        Glide.with(context).load(product.thumbnails.get(0)).into(holder.productThumbnail);
        holder.productName.setText(product.name);
        holder.productPrice.setText(product.price);
        holder.productType.setText(
                ((ProductCategory) List.of(categories.stream().filter(
                        productCategory -> productCategory.__id.equals(product.categoryId)
                )).get(0)).name
        );

        holder.itemView.setOnClickListener(v -> {
            // TODO implement product view activity
        });
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView productThumbnail;
        TextView productName;
        TextView productType;
        TextView productPrice;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            productThumbnail = itemView.findViewById(R.id.item_iv_products_menu);
            productName = itemView.findViewById(R.id.item_txt_menu_name);
            productType = itemView.findViewById(R.id.item_txt_menu_type);
            productPrice = itemView.findViewById(R.id.item_txt_menu_price);
        }
    }
}
