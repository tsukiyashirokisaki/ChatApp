package com.io.chatapp.Model;

import android.content.Context;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

public class MyToast {
    private Context context;
    public MyToast(Context context_init) {
        context = context_init;
    }
    public void show(String string){
        Toast toast = Toast.makeText(context,string,Toast.LENGTH_SHORT);
        ViewGroup group = (ViewGroup) toast.getView();
        TextView messageTextView = (TextView) group.getChildAt(0);
        messageTextView.setTextSize(16);
        toast.setGravity(Gravity.CENTER_VERTICAL,10,0);
        toast.show();
    }
}