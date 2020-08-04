package com.collabcreation.kapda.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.collabcreation.kapda.Adapters.BannerSlideAdapter;
import com.collabcreation.kapda.Adapters.CategoryAdapter;
import com.collabcreation.kapda.Adapters.ProductAdapter;
import com.collabcreation.kapda.R;
import com.collabcreation.kapda.model.Banner;
import com.collabcreation.kapda.model.Category;
import com.collabcreation.kapda.model.Common;
import com.collabcreation.kapda.model.Product;
import com.collabcreation.kapda.model.ProductColor;
import com.collabcreation.kapda.model.ProductImage;
import com.collabcreation.kapda.model.ProductSize;
import com.collabcreation.kapda.model.PromoCode;
import com.collabcreation.kapda.model.SubCategory;
import com.collabcreation.kapda.model.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.huanhailiuxin.coolviewpager.CoolViewPager;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.collabcreation.kapda.model.Common.CURRENT_USER;
import static com.collabcreation.kapda.model.Common.KEY_PRODUCT;
import static com.collabcreation.kapda.model.Common.KEY_USER;
import static com.collabcreation.kapda.model.Common.getBannerRef;
import static com.collabcreation.kapda.model.Common.getCategoriesRef;
import static com.collabcreation.kapda.model.Common.getProductRef;
import static com.collabcreation.kapda.model.Common.getPromoRef;
import static com.collabcreation.kapda.model.Common.getWishListRefOf;

public class MainActivity extends AppCompatActivity implements CategoryAdapter.OnCategorySelectedListener, NavigationView.OnNavigationItemSelectedListener, ProductAdapter.OnProductSelectedListener {
    CoolViewPager bannerPager;
    List<Banner> bannerList;
    BannerSlideAdapter bannerSlideAdapter;
    RecyclerView categoryListRecyclerView, productListRecyclerView;
    CategoryAdapter categoryAdapter;
    List<Category> categoryList;
    DrawerLayout drawerLayout;
    LinearLayout container;
    NavigationView navigationView;
    ProductAdapter productAdapter;
    Toolbar toolbar;
    CircleImageView userProfile;
    TextView userName;
    List<Product> productList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        CURRENT_USER = (User) getIntent().getSerializableExtra(KEY_USER);
        initViews();
        setUPNavigation();
        setUPBanner();
        setUPCategory();

