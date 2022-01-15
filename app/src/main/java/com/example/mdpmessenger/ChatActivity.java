package com.example.mdpmessenger;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mdpmessenger.Adapters.MessageAdapter;
import com.example.mdpmessenger.Models.Chat;
import com.example.mdpmessenger.Models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    ImageView profile_image;
    TextView username;
    FirebaseUser thisUser;
    DatabaseReference reference;

    ImageButton btn_send;
    EditText text_send;
    RecyclerView recyclerView;

    MessageAdapter messageAdapter;
    List<Chat> chats;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ChatActivity.this,BottomNavigationActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });

        recyclerView = findViewById(R.id.messages_recycle_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        profile_image = findViewById(R.id.profile_image);
        username = findViewById(R.id.chat_username);
        btn_send = findViewById(R.id.btn_send);
        text_send = findViewById(R.id.text_send);

        intent = getIntent();
        final String userid = intent.getStringExtra("userid");


        thisUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance("https://dmp-messenger-database-default-rtdb.europe-west1.firebasedatabase.app").getReference("Users").child(userid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                User user  = datasnapshot.getValue(User.class);
                username.setText(user.getUsername());
                //TODO: User image
                readMessage(thisUser.getUid(),userid,user.getImageURL());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: notify function
                String msg = text_send.getText().toString();
                if (!msg.equals("")) {
                    sendMessage(thisUser.getUid(),userid,msg);
                }else Toast.makeText(ChatActivity.this,"Cant send empty message",Toast.LENGTH_SHORT).show();
                text_send.setText("");
            }
        });

        seenMessage(userid);
    }

    private void sendMessage(String sender,String receiver, String message) {
        reference = FirebaseDatabase.getInstance("https://dmp-messenger-database-default-rtdb.europe-west1.firebasedatabase.app").getReference();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("receiver", receiver);
        hashMap.put("message", message);
        hashMap.put("isseen", false);

        reference.child("Chats").push().setValue(hashMap);

        final String userid = intent.getStringExtra("userid");

        final DatabaseReference chatRefSender = FirebaseDatabase.getInstance("https://dmp-messenger-database-default-rtdb.europe-west1.firebasedatabase.app").getReference("Chatlist").child(thisUser.getUid()).child(userid);
        chatRefSender.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()){
                    chatRefSender.child("id").setValue(userid);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

        final DatabaseReference chatRefReciever = FirebaseDatabase.getInstance("https://dmp-messenger-database-default-rtdb.europe-west1.firebasedatabase.app").getReference("Chatlist").child(userid).child(thisUser.getUid());
        chatRefReciever.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()){
                    chatRefReciever.child("id").setValue(thisUser.getUid());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

        reference = FirebaseDatabase.getInstance("https://dmp-messenger-database-default-rtdb.europe-west1.firebasedatabase.app").getReference("Users").child(thisUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
//               Here will be notification
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void seenMessage(final String userid){
        reference = FirebaseDatabase.getInstance("https://dmp-messenger-database-default-rtdb.europe-west1.firebasedatabase.app").getReference().child("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                for (DataSnapshot snapshot: datasnapshot.getChildren()){
                    Chat chat = snapshot.getValue(Chat.class);
                    if (chat.getSender().equals(userid) && chat.getReceiver().equals(thisUser.getUid())){
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("isseen",true);
                        snapshot.getRef().updateChildren(hashMap);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readMessage(final String myid,final String userid,final String imageurl){
        chats =new ArrayList<>();

        reference = FirebaseDatabase.getInstance("https://dmp-messenger-database-default-rtdb.europe-west1.firebasedatabase.app").getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chats.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Chat chat = snapshot.getValue(Chat.class);
                    if (chat.getReceiver() != null && chat.getReceiver().equals(myid) && chat.getSender().equals(userid) || chat.getReceiver() != null && chat.getReceiver().equals(userid) && chat.getSender().equals(myid)) {
                        chats.add(chat);
                    }

                    messageAdapter= new MessageAdapter(ChatActivity.this, chats);
                    recyclerView.setAdapter(messageAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void currentUser(String userid){
        SharedPreferences.Editor editor = getSharedPreferences("PREFS", MODE_PRIVATE).edit();
        editor.putString("currentuser",userid);
        editor.apply();
    }
}