package com.io.chatapp;

import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;
import com.io.chatapp.Model.Message;
import com.io.chatapp.Model.MyToast;
import com.io.chatapp.Model.User;
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
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.io.chatapp.ViewHolder.MessageViewHolder;

import static com.io.chatapp.Utils.glideOptions;
import static com.io.chatapp.Utils.isNetworkAvailable;

public class ChatActivity extends AppCompatActivity {

    private Button send_btn;
    private User rival;
    private MyToast myToast;
    private String path1,path2;
    private Boolean comp;
    private FirebaseRecyclerAdapter<Message, MessageViewHolder> adapter;
    private int mLastContentHeight;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        rival = (User) getIntent().getSerializableExtra("rival");
        setTitle(rival.getName());
        send_btn = (Button) findViewById(R.id.send_btn);
        myToast = new MyToast(ChatActivity.this);
        comp = Prevalent.currentOnlineUser.getUid().compareTo(rival.getUid())>0;
        if (comp){
            path1 = Prevalent.currentOnlineUser.getUid();
            path2 = rival.getUid();
        }
        else{
            path1 = rival.getUid();
            path2 = Prevalent.currentOnlineUser.getUid();
        }
        send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText input = (EditText) findViewById(R.id.input_text);
                if (!input.getText().toString().equals("")){
                    if (!isNetworkAvailable(getApplication())){
                        myToast.show("Network is unavailable.");
                        return;
                    }
                    FirebaseDatabase.getInstance().
                            getReference("Messages").
                            child(path1).
                            child(path2).
                            push().
                            setValue(new Message(input.getText().toString(), comp));
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
                .setQuery(FirebaseDatabase.getInstance().
                        getReference("Messages").
                        child(path1).
                        child(path2).
                        orderByChild("time"), Message.class).build();
        adapter = new FirebaseRecyclerAdapter<Message, MessageViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MessageViewHolder messageViewHolder, int i, @NonNull Message message) {
                messageViewHolder.messageText.setText(message.getContent());
                messageViewHolder.messageTime.setText(DateFormat.format("HH:mm", message.getTime()));
                if ((message.getSendfromRoot() & path1.equals(Prevalent.currentOnlineUser.getUid())) | (!message.getSendfromRoot() & path2.equals(Prevalent.currentOnlineUser.getUid()))){
                    messageViewHolder.userName.setText(Prevalent.currentOnlineUser.getName());
                    Glide.with(ChatActivity.this).load(Prevalent.currentOnlineUser.getImage()).apply(glideOptions).into(messageViewHolder.profileImage);
                }
                else{
                    messageViewHolder.userName.setText(rival.getName());
                    Glide.with(ChatActivity.this).load(rival.getImage()).apply(glideOptions).into(messageViewHolder.profileImage);
                }
                }
            @NonNull
            @Override
            public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                if (viewType==1){
                    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.master_message_item, parent, false);
                    return new MessageViewHolder(view,viewType);
                }
                else{
                    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rival_message_item, parent, false);
                    return new MessageViewHolder(view,viewType);
                }
            }
            @Override
            public int getItemViewType(int position){
                Message message = getItem(position);
                if ((message.getSendfromRoot() & path1.equals(Prevalent.currentOnlineUser.getUid())) | (!message.getSendfromRoot() & path2.equals(Prevalent.currentOnlineUser.getUid()))){
                    return 1;
                }
                else{
                    return 0;
                }
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
        RecyclerViewListen(recyclerView);
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            public void onItemRangeInserted(int positionStart, final int itemCount) {
                RecyclerViewListen(recyclerView);
            }
        });
        mLastContentHeight = findViewById(Window.ID_ANDROID_CONTENT).getHeight();
        recyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                int currentContentHeight = findViewById(Window.ID_ANDROID_CONTENT).getHeight();
                if (mLastContentHeight > currentContentHeight + 100) {
                    Log.d("keyboard","open");
                    mLastContentHeight = currentContentHeight;
                    RecyclerViewListen(recyclerView);
                } else if (currentContentHeight > mLastContentHeight + 100) {
                    Log.d("keyboard","close");
                    mLastContentHeight = currentContentHeight;
                }
            }
        });
    }
    private void RecyclerViewListen(final RecyclerView recyclerView) {
        recyclerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Log.d("item count", "" + recyclerView.getAdapter().getItemCount());
                recyclerView.scrollToPosition(recyclerView.getAdapter().getItemCount() - 1);
                recyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
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