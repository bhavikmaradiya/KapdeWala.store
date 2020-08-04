package com.collabcreation.kapda.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.collabcreation.kapda.R;
import com.collabcreation.kapda.model.Product;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class RelatedProductAdapter extends RecyclerView.Adapter<RelatedProductAdapter.ProductHolder> {
    Context context;
    List<Product> productList;
    ProductAdapter.OnProductSelectedListener onProductSelectedListener;

    public RelatedProductAdapter(Context context, List<Product> productList, ProductAdapter.OnProductSelectedListener onProductSelectedListener) {
        this.context = context;
        this.productList = productList;
        this.onProductSelectedListener = onProductSelectedListener;
    }

    @NonNull
    @Override
    public ProductHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ProductHolder(LayoutInflater.from(context).inflate(R.layout.item_related_product, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ProductHolder holder, int position) {
        final Product product = productList.get(holder.getAdapterPosition());
        Glide.with(context)
                .load(product.getProductThumbnail())
                .into(holder.produchImage);
        holder.productName.setText(product.getProductName());
        holder.productPrice.setText(NumberFormat.getCurrencyInstance(new Locale("en", "in")).format(product.getProductOriginalPrice()).trim());
        holder.itemProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onProductSelectedListener.onProductSelected(product);
            }
        });

    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    class ProductHolder extends RecyclerView.ViewHolder {
        ImageView produchImage;
        TextView productName, productPrice;
        LinearLayout itemProduct;

        ProductHolder(@NonNull View itemView) {
            super(itemView);
            produchImage = itemView.findViewById(R.id.productImage);
            productName = itemView.findViewById(R.id.productName);
            productPrice = itemView.findViewById(R.id.price);
            itemProduct = itemView.findViewById(R.id.product);
        }
    }
}
