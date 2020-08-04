package com.collabcreation.kapda.Activity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.collabcreation.kapda.R;
import com.collabcreation.kapda.model.Common;
import com.collabcreation.kapda.model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.collabcreation.kapda.model.Common.CURRENT_USER;
import static com.collabcreation.kapda.model.Common.getUserRefOf;

public class ProfileActivity extends AppCompatActivity {
    TextView editAddress, selectedAddress, forgotPassword, verifyAddress;
    Toolbar toolbar;
    ImageView editProfile;
    EditText emailAddress, name, number;
    CircleImageView userProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        initView();
        forgotPassword.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), ForgotPassword.class)));

        editProfile.setOnClickListener(v -> CropImage.activity()
                .setAspectRatio(5, 5)
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(ProfileActivity.this));

        verifyAddress.setOnClickListener(v -> {
            if (verifyAddress.getText().toString().toLowerCase().equals("verify")) {
                FirebaseAuth.getInstance().getCurrentUser().sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(ProfileActivity.this, "Check your inbox", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });


        Common.getUserRefOf(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                CURRENT_USER = user;
                Glide.with(getApplicationContext())
                        .load(user.getProfileUrl())
                        .placeholder(R.drawable.ic_person)
                        .into(userProfile);
                emailAddress.setText(user.getEmail());
                name.setText(user.getName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        editAddress.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), AddressListActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        selectedAddress.setText(getAddress());
        if (Common.getDefaultPos(getApplicationContext()) != -1) {
            number.setText("+91 " + Common.getDefaultPhoneNo(getApplicationContext()));
        } else {
            number.setText("");
        }
        if (FirebaseAuth.getInstance().getCurrentUser().isEmailVerified()) {
            verifyAddress.setText("Change");
        } else {
            verifyAddress.setText("Verify");
        }

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            super.onBackPressed();
        }
        return true;
    }


    private String getAddress() {
        if (Common.getDefaultPos(getApplicationContext()) != -1) {
            return Common.getDefaultAddress(getApplicationContext()) + " \n" + Common.getDefaultPincode(getApplicationContext()).toUpperCase() + " - " + Common.getDefaultCity(getApplicationContext()).toUpperCase() + ", " + Common.getDefaultState(getApplicationContext()).toUpperCase();
        } else {
            return "No Default Selected";
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                final ProgressDialog progressDialog = new ProgressDialog(this);
                progressDialog.setMessage("Updating...");
                progressDialog.setCancelable(false);
                progressDialog.show();
                final StorageReference reference = FirebaseStorage.getInstance().getReference("Profile").child(CURRENT_USER.getUserId() + "." + getExtension(resultUri));
                reference.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        reference.getDownloadUrl().addOnSuccessListener(uri -> getUserRefOf(CURRENT_USER.getUserId()).child("profileUrl").setValue(uri.toString()).addOnSuccessListener(aVoid -> {
                            if (progressDialog.isShowing())
                                progressDialog.dismiss();
                        }).addOnFailureListener(e -> {
                            if (progressDialog.isShowing())
                                progressDialog.dismiss();

                        }));
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if (progressDialog.isShowing())
                            progressDialog.dismiss();
                    }
                });

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    private void initView() {
        verifyAddress = findViewById(R.id.verifyAddress);
        emailAddress = findViewById(R.id.emailAddress);
        forgotPassword = findViewById(R.id.forgotPassword);
        name = findViewById(R.id.name);
        userProfile = findViewById(R.id.userProfile);
        editProfile = findViewById(R.id.editProfile);
        number = findViewById(R.id.number);
        editAddress = findViewById(R.id.editAddress);
        selectedAddress = findViewById(R.id.selectedAddress);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        emailAddress.setEnabled(false);
        emailAddress.setClickable(false);

    }

    private String getExtension(Uri uri) {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(uri));
    }
}
