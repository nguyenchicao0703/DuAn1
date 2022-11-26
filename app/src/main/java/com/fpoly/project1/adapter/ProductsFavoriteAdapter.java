package com.fpoly.project1.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.fpoly.project1.R;
import com.fpoly.project1.firebase.controller.ControllerCustomer;
import com.fpoly.project1.firebase.controller.ControllerProduct;
import com.fpoly.project1.firebase.controller.ControllerProductCategory;
import com.fpoly.project1.firebase.model.Customer;
import com.fpoly.project1.firebase.model.Product;
import com.fpoly.project1.firebase.model.ProductCategory;

import java.util.ArrayList;
import java.util.List;

public class ProductsFavoriteAdapter extends RecyclerView.Adapter<ProductsFavoriteAdapter.ViewHolder> {
    private final Context context;
    private final LayoutInflater layoutInflater;
    private final ControllerProduct controllerProduct = new ControllerProduct();
    private final ControllerCustomer controllerCustomer = new ControllerCustomer();
    private final ArrayList<ProductCategory> categories = new ControllerProductCategory().getAllSync();
    private final List<String> favoriteList;
    private final Customer customer;

    public ProductsFavoriteAdapter(Context context, Customer account) {
        this.context = context;
        this.customer = account;
        this.layoutInflater = LayoutInflater.from(context);

        favoriteList = account.favoriteIds;
    }

    @NonNull
    @Override
    public ProductsFavoriteAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(layoutInflater.inflate(R.layout.item_recycler_favorite, parent));
    }

    @Override
    public void onBindViewHolder(@NonNull ProductsFavoriteAdapter.ViewHolder holder, int position) {
        String favoriteId = favoriteList.get(position);
        Product product = controllerProduct.getSync(favoriteId);

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
        holder.favoriteStatus.setOnClickListener(v -> {
            customer.favoriteIds.remove(position);

            if (controllerCustomer.setSync(customer, true)) {
                Toast.makeText(context, "Removed from favorites", Toast.LENGTH_SHORT).show();

                notifyItemRemoved(position);
            } else {
                Toast.makeText(context, "Unable to remove from favorites", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return favoriteList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView productThumbnail;
        TextView productName;
        TextView productType;
        TextView productPrice;
        ImageView favoriteStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            productThumbnail = itemView.findViewById(R.id.item_iv_products_favorite);
            productName = itemView.findViewById(R.id.item_txt_favorite_name);
            productType = itemView.findViewById(R.id.item_txt_favorite_type);
            productPrice = itemView.findViewById(R.id.item_txt_favorite_price);
            favoriteStatus = itemView.findViewById(R.id.item_iv_favorite);
        }
    }
}
