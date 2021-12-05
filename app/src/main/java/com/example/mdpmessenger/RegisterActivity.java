package com.example.mdpmessenger;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    EditText Email, Password, Name ;
    Button Register,BackLogin;

    DatabaseReference reference;
    FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Register");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Register = findViewById(R.id.buttonRegister);
        BackLogin = findViewById(R.id.backLogin);
        Email = findViewById(R.id.editEmail);
        Password = findViewById(R.id.editPassword);
        Name = findViewById(R.id.editName);

        auth=FirebaseAuth.getInstance();

        Register.setOnClickListener(v -> {
            String txtUsername = Name.getText().toString();
            String txtEmail = Email.getText().toString();
            String txtPassword = Password.getText().toString();

            if (TextUtils.isEmpty(txtEmail)|| TextUtils.isEmpty(txtPassword)|| TextUtils.isEmpty(txtUsername)){
                Toast.makeText(RegisterActivity.this,"Some informations are missing",Toast.LENGTH_SHORT).show();
            }else if (txtPassword.length()<6){
                Toast.makeText(RegisterActivity.this,"Password needs to have at leaats 6 characters",Toast.LENGTH_LONG).show();
            }else Register(txtUsername,txtEmail,txtPassword);
        });
    }

    private void Register(final String Name, String Email,String Password){
            auth.createUserWithEmailAndPassword(Email,Password).addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    FirebaseUser firebaseUser = auth.getCurrentUser();
                    assert firebaseUser != null;
                    String userID = firebaseUser.getUid();
                    reference = FirebaseDatabase.getInstance().getReference("Users").child(userID);

                    HashMap<String,String> hashMap = new HashMap<>();
                    hashMap.put("id",userID);
                    hashMap.put("username",Name);
                    hashMap.put("imageURL","default");
                    hashMap.put("status", "offline");
                    hashMap.put("search", Name.toLowerCase());

                    reference.setValue(hashMap).addOnCompleteListener(task1 -> {
                        if(task1.isSuccessful()){
                            Intent intent = new Intent(RegisterActivity.this, WelcomeActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        }
                    });
                }else {
                    Toast.makeText(RegisterActivity.this,"Wrong Email or password",Toast.LENGTH_LONG).show();
                }
            });
    }
}