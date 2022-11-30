package com.fpoly.project1.activity.account;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.facebook.login.LoginManager;
import com.fpoly.project1.R;
import com.fpoly.project1.activity.request_codes.RequestCode;
import com.fpoly.project1.firebase.SessionUser;
import com.google.firebase.auth.FirebaseAuth;

public class AccountPanel extends Fragment {
    public AccountPanel() {
        super(R.layout.fragment_profile);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Activity activity = (Activity) context;

        // back button?
        activity.findViewById(R.id.profile_iv_back).setOnClickListener(v -> {
            // TODO: add back function?
        });

        // set avatar
        Glide.with(context)
                .load(SessionUser.getAvatar())
                .into(((ImageView) activity.findViewById(R.id.profile_iv_avt)));

        // view information activity
        activity.findViewById(R.id.profile_cardView_use_information)
                .setOnClickListener(v -> startActivity(new Intent(context, AccountEditProfile.class))); // TODO add information activity

        // chat activity
        activity.findViewById(R.id.profile_cardView_chat)
                .setOnClickListener(v -> startActivity(new Intent(context, ChatSelector.class)));

        // order history activity
        activity.findViewById(R.id.profile_cardView_order_history)
                .setOnClickListener(v -> startActivity(new Intent(context, AccountOrderHistory.class)));

        // favorite products activity
        activity.findViewById(R.id.profile_cardView_favourite)
                .setOnClickListener(v -> startActivity(new Intent(context, AccountFavorites.class)));

        // session logout
        activity.findViewById(R.id.profile_cardView_logOut)
                .setOnClickListener(v -> {
                    FirebaseAuth.getInstance().signOut();
                    LoginManager.getInstance().logOut();

                    activity.finishActivity(RequestCode.LOGOUT);
                    activity.finish();
                });
    }
}
