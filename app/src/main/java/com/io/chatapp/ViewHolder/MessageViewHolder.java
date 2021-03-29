package com.io.chatapp.ViewHolder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.io.chatapp.Interface.ItemClickListener;
import com.io.chatapp.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView userName,messageText,messageTime;
    public CircleImageView profileImage;
    public ItemClickListener listener;

    public MessageViewHolder(@NonNull View itemView) {
        super(itemView);
        userName = (TextView) itemView.findViewById(R.id.message_user);
        messageText = (TextView) itemView.findViewById(R.id.message_text);
        messageTime = (TextView) itemView.findViewById(R.id.message_time);
        profileImage = (CircleImageView) itemView.findViewById(R.id.message_profile_image);
    }
    @Override
    public void onClick(View view) {
        listener.onclick(view,getAdapterPosition(),false);
    }
    public void setItemClickListener(ItemClickListener listener){
        this.listener = listener;
    }
}
