package com.fpoly.project1.activity.account;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.fpoly.project1.R;
import com.fpoly.project1.activity.account.adapter.FavoriteProductsAdapter;
import com.fpoly.project1.firebase.Firebase;
import com.fpoly.project1.firebase.controller.ControllerCustomer;
import com.fpoly.project1.firebase.controller.ControllerProduct;
import com.fpoly.project1.firebase.model.Customer;
import com.fpoly.project1.firebase.model.Product;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FavoriteProducts extends AppCompatActivity {
    private final List<Product> productList = new ArrayList<>();
    private final ControllerCustomer controllerCustomer = new ControllerCustomer();
    private final ControllerProduct controllerProduct = new ControllerProduct();
    private RecyclerView favoriteRecycler;
    private EditText favoriteSearchBox;
    private FavoriteProductsAdapter favoriteProductsAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_product);

        Customer customer = controllerCustomer.getSync(Firebase.getSessionId());
        customer.favoriteIds.forEach(productId -> productList.add(controllerProduct.getSync(productId)));

        favoriteSearchBox = findViewById(R.id.favorite_edt_search);
        favoriteSearchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String searchQuery = favoriteSearchBox.getText().toString();
                if (searchQuery.length() != 0) {
                    List<Product> matchingProductList = productList.stream().filter(product ->
                            product.name.equalsIgnoreCase(searchQuery)
                    ).collect(Collectors.toList());

                    favoriteProductsAdapter.updateList(matchingProductList);
                } else {
                    favoriteProductsAdapter.updateList(productList);
                }
            }
        });

        favoriteProductsAdapter = new FavoriteProductsAdapter(
                this,
                productList,
                controllerCustomer.getSync(Firebase.getSessionId())
        );
        favoriteRecycler = findViewById(R.id.favorite_recycler_favorite);
        favoriteRecycler.setAdapter(favoriteProductsAdapter);
    }
}
