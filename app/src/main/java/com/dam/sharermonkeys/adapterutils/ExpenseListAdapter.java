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
import com.dam.sharermonkeys.pojos.Expense;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ExpenseListAdapter extends RecyclerView.Adapter<ExpenseListAdapter.ItemVH>{

    ArrayList<Expense> expenseList;
    Context context;
    DatabaseReference databaseReference;

    public ExpenseListAdapter(ArrayList<Expense> expenseList, Context context) {
        this.expenseList = expenseList;
        this.context = context;
        databaseReference = FirebaseDatabase.getInstance(MainActivity.REALTIME_PATH).getReference();
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

        // Obtén el ID de usuario del objeto Expense
        String userId = expense.getIdUserPayer();

        // Verifica si el ID de usuario no es nulo ni está vacío antes de buscar el nombre de usuario correspondiente
        if (userId != null && !userId.isEmpty()) {
            // Busca el nombre de usuario correspondiente al ID de usuario en la base de datos Firebase
            findUsernameById(userId, holder.tvUserName);
        } else {
            holder.tvUserName.setText("");
            Toast.makeText(context, R.string.user_id_null, Toast.LENGTH_SHORT).show();
            Log.e("ExpenseListAdapter", "El ID de usuario es nulo");
        }
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
            tvUserName = itemView.findViewById(R.id.tvUserName);
        }
    }

    // Método para buscar el nombre de usuario por ID de usuario en la base de datos Firebase
    private void findUsernameById(String userId, TextView tvUserName) {
        DatabaseReference usersRef = databaseReference.child("Users").child(userId).child("username");
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String userName = dataSnapshot.getValue(String.class);
                    System.out.println("USERNAME" + userName);
                    tvUserName.setText(userName);
                } else {
                    tvUserName.setText("ERROR");
                    Toast.makeText(context, R.string.username_not_found, Toast.LENGTH_SHORT).show();
                    System.out.println("NO ESTA");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                tvUserName.setText("");
                Toast.makeText(context, R.string.unable_fetch_username, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
