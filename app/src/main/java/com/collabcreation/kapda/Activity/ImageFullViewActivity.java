package com.collabcreation.kapda.Activity;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.collabcreation.kapda.Adapters.PreviewListAdapter;
import com.collabcreation.kapda.R;
import com.collabcreation.kapda.model.Common;
import com.collabcreation.kapda.model.Product;
import com.collabcreation.kapda.model.ProductImage;
import com.github.chrisbanes.photoview.PhotoView;

import java.util.ArrayList;
import java.util.List;

public class ImageFullViewActivity extends AppCompatActivity implements PreviewListAdapter.OnImageSelectedListener {
    Product product;
    PhotoView photoView;
    int selectedColor;
    Toolbar toolbar;
    RecyclerView previewImageList;
    List<ProductImage> productImageList;
    PreviewListAdapter previewListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_full_view);
        productImageList = new ArrayList<>();
        previewListAdapter = new PreviewListAdapter(getApplicationContext(), productImageList, this);
        product = (Product) getIntent().getSerializableExtra(Common.KEY_PRODUCT);
        selectedColor = getIntent().getIntExtra(Common.SELECTED_COLOR, product.getProductColorList().get(0).getProductColor());
        toolbar = findViewById(R.id.toolbar);
        photoView = findViewById(R.id.productImage);
        previewImageList = findViewById(R.id.previewImagesList);
        for (int i = 0; i < product.getProductImageList().size(); i++) {
            ProductImage image = product.getProductImageList().get(i);
            if (image.getProductColor() == selectedColor) {
                productImageList.add(image);
                previewListAdapter.notifyDataSetChanged();
                Glide.with(getApplicationContext())
                        .load(productImageList.get(0).getProductImage())
                        .into(photoView);


            }
        }
        previewImageList.setHasFixedSize(true);
        previewImageList.setLayoutManager(new LinearLayoutManager(getApplicationContext(), RecyclerView.HORIZONTAL, false));
        previewImageList.setAdapter(previewListAdapter);
        toolbar.setTitle("");
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

    @Override
    public void onImageSelected(String url, int pos) {
        Glide.with(getApplicationContext())
                .load(url)
                .into(photoView);
        previewImageList.smoothScrollToPosition(pos);
    }
}
