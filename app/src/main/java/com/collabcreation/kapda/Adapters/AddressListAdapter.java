package com.collabcreation.kapda.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.collabcreation.kapda.Activity.UpdateAddressActivity;
import com.collabcreation.kapda.R;
import com.collabcreation.kapda.model.Address;
import com.collabcreation.kapda.model.Common;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.List;

import static com.collabcreation.kapda.model.Common.CURRENT_USER;

public class AddressListAdapter extends RecyclerView.Adapter<AddressListAdapter.AddressHolder> {
    private int selected = -1;
    private Context context;
    private List<Address> addressList;

    public AddressListAdapter(int selected, Context context, List<Address> addressList) {
        this.selected = selected;
        this.context = context;
        this.addressList = addressList;
    }

    @NonNull
    @Override
    public AddressHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AddressHolder(LayoutInflater.from(context).inflate(R.layout.item_address, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull AddressHolder holder, final int position) {
        final Address currentAddress = addressList.get(position);
        if (selected == position) {
            holder.radioBtn.setVisibility(View.GONE);
            holder.checked.setVisibility(View.VISIBLE);
            Common.changeAddress(context, currentAddress);
        } else {
            holder.radioBtn.setVisibility(View.VISIBLE);
            holder.radioBtn.setChecked(false);
            holder.checked.setVisibility(View.GONE);
        }

        holder.name.setText(currentAddress.getReceiverName());
        holder.number.setText("+91 " + currentAddress.getPhoneNo());

        holder.address.setText(currentAddress.getAddress() + " \n" + currentAddress.getPostcode().toUpperCase() + " - " + currentAddress.getCity().toUpperCase() + ", " + currentAddress.getState().toUpperCase());

        holder.selectedLayout.setOnClickListener(view -> {
            if (position != selected) {
                selected = position;
                Common.changeAddress(context, currentAddress);
                context.getSharedPreferences(CURRENT_USER.getUserId(), Context.MODE_PRIVATE).edit().putInt(Common.ADDRESS_POS, position).apply();
                notifyDataSetChanged();
            }
        });
        holder.radioBtn.setOnClickListener(view -> {
            if (position != selected) {
                selected = position;
                context.getSharedPreferences(CURRENT_USER.getUserId(), Context.MODE_PRIVATE).edit().putInt(Common.ADDRESS_POS, position).apply();
                Common.changeAddress(context, currentAddress);
                notifyDataSetChanged();
            }
        });

        holder.editAddress.setOnClickListener(v -> context.startActivity(new Intent(context, UpdateAddressActivity.class).putExtra("isEdit", true).putExtra(Common.ADDRESS, currentAddress).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)));

        holder.deleteAddress.setOnClickListener(v -> Common.getAddressRef(CURRENT_USER.getUserId()).child(currentAddress.getAddressId()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                if (selected == position) {
                    selected = -1;
                    context.getSharedPreferences(CURRENT_USER.getUserId(), Context.MODE_PRIVATE).edit().putInt(Common.ADDRESS_POS, -1).apply();
                }
                Toast.makeText(context, "Address Removed!", Toast.LENGTH_SHORT).show();
                notifyDataSetChanged();
            }
        }));

        holder.itemView.setOnClickListener(v -> context.startActivity(new Intent(context, UpdateAddressActivity.class).putExtra("isEdit", true).putExtra(Common.ADDRESS, currentAddress).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)));


    }

    @Override
    public int getItemCount() {
        return addressList.size();
    }

    class AddressHolder extends RecyclerView.ViewHolder {
        private TextView address, name, number;
        ImageView checked;
        private RadioButton radioBtn;
        private LinearLayout selectedLayout, deleteAddress, editAddress;

        AddressHolder(@NonNull View itemView) {
            super(itemView);
            address = itemView.findViewById(R.id.address);
            radioBtn = itemView.findViewById(R.id.radioBtn);
            selectedLayout = itemView.findViewById(R.id.selectedLayout);
            deleteAddress = itemView.findViewById(R.id.delete);
            checked = itemView.findViewById(R.id.checked);
            editAddress = itemView.findViewById(R.id.edit);
            name = itemView.findViewById(R.id.name);
            number = itemView.findViewById(R.id.number);
        }
    }


}
