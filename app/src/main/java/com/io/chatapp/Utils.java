package com.io.chatapp;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.io.chatapp.Model.MyToast;
import com.io.chatapp.Model.User;
import com.io.chatapp.Prevalent.Prevalent;

import de.hdodenhof.circleimageview.CircleImageView;

public class Utils {
    static public RequestOptions glideOptions = new RequestOptions()
            .placeholder(R.drawable.loading)
            .error(R.drawable.profile)
            .centerCrop();
    static public Boolean isNetworkAvailable(Application application) {
        ConnectivityManager connectivityManager = (ConnectivityManager) application.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Network nw = connectivityManager.getActiveNetwork();
            if (nw == null) return false;
            NetworkCapabilities actNw = connectivityManager.getNetworkCapabilities(nw);
            return actNw != null && (actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) || actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) || actNw.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH));
        } else {
            NetworkInfo nwInfo = connectivityManager.getActiveNetworkInfo();
            return nwInfo != null && nwInfo.isConnected();
        }
    }
    static public void LoadAccountData(final String uid, final Context context, final TextView userName, final CircleImageView profileImage) {
        final DatabaseReference RootRef = FirebaseDatabase.getInstance().getReference();
        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("Users").child(uid).exists()){
                    User user = snapshot.child("Users").child(uid).getValue(User.class);
                    Prevalent.currentOnlineUser = user;
                    if (userName!=null){
                        userName.setText(user.getName());
                        Glide.with(context).load(user.getImage()).apply(glideOptions).into(profileImage);
                    }
                    Log.d("uid","Change user status.");
                }
                else{
                    MyToast myToast = new MyToast(context);
                    Log.d("uid","Prevalent key is not remembered.");
//                    myToast.show("Unknown error.");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}
