package com.collabcreation.kapda.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.collabcreation.kapda.R;
import com.collabcreation.kapda.model.ProductColor;

import java.util.List;

public class ColorListAdapter extends RecyclerView.Adapter<ColorListAdapter.ColorHolder> {
    private List<ProductColor> productColorList;
    private Context context;
    private OnColorSelectedListener onColorSelectedListener;
    private int selectedColor = -1;
    private boolean isFirstTime;

    public ColorListAdapter(boolean isFirstTime, List<ProductColor> productColorList, Context context, OnColorSelectedListener onColorSelectedListener) {
        this.productColorList = productColorList;
        this.context = context;
        this.onColorSelectedListener = onColorSelectedListener;
        this.isFirstTime = isFirstTime;
    }

    @NonNull
    @Override
    public ColorHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ColorHolder(LayoutInflater.from(context).inflate(R.layout.item_color, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ColorHolder holder, final int position) {
        final ProductColor currentColor = productColorList.get(holder.getAdapterPosition());
        if (selectedColor == position) {
            holder.background.setBackgroundResource(R.drawable.border);
            if (!isFirstTime) {
                onColorSelectedListener.onColorSelected(currentColor);
            }
        } else {
            holder.background.setBackgroundResource(R.drawable.nullbg);
        }

        holder.color.setCardBackgroundColor(currentColor.getProductColor());
        holder.itemView.setOnClickListener(v -> {
            if (selectedColor != position) {
                if (!isFirstTime) {
                    selectedColor = position;
//                        onColorSelectedListener.onColorSelected(currentColor);
                    notifyDataSetChanged();
                } else {
                    Toast.makeText(context, "Select Size", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return productColorList.size();
    }

    static class ColorHolder extends RecyclerView.ViewHolder {
        CardView color;
        RelativeLayout background;

        ColorHolder(@NonNull View itemView) {
            super(itemView);
            color = itemView.findViewById(R.id.color);
            background = itemView.findViewById(R.id.background);
        }
    }

    public interface OnColorSelectedListener {
        void onColorSelected(ProductColor color);
    }
}
