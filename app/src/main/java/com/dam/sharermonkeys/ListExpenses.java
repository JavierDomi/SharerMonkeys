package com.dam.sharermonkeys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.dam.sharermonkeys.adapterutils.ExpenseListAdapter;
import com.dam.sharermonkeys.adapterutils.FairShareListAdapter;
import com.dam.sharermonkeys.pojos.Expense;
import com.dam.sharermonkeys.pojos.FairShare;
import com.dam.sharermonkeys.pojos.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

//LA VENTANA CUANDO ENTRAS A UN GRUPO DE GASTOS, TIENE QUE APARECER UNA LISTA DE TODOS GASTOS

public class ListExpenses extends AppCompatActivity {

    public static final String REALTIME_PATH = "https://fairshare-ae0be-default-rtdb.europe-west1.firebasedatabase.app/";
    RecyclerView recyclerView;
    DatabaseReference reference;
    ArrayList<Expense>list;

    ExpenseListAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_expenses);

        String fairshareId = getIntent().getStringExtra("id_fairshare");


        reference = FirebaseDatabase.getInstance(REALTIME_PATH).getReference("FairShares")
                .child(String.valueOf(fairshareId))
                .child("Expenses");

        recyclerView = findViewById(R.id.rvListExpenses);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        list = new ArrayList<>();

        adapter = new ExpenseListAdapter(list, this);
        recyclerView.setAdapter(adapter);


        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    Expense expense = snapshot.getValue(Expense.class);
                    list.add(expense);
                    Log.d("LISTEXPENSES", "Expense: " + expense.getName());

                }
                adapter.notifyDataSetChanged();
                Log.d("LISTEXPENSES", "Number of items in list: " + list.size());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("LISTEXPENSES", "Firebase Database Error: " + databaseError.getMessage());


            }
        });
    }




}
