package com.collabcreation.kapda.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.collabcreation.kapda.R;
import com.collabcreation.kapda.model.ProductSize;

import java.util.List;

public class SizeListAdapter extends RecyclerView.Adapter<SizeListAdapter.SizeHolder> {
    private List<ProductSize> productSizeList;
    private Context context;
    private OnSizeSelectedListener onSizeSelectedListener;
    private int selectedPos = 0;

    public SizeListAdapter(List<ProductSize> productSizeList, Context context, OnSizeSelectedListener onSizeSelectedListener) {
        this.productSizeList = productSizeList;
        this.context = context;
        this.onSizeSelectedListener = onSizeSelectedListener;
    }

    @NonNull
    @Override
    public SizeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SizeHolder(LayoutInflater.from(context).inflate(R.layout.item_size, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SizeHolder holder, final int position) {
        final ProductSize currentSize = productSizeList.get(holder.getAdapterPosition());
        if (selectedPos == position) {
            holder.cardSize.setCardBackgroundColor(Color.parseColor("#C2C2C2"));
            holder.background.setBackgroundResource(R.drawable.border);
            holder.textSize.setTextColor(Color.WHITE);
            onSizeSelectedListener.onSizeSelected(currentSize);
        } else {
            holder.background.setBackgroundResource(R.drawable.nullbg);
            holder.cardSize.setCardBackgroundColor(Color.WHITE);
            holder.textSize.setTextColor(Color.BLACK);
        }

        holder.textSize.setText(currentSize.getProductSize());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedPos != position) {
                    selectedPos = position;
                    notifyDataSetChanged();
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return productSizeList.size();
    }

    class SizeHolder extends RecyclerView.ViewHolder {
        CardView cardSize;
        TextView textSize;
        RelativeLayout background;

        SizeHolder(@NonNull View itemView) {
            super(itemView);
            background = itemView.findViewById(R.id.background);
            textSize = itemView.findViewById(R.id.textSize);
            cardSize = itemView.findViewById(R.id.cardSize);
        }
    }

    public interface OnSizeSelectedListener {
        void onSizeSelected(ProductSize productSize);
    }
}
