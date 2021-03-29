package com.io.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.io.chatapp.Model.MyToast;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {
    private EditText inputName,inputEmail,inputPassword;
    private MyToast myToast;
    private ProgressDialog loadingBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        myToast = new MyToast(RegisterActivity.this);
        loadingBar = new ProgressDialog(this);
        inputName = (EditText) findViewById(R.id.register_name_input);
        inputEmail = (EditText) findViewById(R.id.register_email_input);
        inputPassword = (EditText) findViewById(R.id.register_password_input);
        Button register_btn = (Button) findViewById(R.id.register_enter_btn);
        final FirebaseAuth mAuth = FirebaseAuth.getInstance();
        register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreateAccount();
            }
        });
    }
    private void CreateAccount() {
        String name = inputName.getText().toString();
        String email = inputEmail.getText().toString();
        String password = inputPassword.getText().toString();
        if (TextUtils.isEmpty(name)){
            myToast.show("Please write your name.");
            inputName.setBackgroundTintList( ColorStateList.valueOf( Color.rgb(255,128,171) ) );
            }
        if (TextUtils.isEmpty(email)){
            myToast.show("Please write your email address.");
            inputEmail.setBackgroundTintList( ColorStateList.valueOf( Color.rgb(255,128,171) ) );
            }
        if (TextUtils.isEmpty(password)){
            myToast.show("Please write your password.");
            inputPassword.setBackgroundTintList( ColorStateList.valueOf( Color.rgb(255,128,171) ) );}
      else if (!TextUtils.isEmpty(name) & !TextUtils.isEmpty(email) & ! TextUtils.isEmpty(password)){
            loadingBar.setTitle("Creating your account.");
            loadingBar.setMessage("Please wait.");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();
            ValidationFirebaseAuth(name,email,password);
        }
    }
    private void ValidationFirebaseAuth(final String name, final String email, final String password) {
        final FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(RegisterActivity.this, "success", Toast.LENGTH_SHORT).show();
                            createInfo(name,mAuth.getCurrentUser().getUid(),email);
                            mAuth.getCurrentUser().sendEmailVerification()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                myToast.show("Verification Email Sent to: "+FirebaseAuth.getInstance().getCurrentUser().getEmail());
                                            }
                                            else{
                                                myToast.show("Fail to send verification email.");
                                            }
                                        }
                                    });
                            Intent intent = new Intent(RegisterActivity.this,StatusActivity.class);
                            intent.putExtra("password",password);
                            startActivity(intent);
                        }
                        else {
                            loadingBar.dismiss();
                            try {
                                throw task.getException();
                            } catch(FirebaseAuthWeakPasswordException e) {
                                myToast.show("The password should contain at least 6 characters.");
                            } catch(FirebaseAuthInvalidCredentialsException e) {
                                myToast.show("Check your Internet.");
                            }catch(FirebaseAuthUserCollisionException e) {
                                myToast.show("This account already exists.");
                            } catch(Exception e) {

                                myToast.show("Unknown Error\n"+e.toString());
                            }
                        }
                    }
                });
    }
    private void createInfo(final String name, final String uid,final  String email) {
        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();
        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!(dataSnapshot.child("Users").child(uid).exists())){
                    HashMap<String,Object> userdataMap = new HashMap<>();
                    userdataMap.put("name",name);
                    userdataMap.put("email",email);
                    userdataMap.put("uid",uid);
                    RootRef.child("Users").child(uid).updateChildren(userdataMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        Toast.makeText(RegisterActivity.this,"Success!",Toast.LENGTH_SHORT);
                                        loadingBar.dismiss();
                                    }
                                    else{
                                        loadingBar.dismiss();
                                        Toast.makeText(RegisterActivity.this, "Network Error. Please try again.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
                else{
                    myToast.show("This account already exists.");
                    loadingBar.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

}