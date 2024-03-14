package com.dam.sharermonkeys.fragments;
import com.google.firebase.database.DatabaseError;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.dam.sharermonkeys.ListExpenses;
import com.dam.sharermonkeys.R;
import com.dam.sharermonkeys.adapterutils.BalanceAdapter;
import com.dam.sharermonkeys.pojos.Balance;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class BalanceFragment extends Fragment {
    RecyclerView recyclerViewBalance;
    DatabaseReference reference;
    ArrayList<Balance>list;
    BalanceAdapter balanceAdapter;
    String fairshareId;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_balance, container, false);

        // Obtener una referencia a la base de datos
        reference = FirebaseDatabase.getInstance().getReference();

        // Inicializar la lista de balances
        list = new ArrayList<>();

        // Inicializar el RecyclerView y el adaptador
        recyclerViewBalance = view.findViewById(R.id.rvGrafic);
        recyclerViewBalance.setLayoutManager(new LinearLayoutManager(getActivity()));
        balanceAdapter = new BalanceAdapter(list, getActivity());
        recyclerViewBalance.setAdapter(balanceAdapter);

        // Llamar al método para cargar los datos desde la base de datos
        fetchBalances();



        return view;
    }

    private void fetchBalances() {
        // Referencia a la ubicación de los balances en la base de datos
        DatabaseReference balancesRef = reference.child("Balance");

        // Consulta para filtrar los balances por el ID del FairShare
        balancesRef.orderByChild("id_fairshare").equalTo(fairshareId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Limpiar la lista actual de balances
                list.clear();
                // Iterar sobre los balances encontrados
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // Obtener cada balance y añadirlo a la lista
                    Balance balance = snapshot.getValue(Balance.class);

                    //Set manually porque firebase no funciona bien
                    String userId = snapshot.child("id_user").getValue(String.class);
                    balance.setIdUser(userId);

                    list.add(balance);
                    Log.d("LISTBALANCES", "Balance: " + balance.getIdUser());
                }
                // Notificar al adaptador que los datos han cambiado
                balanceAdapter.notifyDataSetChanged();
                Log.d("LISTBALANCES", "Number of items in list: " + list.size());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("LISTBALANCES", "Firebase Database Error: " + error.getMessage());

            }

        });
    }


}
