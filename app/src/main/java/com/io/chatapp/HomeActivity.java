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

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;
import com.io.chatapp.Model.User;
import com.io.chatapp.Prevalent.Prevalent;

import ViewHolder.UserViewHolder;
import io.paperdb.Paper;

public class HomeActivity extends AppCompatActivity {
    private TextView userName;
    private Button chat_btn,logout_btn;
    private ProgressDialog loadingBar;
    private String userUidKey;
    private String userPasswordKey;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Paper.init(this);
        userUidKey = Paper.book().read(Prevalent.UserUidKey);
        userPasswordKey = Paper.book().read(Prevalent.UserPasswordKey);
        userName = (TextView) findViewById(R.id.home_user_name);
        loadingBar = new ProgressDialog(this);
        Log.d("uid",userUidKey);
        chat_btn = (Button) findViewById(R.id.chat_btn);
        logout_btn = (Button) findViewById(R.id.go_to_login);
        chat_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this,ChatActivity.class);
                startActivity(intent);
            }
        });
        logout_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Paper.book().destroy();
                Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

    }
    @Override
    protected void onStart () {
        super.onStart();
        if (userUidKey!=null){
            Utils.LoadAccountData(userUidKey,HomeActivity.this,userName);
        }
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.listOfUser);
        displayUsers(recyclerView);
    }

    private void displayUsers(final  RecyclerView recyclerView) {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        FirebaseRecyclerOptions<User> options = new FirebaseRecyclerOptions.Builder<User>()
                .setQuery(FirebaseDatabase.getInstance().getReference().child("Users").orderByChild("name"),User.class).build();
        FirebaseRecyclerAdapter<User, UserViewHolder> adapter = new FirebaseRecyclerAdapter<User, UserViewHolder>(options) {
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
                userViewHolder.profileImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(HomeActivity.this,ChatActivity.class);
                        intent.putExtra("rivalUid",user.getUid());
                        intent.putExtra("rivalName",user.getName());
                        startActivity(intent);
                    }
                });
            }
            @NonNull
            @Override
            public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sender_item,parent,false);
                UserViewHolder holder = new UserViewHolder(view);
                return holder;
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();



    }
}