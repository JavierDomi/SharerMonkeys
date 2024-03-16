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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class OwesAdapter extends RecyclerView.Adapter<OwesAdapter.ItemVH> {

    ArrayList<Balance>owesList;
    Context context;
    DatabaseReference databaseReference;


    public OwesAdapter(ArrayList<Balance> owesList, Context context) {
        this.owesList = owesList;
        this.context = context;
        this.databaseReference = FirebaseDatabase.getInstance(MainActivity.REALTIME_PATH).getReference();;
    }

    @NonNull
    @Override
    public OwesAdapter.ItemVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.owes_item, parent, false);
        return new ItemVH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull OwesAdapter.ItemVH holder, int position) {

        Balance balance = owesList.get(position);

        holder.tvUser1.setText(balance.getIdUser());
        //holder.tvUser2.setText(balance.getIdUser());

        //TODO: CAMBIAR PARA MOSTRAR BIEN LOS DATOS
        holder.tvEuro.setText(String.valueOf(balance.getPayments()));

        String userId = balance.getIdUser();

        // Verifica si el ID de usuario no es nulo ni está vacío antes de buscar el nombre de usuario correspondiente
        if (userId != null && !userId.isEmpty()) {
            // Busca el nombre de usuario correspondiente al ID de usuario en la base de datos Firebase

            //TODO: CAMBIAR PARA MOSTRAR BIEN LOS DATOS
            findUsernameById(userId, holder.tvUser1);
           // findUsernameById(userId, holder.tvUser2);
        } else {

            //TODO: CAMBIAR PARA MOSTRAR BIEN LOS DATOS
            holder.tvUser1.setText("");
           // holder.tvUser2.setText("");
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
        return owesList.size();
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
