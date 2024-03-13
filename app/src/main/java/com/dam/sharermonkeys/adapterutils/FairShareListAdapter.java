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
import com.dam.sharermonkeys.pojos.Expense;
import com.dam.sharermonkeys.pojos.FairShare;

import java.util.ArrayList;

public class FairShareListAdapter extends RecyclerView.Adapter<FairShareListAdapter.ItemVH> {

    ArrayList<FairShare> fairShareList;
    ArrayList<Expense> listExpenses;
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
        holder.tvId.setText(fairShare.getIdFairshare());
        holder.tvDescription.setText(fairShare.getDescription());

        //Creamos un onClickListener para que cuando sea clicado un elemento del RecyclerView nos llevemos los datos a la siguiente vista
        holder.recCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // TODO PASAR DATOS SIGUIENTE ACTIVITY
                FairShare fairShare = fairShareList.get(position);
                Intent intent = new Intent(context, ListExpenses.class);
                intent.putExtra("id_fairshare", fairShare.getIdFairshare());
                context.startActivity(intent);






            }
        });

        holder.recCard.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                // TODO BORRAR GRUPO DE GASTOS EN LONG CLICK

                return false;
            }
        });

    }

    @Override
    public int getItemCount() {return fairShareList.size(); }

    public static class ItemVH extends RecyclerView.ViewHolder {

        CardView recCard;

        TextView tvTittle, tvDescription;
        TextView tvId;

        public ItemVH(@NonNull View itemView) {
            super(itemView);

            recCard = itemView.findViewById(R.id.recCard);

            tvTittle = itemView.findViewById(R.id.tvTitle);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvId = itemView.findViewById(R.id.tvId);

        }
    }
}
