package com.dam.sharermonkeys.adapterutils;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.dam.sharermonkeys.R;
import java.util.ArrayList;

public class ParticipantListAdapter extends RecyclerView.Adapter<ParticipantListAdapter.ItemVH> {

    private ArrayList<String> participantsList;

    public ParticipantListAdapter(ArrayList<String> participantsList) {
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
        String email = participantsList.get(position);
        holder.tvEmail.setText(email);
    }

    @Override
    public int getItemCount() {
        return participantsList.size();
    }

    static class ItemVH extends RecyclerView.ViewHolder {
        TextView tvEmail;

        public ItemVH(@NonNull View itemView) {
            super(itemView);
            tvEmail = itemView.findViewById(R.id.tvEmail);
        }
    }
}
