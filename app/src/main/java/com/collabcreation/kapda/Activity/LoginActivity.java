package com.collabcreation.kapda.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.collabcreation.kapda.R;
import com.collabcreation.kapda.model.Common;
import com.collabcreation.kapda.model.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import static com.collabcreation.kapda.model.Common.CURRENT_USER;
import static com.collabcreation.kapda.model.Common.getTokenRefOf;
import static com.collabcreation.kapda.model.Common.getUserRefOf;

public class LoginActivity extends AppCompatActivity {
    EditText email, password;
    TextView register, forgot;
    Button login;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        forgot = findViewById(R.id.forgotPassword);
        register = findViewById(R.id.register);
        login = findViewById(R.id.signin);
        email = findViewById(R.id.et_UserEmail);
        password = findViewById(R.id.et_UserPassword);
        register.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
            finish();
        });

        forgot.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), ForgotPassword.class)));

        login.setOnClickListener(v -> {
            if (!email.getText().toString().trim().isEmpty() && !password.getText().toString().trim().isEmpty() && password.getText().toString().trim().length() >= 8) {
                final ProgressDialog dialog = new ProgressDialog(LoginActivity.this);
                dialog.setCancelable(false);
                dialog.setMessage("Please wait....");
                dialog.show();
                FirebaseAuth.getInstance().signInWithEmailAndPassword(email.getText().toString(), password.getText().toString()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        getUserRefOf(authResult.getUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                                CURRENT_USER = dataSnapshot.getValue(User.class);
                                FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(instanceIdResult -> {
                                    getTokenRefOf(dataSnapshot.getValue(User.class).getUserId()).setValue(instanceIdResult.getToken());
                                    if (dialog.isShowing()) {
                                        dialog.dismiss();
                                    }
                                    startActivity(new Intent(getApplicationContext(), MainActivity.class).putExtra(Common.KEY_USER, dataSnapshot.getValue(User.class)));
                                    finish();
                                });

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                    }
                }).addOnFailureListener(e -> {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                });
            } else if (password.getText().toString().trim().isEmpty() || password.getText().toString().length() < 8) {
                Toast.makeText(LoginActivity.this, "Enter Password!", Toast.LENGTH_SHORT).show();
            } else if (email.getText().toString().trim().isEmpty()) {
                Toast.makeText(LoginActivity.this, "Enter Email!", Toast.LENGTH_SHORT).show();
            }
        });


    }

}
