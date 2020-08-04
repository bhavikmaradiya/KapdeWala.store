package com.collabcreation.kapda.Activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.collabcreation.kapda.Adapters.ChipAdapter;
import com.collabcreation.kapda.Adapters.SearchProductAdapter;
import com.collabcreation.kapda.R;
import com.collabcreation.kapda.model.Category;
import com.collabcreation.kapda.model.Common;
import com.collabcreation.kapda.model.Product;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mancj.materialsearchbar.MaterialSearchBar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.collabcreation.kapda.model.Common.getCategoriesRef;
import static com.collabcreation.kapda.model.Common.getProductRef;

public class BrowseProductActivity extends AppCompatActivity implements ChipAdapter.OnChipStateChangeListener {
    MaterialSearchBar searchBar;
    List<Product> productList;
    List<Category> chipList;
    SearchProductAdapter searchProductAdapter;
    RecyclerView productListRecyclerView, chipListRecyclerView;
    ChipAdapter chipAdapter;
    ImageView back;
    boolean isChipSelected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse_product);
        initView();


        searchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().isEmpty()) {
                    Query query = getProductRef().orderByChild("productName").startAt(s.toString()).endAt(s.toString() + "\uf8ff");
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            productList.clear();
                            if (dataSnapshot.hasChildren()) {
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    productList.add(snapshot.getValue(Product.class));
                                    searchProductAdapter.notifyDataSetChanged();
                                }
                            } else {
                                searchProductAdapter.notifyDataSetChanged();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                } else {
                    if (!isChipSelected) {
                        loadProducts();
                    }
                }
            }
        });

        searchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {
                if (!enabled) {
                    back.setVisibility(View.VISIBLE);
                    if (!isChipSelected) {
                        loadProducts();
                    }
                } else {
                    back.setVisibility(View.GONE);
                }
            }

            @Override
            public void onSearchConfirmed(CharSequence s) {
                if (!s.toString().isEmpty()) {
                    Query query = getProductRef().orderByChild("productName").startAt(s.toString()).endAt(s.toString() + "\uf8ff");
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            productList.clear();
                            if (dataSnapshot.hasChildren()) {
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    productList.add(snapshot.getValue(Product.class));
                                    searchProductAdapter.notifyDataSetChanged();
                                }
                            } else {
                                searchProductAdapter.notifyDataSetChanged();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                } else {
                    if (!isChipSelected) {
                        loadProducts();
                    }
                }
            }

            @Override
            public void onButtonClicked(int buttonCode) {
                if (buttonCode == MaterialSearchBar.BUTTON_NAVIGATION) {
                    if (!isChipSelected) {
                        loadProducts();
                        Toast.makeText(BrowseProductActivity.this, "nav", Toast.LENGTH_SHORT).show();
                    }
                }
                Toast.makeText(BrowseProductActivity.this, "Button click " + buttonCode, Toast.LENGTH_SHORT).show();
                Log.d("Button", "onButtonClicked: " + buttonCode);

            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BrowseProductActivity.super.onBackPressed();
            }
        });
    }

    private void initView() {
        productList = new ArrayList<>();
        chipList = new ArrayList<>();
        back = findViewById(R.id.back);
        productListRecyclerView = findViewById(R.id.productList);
        chipListRecyclerView = findViewById(R.id.chipList);
        searchBar = findViewById(R.id.searchBar);
        if (getIntent().getStringExtra(Common.FROM) != null) {
            searchBar.requestFocus();
            InputMethodManager imm = (InputMethodManager)getSystemService(this.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,InputMethodManager.HIDE_IMPLICIT_ONLY);

        }
        setUPDefaultProducts();
        setUPChip();
    }

    private void setUPChip() {
        chipAdapter = new ChipAdapter(getApplicationContext(), chipList, this);
        chipListRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), RecyclerView.HORIZONTAL, false));
        chipListRecyclerView.setHasFixedSize(true);
        chipListRecyclerView.setAdapter(chipAdapter);
        getCategoriesRef().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chipList.clear();
                if (dataSnapshot.hasChildren()) {
                    chipListRecyclerView.setVisibility(View.VISIBLE);
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        chipList.add(snapshot.getValue(Category.class));
                    }
                    Collections.reverse(chipList);
                    chipAdapter.notifyDataSetChanged();

                } else {
                    chipListRecyclerView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setUPDefaultProducts() {
        productListRecyclerView.setHasFixedSize(true);
        productListRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        loadProducts();


    }

    private void loadProducts() {
        getProductRef().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                productList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    productList.add(snapshot.getValue(Product.class));
                    Log.d("PRODUCT", "onDataChange: " + snapshot.getValue(Product.class).getProductName());

                }
                if (searchProductAdapter == null) {
                    searchProductAdapter = new SearchProductAdapter(getApplicationContext(), productList);
                    productListRecyclerView.setAdapter(searchProductAdapter);
                }
                Collections.reverse(productList);
                searchProductAdapter.notifyDataSetChanged();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onChipSelected(@NonNull String id) {
        isChipSelected = true;
        getProductRef().orderByChild("categoryId").equalTo(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                productList.clear();
                if (dataSnapshot.hasChildren()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        productList.add(snapshot.getValue(Product.class));
                    }
                    Collections.reverse(productList);
                    searchProductAdapter.notifyDataSetChanged();
                }
                searchProductAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onChipClear() {
        isChipSelected = false;
        loadProducts();
    }
}
