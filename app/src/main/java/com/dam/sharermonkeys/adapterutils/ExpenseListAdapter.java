package com.dam.sharermonkeys.adapterutils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.dam.sharermonkeys.R;
import com.dam.sharermonkeys.pojos.Expense;

import java.util.ArrayList;

public class ExpenseListAdapter extends RecyclerView.Adapter<ExpenseListAdapter.ItemVH>{

    ArrayList<Expense> expenseList;
    Context context;

    public ExpenseListAdapter(ArrayList<Expense> expenseList, Context context) {
        this.expenseList = expenseList;
        this.context = context;
    }

    @NonNull
    @Override
    public ItemVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.expense_item, parent, false);
        return new ItemVH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ExpenseListAdapter.ItemVH holder, int position) {
        Expense expense = expenseList.get(position);
        holder.tvName.setText(expense.getName());
        holder.tvAmount.setText(String.valueOf(expense.getAmount()));
        //holder.tvUserName.setText(expense.getIdUser());



    }

    @Override
    public int getItemCount() {
        return expenseList.size();
    }

    public static class ItemVH extends RecyclerView.ViewHolder {

        CardView recCard2;
        TextView tvName, tvAmount, tvUserName;

        public ItemVH(@NonNull View itemView) {
            super(itemView);

            recCard2 = itemView.findViewById(R.id.recCard2);
            tvName = itemView.findViewById(R.id.tvTitle);
            tvAmount = itemView.findViewById(R.id.tvAmount);
            //tvUserName = itemView.findViewById(R.id.tvUserName);

        }
    }
}
