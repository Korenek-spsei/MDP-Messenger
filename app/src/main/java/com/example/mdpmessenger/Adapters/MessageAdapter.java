package com.example.mdpmessenger.Adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mdpmessenger.Models.Message;
import com.example.mdpmessenger.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    public static final int MSG_TYPE_LEFT= 0;
    public static final int MSG_TYPE_RIGHT= 1;
    private final Context context;
    private final List<Message> messages;
    private String imageUrl;

    FirebaseUser thisUser;

    public MessageAdapter(Context context, List<Message> mMessage,String imageUrl){
        this.context= context;
        this.messages = mMessage;
        this.imageUrl = imageUrl;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MSG_TYPE_RIGHT) {
            View view = LayoutInflater.from(context).inflate(R.layout.chat_item_right, parent, false);
            return new ViewHolder(view);
        }else  {
            View view = LayoutInflater.from(context).inflate(R.layout.chat_item_left, parent, false);
            return new ViewHolder(view);
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public TextView show_message;
        public ImageView profile_image;

        public TextView text_seen;

        public ViewHolder(View itemView){
            super(itemView);

            show_message = itemView.findViewById(R.id.show_message);
            profile_image = itemView.findViewById(R.id.chat_profile_image);
            text_seen = itemView.findViewById(R.id.txt_seen);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Message message =  messages.get(position);
        holder.show_message.setText(message.getMessage());

//        if (imageUrl.equals("default")){
//            holder.profile_image.setImageResource(R.mipmap.ic_launcher);
//        }else Glide.with(context).load(imageUrl).into(holder.profile_image);


        if (position == messages.size()-1){
            if (message.isSeen()){
                holder.text_seen.setText("Seen");
            } else holder.text_seen.setText("Delivered");
        } else holder.text_seen.setVisibility(View.GONE);
    }

    @Override
    public int getItemViewType(int position) {
        thisUser = FirebaseAuth.getInstance().getCurrentUser();
        if (messages.get(position).getSender().equals(thisUser.getUid())){
            return MSG_TYPE_RIGHT;
        }else return MSG_TYPE_LEFT;
    }
}
