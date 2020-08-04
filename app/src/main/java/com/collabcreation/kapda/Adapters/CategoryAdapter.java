package com.collabcreation.kapda.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.collabcreation.kapda.R;
import com.collabcreation.kapda.model.Category;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryHolder> {
    private Context context;
    private List<Category> categoryList;
    private OnCategorySelectedListener onCategorySelectedListener;

    public CategoryAdapter(Context context, List<Category> categoryList, OnCategorySelectedListener onCategorySelectedListener) {
        this.context = context;
        this.categoryList = categoryList;
        this.onCategorySelectedListener = onCategorySelectedListener;
    }

    @NonNull
    @Override
    public CategoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CategoryHolder(LayoutInflater.from(context).inflate(R.layout.item_category, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final CategoryHolder holder, int position) {
        Category currenttCategory = categoryList.get(holder.getAdapterPosition());
        holder.categoryName.setText(currenttCategory.getCategoryName());
        Glide.with(context)
                .load(currenttCategory.getCategoryPhoto())
                .into(holder.categoryImage);

        holder.itemView.setOnClickListener(v -> onCategorySelectedListener.onCategorySelected(categoryList.get(holder.getAdapterPosition())));

    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    static class CategoryHolder extends RecyclerView.ViewHolder {
        ImageView categoryImage;
        TextView categoryName;

        CategoryHolder(@NonNull View itemView) {
            super(itemView);
            categoryImage = itemView.findViewById(R.id.categoryImage);
            categoryName = itemView.findViewById(R.id.categoryName);
        }
    }

    public interface OnCategorySelectedListener {
        void onCategorySelected(Category category);
    }
}
