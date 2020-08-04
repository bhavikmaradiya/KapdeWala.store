package com.collabcreation.kapda;


import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;


/**
 * A simple {@link Fragment} subclass.
 */
public class AddToCartDialog extends BottomSheetDialogFragment {
    private Context context;
    private OnNumberPickedListener onNumberPickedListener;
    private int selected = 1;
    private Button btnCheckOut;
    private TextView productSize, productQuantity;
    private ImageView productImage, increase, decrease;
    private String size, imageUrl;

    public AddToCartDialog(Context context, String size, String imageUrl, OnNumberPickedListener onNumberPickedListener) {
        this.context = context;
        this.size = size;
        this.imageUrl = imageUrl;
        this.onNumberPickedListener = onNumberPickedListener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_to_cart_dialog, container, false);
        productImage = view.findViewById(R.id.productImage);
        productSize = view.findViewById(R.id.productSize);
        btnCheckOut = view.findViewById(R.id.btnCheckOut);
        productQuantity = view.findViewById(R.id.quantity);
        increase = view.findViewById(R.id.increase);
        decrease = view.findViewById(R.id.decrease);
        productSize.setText(size);
        productQuantity.setText(String.valueOf(selected));
        Glide.with(context)
                .load(imageUrl)
                .into(productImage);

        increase.setOnClickListener(v -> {
            if (selected != 10 && selected < 10) {
                selected = selected + 1;
                productQuantity.setText(String.valueOf(selected));
            }
        });

        decrease.setOnClickListener(v -> {
            if (selected != 1 && selected > 1) {
                selected = selected - 1;
                productQuantity.setText(String.valueOf(selected));
            }
        });

        btnCheckOut.setOnClickListener(view1 -> {
            if (selected != 0) {
                onNumberPickedListener.onNuberPicked(selected);
                dismiss();
            } else {
                Toast.makeText(context, "Enter Valid Quantity", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        selected = 1;
    }

    public interface OnNumberPickedListener {
        void onNuberPicked(int quantity);
    }

}
