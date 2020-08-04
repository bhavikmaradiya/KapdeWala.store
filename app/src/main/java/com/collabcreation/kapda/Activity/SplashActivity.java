package com.collabcreation.kapda.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.collabcreation.kapda.R;
import com.collabcreation.kapda.model.Common;
import com.collabcreation.kapda.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import static com.collabcreation.kapda.model.Common.CURRENT_USER;
import static com.collabcreation.kapda.model.Common.getUserRefOf;

public class SplashActivity extends AppCompatActivity {
    TextView name;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        progressBar = findViewById(R.id.process);
        name = findViewById(R.id.name);


        new Handler().postDelayed(() -> {
            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                getUserRefOf(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        CURRENT_USER = dataSnapshot.getValue(User.class);
                        getSharedPreferences(Common.USER, MODE_PRIVATE).edit().putString(Common.USER, FirebaseAuth.getInstance().getCurrentUser().getUid()).apply();
                        startActivity(new Intent(getApplicationContext(), MainActivity.class).putExtra(Common.KEY_USER, dataSnapshot.getValue(User.class)));
                        finish();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            } else {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            }
        }, 2500);
    }
}
