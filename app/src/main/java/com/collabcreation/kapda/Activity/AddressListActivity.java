package com.collabcreation.kapda.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.collabcreation.kapda.Adapters.AddressListAdapter;
import com.collabcreation.kapda.R;
import com.collabcreation.kapda.model.Address;
import com.collabcreation.kapda.model.Common;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static com.collabcreation.kapda.model.Common.CURRENT_USER;

public class AddressListActivity extends AppCompatActivity {
    List<Address> addressList;
    RecyclerView addressListRecyclerView;
    Button addNewAddressbtn;
    AddressListAdapter addressListAdapter;
    LinearLayout emptylist;
    Toolbar toolbar;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_list);
        initView();
        addNewAddressbtn.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), UpdateAddressActivity.class).putExtra("isEdit", false).putExtra("total", addressList.size())));

    }

    @Override
    protected void onResume() {
        super.onResume();
        Common.getAddressRef(CURRENT_USER.getUserId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                addressList.clear();
                if (dataSnapshot.hasChildren()) {
                    addressListRecyclerView.setVisibility(View.VISIBLE);
                    emptylist.setVisibility(View.GONE);
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        addressList.add(snapshot.getValue(Address.class));
                        if (addressListAdapter != null) {
                            addressListAdapter.notifyDataSetChanged();
                        }
                    }

                } else {
                    addressListRecyclerView.setVisibility(View.GONE);
                    emptylist.setVisibility(View.VISIBLE);
                }

                addressListAdapter = new AddressListAdapter(getSelected(), getApplicationContext(), addressList);
                addressListRecyclerView.setAdapter(addressListAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
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

    private int getSelected() {
        return getSharedPreferences(CURRENT_USER.getUserId(), Context.MODE_PRIVATE).getInt(Common.ADDRESS_POS, -1);
    }

    private void initView() {
        addressList = new ArrayList<>();
        addNewAddressbtn = findViewById(R.id.newAddressbtn);
        emptylist = findViewById(R.id.emptylist);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        dialog.setMessage("Loading..");
        dialog.show();
        addressListRecyclerView = findViewById(R.id.addressListRecyclerView);
        addressListRecyclerView.setHasFixedSize(true);
        addressListRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

    }


}
