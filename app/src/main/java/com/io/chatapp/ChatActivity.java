package com.io.chatapp;

import android.os.Bundle;

import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;
import com.io.chatapp.Model.Message;
import com.io.chatapp.Prevalent.Prevalent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;

import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.io.chatapp.ViewHolder.MessageViewHolder;

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
        setTitle(rivalName);

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
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.message_recyclerview);
        recyclerView.setLayoutManager(layoutManager);
        FirebaseRecyclerOptions<Message> options = new FirebaseRecyclerOptions.Builder<Message>()
                .setQuery(FirebaseDatabase.getInstance().getReference().child("Messages").orderByChild("time"), Message.class).build();
        final FirebaseRecyclerAdapter<Message, MessageViewHolder> adapter = new FirebaseRecyclerAdapter<Message, MessageViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MessageViewHolder messageViewHolder, int i, @NonNull Message message) {
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) messageViewHolder.userName.getLayoutParams();
                if (message.getSender_uid().equals(Prevalent.currentOnlineUser.getUid())) {
                    messageViewHolder.messageText.setText(message.getContent());
                    messageViewHolder.userName.setText(Prevalent.currentOnlineUser.getName());
                    messageViewHolder.messageTime.setText(DateFormat.format("HH:mm", message.getTime()));

                    params.addRule(RelativeLayout.END_OF, messageViewHolder.profileImage.getId());
                    messageViewHolder.userName.setLayoutParams(params);

                    params = (RelativeLayout.LayoutParams) messageViewHolder.messageTime.getLayoutParams();
                    params.addRule(RelativeLayout.END_OF, messageViewHolder.messageText.getId());
                    messageViewHolder.messageTime.setLayoutParams(params);

                } else if (message.getSender_uid().equals(rivalUid)) {
                    messageViewHolder.userName.setLayoutParams(params);
                    messageViewHolder.messageText.setText(message.getContent());
                    messageViewHolder.userName.setText(rivalName);
                    messageViewHolder.messageTime.setText(DateFormat.format("HH:mm", message.getTime()));

                    params.addRule(RelativeLayout.START_OF, messageViewHolder.profileImage.getId());
                    messageViewHolder.userName.setLayoutParams(params);

                    params = (RelativeLayout.LayoutParams) messageViewHolder.profileImage.getLayoutParams();
                    params.addRule(RelativeLayout.ALIGN_PARENT_END);
                    messageViewHolder.profileImage.setLayoutParams(params);

                    params = (RelativeLayout.LayoutParams) messageViewHolder.messageTime.getLayoutParams();
                    params.addRule(RelativeLayout.START_OF, messageViewHolder.messageText.getId());
                    messageViewHolder.messageTime.setLayoutParams(params);

                    params = (RelativeLayout.LayoutParams) messageViewHolder.messageText.getLayoutParams();
                    params.addRule(RelativeLayout.ALIGN_PARENT_END);
                    messageViewHolder.messageText.setLayoutParams(params);


                } else {
                    messageViewHolder.messageText.setVisibility(View.GONE);
                    messageViewHolder.messageTime.setVisibility(View.GONE);
                    messageViewHolder.userName.setVisibility(View.GONE);
                }
            }

            @NonNull
            @Override
            public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_item, parent, false);
                MessageViewHolder holder = new MessageViewHolder(view);
                return holder;
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            public void onItemRangeInserted(int positionStart, int itemCount) {
                recyclerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        Log.d("item count", "" + adapter.getItemCount());
                        recyclerView.scrollToPosition(adapter.getItemCount() - 1);
                        recyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                });
            }
        });


//        recyclerView.post(new Runnable() {
//            @Override
//            public void run() {
//
//            }
//        });

    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//        adapter.startListening();
//    }
//    @Override
//    protected void onStop() {
//        super.onStop();
//        adapter.stopListening();
//    }
}