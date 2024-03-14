package com.dam.sharermonkeys.adapterutils;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.dam.sharermonkeys.R;

public class OwesAdapter extends RecyclerView.Adapter<OwesAdapter.ItemVH> {

    //TODO:


    @NonNull
    @Override
    public OwesAdapter.ItemVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull OwesAdapter.ItemVH holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public static class ItemVH extends RecyclerView.ViewHolder {

        CardView recCardOwes;
        TextView tvUser1, tvUser2, tvEuro;
        Button btnPay;

        public ItemVH(@NonNull View itemView) {
            super(itemView);

            recCardOwes = itemView.findViewById(R.id.recCardOwes);
            tvUser1 = itemView.findViewById(R.id.tvUser1);
            tvUser2 = itemView.findViewById(R.id.tvUser2);
            btnPay = itemView.findViewById(R.id.btnPay);
        }
    }
}
