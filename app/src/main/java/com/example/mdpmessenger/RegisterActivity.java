package com.example.mdpmessenger;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.mdpmessenger.Models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    Boolean newUser;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Toolbar toolbar = findViewById(R.id.toolbar_register);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Register");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Button RegisterBttn = findViewById(R.id.buttonRegister);
        Button LoginBttn = findViewById(R.id.backLogin);
        EditText Email = findViewById(R.id.editEmail);
        EditText Password = findViewById(R.id.editPassword);
        EditText PasswordCheck = findViewById(R.id.editPasswordCheck);
        EditText Name = findViewById(R.id.editName);

        //připojuje zde Firebase funkce pro autentifikace
        auth=FirebaseAuth.getInstance();

        LoginBttn.setOnClickListener(v -> startActivity(new Intent(RegisterActivity.this, LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)));

        RegisterBttn.setOnClickListener(v -> {
            newUser=true;
            String txtUsername = Name.getText().toString();
            String txtEmail = Email.getText().toString();
            String txtPassword = Password.getText().toString();
            String txtPasswordCheck = PasswordCheck.getText().toString();

            DatabaseReference reference = FirebaseDatabase.getInstance("https://dmp-messenger-database-default-rtdb.europe-west1.firebasedatabase.app").getReference().child("Users");
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User user = snapshot.getValue(User.class);
                    if(txtUsername.equals(user.getUsername())){
                        newUser=false;
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            //kontroluje jestli jsou všechny udaje správně zadané
            if (TextUtils.isEmpty(txtEmail)|| TextUtils.isEmpty(txtPassword) || TextUtils.isEmpty(txtPasswordCheck) || TextUtils.isEmpty(txtUsername)){
                Toast.makeText(RegisterActivity.this,"Some informations are missing",Toast.LENGTH_SHORT).show();
            }else if (txtPassword.length()<6){
                Toast.makeText(RegisterActivity.this,"Password needs to have at leaats 6 characters",Toast.LENGTH_SHORT).show();
            }else if (!newUser){
                Toast.makeText(RegisterActivity.this,"This username already exist, please choose something different",Toast.LENGTH_SHORT).show();
            }else if (!txtPassword.equals(txtPasswordCheck)){
                Toast.makeText(RegisterActivity.this,"Passwords needs to be same",Toast.LENGTH_SHORT).show();
            }else Register(txtUsername,txtEmail,txtPassword);
        });
    }

    private void Register(final String Name, String Email,String Password){
        //vytvoří ve Firebase Authentification uživatele s emailem a heslem přes kterého se dá pak přihlašovat
        auth.createUserWithEmailAndPassword(Email, Password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                //zjištuje přihlášeného uživatele
                String userID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
                //propojení s Firebase Database pro uložení dat o uživateli pro budoucí práci s daty. Vytváří místo pro ukládání
                DatabaseReference reference = FirebaseDatabase.getInstance("https://dmp-messenger-database-default-rtdb.europe-west1.firebasedatabase.app")
                        .getReference().child("Users").child(userID);

                //toto jsou zakladní data která budu od uživatele ukládat + přidání dat o uživateli do HashMapy
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("id", userID);
                hashMap.put("username", Name);
                hashMap.put("search", Name.toLowerCase());
                hashMap.put("imageURL", "default");

                //posílá hashmapu do databaze
                reference.setValue(hashMap).addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()) {
                        Intent intent = new Intent(RegisterActivity.this, WelcomeActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }
                });
            }else {
                Toast.makeText(RegisterActivity.this, "Wrong email or it already exist try again!", Toast.LENGTH_LONG).show();
            }
        });
    }
}
