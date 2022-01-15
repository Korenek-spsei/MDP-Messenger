package com.example.mdpmessenger;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    Button Register,Login;

    FirebaseUser FUser;

    @Override
    protected void onStart() {
        super.onStart();
        //Checks if user is null (user is new)
        FUser = FirebaseAuth.getInstance().getCurrentUser();

        if (FUser != null){
            //pokud už pro zřízení uživatel existuje spouští aktivitu po přihlášení
            Intent intent = new Intent(MainActivity.this, WelcomeActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Register = findViewById(R.id.buttonRegister);
        Login= findViewById(R.id.buttonLogin);


        Register.setOnClickListener(v -> {
            //spouští aktivitu pro registraci
            Intent intent=new Intent(MainActivity.this,RegisterActivity.class);
            startActivity(intent);
        });

        Login.setOnClickListener(v -> {
            //spouští aktivitu pro login
            Intent intent=new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        });
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("some_messChannel_id", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
