package com.dam.sharermonkeys.adapterutils;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
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

        //holder.tvUser.setText(balance.getIdUser());
        double total = balance.calculateTotal();
        double roundedTotal = Math.round(total * 100.0) / 100.0; // Redondea a dos decimales
        holder.tvCantidad.setText(String.format("%.2f", roundedTotal));

        // Verificar si el valor de payments es menor que 0
        if (balance.calculateTotal() < 0) {
            int colorRojo = ContextCompat.getColor(context, R.color.red);
            holder.llCantidadBalance.setBackgroundColor(colorRojo);
        } else if (balance.calculateTotal() > 0) {
            int colorVerde = ContextCompat.getColor(context, R.color.green);
            holder.llCantidadBalance.setBackgroundColor(colorVerde);
        } else {
            int colorShare = ContextCompat.getColor(context, R.color.share);
            holder.llCantidadBalance.setBackgroundColor(colorShare);
        }

        // Obtén el ID de usuario del objeto Expense
        String userId = balance.getIdUser();

        // Verifica si el ID de usuario no es nulo ni está vacío antes de buscar el nombre de usuario correspondiente
        if (userId != null && !userId.isEmpty()) {
            // Busca el nombre de usuario correspondiente al ID de usuario en la base de datos Firebase
            findUsernameById(userId, holder.tvUser);
        } else {
            holder.tvUser.setText("");
            Toast.makeText(context, R.string.user_id_null, Toast.LENGTH_SHORT).show();
            Log.e("BalanceAdapter", "El ID de usuario es nulo");
        }
    }

    private void findUsernameById(String userId, TextView tvUser) {
        DatabaseReference usersRef = databaseReference.child("Users").child(userId).child("username");
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String userName = dataSnapshot.getValue(String.class);
                    System.out.println("USERNAME: " + userName);
                    tvUser.setText(userName);
                } else {
                    tvUser.setText("ERROR");
                    Toast.makeText(context, R.string.username_not_found, Toast.LENGTH_SHORT).show();
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
        LinearLayout llCantidadBalance;

        public ItemVH(@NonNull View itemView) {
            super(itemView);

            recCardBalance = itemView.findViewById(R.id.recCardBalance);
            tvUser = itemView.findViewById(R.id.tvUserBalance);
            tvCantidad = itemView.findViewById(R.id.tvCantidadBalance);
            llCantidadBalance = itemView.findViewById(R.id.llCantidadBalance);

        }
    }
}