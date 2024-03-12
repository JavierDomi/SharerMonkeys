package com.dam.sharermonkeys.adapterutils;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.dam.sharermonkeys.R;
import com.dam.sharermonkeys.pojos.User;

import java.util.ArrayList;

public class ParticipantListAdapter extends RecyclerView.Adapter<ParticipantListAdapter.ItemVH> {

    private ArrayList<User> participantsList;

    public ParticipantListAdapter(ArrayList<User> participantsList) {
        this.participantsList = participantsList;
    }

    @NonNull
    @Override
    public ItemVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_participant_list_adapter, parent, false);
        return new ItemVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemVH holder, int position) {
        User user = participantsList.get(position);
        holder.tvUsername.setText(user.getUsername());
        holder.tvEmail.setText(user.getEmail());
    }

    @Override
    public int getItemCount() {
        return participantsList.size();
    }

    static class ItemVH extends RecyclerView.ViewHolder {
        TextView tvEmail, tvUsername;

        public ItemVH(@NonNull View itemView) {
            super(itemView);
            tvEmail = itemView.findViewById(R.id.tvEmailAdapter);
            tvUsername = itemView.findViewById(R.id.tvUsernameAdapter);
        }
    }
}

