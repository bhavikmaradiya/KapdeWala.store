package com.collabcreation.kapda.Adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.collabcreation.kapda.model.Product;
import com.collabcreation.kapda.model.ProductImage;

import java.util.ArrayList;
import java.util.List;

public class ProductImageAdapter extends PagerAdapter {
    private Context context;
    private List<ProductImage> productImageList;
    private OnImageSelectedListener onImageSelectedListener;
    private Product product;


    public ProductImageAdapter(Context context, List<ProductImage> productImageList, int color, Product product, OnImageSelectedListener onImageSelectedListener) {
        this.productImageList = new ArrayList<>();
        this.productImageList.clear();
        for (int i = 0; i < productImageList.size(); i++) {
            ProductImage image = productImageList.get(i);
            if (image.getProductColor() == color) {
                this.productImageList.add(image);
            }
        }
        this.context = context;
        this.product = product;
        this.onImageSelectedListener = onImageSelectedListener;

    }

    @Override
    public int getCount() {
        return productImageList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        ImageView imageView = new ImageView(container.getContext());
        imageView.setPadding(25, 25, 25, 25);
        imageView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        Glide.with(context)
                .load(productImageList.get(position).getProductImage())
                .into(imageView);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onImageSelectedListener != null) {
                    onImageSelectedListener.onImageSelected(productImageList.get(position), product);
                }
            }
        });

        container.addView(imageView);
        return imageView;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    public interface OnImageSelectedListener {
        void onImageSelected(ProductImage image, Product product);
    }
}
