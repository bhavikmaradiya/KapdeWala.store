package com.collabcreation.kapda.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.collabcreation.kapda.Adapters.CartAdapter;
import com.collabcreation.kapda.R;
import com.collabcreation.kapda.model.CartItem;
import com.collabcreation.kapda.model.Common;
import com.collabcreation.kapda.model.Product;
import com.collabcreation.kapda.model.ProductColor;
import com.collabcreation.kapda.model.ProductSize;
import com.collabcreation.kapda.model.PromoCode;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CartActivity extends AppCompatActivity {
    RecyclerView cartListRecyclerView;
    Toolbar toolbar;
    List<CartItem> cartItemList;
    TextView payableAmount, totalPriceTv;
    Button btnCheckOut;
    LinearLayout bill;
    Product product;
    LinearLayout emptycart;
    CartAdapter cartAdapter;
    double discount = 0, shipping = 0;
    double totalPayable = 0, totalPrice = 0;
    boolean isSHow = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        initView();
        setUPCartList();

        btnCheckOut.setOnClickListener(v -> {
            isSHow = false;
            final EditText editText = new EditText(getApplicationContext());
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(10, 0, 10, 0);
            editText.setLayoutParams(layoutParams);
            new AlertDialog.Builder(CartActivity.this)
                    .setView(editText)
                    .setTitle("Have Promocode ?")
                    .setPositiveButton("Apply", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (editText.getText().toString().trim().length() > 0) {
                                Common.getProductRef().addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                                        for (int i1 = 0; i1 <= cartItemList.size() - 1; i1++) {
                                            Log.d("I", "onDataChange: " + i1);
                                            final int finalI = i1;
//                                                Log.d("CART POS", "onDataChange: "+ pos+","+  + cartItemList.size());
                                            product = dataSnapshot.child(cartItemList.get(i1).getProduct()).getValue(Product.class);
                                            if (product.getCategoryId() != null) {
                                                Log.d("PRODUCTID", "onDataChange: " + product.getCategoryId());
                                                Common.getPromoRef().orderByChild("promoCode").equalTo(editText.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot1) {
                                                        Log.d("CART POS", "onDataChange: " + finalI + "," + +cartItemList.size());
                                                        if (dataSnapshot1.hasChildren()) {
                                                            for (DataSnapshot dataSnapshot : dataSnapshot1.getChildren()) {
                                                                if (dataSnapshot.getValue() != null) {
                                                                    PromoCode promoCode = dataSnapshot.getValue(PromoCode.class);
                                                                    if (!promoCode.isUsed()) {
                                                                        Log.d("PROMO", "onDataChange: " + promoCode.getPromoId());
                                                                        Log.d("proId", "onDataChange: " + promoCode.getCategoryId() + " " + product.getCategoryId());
                                                                        if (product.getCategoryId().equals(promoCode.getCategoryId())) {
                                                                            if (!isSHow) {
                                                                                startActivity(new Intent(getApplicationContext(), CheckOutActivity.class).putExtra(Common.PROMO, promoCode.getDiscountPrice()));
                                                                                isSHow = true;
                                                                                return;
                                                                            }
                                                                        } else if (finalI == cartItemList.size() - 1) {
                                                                            Toast.makeText(CartActivity.this, "Can't Applicable", Toast.LENGTH_SHORT).show();
                                                                            startActivity(new Intent(getApplicationContext(), CheckOutActivity.class).putExtra(Common.PROMO, 0));
                                                                            return;
                                                                        }
                                                                    } else {
                                                                        Toast.makeText(CartActivity.this, "Promocode is already used", Toast.LENGTH_SHORT).show();
                                                                    }
                                                                } else {
                                                                    Toast.makeText(CartActivity.this, "Invalid Promocode", Toast.LENGTH_SHORT).show();
                                                                    startActivity(new Intent(getApplicationContext(), CheckOutActivity.class).putExtra(Common.PROMO, 0));
                                                                }
                                                            }
                                                        } else {
                                                            if (!isSHow) {
                                                                Toast.makeText(CartActivity.this, "Invalid Promocode", Toast.LENGTH_SHORT).show();
                                                                startActivity(new Intent(getApplicationContext(), CheckOutActivity.class).putExtra(Common.PROMO, 0));
                                                                isSHow = true;
                                                                return;
                                                            }
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                                    }
                                                });

                                            } else if (finalI == cartItemList.size() - 1) {
                                                if (!isSHow) {
                                                    isSHow = true;
                                                    Toast.makeText(CartActivity.this, "Can't Applicable", Toast.LENGTH_SHORT).show();
                                                    startActivity(new Intent(getApplicationContext(), CheckOutActivity.class).putExtra(Common.PROMO, 0));
                                                    return;
                                                }
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            } else {
                                Toast.makeText(CartActivity.this, "Enter Valid Promocode", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(), CheckOutActivity.class).putExtra(Common.PROMO, 0));
                            }
                        }
                    }).setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startActivity(new Intent(getApplicationContext(), CheckOutActivity.class).putExtra(Common.PROMO, 0));
                }
            }).create().show();

        });
    }

    private void setUPCartList() {
        Common.getCartRef().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    totalPayable = 0;
                    totalPrice = 0;
                    discount = 0;
                    shipping = 30;
                    cartItemList.clear();
                    emptycart.setVisibility(View.GONE);
                    cartListRecyclerView.setVisibility(View.VISIBLE);
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        final CartItem cartItem = snapshot.getValue(CartItem.class);
                        Common.getProductOf(cartItem.getProduct()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.getValue() != null) {
                                    Product product = dataSnapshot.getValue(Product.class);
                                    for (int i = 0; i < product.getProductSizeList().size(); i++) {
                                        if (product.getProductSizeList().get(i).getProductSize().equals(cartItem.getSize())) {
                                            ProductSize size = product.getProductSizeList().get(i);
                                            for (int j = 0; j < size.getProductColorList().size(); j++) {
                                                if (size.getProductColorList().get(j).getProductColor() == cartItem.getColor()) {
                                                    ProductColor color = size.getProductColorList().get(j);
                                                    cartItem.setTotalQuantity(color.getProductQuantity());
                                                    Log.d("CART " + product.getProductId(), "onDataChange: " + cartItem.getTotalQuantity());
                                                    if (color.getProductQuantity() != 0 && color.getProductQuantity() > 0 && cartItem.getQuantity() <= cartItem.getTotalQuantity()) {
                                                        if (product.getProductOriginalPrice() != product.getPayablePrice()) {
                                                            discount = discount + ((product.getProductOriginalPrice() - product.getPayablePrice()) * cartItem.getQuantity());
                                                        }
                                                        totalPayable = totalPayable + (product.getPayablePrice() * cartItem.getQuantity());


                                                        payableAmount.setText(NumberFormat.getCurrencyInstance(new Locale("en", "in")).format(totalPayable + 30));

                                                        totalPrice = totalPrice + (product.getProductOriginalPrice() * cartItem.getQuantity());
                                                        bill.setVisibility(View.VISIBLE);
                                                    }
                                                    return;
                                                }
                                            }
                                            return;
                                        }

                                    }

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                        cartItemList.add(cartItem);
                        if (cartAdapter != null) {
                            cartAdapter.notifyDataSetChanged();
                        }
                    }
                } else {
                    cartListRecyclerView.setVisibility(View.GONE);
                    emptycart.setVisibility(View.VISIBLE);
                    bill.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        cartAdapter = new CartAdapter(cartItemList, CartActivity.this);
        cartListRecyclerView.setHasFixedSize(true);
        cartListRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        cartListRecyclerView.setAdapter(cartAdapter);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            super.onBackPressed();
        }
        return true;
    }


    private void initView() {
        cartListRecyclerView = findViewById(R.id.cartListRecyclerView);
        toolbar = findViewById(R.id.toolbar);
        emptycart = findViewById(R.id.emptycart);
        setSupportActionBar(toolbar);
        bill = findViewById(R.id.bill);
        totalPriceTv = findViewById(R.id.totalPrice);
        payableAmount = findViewById(R.id.payableAmount);
        btnCheckOut = findViewById(R.id.btnCheckOut);
        cartItemList = new ArrayList<>();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        emptycart.setVisibility(View.GONE);
        cartListRecyclerView.setVisibility(View.GONE);
    }


}
