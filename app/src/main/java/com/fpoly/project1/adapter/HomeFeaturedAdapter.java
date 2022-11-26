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
import com.fpoly.project1.R;
import com.fpoly.project1.firebase.model.Product;

import java.util.List;

public class HomeFeaturedAdapter extends RecyclerView.Adapter<HomeFeaturedAdapter.ViewHolder> {
    private final Context context;
    private final LayoutInflater layoutInflater;
    private final List<Product> products;

    public HomeFeaturedAdapter(Context context, List<Product> products) {
        this.context = context;
        this.products = products;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public HomeFeaturedAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(layoutInflater.inflate(R.layout.item_recycler_featured_products, parent));
    }

    @Override
    public void onBindViewHolder(@NonNull HomeFeaturedAdapter.ViewHolder holder, int position) {
        Product product = products.get(position);

        Glide.with(context).load(product.thumbnails.get(0)).into(holder.productThumbnail);
        holder.productName.setText(product.name);
        holder.productPrice.setText(product.price);

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
        TextView productPrice;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            productThumbnail = itemView.findViewById(R.id.item_iv_product_featured);
            productName = itemView.findViewById(R.id.item_txt_featured_name);
            productPrice = itemView.findViewById(R.id.item_txt_featured_price);
        }
    }
}
