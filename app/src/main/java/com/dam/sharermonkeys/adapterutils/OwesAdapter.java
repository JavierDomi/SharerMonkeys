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

    ArrayList<Balance> balances;
    Context context;
    DatabaseReference databaseReference;
    private List<Transaction> transacciones = new ArrayList<>();


    public OwesAdapter(ArrayList<Balance> balances, Context context) {
        this.balances = balances;
        this.context = context;
        this.databaseReference = FirebaseDatabase.getInstance(MainActivity.REALTIME_PATH).getReference();

        //calculateTransactions();
    }

    @NonNull
    @Override
    public OwesAdapter.ItemVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.owes_item, parent, false);
        return new ItemVH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull OwesAdapter.ItemVH holder, int position) {

        Balance balance = balances.get(position);

        String userId = balance.getIdUser();

        findUsernameById(balance.getIdUser(), holder.tvUser1, balance.calculateTotal(), holder.tvEuro);

        /*if (userId != null && !userId.isEmpty()) {
            if (!transacciones.isEmpty() && position < transacciones.size()) {
                Transaction transaction = transacciones.get(position);
                findUsernameById(transaction.getAcreedor(), holder.tvUser1, transaction, holder.tvEuro);
                findUsernameById(transaction.getDeudor(), holder.tvUser2, transaction, holder.tvEuro);
            } else {
                Toast.makeText(context, "No hay transacciones disponibles", Toast.LENGTH_SHORT).show();
            }
        } else {
            holder.tvUser1.setText("");
            holder.tvUser2.setText("");
            Toast.makeText(context, R.string.user_id_null, Toast.LENGTH_SHORT).show();
            Log.e("BalanceAdapter", "El ID de usuario es nulo");
        }*/
    }

    private void calculateTransactions() {

        transacciones.clear();

        // Paso 1: Calcular los saldos individuales
        Map<String, Double> saldosIndividuales = new HashMap<>();
        for (Balance balance : balances) {
            double saldoTotal = balance.calculateTotal();
            saldosIndividuales.put(balance.getIdUser(), saldoTotal);
        }

        // Paso 2: Identificar deudores y acreedores
        List<String> deudores = new ArrayList<>();
        List<String> acreedores = new ArrayList<>();
        for (Map.Entry<String, Double> entry : saldosIndividuales.entrySet()) {
            String userId = entry.getKey();
            double saldo = entry.getValue();
            if (saldo < 0) {
                deudores.add(userId);
            } else if (saldo > 0) {
                acreedores.add(userId);
            }
        }

        // Mostrar resultados en el RecyclerView
        for (String deudor : deudores) {
            for (String acreedor : acreedores) {
                double saldoDeudor = saldosIndividuales.get(deudor);
                double saldoAcreedor = saldosIndividuales.get(acreedor);
                if (saldoDeudor < 0 && saldoAcreedor > 0) {
                    Transaction transaction = new Transaction(deudor, acreedor, saldoAcreedor);
                    transacciones.add(transaction);
                }
            }
        }

        // Agregar logs para verificar si se están agregando transacciones correctamente
        Log.d("OwesAdapter", "Número de transacciones: " + transacciones.size());
        for (Transaction transaction : transacciones) {
            Log.d("OwesAdapter", "Deudor: " + transaction.getDeudor() + ", Acreedor: " + transaction.getAcreedor() + ", Cantidad: " + transaction.getCantidad());
        }

        notifyDataSetChanged();
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
                    tvEuro.setText(String.valueOf(transaction));
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
        return balances.size();
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
