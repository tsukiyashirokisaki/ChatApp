package com.io.chatapp;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.io.chatapp.Prevalent.Prevalent;

import io.paperdb.Paper;

public class HomeActivity extends AppCompatActivity {
    private TextView userName;
    private Button chat_btn,login_btn;
    private ProgressDialog loadingBar;
    private String userUidKey;
    private String userPasswordKey;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Paper.init(this);
        userName = (TextView) findViewById(R.id.home_user_name);
        loadingBar = new ProgressDialog(this);
        userUidKey = Paper.book().read(Prevalent.UserUidKey);
        userPasswordKey = Paper.book().read(Prevalent.UserPasswordKey);
        Intent intent = new Intent(HomeActivity.this,LoginActivity.class);
        if (userUidKey!=null){
            if (!userUidKey.equals("default_key")){
                Utils.LoadAccountData(userUidKey,HomeActivity.this,userName);
            }
            else{
                startActivity(intent);
            }
        }
        else{
            startActivity(intent);
        }

        chat_btn = (Button) findViewById(R.id.chat_btn);
        login_btn = (Button) findViewById(R.id.go_to_login);
        chat_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this,ChatActivity.class);
                startActivity(intent);
            }
        });
        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

    }

}