package com.collabcreation.kapda.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.collabcreation.kapda.R;
import com.collabcreation.kapda.model.Common;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassword extends AppCompatActivity implements Common.RegisterCompleteListener {
    EditText emailaddress;
    TextView about, login;
    ImageView imageView, close;
    Button send;
    Common.RegisterCompleteListener registerCompleteListener;
    ProgressBar process;
    boolean completed;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        emailaddress = findViewById(R.id.emailaddress);
        about = findViewById(R.id.about);
        login = findViewById(R.id.login);
        imageView = findViewById(R.id.imageView);
        close = findViewById(R.id.close);
        send = findViewById(R.id.send);
        completed = true;
        registerCompleteListener = this;
        process = findViewById(R.id.process);
        auth = FirebaseAuth.getInstance();

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (auth.getCurrentUser() != null) {
                    auth.signOut();
                }
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                finish();

            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (completed) {
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "wait running..", Toast.LENGTH_SHORT).show();
                }
            }

        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = emailaddress.getText().toString().trim();
                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Enter your email", Toast.LENGTH_SHORT).show();
                } else {
                    process.setVisibility(View.VISIBLE);
                    send.setEnabled(false);
                    close.setEnabled(false);
                    registerCompleteListener.onComplete(false);
                    send.setText("wait..");
                    emailaddress.setEnabled(false);
                    auth.sendPasswordResetEmail(emailaddress.getText().toString())

                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        send.setVisibility(View.GONE);
                                        emailaddress.setVisibility(View.GONE);
                                        login.setVisibility(View.VISIBLE);
                                        registerCompleteListener.onComplete(true);
                                        close.setVisibility(View.INVISIBLE);
                                        process.setVisibility(View.INVISIBLE);
                                        imageView.setImageResource(R.drawable.ic_mail_sent);
                                        about.setText("Reset Email Sent\n Please check your inbox for the password reset link.");
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            send.setEnabled(true);
                            login.setVisibility(View.GONE);
                            close.setEnabled(true);
                            send.setText("Send Reset Link");
                            registerCompleteListener.onComplete(true);
                            emailaddress.setEnabled(true);
                            emailaddress.requestFocus();
                            InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            inputManager.showSoftInput(emailaddress, InputMethodManager.SHOW_IMPLICIT);
                            emailaddress.setError(e.getMessage());
                            process.setVisibility(View.INVISIBLE);
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (completed) {

        } else {
            Toast.makeText(getApplicationContext(), "wait running..", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onComplete(boolean isComplete) {
        completed = isComplete;
    }


}
