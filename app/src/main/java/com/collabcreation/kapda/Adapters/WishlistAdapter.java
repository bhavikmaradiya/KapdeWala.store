package com.collabcreation.kapda.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.collabcreation.kapda.Activity.ProductActivity;
import com.collabcreation.kapda.R;
import com.collabcreation.kapda.model.Common;
import com.collabcreation.kapda.model.Product;
import com.collabcreation.kapda.model.WishItem;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import static com.collabcreation.kapda.model.Common.CURRENT_USER;
import static com.collabcreation.kapda.model.Common.KEY_PRODUCT;
import static com.collabcreation.kapda.model.Common.getWishListRefOf;

public class WishlistAdapter extends RecyclerView.Adapter<WishlistAdapter.CartHolder> {
    private List<WishItem> wishList;
    private Context context;

    public WishlistAdapter(List<WishItem> wishLists, Context context) {
        this.wishList = wishLists;
        this.context = context;

    }

    @NonNull
    @Override
    public CartHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CartHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_wishlist, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final CartHolder holder, int position) {
        final Product[] product = {null};
        final WishItem wishItem = wishList.get(holder.getAdapterPosition());

        Common.getProductOf(wishItem.getProductId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    product[0] = dataSnapshot.getValue(Product.class);
                    holder.productName.setText(product[0].getProductName());
                    Glide.with(context)
                            .load(product[0].getProductThumbnail())
                            .into(holder.productImage);
                    if (product[0].isInOffer()) {
                        holder.productOriginalPrice.setVisibility(View.VISIBLE);
                        holder.productOriginalPrice.setText(NumberFormat.getCurrencyInstance(new Locale("en", "in")).format(product[0].getProductOriginalPrice()));
                        holder.productOriginalPrice.setPaintFlags(holder.productOriginalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                        holder.productPrice.setText(NumberFormat.getCurrencyInstance(new Locale("en", "in")).format(product[0].getPayablePrice()));
                    } else {
                        holder.productOriginalPrice.setVisibility(View.INVISIBLE);
                        holder.productPrice.setText(NumberFormat.getCurrencyInstance(new Locale("en", "in")).format(product[0].getPayablePrice()));
                    }


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(new Intent(context, ProductActivity.class).putExtra(KEY_PRODUCT, product[0]).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        });


        holder.remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Common.getWishListRefOf(CURRENT_USER.getUserId()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(wishItem.getProductId())) {
                            if (wishList.size() == 1) {
                                getWishListRefOf(CURRENT_USER.getUserId()).child(wishItem.getProductId()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        wishList.clear();
                                        Toast.makeText(context, "Empty Wishlist", Toast.LENGTH_SHORT).show();
                                        notifyDataSetChanged();
                                    }
                                });
                            } else if (wishList.size() != 0) {
                                getWishListRefOf(CURRENT_USER.getUserId()).child(wishItem.getProductId()).removeValue();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

    }

    @Override
    public int getItemCount() {
        return wishList.size();
    }

    class CartHolder extends RecyclerView.ViewHolder {
        TextView productName, productPrice, productOriginalPrice;
        ImageView productImage;
        ImageView remove;

        CartHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.productThumbnail);
            productName = itemView.findViewById(R.id.productName);
            productPrice = itemView.findViewById(R.id.productPrice);
            remove = itemView.findViewById(R.id.remove);
            productOriginalPrice = itemView.findViewById(R.id.productOriginalPrice);
        }
    }


}
