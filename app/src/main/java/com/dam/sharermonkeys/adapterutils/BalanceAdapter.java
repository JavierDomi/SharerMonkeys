package com.dam.sharermonkeys.adapterutils;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.dam.sharermonkeys.MainActivity;
import com.dam.sharermonkeys.R;
import com.dam.sharermonkeys.pojos.Balance;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class BalanceAdapter extends RecyclerView.Adapter<BalanceAdapter.ItemVH> {

    ArrayList<Balance> balancesList;
    Context context;
    DatabaseReference databaseReference;

    public BalanceAdapter(ArrayList<Balance> balancesList, Context context) {
        this.balancesList = balancesList;
        this.context = context;
        this.databaseReference = FirebaseDatabase.getInstance(MainActivity.REALTIME_PATH).getReference();;
    }


    @NonNull
    @Override
    public BalanceAdapter.ItemVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.balance_item, parent, false);
        return new ItemVH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull BalanceAdapter.ItemVH holder, int position) {

        Balance balance = balancesList.get(position);

        //TODO:

        holder.tvUser.setText(balance.getIdUser());
        holder.tvCantidad.setText(String.valueOf(balance.getPayment()));


        // Obtén el ID de usuario del objeto Expense
        String userId = balance.getIdUser();

        // Verifica si el ID de usuario no es nulo ni está vacío antes de buscar el nombre de usuario correspondiente
        if (userId != null && !userId.isEmpty()) {
            // Busca el nombre de usuario correspondiente al ID de usuario en la base de datos Firebase
            findUsernameById(userId, holder.tvUser);
        } else {
            holder.tvUser.setText("");
            Toast.makeText(context, R.string.user_id_null, Toast.LENGTH_SHORT).show();
            Log.e("ExpenseListAdapter", "El ID de usuario es nulo");
        }




    }

    private void findUsernameById(String userId, TextView tvUser) {
        DatabaseReference usersRef = databaseReference.child("Users").child(userId).child("username");
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String userName = dataSnapshot.getValue(String.class);
                    System.out.println("USERNAME" + userName);
                    tvUser.setText(userName);
                } else {
                    tvUser.setText("ERROR");
                    Toast.makeText(context, R.string.username_not_found, Toast.LENGTH_SHORT).show();
                    System.out.println("NO ESTA");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                tvUser.setText("");
                Toast.makeText(context, R.string.unable_fetch_username, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return balancesList.size();
    }

    public static class ItemVH extends RecyclerView.ViewHolder {

        CardView recCardBalance;
        TextView tvUser, tvCantidad;

        public ItemVH(@NonNull View itemView) {
            super(itemView);

            recCardBalance = itemView.findViewById(R.id.recCardBalance);
            tvUser = itemView.findViewById(R.id.tvUser);
            tvCantidad = itemView.findViewById(R.id.tvCantidad);

        }
    }
}
