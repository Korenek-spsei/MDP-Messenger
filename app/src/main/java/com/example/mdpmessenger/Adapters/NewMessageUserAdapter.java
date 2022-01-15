package com.example.mdpmessenger.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mdpmessenger.ChatActivity;
import com.example.mdpmessenger.Models.User;
import com.example.mdpmessenger.R;

import java.util.List;

public class NewMessageUserAdapter extends RecyclerView.Adapter<NewMessageUserAdapter.ViewHolder> {

    private Context context;
    private List<User> users;

    public NewMessageUserAdapter(Context context, List<User> users){
        this.context= context;
        this.users = users;
    }

    //konstruktor
    public static class ViewHolder extends RecyclerView.ViewHolder{
        public TextView username;
        public ImageView profile_image;

        public ViewHolder(@NonNull View itemView){
            super(itemView);

            username = itemView.findViewById(R.id.new_message_username);
            profile_image = itemView.findViewById(R.id.new_message_profile_image);
        }
    }

    //po vytvoření ViewHolderu ho naplnuje pohledem
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_item_new_message, parent, false);
        return new ViewHolder(view);
    }

    //vyplnuje viewHolder daty uživatelu, když objeví na obrazovce
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final User user = users.get(position);
        holder.username.setText(user.getUsername());

            //po kliknutí na konkrétní view(uživatele) se spustí ChatActivity a je do ní posláno id od uživatele na kterého se kliklo
            holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(context, ChatActivity.class);
                intent.putExtra("userid",user.getId());
                context.startActivity(intent);
            }
        });
    }

    //vrací kolik instanci uživatelu naplní viewHoldery
    @Override
    public int getItemCount() {
        return users.size();
    }
}
