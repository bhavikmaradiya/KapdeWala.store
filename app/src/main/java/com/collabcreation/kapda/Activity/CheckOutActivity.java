package com.collabcreation.kapda.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.collabcreation.kapda.Adapters.CartAdapter;
import com.collabcreation.kapda.R;
import com.collabcreation.kapda.model.Api;
import com.collabcreation.kapda.model.CartItem;
import com.collabcreation.kapda.model.Checksum;
import com.collabcreation.kapda.model.Common;
import com.collabcreation.kapda.model.Constants;
import com.collabcreation.kapda.model.Paytm;
import com.collabcreation.kapda.model.Product;
import com.collabcreation.kapda.model.ProductColor;
import com.collabcreation.kapda.model.ProductSize;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.collabcreation.kapda.model.Common.CURRENT_USER;

public class CheckOutActivity extends AppCompatActivity implements PaytmPaymentTransactionCallback {
    Toolbar toolbar;
    TextView address, name, number;
    List<CartItem> cartItemList;
    RecyclerView itemListRecyclerView;
    ProgressDialog dialog;
    double discount = 0, shipping = 0;
    double totalPayable = 0, totalPrice = 0, promoPrice;
    CartAdapter cartAdapter;
    RelativeLayout discountCard, promoCard;
    TextView totalPriceTv, shippingChargeTv, discountAmountTv, payableAmount, promoCodeTv;
    Button changeAddress, btnPlaceOrder;
    List<Boolean> booleans = new ArrayList<>();
    boolean isApplied = false, isDelivery = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_out);
        changeAddress = findViewById(R.id.changeAddress);
        totalPriceTv = findViewById(R.id.totalPrice);
        shippingChargeTv = findViewById(R.id.shippingCharge);
        discountAmountTv = findViewById(R.id.discountAmount);
        payableAmount = findViewById(R.id.payableAmount);
        discountCard = findViewById(R.id.discountCard);
        promoCard = findViewById(R.id.promoCard);
        promoCodeTv = findViewById(R.id.promoCode);
        address = findViewById(R.id.address);
        btnPlaceOrder = findViewById(R.id.btnPlaceOrder);
        name = findViewById(R.id.name);
        dialog = new ProgressDialog(this);
        cartItemList = new ArrayList<>();
        promoPrice = getIntent().getDoubleExtra(Common.PROMO, 0);
        itemListRecyclerView = findViewById(R.id.itemListRecyclerView);
        number = findViewById(R.id.number);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        dialog.setMessage("Loading....");
        dialog.setCancelable(false);
        dialog.show();

        Common.getCartRef().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dialog.isShowing()){
                    dialog.dismiss();
                }
                if (dataSnapshot.hasChildren()) {
                    totalPayable = 0;
                    totalPrice = 0;
                    discount = 0;
                    shipping = 30;
                    isDelivery = false;
                    isApplied = false;
                    cartItemList.clear();
                    booleans.clear();
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
                                                        booleans.add(true);
                                                        totalPayable = totalPayable + (product.getPayablePrice() * cartItem.getQuantity());
                                                        totalPrice = totalPrice + (product.getProductOriginalPrice() * cartItem.getQuantity());
                                                        if (product.getProductOriginalPrice() != product.getPayablePrice()) {
                                                            discount = discount + ((product.getProductOriginalPrice() - product.getPayablePrice()) * cartItem.getQuantity());
                                                        }
                                                        if (!isDelivery) {
                                                            totalPayable = totalPayable + shipping;
                                                            isDelivery = true;
                                                            shippingChargeTv.setText(getFormatedText(shipping));
//
                                                        }

                                                        if (discount != 0) {
                                                            promoCard.setVisibility(View.GONE);
                                                            discountCard.setVisibility(View.VISIBLE);
                                                            discountAmountTv.setText("-" + getFormatedText(discount));
                                                        } else {
                                                            promoCodeTv.setTextColor(Color.parseColor("#2E9B00"));
                                                            if (promoPrice != 0) {
                                                                promoCard.setVisibility(View.VISIBLE);
                                                                promoCodeTv.setText("-" + getFormatedText(promoPrice));
                                                                if (!isApplied) {
                                                                    totalPayable = totalPayable - promoPrice;
                                                                    isApplied = true;
                                                                }

                                                            } else {
                                                                promoCard.setVisibility(View.GONE);
                                                            }

                                                            discountCard.setVisibility(View.GONE);
                                                        }
                                                        payableAmount.setText(getFormatedText(totalPayable));
                                                        totalPriceTv.setText(getFormatedText(totalPrice));


                                                    } else {
                                                        booleans.add(false);
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
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        cartAdapter = new CartAdapter(cartItemList, CheckOutActivity.this);
        itemListRecyclerView.setHasFixedSize(true);
        itemListRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        itemListRecyclerView.setAdapter(cartAdapter);

        changeAddress.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), AddressListActivity.class)));

        btnPlaceOrder.setOnClickListener(v -> {
            if (booleans.contains(false)) {
                Toast.makeText(CheckOutActivity.this, "Recheck Your Orders", Toast.LENGTH_SHORT).show();
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

    @Override
    protected void onResume() {
        super.onResume();
        if (Common.getDefaultPos(getApplicationContext()) != -1) {
            name.setText(Common.getDefaultUser(getApplicationContext()));
            number.setText("+91 " + Common.getDefaultPhoneNo(getApplicationContext()));
            address.setText(Common.getDefaultAddress(getApplicationContext()) + " \n" + Common.getDefaultPincode(getApplicationContext()).toUpperCase() + " - " + Common.getDefaultCity(getApplicationContext()).toUpperCase() + ", " + Common.getDefaultState(getApplicationContext()).toUpperCase());
        } else {
            Toast.makeText(this, "Select Address!", Toast.LENGTH_SHORT).show();
        }

    }

    private String getFormatedText(double text) {
        return NumberFormat.getCurrencyInstance(new Locale("en", "in")).format(text);
    }

    private void generateCheckSum() {

        //getting the tax amount first.
        String txnAmount = payableAmount.getText().toString().trim();

        //creating a retrofit object.
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Api.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        //creating the retrofit api service
        Api apiService = retrofit.create(Api.class);

        //creating paytm object
        //containing all the values required
        final Paytm paytm = new Paytm(
                Constants.M_ID,
                Constants.CHANNEL_ID,
                txnAmount,
                Constants.WEBSITE,
                Constants.CALLBACK_URL,
                Constants.INDUSTRY_TYPE_ID
        );

        //creating a call object from the apiService
        Call<Checksum> call = apiService.getChecksum(
                paytm.getmId(),
                paytm.getOrderId(),
                paytm.getCustId(),
                paytm.getChannelId(),
                paytm.getTxnAmount(),
                paytm.getWebsite(),
                paytm.getCallBackUrl(),
                paytm.getIndustryTypeId()
        );

        //making the call to generate checksum
        call.enqueue(new Callback<Checksum>() {
            @Override
            public void onResponse(Call<Checksum> call, Response<Checksum> response) {

                //once we get the checksum we will initiailize the payment.
                //the method is taking the checksum we got and the paytm object as the parameter
                initializePaytmPayment(response.body().getChecksumHash(), paytm);
            }

            @Override
            public void onFailure(Call<Checksum> call, Throwable t) {

            }
        });
    }



    private void initializePaytmPayment(String checksumHash, Paytm paytm) {

        //getting paytm service
        PaytmPGService Service = PaytmPGService.getStagingService();

        //use this when using for production
        //PaytmPGService Service = PaytmPGService.getProductionService();

        //creating a hashmap and adding all the values required
        HashMap<String, String> paramMap = new HashMap<>();
        paramMap.put("MID", Constants.M_ID);
        paramMap.put("ORDER_ID", paytm.getOrderId());
        paramMap.put("CUST_ID", paytm.getCustId());
        paramMap.put("CHANNEL_ID", paytm.getChannelId());
        paramMap.put("TXN_AMOUNT", paytm.getTxnAmount());
        paramMap.put("WEBSITE", paytm.getWebsite());
        paramMap.put("CALLBACK_URL", paytm.getCallBackUrl());
        paramMap.put("CHECKSUMHASH", checksumHash);
        paramMap.put("INDUSTRY_TYPE_ID", paytm.getIndustryTypeId());


        //creating a paytm order object using the hashmap
        PaytmOrder order = new PaytmOrder(paramMap);

        //intializing the paytm service
        Service.initialize(order, null);

        //finally starting the payment transaction
        Service.startPaymentTransaction(this, true, true, this);

    }

    @Override
    public void onTransactionResponse(Bundle bundle) {

        Toast.makeText(this, bundle.toString(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void networkNotAvailable() {
        Toast.makeText(this, "Network error", Toast.LENGTH_LONG).show();
    }

    @Override
    public void clientAuthenticationFailed(String s) {
        Toast.makeText(this, s, Toast.LENGTH_LONG).show();
    }

    @Override
    public void someUIErrorOccurred(String s) {
        Toast.makeText(this, s, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onErrorLoadingWebPage(int i, String s, String s1) {
        Toast.makeText(this, s, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBackPressedCancelTransaction() {
        Toast.makeText(this, "Back Pressed", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onTransactionCancel(String s, Bundle bundle) {
        Toast.makeText(this, s + bundle.toString(), Toast.LENGTH_LONG).show();
    }
}
