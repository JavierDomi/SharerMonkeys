package com.dam.sharermonkeys;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.dam.sharermonkeys.adapterutils.FairShareListAdapter;
import com.dam.sharermonkeys.pojos.Expense;
import com.dam.sharermonkeys.pojos.FairShare;
import com.dam.sharermonkeys.pojos.User;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

//LA VENTANA CUANDO ENTRAS A UN GRUPO DE GASTOS, TIENE QUE APARECER UNA LISTA DE TODOS GASTOS

public class ListExpenses extends AppCompatActivity {

    public static final String REALTIME_PATH = "https://fairshare-ae0be-default-rtdb.europe-west1.firebasedatabase.app/";
    RecyclerView recyclerView;
    DatabaseReference reference;
    ArrayList<Expense>list;

    FairShareListAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_expenses);

        reference = FirebaseDatabase.getInstance(REALTIME_PATH).getReference("FairShares");

        recyclerView = findViewById(R.id.rvListExpenses);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        list = new ArrayList<>();








    }
}