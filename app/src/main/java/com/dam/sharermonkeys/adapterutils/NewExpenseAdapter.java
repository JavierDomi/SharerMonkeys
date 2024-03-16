package com.dam.sharermonkeys.adapterutils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dam.sharermonkeys.MainActivity;
import com.dam.sharermonkeys.R;
import com.dam.sharermonkeys.pojos.User;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class NewExpenseAdapter extends RecyclerView.Adapter<NewExpenseAdapter.ItemVH> {
    private ArrayList<User> participantsList;
    Context context;
    DatabaseReference databaseReference;

    public NewExpenseAdapter(ArrayList<User> participantsList, Context context) {
        this.participantsList = participantsList;
        this.context = context;
        databaseReference = FirebaseDatabase.getInstance(MainActivity.REALTIME_PATH).getReference();
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
        holder.cbUser.setText(user.getUsername());
        holder.cbUser.setChecked(user.isSelected());

        holder.cbUser.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                user.setSelected(isChecked);
            }
        });
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

    public ArrayList<User> getSelectedUsers() {
        ArrayList<User> selectedUsers = new ArrayList<>();
        for (User user : participantsList) {
            if (user.isSelected()) {
                selectedUsers.add(user);
            }
        }
        return selectedUsers;
    }
}