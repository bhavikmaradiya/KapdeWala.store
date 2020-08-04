package com.collabcreation.kapda.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.collabcreation.kapda.R;
import com.collabcreation.kapda.model.Category;

import java.util.List;

public class ChipAdapter extends RecyclerView.Adapter<ChipAdapter.ChipHolder> {
    private Context context;
    private List<Category> categoryList;
    private int selected = -1;
    private OnChipStateChangeListener onChipStateChangeListener;

    public ChipAdapter(Context context, List<Category> categoryList, OnChipStateChangeListener onChipStateChangeListener) {
        this.context = context;
        this.categoryList = categoryList;
        this.onChipStateChangeListener = onChipStateChangeListener;
    }

    @NonNull
    @Override
    public ChipHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ChipHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chip, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ChipHolder holder, final int position) {
        final Category currentCategoty = categoryList.get(position);
        if (selected == position) {
            holder.close.setVisibility(View.VISIBLE);
        } else {
            holder.close.setVisibility(View.GONE);
        }
        holder.categotyName.setText(currentCategoty.getCategoryName());

        holder.itemView.setOnClickListener(v -> {
            if (selected != position) {
                selected = position;
                onChipStateChangeListener.onChipSelected(currentCategoty.getCategoryId());
                notifyDataSetChanged();
            }
        });

        holder.close.setOnClickListener(v -> {
            selected = -1;
            onChipStateChangeListener.onChipClear();
            notifyDataSetChanged();
        });
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    static class ChipHolder extends RecyclerView.ViewHolder {
        TextView categotyName;
        ImageView close;

        ChipHolder(@NonNull View itemView) {
            super(itemView);
            categotyName = itemView.findViewById(R.id.categotyName);
            close = itemView.findViewById(R.id.close);
        }
    }

    public interface OnChipStateChangeListener {
        void onChipSelected(@NonNull String id);
        void onChipClear();
    }
}
