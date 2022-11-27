package com.fpoly.project1.activity.home;

import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.fpoly.project1.R;
import com.fpoly.project1.activity.home.adapter.FeaturedAdapter;
import com.fpoly.project1.activity.home.adapter.MenuAdapter;
import com.fpoly.project1.firebase.controller.ControllerCustomer;
import com.fpoly.project1.firebase.controller.ControllerProduct;

public class HomeFragment extends Fragment {
    private final ControllerCustomer controllerCustomer = new ControllerCustomer();
    private final ControllerProduct controllerProduct = new ControllerProduct();
    private TextView statusBarWelcomeView;

    public HomeFragment() {
        super(R.layout.fragment_home);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        statusBarWelcomeView = requireActivity().findViewById(R.id.home_txt_statusBar_title);

        String email = requireActivity().getSharedPreferences("cheetah", Context.MODE_PRIVATE).getString("email", null);
        controllerCustomer.getAllSync().forEach(account -> {
            if (account.emailAddress.equals(email)) {
                statusBarWelcomeView.setText("Hello, " + account.fullName.split("/ /g")[-1]);
            }
        });

        RecyclerView menuView = requireActivity().findViewById(R.id.home_recyclerView_product_menu);
        menuView.setAdapter(new MenuAdapter(requireContext(), controllerProduct.getAllSync()));

        // TODO implement featured products by purchase amounts for past week (or a period of time)
        RecyclerView featuredView = requireActivity().findViewById(R.id.home_recyclerView_product_featured);
        featuredView.setAdapter(new FeaturedAdapter(requireContext(), controllerProduct.getAllSync()));
    }
}
