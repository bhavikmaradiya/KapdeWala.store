package com.collabcreation.kapda.Activity;

import android.content.Context;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;

import com.collabcreation.kapda.R;
import com.collabcreation.kapda.model.Address;
import com.collabcreation.kapda.model.Common;
import com.google.android.gms.tasks.OnSuccessListener;

import static com.collabcreation.kapda.model.Common.CURRENT_USER;
import static com.collabcreation.kapda.model.Common.changeAddress;
import static com.collabcreation.kapda.model.Common.getAddressRef;

public class UpdateAddressActivity extends AppCompatActivity {
    Toolbar toolbar;
    EditText etName, etPhone, etAddress, etPincode, etLandmark, etCity, etState;
    Button btnSave;
    Address currentAddress;
    SwitchCompat makeDefault;
    LinearLayout makeDefaultLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_address);
        initView();
        if (getIntent().getBooleanExtra("isEdit", false)) {
            currentAddress = (Address) getIntent().getSerializableExtra(Common.ADDRESS);
            getSupportActionBar().setTitle("Edit Address");
            etLandmark.setText(currentAddress.getLandMark());
            etName.setText(currentAddress.getReceiverName());
            etPincode.setText(currentAddress.getPostcode());
            etPhone.setText(currentAddress.getPhoneNo());
            etCity.setText(currentAddress.getCity());
            etState.setText(currentAddress.getState());
            etAddress.setText(currentAddress.getAddress());
            makeDefaultLayout.setVisibility(View.GONE);
        } else {
            currentAddress = null;
            getSupportActionBar().setTitle("New Address");
            makeDefaultLayout.setVisibility(View.VISIBLE);
        }

        btnSave.setOnClickListener(v -> {
            if (!etCity.getText().toString().trim().isEmpty() && !etAddress.getText().toString().trim().isEmpty() && !etState.getText().toString().trim().isEmpty() && !etPincode.getText().toString().trim().trim().isEmpty() && !etLandmark.getText().toString().trim().isEmpty() && !etName.getText().toString().trim().isEmpty() && !etPhone.getText().toString().trim().isEmpty()) {
                if (etPincode.getText().toString().length() == 6) {
                        if (etPhone.getText().toString().length() == 10) {
                            if (currentAddress != null) {
                                Address address = new Address(currentAddress.getAddressId(), etCity.getText().toString(), etAddress.getText().toString(), etState.getText().toString(), etPincode.getText().toString().trim(), etLandmark.getText().toString(), etName.getText().toString(), etPhone.getText().toString());
                                Common.getAddressRef(CURRENT_USER.getUserId()).child(currentAddress.getAddressId()).setValue(address).addOnSuccessListener(aVoid -> {
                                    Toast.makeText(UpdateAddressActivity.this, "Saved Successfully", Toast.LENGTH_SHORT).show();
                                    UpdateAddressActivity.super.onBackPressed();
                                });
                            } else {
                                final Address address = new Address(getAddressRef(CURRENT_USER.getUserId()).push().getKey(), etCity.getText().toString(), etAddress.getText().toString(), etState.getText().toString(), etPincode.getText().toString().trim(), etLandmark.getText().toString(), etName.getText().toString(), etPhone.getText().toString());
                                Common.getAddressRef(CURRENT_USER.getUserId()).child(address.getAddressId()).setValue(address).addOnSuccessListener(aVoid -> {
                                    if (makeDefault.isChecked()) {
                                        changeAddress(getApplicationContext(), address);
                                        getSharedPreferences(CURRENT_USER.getUserId(), Context.MODE_PRIVATE).edit().putInt(Common.ADDRESS_POS, getIntent().getIntExtra("total", -1)).apply();
                                    }
                                    Toast.makeText(UpdateAddressActivity.this, "Saved Successfully", Toast.LENGTH_SHORT).show();
                                    UpdateAddressActivity.super.onBackPressed();
                                });
                            }
                        } else {
                            Toast.makeText(UpdateAddressActivity.this, "Enter valid number", Toast.LENGTH_SHORT).show();
                        }

                } else {
                    Toast.makeText(UpdateAddressActivity.this, "Enter valid pincode", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(UpdateAddressActivity.this, "Enter Valid Details", Toast.LENGTH_SHORT).show();
            }
        });


    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            super.onBackPressed();
        }
        return true;
    }

    private void initView() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        btnSave = findViewById(R.id.btnSave);
        etName = findViewById(R.id.etName);
        etAddress = findViewById(R.id.etAddress);
        etPhone = findViewById(R.id.etNumber);
        etState = findViewById(R.id.etState);
        etCity = findViewById(R.id.etCity);
        makeDefault = findViewById(R.id.makeDefault);
        makeDefaultLayout = findViewById(R.id.makeDefaultLayout);
        etPincode = findViewById(R.id.etPincode);
        etLandmark = findViewById(R.id.etLandmark);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
