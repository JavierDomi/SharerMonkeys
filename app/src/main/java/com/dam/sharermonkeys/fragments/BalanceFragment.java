package com.dam.sharermonkeys.fragments;
import com.dam.sharermonkeys.adapterutils.OwesAdapter;
import com.dam.sharermonkeys.intefaces.FetchBalancesCallback;

import com.dam.sharermonkeys.pojos.Transaction;
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
import android.widget.Toast;


import com.dam.sharermonkeys.R;
import com.dam.sharermonkeys.adapterutils.BalanceAdapter;
import com.dam.sharermonkeys.pojos.Balance;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BalanceFragment extends Fragment implements FetchBalancesCallback {

    public static final String REALTIME_PATH = "https://fairshare-ae0be-default-rtdb.europe-west1.firebasedatabase.app/";

    RecyclerView recyclerViewBalance;
    RecyclerView recyclerViewOwes;
    DatabaseReference reference;
    ArrayList<Balance> balances;
    ArrayList<Transaction> transactions;
    BalanceAdapter balanceAdapter;
    OwesAdapter owesAdapter;
    String fairshareId;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_balance, container, false);


        //Obtener argumentos del fragmento
        Bundle args = getArguments();
        if (args != null) {
            fairshareId = args.getString("id_fairshare");
        } else {
            // Manejar el caso donde no hay argumentos pasados
            Log.e("BalanceFragment", "No se pasaron argumentos al fragmento.");
        }

        balances = new ArrayList<>();

        // Obtener una referencia a la base de datos
        reference = FirebaseDatabase.getInstance(REALTIME_PATH).getReference();

        // Llamar al método para cargar los datos desde la base de datos
        fetchBalances(this);

        transactions = new ArrayList<Transaction>();
        transactions = calculateTransactions();

        // Inicializar el RecyclerView y el adaptador
        recyclerViewBalance = view.findViewById(R.id.rvGrafic);
        recyclerViewBalance.setHasFixedSize(true);
        recyclerViewBalance.setLayoutManager(new LinearLayoutManager(getActivity()));
        balanceAdapter = new BalanceAdapter(balances,getActivity());
        recyclerViewBalance.setAdapter(balanceAdapter);


        //SEGUNDO RECYCLERVIEW DE OWES
        recyclerViewOwes = view.findViewById(R.id.rvOwesTo);
        recyclerViewOwes.setHasFixedSize(true);
        recyclerViewOwes.setLayoutManager(new LinearLayoutManager(getActivity()));
        owesAdapter = new OwesAdapter(transactions, getActivity());
        recyclerViewOwes.setAdapter(owesAdapter);

        return view;
    }


    private ArrayList<Transaction> calculateTransactions() {

        transactions.clear();

        // Paso 1: Calcular el balance total de cada usuario
        Map<String, Double> balancesMap = new HashMap<>();
        for (Balance balance : balances) {
            String userId = balance.getIdUser();
            double total = balancesMap.getOrDefault(userId, 0.0);
            total += balance.calculateTotal();
            balancesMap.put(userId, total);
        }

        // Paso 2: Encontrar los usuarios que deben dinero y los que deben recibir dinero
        List<String> debtors = new ArrayList<>();
        List<String> creditors = new ArrayList<>();
        for (Map.Entry<String, Double> entry : balancesMap.entrySet()) {
            double total = entry.getValue();
            if (total < 0) {
                debtors.add(entry.getKey());
            } else if (total > 0) {
                creditors.add(entry.getKey());
            }
        }

        for (String debtor : debtors) {
            double debt = balancesMap.get(debtor);
            for (String creditor : creditors) {
                if (balancesMap.get(creditor) > 0) {
                    double credit = balancesMap.get(creditor);
                    double amount = Math.min(debt, credit);
                    balancesMap.put(debtor, debt - amount);
                    balancesMap.put(creditor, credit - amount);
                    transactions.add(new Transaction(debtor, creditor, amount * -1));
                    if (debt <= credit) {
                        break;
                    }
                }
            }
        }

        return transactions;

    }


    private void fetchBalances(FetchBalancesCallback callback) {
        // Referencia a la ubicación de los balances en la base de datos
        DatabaseReference balancesRef = reference.child("Balance");

        // Consulta para filtrar los balances por el ID del FairShare
        balancesRef.orderByChild("id_fairshare").equalTo(fairshareId).addListenerForSingleValueEvent(new ValueEventListener() {
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

                callback.onBalancesFetched();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("LISTBALANCES", "Firebase Database Error: " + error.getMessage());

            }
        });
    }

    @Override
    public void onBalancesFetched() {

        Toast.makeText(getContext(), String.valueOf(balances.size()), Toast.LENGTH_SHORT).show();

        transactions = calculateTransactions(); // Llamar a calculateTransactions() aquí
        owesAdapter.notifyDataSetChanged();

    }


}
