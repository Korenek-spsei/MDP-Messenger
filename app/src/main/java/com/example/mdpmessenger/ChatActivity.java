package com.example.mdpmessenger;

import android.content.Intent;
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
import com.example.mdpmessenger.Models.Message;
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

    ImageView profile_image,profile_image_toolbar;
    TextView username;
    FirebaseUser thisUser;
    DatabaseReference reference;

    ImageButton btn_send;
    EditText text_send;
    RecyclerView recyclerView;

    MessageAdapter messageAdapter;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Toolbar toolbar = findViewById(R.id.toolbar_chat);
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

        profile_image = findViewById(R.id.chat_profile_image);
        profile_image_toolbar = findViewById(R.id.profile_image_toolbar);
        username = findViewById(R.id.chat_username);
        btn_send = findViewById(R.id.btn_send);
        text_send = findViewById(R.id.text_send);

        intent = getIntent();

        final String userid = intent.getStringExtra("userid");
        thisUser = FirebaseAuth.getInstance().getCurrentUser();
        //p??id??v?? jmeno u??ivatelele do z??hlav?? dle jeho id
        reference = FirebaseDatabase.getInstance("https://dmp-messenger-database-default-rtdb.europe-west1.firebasedatabase.app").getReference("Users").child(userid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                User user  = datasnapshot.getValue(User.class);
                username.setText(user.getUsername());
                //TODO: User image
                readMessage(thisUser.getUid(),userid,user.getImageURL());

                if (user.getImageURL().equals("default")){
                    profile_image_toolbar.setImageResource(R.mipmap.ic_launcher);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //pokud se zm????kne tla????tko ode??le se zp??va
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
        //vytvo???? z??znam zpr??vy do datab??ze
        DatabaseReference messReference = FirebaseDatabase
                .getInstance("https://dmp-messenger-database-default-rtdb.europe-west1.firebasedatabase.app")
                .getReference().child("Chats");
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("receiver", receiver);
        hashMap.put("message", message);
        hashMap.put("isSeen", false);

        messReference.push().setValue(hashMap);

        final String userid = intent.getStringExtra("userid");

        //p??id?? do datab??ze z??znam do v??tve Chatlists pod jeho id z??znam id od u??ivatele kter??mu napsal -> t??m vytvo???? z??znam chatu
        DatabaseReference chatRefSender = FirebaseDatabase.getInstance("https://dmp-messenger-database-default-rtdb.europe-west1.firebasedatabase.app").getReference("Chatlist").child(thisUser.getUid()).child(userid);
        chatRefSender.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if (!snapshot.exists()){
//                  toto m??lo ukladat data hned na za??atku, ale n??kde mi to blbne a ja fakt nevim kde. funguje to tak i tak
//                  jen to dokola uklada stejnou prom??nnou
//                }

                chatRefSender.child("id").setValue(userid);
                chatRefSender.child("newMessage").setValue(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

        //p??id?? do datab??ze z??znam sv??ho id pod id toho komu napsal
        final DatabaseReference chatRefReciever = FirebaseDatabase.getInstance("https://dmp-messenger-database-default-rtdb.europe-west1.firebasedatabase.app").getReference("Chatlist").child(userid).child(thisUser.getUid());
        chatRefReciever.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatRefReciever.child("id").setValue(thisUser.getUid());
                chatRefReciever.child("newMessage").setValue(true);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

    }

    //m??n?? v datab??zi hodnotu isSeen u zpr??v
    private void seenMessage(final String userid){
        DatabaseReference seenMessRef = FirebaseDatabase.getInstance("https://dmp-messenger-database-default-rtdb.europe-west1.firebasedatabase.app").getReference().child("Chats");
        seenMessRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                for (DataSnapshot snapshot: datasnapshot.getChildren()){
                    Message message = snapshot.getValue(Message.class);
                    if (message.getSender().equals(userid) && message.getReceiver().equals(thisUser.getUid())){
                        //vytv??????m hashmapu k zm??n?? hodnoty isSeen v datab??zi. Jeliko?? nezn??m id od zpr??vy mus??m to p??ed??vat p??es snapshot updateChildren
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("isSeen",true);
                        snapshot.getRef().updateChildren(hashMap);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        //zaznamena do databaze ??e tento u??ivatel nem?? ??adn?? nov?? zpr??vy
        DatabaseReference chatRefReciever = FirebaseDatabase.getInstance("https://dmp-messenger-database-default-rtdb.europe-west1.firebasedatabase.app").getReference("Chatlist").child(thisUser.getUid()).child(userid);
        chatRefReciever.child("newMessage").setValue(false);
    }

    //is reading messages from database
    private void readMessage(final String myid,final String userid,final String imageurl){
        List<Message> messages = new ArrayList<>();

        //
        DatabaseReference readMessRef = FirebaseDatabase.getInstance("https://dmp-messenger-database-default-rtdb.europe-west1.firebasedatabase.app").getReference("Chats");
        readMessRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                messages.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Message message = snapshot.getValue(Message.class);
                    //pokus se moje id nebo dan??ho u??ivatele shoduje s Reciver id u zpr??vy a Sender je ten druhy tak se zprava p??id?? do listu zpr??v
                    if (message.getReceiver() != null && message.getReceiver().equals(myid) && message.getSender().equals(userid) || message.getReceiver() != null && message.getReceiver().equals(userid) && message.getSender().equals(myid)) {
                        messages.add(message);
                    }
                    //list zpr??v pak zpracuje adapter kter?? pak napln?? recycleView
                    messageAdapter= new MessageAdapter(ChatActivity.this, messages,imageurl);
                    recyclerView.setAdapter(messageAdapter);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}