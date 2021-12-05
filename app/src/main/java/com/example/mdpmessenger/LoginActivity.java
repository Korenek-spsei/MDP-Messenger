package com.example.mdpmessenger;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    EditText Email, Password;
    Button Register,BackLogin;

    FirebaseAuth auth;
    TextView forgot_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Login");
        Register = findViewById(R.id.buttonRegister);
        BackLogin = findViewById(R.id.backLogin);
        Email = findViewById(R.id.editEmail);
        Password = findViewById(R.id.editPassword);
        forgot_password = findViewById(R.id.forgot_password);

        forgot_password.setOnClickListener(v -> {
            //startActivity(new Intent(LoginActivity.this, ResetPasswordActivity.class));
        });

        Register.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, RegisterActivity.class)));

        auth= FirebaseAuth.getInstance();

        BackLogin.setOnClickListener(v -> {
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
