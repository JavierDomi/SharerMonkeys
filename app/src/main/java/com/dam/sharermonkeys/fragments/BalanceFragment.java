package com.dam.sharermonkeys.fragments;
import com.dam.sharermonkeys.adapterutils.OwesAdapter;
import com.google.firebase.database.DatabaseError;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.dam.sharermonkeys.R;
import com.dam.sharermonkeys.adapterutils.BalanceAdapter;
import com.dam.sharermonkeys.pojos.Balance;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class BalanceFragment extends Fragment {

    public static final String REALTIME_PATH = "https://fairshare-ae0be-default-rtdb.europe-west1.firebasedatabase.app/";

    RecyclerView recyclerViewBalance;
    RecyclerView recyclerViewOwes;
    DatabaseReference reference;
    ArrayList<Balance> balances;
    BalanceAdapter balanceAdapter;
    OwesAdapter owesAdapter;
    String fairShareId;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_balance, container, false);


        //Obtenemos argumentos del fragmento
        Bundle args = getArguments();
        if (args != null) {
            fairShareId = args.getString("id_fairshare");
        } else {
            // Manejar el caso donde no hay argumentos pasados.
            Log.e("BalanceFragment", "No se pasaron argumentos al fragmento.");
            // Considera cerrar el fragmento o mostrar un mensaje adecuado.
        }

        // Obtener el fairshareId de los argumentos
        fairShareId = getArguments().getString("id_fairshare");
        // Inicializar el RecyclerView y el adaptador
        recyclerViewBalance = view.findViewById(R.id.rvGrafic);
        recyclerViewBalance.setHasFixedSize(true);
        recyclerViewBalance.setLayoutManager(new LinearLayoutManager(getActivity()));
        // Inicializar la lista de balances
        balances = new ArrayList<>();
        balanceAdapter = new BalanceAdapter(balances,getActivity());
        recyclerViewBalance.setAdapter(balanceAdapter);



        //SEGUNDO RECYCLERVIEW DE OWES
        recyclerViewOwes = view.findViewById(R.id.rvOwesTo);
        recyclerViewOwes.setHasFixedSize(true);
        recyclerViewOwes.setLayoutManager(new LinearLayoutManager(getActivity()));
        owesAdapter = new OwesAdapter(balances, getActivity());
        recyclerViewOwes.setAdapter(owesAdapter);



        // Obtener una referencia a la base de datos
        reference = FirebaseDatabase.getInstance(REALTIME_PATH).getReference();


        // Llamar al método para cargar los datos desde la base de datos
        fetchBalances();
        return view;
    }

    private void fetchBalances() {
        // Referencia a la ubicación de los balances en la base de datos
        DatabaseReference balancesRef = reference.child("Balance");

        // Consulta para filtrar los balances por el ID del FairShare
        balancesRef.orderByChild("id_fairshare").equalTo(fairShareId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                balances.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Balance balance = snapshot.getValue(Balance.class);
                    if (balance != null) {
                        String userId = snapshot.child("id_user").getValue(String.class);
                        balance.setIdUser(userId);
                        balances.add(balance);
                        Log.d("LISTBALANCES", "Balance: " + balance.getIdUser());
                    }
                }
                if (!balances.isEmpty()) {
                    balanceAdapter.notifyDataSetChanged();
                    owesAdapter.notifyDataSetChanged();
                    Log.d("LISTBALANCES", "Number of items in list: " + balances.size());
                } else {
                    Log.d("LISTBALANCES", "List is empty.");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("LISTBALANCES", "Firebase Database Error: " + error.getMessage());

            }
        });
    }
}
