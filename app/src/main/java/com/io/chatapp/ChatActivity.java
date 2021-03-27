package com.io.chatapp;

import android.os.Bundle;

import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.io.chatapp.Model.Message;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import android.text.format.DateFormat;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

public class ChatActivity extends AppCompatActivity {

    private FirebaseListAdapter<Message> adapter;
    private  static  int SIGN_RESULT_CODE = 1;
    Button send_btn ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        send_btn = (Button) findViewById(R.id.send_btn);
        Log.d("btn",""+send_btn);
        send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText input = (EditText) findViewById(R.id.input_text);
                if (!input.getText().toString().equals("")){
                    FirebaseDatabase.getInstance().
                            getReference("Message").
                            push().
                            setValue(new Message(input.getText().toString(),
                                    "123"));
                    Log.d("btn","send message");
                    input.setText("");


                }

            }
        });
        displayChatMessage();


    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);

    }

    private void displayChatMessage() {
        final ListView listOfMessage = (ListView) findViewById(R.id.message_listview);
        Query query = FirebaseDatabase.getInstance().getReference().child("Message").orderByChild("message_time");
        FirebaseListOptions<Message> options =
                new FirebaseListOptions.Builder<Message>()
                        .setQuery(query, Message.class)
                        .setLayout(R.layout.message_item)
                        .build();
        adapter = new FirebaseListAdapter<Message>(options){
            @Override
            protected void populateView(View v, Message model, int position) {
                TextView messageText,messageUser,messageTime;
                messageText = (TextView) v.findViewById(R.id.message_text);
                messageUser = (TextView) v.findViewById(R.id.message_user);
                messageTime = (TextView) v.findViewById(R.id.message_time);
                Log.d("text",""+model.getMessageText());
                messageText.setText(model.getMessageText());
                messageUser.setText(model.getMessageUser());
              messageTime.setText(DateFormat.format("HH:mm",model.getMessageTime()));

            }
        };
        listOfMessage.setAdapter(adapter);
        listOfMessage.post(new Runnable() {
            @Override
            public void run() {
                listOfMessage.setSelection(10);
            }
        });



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