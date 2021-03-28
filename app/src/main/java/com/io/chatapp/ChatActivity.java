package com.io.chatapp;

import android.content.Intent;
import android.os.Bundle;

import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.io.chatapp.Model.Message;
import com.io.chatapp.Prevalent.Prevalent;

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
    private Button send_btn;
    private String rivalUid,rivalName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        rivalUid = getIntent().getStringExtra("rivalUid");
        rivalName = getIntent().getStringExtra("rivalName");
        send_btn = (Button) findViewById(R.id.send_btn);
        Log.d("btn",""+send_btn);
        send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText input = (EditText) findViewById(R.id.input_text);
                if (!input.getText().toString().equals("")){
                    FirebaseDatabase.getInstance().
                            getReference("Messages").
                            push().
                            setValue(new Message(input.getText().toString(),
                                    Prevalent.currentOnlineUser.getUid(),rivalUid));
                    Log.d("btn","send message");
                    input.setText("");
                }
            }
        });
        displayChatMessage();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId()==android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void displayChatMessage() {
        final ListView listOfMessage = (ListView) findViewById(R.id.message_listview);
        Query query = FirebaseDatabase.getInstance().getReference().child("Messages").orderByChild("time");
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
                Log.d("current uid",Prevalent.currentOnlineUser.getUid());
                Log.d("rival uid",rivalUid);
                Log.d("sender uid",model.getSender_uid());
                Log.d("receiver uid",model.getReceiver_uid());
                if (model.getSender_uid().equals(Prevalent.currentOnlineUser.getUid())){
                    messageText.setText(model.getContent());
                    messageUser.setText(Prevalent.currentOnlineUser.getName());
                    messageTime.setText(DateFormat.format("HH:mm",model.getTime()));
                }
                else if (model.getSender_uid().equals(rivalUid)){
                    messageText.setText(model.getContent());
                    messageUser.setText(rivalName);
                    messageTime.setText(DateFormat.format("HH:mm",model.getTime()));
                }
                else{
                    messageText.setVisibility(View.GONE);
                    messageUser.setVisibility(View.GONE);
                    messageTime.setVisibility(View.GONE);
                }
            }
//            @Override
//            public int getItemViewType(int position) {
//                Message message = this.getItem(position);
//                if (message.getSender_uid().equals(Prevalent.currentOnlineUser.getUid())) {
//                    return 0;
//                }
//                return -1;
//
//            }
        };

        listOfMessage.setAdapter(adapter);
        listOfMessage.post(new Runnable() {
            @Override
            public void run() {
                listOfMessage.setSelection(listOfMessage.getAdapter().getCount()-1);
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