package com.collabcreation.kapda.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.collabcreation.kapda.R;
import com.collabcreation.kapda.model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import static com.collabcreation.kapda.model.Common.CURRENT_USER;
import static com.collabcreation.kapda.model.Common.getUserRefOf;

public class RegisterActivity extends AppCompatActivity {
    EditText userEmail, userName, userPassword, userNumber;
    Button signup;
    TextView login;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        userEmail = findViewById(R.id.et_UserEmail);
        userName = findViewById(R.id.et_UserName);
        userPassword = findViewById(R.id.et_UserPassword);
        userNumber = findViewById(R.id.et_UserNumber);
        signup = findViewById(R.id.signup);
        login = findViewById(R.id.login);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                finish();
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isValid() && userNumber.getText().toString().trim().length() == 10 && userPassword.getText().toString().trim().length() >= 8
                        && userEmail.getText().toString().matches("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+")) {
                    final ProgressDialog progressBar = new ProgressDialog(RegisterActivity.this);
                    progressBar.setMessage("Loading...");
                    progressBar.setCancelable(false);
                    progressBar.show();
                    FirebaseAuth.getInstance().createUserWithEmailAndPassword(userEmail.getText().toString(), userPassword.getText().toString()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(final AuthResult authResult) {
                            final User user = new User(authResult.getUser().getUid(), userName.getText().toString(), userEmail.getText().toString(), userNumber.getText().toString());

                            getUserRefOf(authResult.getUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.getValue() == null) {
                                        getUserRefOf(authResult.getUser().getUid())
                                                .setValue(user).addOnSuccessListener(aVoid -> {
                                                    CURRENT_USER = user;
                                                    if (progressBar.isShowing()) {
                                                        progressBar.dismiss();
                                                    }
                                                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                                                    finish();

                                                });
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            if (progressBar.isShowing()) {
                                progressBar.dismiss();
                                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
                } else if (!userEmail.getText().toString().matches("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+") || userEmail.getText().toString().trim().isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Enter Valid Email!", Toast.LENGTH_SHORT).show();
                } else if (userPassword.getText().toString().trim().isEmpty() || userPassword.getText().toString().length() < 8) {
                    Toast.makeText(RegisterActivity.this, "Enter Password!", Toast.LENGTH_SHORT).show();
                } else if (userNumber.getText().toString().trim().length() != 10) {
                    Toast.makeText(RegisterActivity.this, "Enter Valid Number!", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private boolean isValid() {
        if (userNumber.getText().length() < 10) {
            Toast.makeText(this, "Enter Valid Number", Toast.LENGTH_SHORT).show();
        }


        if (!userEmail.getText().toString().trim().matches("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+")) {
            Toast.makeText(RegisterActivity.this, "Enter valid email", Toast.LENGTH_SHORT).show();
        }

        return userEmail.getText().toString().trim().isEmpty() && userPassword.getText().toString().trim().isEmpty() &&
                userName.getText().toString().trim().isEmpty() && userNumber.getText().toString().trim().isEmpty();

    }
}
