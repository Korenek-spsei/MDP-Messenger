package com.example.mdpmessenger;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Toolbar toolbar = findViewById(R.id.toolbar_login);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Login");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Button RegisterBttn = findViewById(R.id.buttonRegister);
        Button LoginBttn = findViewById(R.id.buttonLogin);
        EditText Email = findViewById(R.id.editEmail);
        EditText Password = findViewById(R.id.editPassword);
        TextView forgot_password = findViewById(R.id.forgot_password);

        forgot_password.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this,ResetPasswdActivity.class ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
        });


        //spouští aktivitu pro registraci
        RegisterBttn.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, RegisterActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)));

        FirebaseAuth auth= FirebaseAuth.getInstance();
        //oveři zda email a heslo odpovídají nekterému z užvatelu
        LoginBttn.setOnClickListener(v -> {
            String txtEmail = Email.getText().toString();
            String txtPassword = Password.getText().toString();

            if (TextUtils.isEmpty(txtEmail)|| TextUtils.isEmpty(txtPassword)){
                Toast.makeText(LoginActivity.this,"Some informations are missing",Toast.LENGTH_SHORT).show();
            }else{
                auth.signInWithEmailAndPassword(txtEmail,txtPassword).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Intent intent = new Intent(LoginActivity.this, WelcomeActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }else Toast.makeText(LoginActivity.this,"Wrong email or password",Toast.LENGTH_SHORT).show();
                });
            }
        });

    }
}
