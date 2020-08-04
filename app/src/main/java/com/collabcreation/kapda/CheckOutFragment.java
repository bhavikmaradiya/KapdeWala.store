package com.collabcreation.kapda;


import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.text.NumberFormat;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 */
public class CheckOutFragment extends BottomSheetDialogFragment {
    Context context;
    double totalPrice, payablePrice, discount, shipping, promoPrice;
    RelativeLayout discountCard, promoCard;
    TextView totalPriceTv, shippingChargeTv, discountAmountTv, payableAmount, promoCodeTv;
    Button btnCheckOut;

    public CheckOutFragment(Context context, double promoPrice, double totalPrice, double payablePrice, double discount, double shipping) {
        this.context = context;
        this.totalPrice = totalPrice;
        this.payablePrice = payablePrice;
        this.discount = discount;
        this.shipping = shipping;
        this.promoPrice = promoPrice;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_check_out, container, false);
        totalPriceTv = view.findViewById(R.id.totalPrice);
        shippingChargeTv = view.findViewById(R.id.shippingCharge);
        discountAmountTv = view.findViewById(R.id.discountAmount);
        payableAmount = view.findViewById(R.id.payableAmount);
        btnCheckOut = view.findViewById(R.id.btnCheckOut);
        discountCard = view.findViewById(R.id.discountCard);
        promoCard = view.findViewById(R.id.promoCard);
        promoCodeTv = view.findViewById(R.id.promoCode);


        totalPriceTv.setText(getFormatedText(totalPrice));
        if (discount != 0) {
            promoCard.setVisibility(View.GONE);
            discountCard.setVisibility(View.VISIBLE);
            discountAmountTv.setText("-" + getFormatedText(discount));
        } else {
            promoCodeTv.setTextColor(Color.parseColor("#2E9B00"));
            if (promoPrice != 0) {
                promoCard.setVisibility(View.VISIBLE);
                promoCodeTv.setText("-" + getFormatedText(promoPrice));
            } else {
                promoCard.setVisibility(View.GONE);
            }

            discountCard.setVisibility(View.GONE);
        }

        if (payablePrice < 600) {
            payableAmount.setText(getFormatedText(payablePrice + shipping));
            shippingChargeTv.setText(getFormatedText(shipping));
            discountAmountTv.setTextColor(Color.parseColor("#303030"));
        }else {
            shippingChargeTv.setText("Free");
            shippingChargeTv.setTextColor(Color.parseColor("#2E9B00"));
            payableAmount.setText(getFormatedText(payablePrice));
        }
        btnCheckOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return view;
    }

    private String getFormatedText(double text) {
        return NumberFormat.getCurrencyInstance(new Locale("en", "in")).format(text);
    }

}
