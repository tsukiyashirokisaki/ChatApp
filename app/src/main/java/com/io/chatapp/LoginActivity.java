package com.io.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.io.chatapp.Model.MyToast;
import com.io.chatapp.Model.User;
import com.io.chatapp.Prevalent.Prevalent;

import io.paperdb.Paper;

public class LoginActivity extends AppCompatActivity {
    private Button loginBtn;
    private EditText inputEmail,inputPassword;
    private ProgressDialog loadingBar;
    private MyToast myToast;
    private String userUidKey,userPasswordKey;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Paper.init(this);
        loginBtn = (Button) findViewById(R.id.login_enter_btn);
        inputEmail = (EditText) findViewById(R.id.login_email_input);
        inputPassword = (EditText) findViewById(R.id.login_password_input);
        loadingBar = new ProgressDialog(this);
        myToast = new MyToast(LoginActivity.this);
        TextView forgetBtn = (TextView) findViewById(R.id.forget_btn);
        TextView registerBtn = (TextView) findViewById(R.id.register_btn);
        userUidKey = Paper.book().read(Prevalent.UserUidKey);
        userPasswordKey = Paper.book().read(Prevalent.UserPasswordKey);
        Intent intent = new Intent(LoginActivity.this,HomeActivity.class);
        if (userUidKey!=null & userPasswordKey!=null){
            if (!userUidKey.equals("default_key")){
                startActivity(intent);
            }
        }
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Login();
            }
        });
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });
        forgetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this,ForgetActivity.class);
                startActivity(intent);
            }
        });
    }
    private void Login() {
        final String email = inputEmail.getText().toString();
        final String password = inputPassword.getText().toString();
        if (TextUtils.isEmpty(email)){
            myToast.show("Please write your email address.");
            inputEmail.setBackgroundTintList( ColorStateList.valueOf( Color.rgb(255,128,171) ) );
        }
        if (TextUtils.isEmpty(password)){
            myToast.show("Please write your password.");
            inputPassword.setBackgroundTintList( ColorStateList.valueOf( Color.rgb(255,128,171) ) );}
        else if (!TextUtils.isEmpty(email) & ! TextUtils.isEmpty(password)){
            loadingBar.setTitle("Prepare to login.");
            loadingBar.setMessage("Please wait, we are checking your credentials.");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();
            ValidationFirebaseAuth(email,password);
        }
    }
    private void ValidationFirebaseAuth(final String email, final String password) {
        final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                            if(!firebaseUser.isEmailVerified()){
                                myToast.show("Please check the verification email.");
                            }
                            else{
                                Paper.book().write(Prevalent.UserUidKey,firebaseUser.getUid());
                                Paper.book().write(Prevalent.UserPasswordKey,password);
                                Log.d("uid","write password"+firebaseUser.getUid()+Prevalent.UserUidKey);
                                Intent intent = new Intent(LoginActivity.this,HomeActivity.class);
                                startActivity(intent);
                            }
                        }
                        else{
                            loadingBar.dismiss();
                            try{
                                String errorCode = ((FirebaseAuthException) task.getException()).getErrorCode();
                                switch (errorCode) {
                                    case "ERROR_INVALID_EMAIL":
                                        myToast.show("Invalid email.");
                                        break;
                                    case "ERROR_USER_NOT_FOUND":
                                        myToast.show("This user does not exits.");
                                        break;
                                    case "ERROR_WRONG_PASSWORD":
                                        myToast.show("密碼錯誤或無此用戶");
                                        break;
                                    default:
                                        myToast.show("Unknown recognized error.\n"+errorCode);
                                        break;
                                }
                            }
                            catch (Error e){
                                myToast.show("Unknown error.");
                            }
                        }
                    }
                });
    }





}