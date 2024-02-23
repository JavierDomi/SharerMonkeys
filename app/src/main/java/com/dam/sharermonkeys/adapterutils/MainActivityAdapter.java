package com.dam.sharermonkeys.adapterutils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dam.sharermonkeys.R;
import com.dam.sharermonkeys.pojos.Fairshare;

import java.util.ArrayList;

public class MainActivityAdapter extends RecyclerView.Adapter<MainActivityAdapter.ItemVH>
implements View.OnClickListener {

    private ArrayList<Fairshare> fairShareList;
    private View.OnClickListener listener;

    public MainActivityAdapter(ArrayList<Fairshare> fairShareList, View.OnClickListener listener) {

        this.fairShareList = fairShareList;
        this.listener = listener;
    }

    @Override
    public void onClick(View v) {

        listener.onClick(v);

    }

    @NonNull
    @Override
    public ItemVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fairshare_item, parent, false);

        v.setOnClickListener(listener);

        ItemVH ivh = new ItemVH(v);

        return ivh;

    }

    @Override
    public void onBindViewHolder(@NonNull ItemVH holder, int position) {

        holder.bindItem(fairShareList.get(position));

    }

    @Override
    public int getItemCount() {

        return fairShareList.size();

    }

    public static class ItemVH extends RecyclerView.ViewHolder {

        TextView tvTittle;
        TextView tvDescription;
        TextView tvId;

        public ItemVH(@NonNull View itemView) {
            super(itemView);

            tvTittle = itemView.findViewById(R.id.tvTittle);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvId = itemView.findViewById(R.id.tvId);

        }

        public void bindItem (Fairshare fairshare) {

            tvTittle.setText(fairshare.getName());
            tvDescription.setText(fairshare.getDescription());
            tvId.setText(fairshare.getIdFairshare());

        }

    }

}
