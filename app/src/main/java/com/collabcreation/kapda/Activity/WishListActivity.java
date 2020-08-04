package com.collabcreation.kapda.Activity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.collabcreation.kapda.Adapters.WishlistAdapter;
import com.collabcreation.kapda.R;
import com.collabcreation.kapda.model.WishItem;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static com.collabcreation.kapda.model.Common.getWishListRef;

public class WishListActivity extends AppCompatActivity {
    Toolbar toolbar;
    RecyclerView wishListRecyclerView;
    WishlistAdapter wishlistAdapter;
    List<WishItem> wishItems;
    LinearLayout emptylist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wish_list);
        initView();
        setUPWishList();
    }

    private void setUPWishList() {
        wishItems = new ArrayList<>();
        wishListRecyclerView.setHasFixedSize(true);
        wishListRecyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2, RecyclerView.VERTICAL, false));
        wishlistAdapter = new WishlistAdapter(wishItems, getApplicationContext());
        wishListRecyclerView.setAdapter(wishlistAdapter);
        getWishListRef().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    wishListRecyclerView.setVisibility(View.VISIBLE);
                    emptylist.setVisibility(View.GONE);
                    wishItems.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        wishItems.add(new WishItem(snapshot.getValue(String.class)));
                        wishlistAdapter.notifyDataSetChanged();

                    }
                } else {
                    wishListRecyclerView.setVisibility(View.GONE);
                    emptylist.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void initView() {
        toolbar = findViewById(R.id.toolbar);
        wishListRecyclerView = findViewById(R.id.wishListRecyclerView);
        emptylist = findViewById(R.id.emptylist);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            super.onBackPressed();
        }
        return true;
    }
}
