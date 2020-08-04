package com.collabcreation.kapda.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.util.Log;
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
import com.collabcreation.kapda.model.CartItem;
import com.collabcreation.kapda.model.Common;
import com.collabcreation.kapda.model.Product;
import com.collabcreation.kapda.model.ProductColor;
import com.collabcreation.kapda.model.ProductSize;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import static com.collabcreation.kapda.model.Common.KEY_PRODUCT;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartHolder> {
    private List<CartItem> cartItemList;
    private Context context;

    public CartAdapter(List<CartItem> cartItemList, Context context) {
        this.cartItemList = cartItemList;
        this.context = context;
    }

    @NonNull
    @Override
    public CartHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CartHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final CartHolder holder, int position) {
        final Product[] product = {null};
        final CartItem cartItem = cartItemList.get(holder.getAdapterPosition());

        Common.getProductOf(cartItem.getProduct()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    product[0] = dataSnapshot.getValue(Product.class);
                    holder.productName.setText(product[0].getProductName());
                    Glide.with(context)
                            .load(cartItem.getImage())
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

                    for (int i = 0; i < product[0].getProductSizeList().size(); i++) {
                        if (product[0].getProductSizeList().get(i).getProductSize().equals(cartItem.getSize())) {
                            ProductSize size = product[0].getProductSizeList().get(i);
                            for (int j = 0; j < size.getProductColorList().size(); j++) {
                                if (size.getProductColorList().get(j).getProductColor() == cartItem.getColor()) {
                                    ProductColor color = size.getProductColorList().get(j);
                                    Common.getCartRefOf(cartItem.getItemId()).child("totalQuantity").setValue(color.getProductQuantity());
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

        holder.itemView.setOnClickListener(view -> context.startActivity(new Intent(context, ProductActivity.class).putExtra(KEY_PRODUCT, product[0]).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)));


        if (cartItem.getTotalQuantity() == 0 || cartItem.getTotalQuantity() < 0) {
            holder.totalQuantity.setText("Out Of Stock");
            holder.totalQuantity.setVisibility(View.VISIBLE);
        } else {
            holder.totalQuantity.setVisibility(View.GONE);
        }

        if (cartItem.getQuantity() > cartItem.getTotalQuantity() && cartItem.getQuantity() != 1) {
            holder.totalQuantity.setText("Decrease Quantity");
            holder.totalQuantity.setVisibility(View.VISIBLE);
        } else if (cartItem.getTotalQuantity() != 0 && cartItem.getTotalQuantity() > 0) {
            holder.totalQuantity.setVisibility(View.GONE);
        }


        holder.productSize.setText(cartItem.getSize());
        holder.productQuantity.setText(String.valueOf(cartItem.getQuantity()));

        holder.increase.setOnClickListener(v -> {
            Log.d("CART" + cartItem.getItemId(), "onClick: " + cartItem.getTotalQuantity() + " " + cartItem.getQuantity());
            if (cartItem.getQuantity() != 10 && cartItem.getQuantity() + 1 <= cartItem.getTotalQuantity()) {
                cartItem.setQuantity(cartItem.getQuantity() + 1);
                holder.productQuantity.setText(String.valueOf(cartItem.getQuantity()));
                Common.getCartRefOf(cartItem.getItemId()).child("quantity").setValue(cartItem.getQuantity());
            } else if (cartItem.getQuantity() + 1 > cartItem.getTotalQuantity()) {
                Toast.makeText(context, "No more tshirt available", Toast.LENGTH_SHORT).show();
            }


        });
        holder.decrease.setOnClickListener(v -> {
            if (cartItem.getQuantity() != 1) {
                cartItem.setQuantity(cartItem.getQuantity() - 1);
                holder.productQuantity.setText(String.valueOf(cartItem.getQuantity()));
                Common.getCartRefOf(cartItem.getItemId()).child("quantity").setValue(cartItem.getQuantity());
            }


        });

        holder.remove.setOnClickListener(v -> {
            if (cartItemList.size() == 1) {
                Common.getCartRefOf(cartItem.getItemId()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        cartItemList.clear();
                        notifyDataSetChanged();
                    }
                });
            } else if (cartItemList.size() != 0) {
                Common.getCartRefOf(cartItem.getItemId()).removeValue();
            }
        });

    }

    @Override
    public int getItemCount() {
        return cartItemList.size();
    }

    static class CartHolder extends RecyclerView.ViewHolder {
        TextView productName, productPrice, productQuantity, productOriginalPrice, productSize, totalQuantity;
        ImageView productImage, increase, decrease;
        ImageView remove;

        CartHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.productImage);
            productName = itemView.findViewById(R.id.productName);
            productPrice = itemView.findViewById(R.id.productPrice);
            increase = itemView.findViewById(R.id.increase);
            decrease = itemView.findViewById(R.id.decrease);
            productQuantity = itemView.findViewById(R.id.productQuantity);
            remove = itemView.findViewById(R.id.remove);
            productOriginalPrice = itemView.findViewById(R.id.productOriginalPrice);
            productSize = itemView.findViewById(R.id.productSize);
            totalQuantity = itemView.findViewById(R.id.productQuantityError);
        }
    }

}
