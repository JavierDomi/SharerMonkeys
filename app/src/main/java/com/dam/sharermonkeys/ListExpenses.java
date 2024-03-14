package com.dam.sharermonkeys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.dam.sharermonkeys.adapterutils.ExpenseListAdapter;
import com.dam.sharermonkeys.fragments.BalanceFragment;
import com.dam.sharermonkeys.pojos.Expense;
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
    ArrayList<Expense> list;

    ExpenseListAdapter adapter;

    String fairshareId;

    Button btnBalance, btnExpenses, btnNewExpense;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_expenses);

        // Inicializar UI
        initializeUI();

        // Obtener el ID de FairShare de la actividad anterior
        fairshareId = getIntent().getStringExtra("id_fairshare");

        // Inicializar el RecyclerView
        recyclerView = findViewById(R.id.rvListExpenses);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Inicializar los botones
        btnExpenses =findViewById(R.id.btnExpenses);
        btnBalance = findViewById(R.id.btnBalance);
        btnNewExpense = findViewById(R.id.btnNewExpense);

        // Inicializar la lista de gastos
        list = new ArrayList<>();

        // Inicializar el adaptador con la lista vacía
        adapter = new ExpenseListAdapter(list, this);
        recyclerView.setAdapter(adapter);

        // Obtener referencia a la base de datos
        reference = FirebaseDatabase.getInstance(REALTIME_PATH).getReference();

        // Obtener los gastos asociados al FairShare
        fetchExpenses();


        btnBalance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                BalanceFragment bf = new BalanceFragment();
                ft.replace(R.id.fragmentContainer, bf);
                ft.addToBackStack(null);
                recyclerView.setVisibility(View.GONE);


                ft.commit();

            }
        });
        btnExpenses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView.setVisibility(View.VISIBLE);
                getSupportFragmentManager().popBackStackImmediate();
            }
        });

        btnNewExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ListExpenses.this, NewExpense.class);
                startActivity(i);
            }
        });
    }

    private void fetchExpenses() {
        // Referencia a la ubicación de los gastos en la base de datos
        DatabaseReference expensesRef = reference.child("Expenses");

        // Consulta para filtrar los gastos por el ID del FairShare
        expensesRef.orderByChild("id_fairshare").equalTo(fairshareId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Limpiar la lista actual de gastos
                list.clear();
                // Iterar sobre los gastos encontrados
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // Obtener cada gasto y añadirlo a la lista
                    Expense expense = snapshot.getValue(Expense.class);

                    //Set manually porque firebase no fufa bien
                    String userIdPayer = snapshot.child("id_user_payer").getValue(String.class);
                    expense.setIdUserPayer(userIdPayer);

                    list.add(expense);
                    System.out.println(expense.getIdUserPayer());
                    Log.d("LISTEXPENSES", "Expense: " + expense.getName());
                }
                // Notificar al adaptador que los datos han cambiado
                adapter.notifyDataSetChanged();
                Log.d("LISTEXPENSES", "Number of items in list: " + list.size());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Manejar errores de la base de datos
                Log.e("LISTEXPENSES", "Firebase Database Error: " + databaseError.getMessage());
                Toast.makeText(ListExpenses.this, R.string.db_error, Toast.LENGTH_SHORT).show();
            }
        });

        Log.e("ExpenseListSize", "Size: " + String.valueOf(list.size()));
        Log.e("FairShare id", fairshareId);

    }

    private void initializeUI() {
        // Configurar la barra de acción
        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#2B2B2B")));
        actionBar.setLogo(R.drawable.fairshare2_small);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setTitle(Html.fromHtml("<font color=\"#2B2B2B\">" + getString(R.string.app_name) + "</font>"));
    }
}
