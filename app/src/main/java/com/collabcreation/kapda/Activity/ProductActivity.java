package com.collabcreation.kapda.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.collabcreation.kapda.Adapters.ColorListAdapter;
import com.collabcreation.kapda.Adapters.ProductAdapter;
import com.collabcreation.kapda.Adapters.ProductImageAdapter;
import com.collabcreation.kapda.Adapters.RelatedProductAdapter;
import com.collabcreation.kapda.Adapters.SizeListAdapter;
import com.collabcreation.kapda.AddToCartDialog;
import com.collabcreation.kapda.R;
import com.collabcreation.kapda.model.CartItem;
import com.collabcreation.kapda.model.Common;
import com.collabcreation.kapda.model.Product;
import com.collabcreation.kapda.model.ProductColor;
import com.collabcreation.kapda.model.ProductImage;
import com.collabcreation.kapda.model.ProductSize;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.rd.PageIndicatorView;
import com.rd.animation.type.AnimationType;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.collabcreation.kapda.model.Common.CURRENT_USER;
import static com.collabcreation.kapda.model.Common.KEY_PRODUCT;
import static com.collabcreation.kapda.model.Common.checkPincodeStatus;
import static com.collabcreation.kapda.model.Common.getCartRef;

public class ProductActivity extends AppCompatActivity implements SizeListAdapter.OnSizeSelectedListener, ColorListAdapter.OnColorSelectedListener, ProductImageAdapter.OnImageSelectedListener, ProductAdapter.OnProductSelectedListener, AddToCartDialog.OnNumberPickedListener {
    Toolbar toolbar;
    ViewPager imageListPager;
    Product product;
    List<Product> relatedProducts;
    ProductImageAdapter productImageAdapter;
    TextView productPrice, productOriginalPrice, offerPercentage, pincodeLable, pincodeStatus, productName, relatedProductLabel, descriptionLabel, productDescription, sizeLabel, sizeChart, colorLabel, colorHurry;
    PageIndicatorView pageIndicatorView;
    RecyclerView sizeListRecyclerView, colorListRecyclerView, relatedProductList;
    String selectedSize = null;
    EditText pincode;
    int selectedColor = 0, colors = 0;
    NestedScrollView nestedScrollView;
    Button addToCart, btnPincode;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);
        initViews();
        initProduct();
        setUPProductImage();
        setUPProductSizeList();
        setUPProductColorList();
        setUPRelatedProducts();


        productName.setText(product.getProductName());
        if (product.getProductDescription() != null) {
            if (!product.getProductDescription().trim().isEmpty()) {
                descriptionLabel.setVisibility(View.VISIBLE);
                productDescription.setVisibility(View.VISIBLE);
                productDescription.setText(product.getProductDescription());
            } else {
                descriptionLabel.setVisibility(View.GONE);
                productDescription.setVisibility(View.GONE);
            }
        } else {
            descriptionLabel.setVisibility(View.GONE);
            productDescription.setVisibility(View.GONE);
        }
        if (CURRENT_USER.getPostcode() != null) {
            if (!CURRENT_USER.getPostcode().trim().isEmpty()) {
                pincode.setText(CURRENT_USER.getPostcode());
            }
        }
        if (product.isInOffer()) {
            offerPercentage.setVisibility(View.VISIBLE);
            productOriginalPrice.setVisibility(View.VISIBLE);
            productOriginalPrice.setText(NumberFormat.getCurrencyInstance(new Locale("en", "in")).format(product.getProductOriginalPrice()));
            productOriginalPrice.setPaintFlags(productOriginalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            productPrice.setText(NumberFormat.getCurrencyInstance(new Locale("en", "in")).format(product.getPayablePrice()));
            offerPercentage.setText("- " + product.getOfferPercentage() + "%");
        } else {
            offerPercentage.setVisibility(View.INVISIBLE);
            productOriginalPrice.setVisibility(View.INVISIBLE);
            productPrice.setText(NumberFormat.getCurrencyInstance(new Locale("en", "in")).format(product.getPayablePrice()));
        }


        addToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (addToCart.getText().toString().toLowerCase().equals("add to cart")) {
                    if (selectedSize == null || selectedColor == 0) {
                        if (selectedSize == null) {
                            Toast.makeText(ProductActivity.this, "Select Size", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ProductActivity.this, "Select Color", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        for (int i = 0; i < product.getProductImageList().size(); i++) {
                            ProductImage image = product.getProductImageList().get(i);
                            if (image.getProductColor() == selectedColor) {
                                AddToCartDialog dialog = new AddToCartDialog(getApplicationContext(), selectedSize, image.getProductImage(), ProductActivity.this);
                                dialog.show(getSupportFragmentManager(), "ADDTOCART");
                                return;
                            }
                        }

                    }
                } else {
                    startActivity(new Intent(getApplicationContext(), CartActivity.class));
                }
            }
        });


        btnPincode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = pincode.getText().toString().trim();
                if (code.length() == 6) {
                    pincode.clearFocus();
                    final ProgressDialog dialog = new ProgressDialog(ProductActivity.this);
                    dialog.setMessage("Checking Pincode : " + code);
                    dialog.setCancelable(false);
                    dialog.show();
                    pincodeStatus.setVisibility(View.GONE);
                    StringRequest pincodeRequest = new StringRequest(Request.Method.GET, checkPincodeStatus(code), new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject object = new JSONObject(response);
                                if (object.getBoolean("success")) {
                                    if (dialog.isShowing()) {
                                        dialog.dismiss();
                                    }
                                    pincode.setBackgroundResource(R.drawable.cartbtnbg);
                                    pincodeStatus.setVisibility(View.VISIBLE);
                                    pincodeStatus.setText("Delivery is available for " + object.getJSONObject("postcode_details").getString("postcode") + "(" + object.getJSONObject("postcode_details").getString("city") + ")");
                                    pincodeStatus.setTextColor(Color.parseColor("#3C8600"));
                                } else {
                                    if (dialog.isShowing()) {
                                        dialog.dismiss();
                                    }
                                    pincodeStatus.setVisibility(View.VISIBLE);
                                    pincode.setBackgroundResource(R.drawable.errorbg);
                                    pincodeStatus.setText("Delivery is not available");
                                    pincodeStatus.setTextColor(Color.parseColor("#C50000"));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            if (error.networkResponse != null) {
                                if (dialog.isShowing()) {
                                    dialog.dismiss();
                                }
                                if (error.networkResponse.statusCode == 403) {
                                    if (dialog.isShowing()) {
                                        dialog.dismiss();
                                    }
                                    pincode.setBackgroundResource(R.drawable.errorbg);
                                    pincodeStatus.setText("City/State not found for this pincode");
                                    pincodeStatus.setVisibility(View.VISIBLE);
                                    pincodeStatus.setTextColor(Color.parseColor("#C50000"));
                                }


                            } else {
                                if (dialog.isShowing()) {
                                    dialog.dismiss();
                                }
                                Toast.makeText(ProductActivity.this, "Try again!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    RequestQueue queue = Volley.newRequestQueue(ProductActivity.this);
                    queue.add(pincodeRequest);

                } else {
                    Toast.makeText(ProductActivity.this, "Enter valid pincode", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    private void addToCart(int quantity) {
        final ProgressDialog progressDialog = new ProgressDialog(ProductActivity.this);
        progressDialog.setMessage("Adding to cart...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        String key = getCartRef().push().getKey();
        CartItem cartItem = new CartItem(key, product.getProductId(), selectedColor, quantity, selectedSize);
        for (int i = 0; i < product.getProductImageList().size(); i++) {
            ProductImage image = product.getProductImageList().get(i);
            if (image.getProductColor() == selectedColor) {
                for (int j = 0; j < product.getProductSizeList().size(); j++) {
                    if (product.getProductSizeList().get(j).getProductSize().equals(cartItem.getSize())) {
                        ProductSize size = product.getProductSizeList().get(j);
                        for (int k = 0; k < size.getProductColorList().size(); k++) {
                            if (size.getProductColorList().get(k).getProductColor() == cartItem.getColor()) {
                                ProductColor color = size.getProductColorList().get(k);
                                cartItem.setTotalQuantity(color.getProductQuantity());
                                cartItem.setImage(image.getProductImage());
                                getCartRef().child(key).setValue(cartItem).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        if (progressDialog.isShowing()) {
                                            addToCart.setText("Go to cart");
                                            progressDialog.dismiss();
                                        }
                                    }
                                });
                                return;
                            }
                        }
                        return;
                    }

                }

                return;
            }


        }
    }

    private void setUPRelatedProducts() {
        relatedProducts = new ArrayList<>();
        final RelatedProductAdapter relatedProductAdapter = new RelatedProductAdapter(getApplicationContext(), relatedProducts, ProductActivity.this);
        relatedProductList.setLayoutManager(new GridLayoutManager(getApplicationContext(), 1, RecyclerView.HORIZONTAL, false));
        relatedProductList.setAdapter(relatedProductAdapter);
        if (product.getCategoryId() != null) {
            Common.getProductRef().orderByChild("categoryId").equalTo(product.getCategoryId()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    relatedProducts.clear();
                    if (dataSnapshot.hasChildren()) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            if (!snapshot.getKey().equals(product.getProductId())) {
                                Log.d("ID", "onDataChange: " + snapshot.getKey());
                                relatedProducts.add(snapshot.getValue(Product.class));
                                relatedProductAdapter.notifyDataSetChanged();
                            }
                        }

                    } else if (relatedProducts.isEmpty()) {
                        relatedProductLabel.setVisibility(View.GONE);
                        relatedProductList.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } else {
            relatedProducts.clear();
            relatedProductAdapter.notifyDataSetChanged();
            relatedProductLabel.setVisibility(View.GONE);
            relatedProductList.setVisibility(View.GONE);
        }
    }

    private void setUPProductColorList() {
        colorListRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), RecyclerView.HORIZONTAL, false));
        colorListRecyclerView.setHasFixedSize(true);
        colorListRecyclerView.setAdapter(new ColorListAdapter(true, product.getProductColorList(), getApplicationContext(), ProductActivity.this));
    }

    private void setUPProductSizeList() {
        sizeListRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), RecyclerView.HORIZONTAL, false));
        sizeListRecyclerView.setHasFixedSize(true);
        sizeListRecyclerView.setAdapter(new SizeListAdapter(product.getProductSizeList(), getApplicationContext(), ProductActivity.this));
    }

    private void setUPProductImage() {
        productImageAdapter = new ProductImageAdapter(getApplicationContext(), product.getProductImageList(), product.getProductColorList().get(0).getProductColor(), product, this);
        imageListPager.setAdapter(productImageAdapter);
        imageListPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                pageIndicatorView.setSelection(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void initProduct() {
        product = (Product) getIntent().getSerializableExtra(Common.KEY_PRODUCT);
    }

    private void initViews() {
        productName = findViewById(R.id.productName);
        pageIndicatorView = findViewById(R.id.pageIndicatorView);
        pageIndicatorView.setAnimationType(AnimationType.THIN_WORM);
        productOriginalPrice = findViewById(R.id.productOriginalPrice);
        productPrice = findViewById(R.id.productPrice);
        offerPercentage = findViewById(R.id.offerPercentage);
        sizeListRecyclerView = findViewById(R.id.sizeListRecyclerView);
        nestedScrollView = findViewById(R.id.nestedScrollView);
        toolbar = findViewById(R.id.toolbar);
        pincode = findViewById(R.id.et_pincode);
        pincodeLable = findViewById(R.id.pincodeLabel);
        pincodeStatus = findViewById(R.id.pincodeStatus);
        btnPincode = findViewById(R.id.btn_pincode);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.app_name);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        imageListPager = findViewById(R.id.imageListPager);
        productDescription = findViewById(R.id.productDescription);
        descriptionLabel = findViewById(R.id.descriptionLabel);
        relatedProductList = findViewById(R.id.relatedProductList);
        relatedProductLabel = findViewById(R.id.relatedProductLabel);
        sizeLabel = findViewById(R.id.sizeLabel);
//        buyNow = findViewById(R.id.buyNow);
        addToCart = findViewById(R.id.addToCart);
        colorHurry = findViewById(R.id.colorHurry);
        colorHurry.setVisibility(View.GONE);
        colorLabel = findViewById(R.id.colorLabel);
        colorListRecyclerView = findViewById(R.id.colorListRecyclerView);
        sizeChart = findViewById(R.id.sizeChart);
        sizeLabel.setPaintFlags(sizeLabel.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        pincodeLable.setPaintFlags(pincodeLable.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        relatedProductLabel.setPaintFlags(relatedProductLabel.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        colorLabel.setPaintFlags(colorLabel.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        descriptionLabel.setPaintFlags(descriptionLabel.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
    }


    @Override
    public void onSizeSelected(ProductSize productSize) {
        selectedSize = productSize.getProductSize();
        selectedColor = 0;
        addToCart.setText("Add to cart");
        colorHurry.setVisibility(View.GONE);
        colorListRecyclerView.setAdapter(new ColorListAdapter(false, productSize.getProductColorList(), getApplicationContext(), this));

    }


    @Override
    public void onColorSelected(final ProductColor color) {
        colors = color.getProductColor();
        getCartRef().orderByChild("product").equalTo(product.getProductId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null && dataSnapshot.hasChildren()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        if (snapshot.child("color").getValue(Integer.class) == color.getProductColor() && snapshot.child("size").getValue(String.class).equals(selectedSize)) {
                            CartItem cartItem = snapshot.getValue(CartItem.class);
                            if (cartItem.getColor() == color.getProductColor() && cartItem.getSize().equals(selectedSize)) {
                                addToCart.setText("Go to cart");
                                return;
                            } else {
                                addToCart.setText("Add to cart");
                            }
                        } else {
                            addToCart.setText("Add to cart");
                        }

                    }
                } else {
                    addToCart.setText("Add to cart");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        productImageAdapter = new ProductImageAdapter(getApplicationContext(), product.getProductImageList(), color.getProductColor(), product, this);
        imageListPager.setAdapter(productImageAdapter);
        if (color.getProductQuantity() != 0 && color.getProductQuantity() > 0) {
            selectedColor = color.getProductColor();
        } else {
            selectedColor = 0;
        }
        if (color.getProductQuantity() == 2 || color.getProductQuantity() < 2) {
            if (color.getProductQuantity() == 0 || color.getProductQuantity() < 0) {
                colorHurry.setText("Sorry! Out Of Stock");
//                buyNow.setClickable(false);
                addToCart.setEnabled(false);
//                buyNow.setEnabled(false);
                colorHurry.setVisibility(View.VISIBLE);
            } else {
//                buyNow.setClickable(true);
                addToCart.setEnabled(true);
//                buyNow.setEnabled(true);
                colorHurry.setText("Hurry Only " + color.getProductQuantity() + " left");
                colorHurry.setVisibility(View.VISIBLE);
            }
        } else {
//            buyNow.setClickable(true);
            addToCart.setEnabled(true);
//            buyNow.setEnabled(true);
            colorHurry.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            super.onBackPressed();
        }

        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                break;
            case R.id.myCart:
                startActivity(new Intent(getApplicationContext(), CartActivity.class));
                break;
            case R.id.myWishlist:
                startActivity(new Intent(getApplicationContext(), WishListActivity.class));
                break;
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (colors != 0 && selectedSize != null) {
            getCartRef().orderByChild("product").equalTo(product.getProductId()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null && dataSnapshot.hasChildren()) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            if (snapshot.child("color").getValue(Integer.class) == colors && snapshot.child("size").getValue(String.class).equals(selectedSize)) {
                                CartItem cartItem = snapshot.getValue(CartItem.class);
                                if (cartItem.getColor() == colors && cartItem.getSize().equals(selectedSize)) {
                                    addToCart.setText("Go to cart");
                                    return;
                                } else {
                                    addToCart.setText("Add to cart");
                                }
                            } else {
                                addToCart.setText("Add to cart");
                            }

                        }
                    } else {
                        addToCart.setText("Add to cart");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    @Override
    public void onImageSelected(ProductImage image, Product product) {
        startActivity(new Intent(getApplicationContext(), ImageFullViewActivity.class).putExtra(Common.KEY_PRODUCT, product).putExtra(Common.SELECTED_COLOR, image.getProductColor()));
    }

    @Override
    public void onProductSelected(Product product) {
        startActivity(new Intent(getApplicationContext(), ProductActivity.class).putExtra(KEY_PRODUCT, product));
    }

    @Override
    public void onWishButtonClick(Product product, int position) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.product_menu, menu);
        return true;
    }

    @Override
    public void onNuberPicked(int quantity) {
        addToCart(quantity);
    }
}
