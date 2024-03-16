package com.dam.sharermonkeys.adapterutils;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.dam.sharermonkeys.MainActivity;
import com.dam.sharermonkeys.R;
import com.dam.sharermonkeys.pojos.Balance;
import com.dam.sharermonkeys.pojos.Transaction;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OwesAdapter extends RecyclerView.Adapter<OwesAdapter.ItemVH> {

    ArrayList<Transaction> transactions;
    Context context;
    DatabaseReference databaseReference;



    public OwesAdapter(ArrayList<Transaction> transactions, Context context) {
        this.transactions = transactions;
        this.context = context;
        this.databaseReference = FirebaseDatabase.getInstance(MainActivity.REALTIME_PATH).getReference();



    }

    @NonNull
    @Override
    public OwesAdapter.ItemVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.owes_item, parent, false);
        return new ItemVH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull OwesAdapter.ItemVH holder, int position) {

        Transaction transaction = transactions.get(position);

        findUsernameById(transaction.getDeudor(), holder.tvUser1, transaction.getCantidad(), holder.tvEuro);
        findUsernameById(transaction.getAcreedor(), holder.tvUser2, transaction.getCantidad(), holder.tvEuro);

    }


    private void findUsernameById(String userId, TextView tvUser, double transaction, TextView tvEuro) {
        DatabaseReference usersRef = databaseReference.child("Users").child(userId).child("username");
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String userName = dataSnapshot.getValue(String.class);
                    Log.d("OwesAdapter", "USERNAME: " + userName);
                    tvUser.setText(userName);
                    double roundedTransaction = Math.round(transaction * 100.0) / 100.0;
                    tvEuro.setText(String.format("%.2f", roundedTransaction));
                } else {
                    tvUser.setText("ERROR");
                    Toast.makeText(context, R.string.username_not_found, Toast.LENGTH_SHORT).show();
                    Log.e("OwesAdapter", "No se encontró el nombre de usuario para el ID: " + userId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                tvUser.setText("");
                Toast.makeText(context, R.string.unable_fetch_username, Toast.LENGTH_SHORT).show();
                Log.e("OwesAdapter", "Error al obtener el nombre de usuario para el ID: " + userId);
            }
        });
    }

    @Override
    public int getItemCount() {
        return transactions.size();
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
            tvEuro = itemView.findViewById(R.id.tvEuro);
            btnPay = itemView.findViewById(R.id.btnPay);
        }
    }
}
