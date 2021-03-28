package com.io.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
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

public class StatusActivity extends AppCompatActivity {
    private String password;
    private TextView text_email, text_status;
    private Button btn_send, btn_refresh;
    private MyToast myToast;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);
        password = getIntent().getStringExtra("password");
        text_email = (TextView) findViewById(R.id.text_email);
        text_status = (TextView) findViewById(R.id.text_status);
        btn_refresh = (Button) findViewById(R.id.btn_refresh);
        btn_send = (Button) findViewById(R.id.btn_send);
        myToast = new MyToast(StatusActivity.this);
        setInfo();
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_send.setEnabled(false);
                FirebaseAuth.getInstance().getCurrentUser().sendEmailVerification()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                btn_send.setEnabled(true);
                                if (task.isSuccessful()){
                                    myToast.show("Verification Email has sent to "+FirebaseAuth.getInstance().getCurrentUser().getEmail());
                                }
                                else{
                                    myToast.show("Error! Can not send verification email.");
                                }
                            }
                        });
            }
        });
        btn_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().getCurrentUser().reload()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                            if (firebaseUser.isEmailVerified()){
                                Redirect(firebaseUser.getUid());
//                                Intent intent = new Intent(StatusActivity.this,HomeActivity.class);
//                                startActivity(intent);
                            }
                            else{
                                setInfo();
                            }
                        }
                    });
            }
        });
    }
    private void setInfo() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        text_email.setText(new StringBuilder("").append(user.getEmail()));
        if (!user.isEmailVerified()) {
            text_status.setText(new StringBuilder("Status: Not verified"));
        } else {
            text_status.setText(new StringBuilder("Status: Verified"));
        }
    }
    private void  Redirect(final String uid){
        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();
        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("Users").child(uid).exists()){
                    User user = snapshot.child("Users").child(uid).getValue(User.class);
                    Paper.book().write(Prevalent.UserUidKey,uid);
                    Paper.book().write(Prevalent.UserPasswordKey,password);
                    Prevalent.currentOnlineUser = user;
                    myToast.show("Login successfully.");
                    Intent intent = new Intent(StatusActivity.this,HomeActivity.class);
                    startActivity(intent);
                }
                else{
                    myToast.show("Error");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
});

    }
}