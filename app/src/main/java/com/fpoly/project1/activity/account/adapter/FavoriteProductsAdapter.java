package com.fpoly.project1.activity.account.adapter;

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
import com.fpoly.project1.firebase.controller.ControllerProductCategory;
import com.fpoly.project1.firebase.model.Customer;
import com.fpoly.project1.firebase.model.Product;
import com.fpoly.project1.firebase.model.ProductCategory;

import java.util.ArrayList;
import java.util.List;

public class FavoriteProductsAdapter extends RecyclerView.Adapter<FavoriteProductsAdapter.ViewHolder> {
    private final Context context;
    private final LayoutInflater layoutInflater;
    private final ControllerCustomer controllerCustomer = new ControllerCustomer();
    private final ArrayList<ProductCategory> categories = new ControllerProductCategory().getAllSync();
    private final Customer customer;
    private List<Product> productList;

    public FavoriteProductsAdapter(Context context, List<Product> products, Customer customer) {
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
        this.productList = products;
        this.customer = customer;
    }

    @NonNull
    @Override
    public FavoriteProductsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(layoutInflater.inflate(R.layout.item_recycler_favorite, parent));
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteProductsAdapter.ViewHolder holder, int position) {
        Product product = productList.get(position);

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
            productList.remove(position); // local list
            customer.favoriteIds.remove(position); // account list

            if (controllerCustomer.setSync(customer, true)) { // update account
                Toast.makeText(context, "Removed from favorites", Toast.LENGTH_SHORT).show();

                notifyItemRemoved(position);
            } else {
                Toast.makeText(context, "Unable to remove from favorites", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public void updateList(List<Product> newList) {
        notifyItemRangeRemoved(0, this.productList.size());

        this.productList = newList;

        notifyItemRangeInserted(0, newList.size());
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
