package com.io.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.io.chatapp.Model.MyToast;

public class ForgetActivity extends AppCompatActivity {
    private Button resetBtn;
    private EditText emailInput;
    private MyToast myToast;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget);
        emailInput =  (EditText) findViewById(R.id.forget_email_input);
        resetBtn = (Button) findViewById(R.id.forget_enter_btn);
        myToast = new MyToast(ForgetActivity.this);
        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = emailInput.getText().toString();
                if (email.equals("")){
                    myToast.show("Please enter your registered email.");
                    emailInput.setBackgroundTintList( ColorStateList.valueOf( Color.rgb(255,128,171) ) );
                }
                else{
                    FirebaseAuth auth = FirebaseAuth.getInstance();
                    auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                myToast.show("Send password reset email to"+email);
                                Intent intent = new Intent(ForgetActivity.this,LoginActivity.class);
                                startActivity(intent);
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            myToast.show("Fail to send password reset email\nThe error is"+e.toString());
                        }
                    });
                }
            }
        });
    }
}