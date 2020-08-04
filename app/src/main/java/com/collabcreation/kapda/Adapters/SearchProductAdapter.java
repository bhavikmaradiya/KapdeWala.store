package com.collabcreation.kapda.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.collabcreation.kapda.Activity.ProductActivity;
import com.collabcreation.kapda.R;
import com.collabcreation.kapda.model.Product;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import static com.collabcreation.kapda.model.Common.KEY_PRODUCT;

public class SearchProductAdapter extends RecyclerView.Adapter<SearchProductAdapter.ProductHolder> {
    Context context;
    List<Product> productList;


    public SearchProductAdapter(Context context, List<Product> productList) {
        this.context = context;
        this.productList = productList;
    }

    @NonNull
    @Override
    public ProductHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ProductHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.search_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ProductHolder holder, int position) {
        final Product currentProduct = productList.get(holder.getAdapterPosition());
        holder.productName.setText(currentProduct.getProductName());
        Glide.with(context)
                .load(currentProduct.getProductThumbnail())
                .into(holder.productImage);
        if (currentProduct.isInOffer()) {
            holder.offerBadge.setVisibility(View.VISIBLE);
            holder.offerBadge.setText("-"+currentProduct.getOfferPercentage() + "%");
        } else {
            holder.offerBadge.setVisibility(View.INVISIBLE);
        }
        if (currentProduct.isInOffer()) {
            holder.productOriginalPrice.setVisibility(View.VISIBLE);
            holder.productOriginalPrice.setText(NumberFormat.getCurrencyInstance(new Locale("en", "in")).format(currentProduct.getProductOriginalPrice()));
            holder.productOriginalPrice.setPaintFlags(holder.productOriginalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.productPrice.setText(NumberFormat.getCurrencyInstance(new Locale("en", "in")).format(currentProduct.getPayablePrice()));
        } else {
            holder.productOriginalPrice.setVisibility(View.INVISIBLE);
            holder.productPrice.setText(NumberFormat.getCurrencyInstance(new Locale("en", "in")).format(currentProduct.getPayablePrice()));
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(new Intent(context, ProductActivity.class).putExtra(KEY_PRODUCT, currentProduct).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        });

    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    class ProductHolder extends RecyclerView.ViewHolder {
        TextView productName, productPrice, productOriginalPrice, offerBadge;
        ImageView productImage;

         ProductHolder(@NonNull View itemView) {
            super(itemView);
            offerBadge = itemView.findViewById(R.id.offerBadge);
            productImage = itemView.findViewById(R.id.productImage);
            productName = itemView.findViewById(R.id.productName);
            productOriginalPrice = itemView.findViewById(R.id.productOriginalPrice);
            productPrice = itemView.findViewById(R.id.productPrice);
        }
    }
}
