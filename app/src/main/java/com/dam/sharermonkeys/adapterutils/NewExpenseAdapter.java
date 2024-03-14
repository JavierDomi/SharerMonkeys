package com.dam.sharermonkeys.adapterutils;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.dam.sharermonkeys.R;
import com.dam.sharermonkeys.pojos.User;
import java.util.ArrayList;

public class NewExpenseAdapter extends RecyclerView.Adapter<NewExpenseAdapter.ItemVH> {
    private ArrayList<User> participantsList;

    public NewExpenseAdapter(ArrayList<User> participantsList) {
        this.participantsList = participantsList;
    }

    @NonNull
    @Override
    public ItemVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.new_expense_item, parent, false);
        return new ItemVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemVH holder, int position) {
        User user = participantsList.get(position);
        holder.cbUser.setText(String.valueOf(user.getUsername()));
    }

    @Override
    public int getItemCount() {
        return participantsList.size();
    }

    static class ItemVH extends RecyclerView.ViewHolder {
        CheckBox cbUser;

        public ItemVH(@NonNull View itemView) {
            super(itemView);
            cbUser = itemView.findViewById(R.id.cbUser);
        }
    }
}