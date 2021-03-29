package com.io.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;
import com.io.chatapp.Model.User;
import com.io.chatapp.Prevalent.Prevalent;

import com.io.chatapp.ViewHolder.UserViewHolder;

import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;

public class HomeActivity extends AppCompatActivity {
    private TextView userName;
    private Button logoutBtn;
    private ProgressDialog loadingBar;
    private String userUidKey;
    private String userPasswordKey;
    private CircleImageView profileImage;
    private FirebaseRecyclerAdapter<User, UserViewHolder> adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Paper.init(this);
        userUidKey = Paper.book().read(Prevalent.UserUidKey);
        userPasswordKey = Paper.book().read(Prevalent.UserPasswordKey);
        userName = (TextView) findViewById(R.id.home_user_name);
        loadingBar = new ProgressDialog(this);
        profileImage = (CircleImageView) findViewById(R.id.home_profile_image);
        Log.d("uid",userUidKey);
        logoutBtn = (Button) findViewById(R.id.go_to_login);
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Paper.book().destroy();
                Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this,ChangeProfileImageActivity.class);
                startActivity(intent);
            }
        });
        if (userUidKey!=null){
            Utils.LoadAccountData(userUidKey,HomeActivity.this,userName,profileImage);
        }
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.user_recyclerview);
        displayUsers(recyclerView);
    }

    private void displayUsers(final  RecyclerView recyclerView) {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        final FirebaseRecyclerOptions<User> options = new FirebaseRecyclerOptions.Builder<User>()
                .setQuery(FirebaseDatabase.getInstance().getReference().child("Users").orderByChild("name"),User.class).build();
        adapter = new FirebaseRecyclerAdapter<User, UserViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull UserViewHolder userViewHolder, int i, @NonNull final User user) {
                Log.d("reload user name",user.getName());
                Log.d("reload current name",Prevalent.currentOnlineUser.getName());
                if (user.getUid().equals(userUidKey)){
                    userViewHolder.userName.setVisibility(View.GONE);
                    userViewHolder.profileImage.setVisibility(View.GONE);
                    return ;
                }
                userViewHolder.userName.setText(user.getName());
                Glide.with(HomeActivity.this).load(user.getImage()).apply(Utils.glideOptions).into(userViewHolder.profileImage);
                userViewHolder.relativeLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(HomeActivity.this,ChatActivity.class);
                        intent.putExtra("rival",user);
                        startActivity(intent);
                    }
                });
            }
            @NonNull
            @Override
            public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item,parent,false);
                UserViewHolder holder = new UserViewHolder(view);
                return holder;
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }
    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }
    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}