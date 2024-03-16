package com.dam.sharermonkeys.adapterutils;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.dam.sharermonkeys.ListExpenses;
import com.dam.sharermonkeys.R;
import com.dam.sharermonkeys.pojos.FairShare;

import java.util.ArrayList;

public class FairShareListAdapter extends RecyclerView.Adapter<FairShareListAdapter.ItemVH> {

    ArrayList<FairShare> fairShareList;
    Context context;

    public FairShareListAdapter(ArrayList<FairShare> fairShareList, Context context) {

        this.fairShareList = fairShareList;
        this.context = context;
    }

    @NonNull
    @Override
    public ItemVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(context).inflate(R.layout.fairshare_item, parent, false);
        return new ItemVH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemVH holder, int position) {

        FairShare fairShare = fairShareList.get(position);

        holder.tvTittle.setText(fairShare.getName());
        holder.tvDescription.setText(fairShare.getDescription());
        holder.recCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FairShare fairShare = fairShareList.get(holder.getAdapterPosition());
                Intent intent = new Intent(context, ListExpenses.class);
                intent.putExtra("id_fairshare", fairShare.getIdFairshare());
                context.startActivity(intent);
            }
        });

        holder.recCard.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                // TODO Delete expense group on long click
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {return fairShareList.size(); }

    public void updateFairSharesList(ArrayList<FairShare> fairShareList) {
        this.fairShareList = fairShareList;
        notifyDataSetChanged();
    }

    public static class ItemVH extends RecyclerView.ViewHolder {
        CardView recCard;
        TextView tvTittle, tvDescription;
        public ItemVH(@NonNull View itemView) {
            super(itemView);

            recCard = itemView.findViewById(R.id.recCard);
            tvTittle = itemView.findViewById(R.id.tvTitle);
            tvDescription = itemView.findViewById(R.id.tvDescription);
        }
    }
}