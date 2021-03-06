package com.example.mdpmessenger.ui.chats;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mdpmessenger.Adapters.NewMessageUserAdapter;
import com.example.mdpmessenger.Adapters.UserAdapter;
import com.example.mdpmessenger.Models.ChatList;
import com.example.mdpmessenger.Models.User;
import com.example.mdpmessenger.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ChatsFragment extends Fragment {

    private RecyclerView recyclerView;
    private RecyclerView newMessRrecyclerView;
    private UserAdapter userAdapter;
    private List<User> Users,nUsers;
    private List<ChatList> chats;

    FirebaseUser thisUser;
    DatabaseReference reference;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_chats,container,false);

        recyclerView = view.findViewById(R.id.user_recycle_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        newMessRrecyclerView = view.findViewById(R.id.new_message_recycle_view);
        newMessRrecyclerView.setHasFixedSize(true);
        newMessRrecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        thisUser = FirebaseAuth.getInstance().getCurrentUser();    //id p??ihl????en??ho u??ivatele

        chats = new ArrayList<>();
        //propojen?? s datab??z??
        reference = FirebaseDatabase.getInstance("https://dmp-messenger-database-default-rtdb.europe-west1.firebasedatabase.app").getReference("Chatlist").child(thisUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chats.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ChatList chatlist = snapshot.getValue(ChatList.class);
                    chats.add(chatlist);
                }

                showChats();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        EditText search_bar = view.findViewById(R.id.search_bar);
        search_bar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                searchUsers(charSequence.toString().toLowerCase());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        return view;
    }

    //dle zadaneho stringu v search baru hleda a zobrazi chaty uzivatelu ktere se shoduji se stringem
    private void searchUsers(String s) {
        final FirebaseUser thisUser = FirebaseAuth.getInstance().getCurrentUser();
        //firebase query umo??nuje ????st zm??ny a porovn??vat je s daty v databazi
        Query query = FirebaseDatabase.getInstance("https://dmp-messenger-database-default-rtdb.europe-west1.firebasedatabase.app").getReference("Users").orderByChild("search").startAt(s).endAt(s+"\uf8ff");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Users.clear();
                nUsers.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    if (!user.getId().equals(thisUser.getUid())) {
                             Users.add(user);
                    }
                }
                userAdapter = new UserAdapter(getContext(), Users);
                recyclerView.setAdapter(userAdapter);
                userAdapter = new UserAdapter(getContext(), nUsers);
                newMessRrecyclerView.setAdapter(userAdapter);
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //proji??d?? z??znamy v chatlistu u??ivatele a zobrazuje v recycleView
    private void showChats() {
        Users = new ArrayList<>();
        nUsers = new ArrayList<>();

        reference = FirebaseDatabase.getInstance("https://dmp-messenger-database-default-rtdb.europe-west1.firebasedatabase.app").getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                Users.clear();
                nUsers.clear();
                for (DataSnapshot snapshot : datasnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    for (ChatList chatList : chats) {
                        if (user.getId() != null && user.getId().equals(chatList.getId())) {
                            //naplnuje listy u??ivatel?? podle toho jestli jejich zpravy byli videny nebo ne
                            if(chatList.getNewMessage()!=null && chatList.getNewMessage()){
                                nUsers.add(user);
                            }else
                                Users.add(user);
                        }

                    }
                }

                //list chatu pak zpracuje adapter kter?? pak napln?? recycleView

                NewMessageUserAdapter nUserAdapter = new NewMessageUserAdapter(getContext(), nUsers);
                newMessRrecyclerView.setAdapter(nUserAdapter);
                userAdapter = new UserAdapter(getContext(), Users);
                recyclerView.setAdapter(userAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
