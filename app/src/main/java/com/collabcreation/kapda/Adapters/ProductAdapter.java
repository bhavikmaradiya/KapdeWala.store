package com.collabcreation.kapda.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.collabcreation.kapda.R;
import com.collabcreation.kapda.model.Common;
import com.collabcreation.kapda.model.Product;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import static com.collabcreation.kapda.model.Common.CURRENT_USER;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductHolder> {
    List<Product> productList;
    Context context;
    OnProductSelectedListener onProductSelectedListener;

    public ProductAdapter(List<Product> productList, Context context, OnProductSelectedListener onProductSelectedListener) {
        this.productList = productList;
        this.context = context;
        this.onProductSelectedListener = onProductSelectedListener;
    }

    @NonNull
    @Override
    public ProductHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ProductHolder(LayoutInflater.from(context).inflate(R.layout.item_product, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ProductHolder holder, int position) {
        // TODO: 03-02-2020 Price is to pay, mainPrice is total price if inOffer
        final Product currentProduct = productList.get(holder.getAdapterPosition());
        if (currentProduct.isInOffer()) {
            holder.offerBadge.setVisibility(View.VISIBLE);
            holder.offerBadge.setText("-"+currentProduct.getOfferPercentage() + "%");
        } else {
            holder.offerBadge.setVisibility(View.INVISIBLE);
        }
        holder.price.setText(NumberFormat.getCurrencyInstance(new Locale("en", "in")).format(currentProduct.getProductOriginalPrice()));
        Common.getWishListRefOf(CURRENT_USER.getUserId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(currentProduct.getProductId())) {
                    holder.wishBtn.setImageResource(R.drawable.ic_heart_fill);
                } else {
                    holder.wishBtn.setImageResource(R.drawable.ic_heart);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        holder.itemProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onProductSelectedListener.onProductSelected(currentProduct);
            }
        });

        holder.wishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Common.getWishListRefOf(CURRENT_USER.getUserId()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(currentProduct.getProductId())) {
                            holder.wishBtn.setImageResource(R.drawable.ic_heart);
                        } else {
                            holder.wishBtn.setImageResource(R.drawable.ic_heart_fill);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                onProductSelectedListener.onWishButtonClick(currentProduct, holder.getAdapterPosition());
            }
        });
        holder.productName.setText(currentProduct.getProductName());
        Glide.with(context)
                .load(currentProduct.getProductThumbnail())
                .into(holder.productThumbnail);

    }

    @Override
    public int getItemCount() {
        return productList.size();
    }


    class ProductHolder extends RecyclerView.ViewHolder {
        TextView price, offerBadge, productName;
        ImageView productThumbnail, wishBtn;
        CardView itemProduct;

        ProductHolder(@NonNull View itemView) {
            super(itemView);
            price = itemView.findViewById(R.id.price);
            offerBadge = itemView.findViewById(R.id.offerBadge);
            productThumbnail = itemView.findViewById(R.id.productThumbnail);
            productName = itemView.findViewById(R.id.productName);
            wishBtn = itemView.findViewById(R.id.wishicon);
            itemProduct = itemView.findViewById(R.id.product);
        }
    }

    public interface OnProductSelectedListener {
        void onProductSelected(Product product);

        void onWishButtonClick(Product product, int position);
    }
}