        List<ProductColor> productColorList = new ArrayList<>();
        productColorList.clear();
        productColorList.add(new ProductColor(Color.RED, 1));
        productColorList.add(new ProductColor(Color.WHITE, 1));
        productColorList.add(new ProductColor(Color.BLACK, 1));
        List<ProductImage> productImageList = new ArrayList<>();
        productImageList.clear();
        productImageList.add(new ProductImage(Color.RED, "https://cdn.shopify.com/s/files/1/2222/6375/products/weird_800x.png?v=1506086327"));
        productImageList.add(new ProductImage(Color.WHITE, "https://m.media-amazon.com/images/I/B1JMiB+-dyS._CLa%7C2140,2000%7C618ZudYXLyL.png%7C0,0,2140,2000+645.0,529.0,809.0,971.0.png"));
        productImageList.add(new ProductImage(Color.BLACK, "https://www.kindpng.com/picc/m/63-635260_six-pack-of-peaks-t-shirt-hd-png.png"));
        productImageList.add(new ProductImage(Color.RED, "https://encrypted-tbn0.gstatic.com/images?q=tbn%3AANd9GcTOim0bmN2FnP_tk6i_EUZVbGZv0WnLKP2J72G-1uZ9DGhDI2gf"));
        productImageList.add(new ProductImage(Color.RED, "https://image.freepik.com/free-vector/legends-are-born-january-typography-design-t-shirt_68166-399.jpg"));
        List<ProductSize> productSizeList = new ArrayList<>();
        productSizeList.clear();
        ProductSize size = new ProductSize("S", productColorList);
        ProductSize size2 = new ProductSize("M", productColorList);
        productSizeList.add(size);
        productSizeList.add(size2);
        String key = getProductRef().push().getKey();
        final Product product = new Product("Here Come the Sun T-shirt design", key, 150, "https://www.buytshirtdesigns.net/wp-content/uploads/2018/12/Here-Come-the-Sun-Tshirt.jpg");
        product.setProductColorList(productColorList);
        product.setProductImageList(productImageList);
        product.setProductSizeList(productSizeList);
        product.setCategoryId("-LzlDIXD6Y8UCxe_wk_-");
        product.setInOffer(false, 0);
//        getProductRef().child(key).setValue(product);
//        .addOnSuccessListener(new OnSuccessListener<Void>() {
//            @Override
//            public void onSuccess(Void aVoid) {
//                String key = getProductRef().push().getKey();
//                product.setCategoryId("-LzlDQGN9iWpkb7W43O9");
//                getProductRef().child(key).setValue(product);
//            }
//        });
        String pId = getPromoRef().push().getKey();
        PromoCode promoCode = new PromoCode(50, product.getCategoryId(), false, false, "KAPDEWALA50", pId);
//        getPromoRef().child(pId).setValue(promoCode);


    }

    private void setUPProducts() {
        productList = new ArrayList<>();
        productListRecyclerView.setHasFixedSize(true);
        productListRecyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2, RecyclerView.VERTICAL, false));
        productAdapter = new ProductAdapter(productList, MainActivity.this, MainActivity.this);
        productListRecyclerView.setAdapter(productAdapter);

        getProductRef().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    productList.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        productList.add(snapshot.getValue(Product.class));
                        productAdapter.notifyDataSetChanged();

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setUPNavigation() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            private float scaleFactor = 4f;

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                float slideX = drawerView.getWidth() * slideOffset;
                container.setTranslationX(slideX);
                container.setScaleX(1 - (slideOffset / scaleFactor));
                container.setScaleY(1 - (slideOffset / scaleFactor));
            }
        };
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        toggle.setDrawerSlideAnimationEnabled(false);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void setUPCategory() {
        categoryList = new ArrayList<>();
        categoryListRecyclerView.setHasFixedSize(true);
        categoryListRecyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 1, RecyclerView.HORIZONTAL, false));
        categoryAdapter = new CategoryAdapter(MainActivity.this, categoryList, MainActivity.this);
        categoryListRecyclerView.setAdapter(categoryAdapter);

        getCategoriesRef().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    categoryList.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        categoryList.add(snapshot.getValue(Category.class));
                        categoryAdapter.notifyDataSetChanged();

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setUPBanner() {
        bannerList = new ArrayList<>();
        bannerSlideAdapter = new BannerSlideAdapter(MainActivity.this, bannerList);
        getBannerRef().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    bannerList.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        bannerList.add(snapshot.getValue(Banner.class));
                        bannerPager.setVisibility(View.VISIBLE);

                    }
                    bannerPager.setAdapter(bannerSlideAdapter);
                } else {
                    if (bannerList.isEmpty()) {
                        bannerPager.setVisibility(View.GONE);
                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void initViews() {
        bannerPager = findViewById(R.id.bannerPager);
        toolbar = findViewById(R.id.toolbar);
        navigationView = findViewById(R.id.nav_view);
        drawerLayout = findViewById(R.id.drawerLayout);
        categoryListRecyclerView = findViewById(R.id.categoryList);
        productListRecyclerView = findViewById(R.id.productList);
        container = findViewById(R.id.mainContain);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
    }

    @Override
    public void onCategorySelected(Category category) {
        SubCategory subCategory = category.getSubCategoryList().get(0);

        Toast.makeText(this, subCategory.getCategoryName(), Toast.LENGTH_SHORT).show();

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.logout:
                AlertDialog.Builder ad = new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Logout")
                        .setMessage("are you sure about logout?")
                        .setPositiveButton("Logout", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                FirebaseAuth.getInstance().signOut();
                                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                                finish();
                            }
                        }).setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                AlertDialog alertDialog = ad.create();
                alertDialog.show();

                break;
            case R.id.policy:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.privacypolicygenerator.info/live.php?token=90LanXUZCVmIjk7oXgZOelKB9n42Voyv")));
                break;
            case R.id.rate:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName())));
                break;
            case R.id.myCart:
                startActivity(new Intent(getApplicationContext(), CartActivity.class));
                break;
            case R.id.myWishlist:
                startActivity(new Intent(getApplicationContext(), WishListActivity.class));
                break;
            case R.id.myAccount:
                startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                break;
            case R.id.browseTs:
                startActivity(new Intent(getApplicationContext(), BrowseProductActivity.class));
                break;
            case R.id.home:
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onProductSelected(Product product) {
        startActivity(new Intent(getApplicationContext(), ProductActivity.class).putExtra(KEY_PRODUCT, product));
    }

    @Override
    public void onWishButtonClick(final Product product, final int position) {
        Common.getWishListRefOf(CURRENT_USER.getUserId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(product.getProductId())) {
                    getWishListRefOf(CURRENT_USER.getUserId()).child(product.getProductId()).removeValue();
                } else {
                    getWishListRefOf(CURRENT_USER.getUserId()).child(product.getProductId()).setValue(product.getProductId()).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(MainActivity.this, "Added to wishlist", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search:
                startActivity(new Intent(getApplicationContext(), BrowseProductActivity.class).putExtra(Common.FROM, "mainActivity"));
                break;
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUPProducts();
        navigationView.setCheckedItem(R.id.home);
        Common.getUserRefOf(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                CURRENT_USER = user;
                View headerView = navigationView.getHeaderView(0);
                userName = headerView.findViewById(R.id.userName);
                userProfile = headerView.findViewById(R.id.userProfile);
                Glide.with(getApplicationContext())
                        .load(user.getProfileUrl())
                        .placeholder(R.drawable.ic_person)
                        .into(userProfile);
                userName.setText(user.getName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
