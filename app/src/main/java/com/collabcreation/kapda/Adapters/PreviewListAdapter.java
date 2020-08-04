package com.collabcreation.kapda.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.collabcreation.kapda.R;
import com.collabcreation.kapda.model.ProductImage;

import java.util.List;

public class PreviewListAdapter extends RecyclerView.Adapter<PreviewListAdapter.PreviewHolder> {
    Context context;
    List<ProductImage> productImageList;
    OnImageSelectedListener onImageSelectedListener;
    int selected = -1;

    public PreviewListAdapter(Context context, List<ProductImage> productImageList, OnImageSelectedListener onImageSelectedListener) {
        this.context = context;
        this.productImageList = productImageList;
        this.onImageSelectedListener = onImageSelectedListener;
    }

    @NonNull
    @Override
    public PreviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PreviewHolder(LayoutInflater.from(context).inflate(R.layout.item_preview_image, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull PreviewHolder holder, final int position) {
        if (selected == position) {
            holder.previewImage.setBackgroundResource(R.drawable.previewbg);
        } else {
            holder.previewImage.setBackgroundResource(R.drawable.emptybg);
        }
        Glide.with(context)
                .load(productImageList.get(position).getProductImage())
                .into(holder.previewImage);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selected = position;
                onImageSelectedListener.onImageSelected(productImageList.get(position).getProductImage(),position);
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return productImageList.size();
    }

    class PreviewHolder extends RecyclerView.ViewHolder {
        ImageView previewImage;

        public PreviewHolder(@NonNull View itemView) {
            super(itemView);
            previewImage = itemView.findViewById(R.id.previewImage);
        }
    }

    public interface OnImageSelectedListener {
        void onImageSelected(String url, int pos);
    }
}
