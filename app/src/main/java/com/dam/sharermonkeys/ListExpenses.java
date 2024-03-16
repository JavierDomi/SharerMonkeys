package com.dam.sharermonkeys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.dam.sharermonkeys.adapterutils.ExpenseListAdapter;
import com.dam.sharermonkeys.autentication.LoginActivity;
import com.dam.sharermonkeys.fragments.BalanceFragment;
import com.dam.sharermonkeys.pojos.Balance;
import com.dam.sharermonkeys.pojos.Expense;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

//LA VENTANA CUANDO ENTRAS A UN GRUPO DE GASTOS, TIENE QUE APARECER UNA LISTA DE TODOS GASTOS

public class ListExpenses extends AppCompatActivity {

    public static final String REALTIME_PATH = "https://fairshare-ae0be-default-rtdb.europe-west1.firebasedatabase.app/";
    RecyclerView recyclerViewExpenses;
    //RecyclerView recyclerViewBalance;
    DatabaseReference reference;
    ArrayList<Expense> list;
   // ArrayList<Balance> balances;
    ExpenseListAdapter adapter;
   // BalanceAdapter balanceAdapter;
    String fairshareId;
    Button btnBalance, btnExpenses, btnNewExpense;;
    ImageView imgExpenses, imgBalance;
    TextView tvMyToal, tvTotalExpenses;
    Double totalFairShareExpenses, userExpenses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_expenses);



        // Inicializar UI
        initializeUI();

        userExpenses = 0.00;
        totalFairShareExpenses = 0.00;

        tvMyToal = findViewById(R.id.tvMyTotalAmount);
        tvTotalExpenses = findViewById(R.id.tvTotalExpensesAmount);

        imgExpenses = findViewById(R.id.imgExpenses);
        imgBalance = findViewById(R.id.imgBalance);
        imgBalance.setVisibility(View.GONE);
        imgExpenses.setVisibility(View.VISIBLE);

        // Obtener el ID de FairShare de la actividad anterior
        fairshareId = getIntent().getStringExtra("id_fairshare");

        // Inicializar el RecyclerView de expenses
        recyclerViewExpenses = findViewById(R.id.rvListExpenses);
        recyclerViewExpenses.setHasFixedSize(true);
        recyclerViewExpenses.setLayoutManager(new LinearLayoutManager(this));



        // Inicializar los botones
        btnExpenses =findViewById(R.id.btnExpenses);
        btnBalance = findViewById(R.id.btnBalance);
        btnNewExpense = findViewById(R.id.btnNewExpense);


        // Inicializar la lista de gastos
        list = new ArrayList<>();

        // Inicializar el adaptador con la lista vacía
        adapter = new ExpenseListAdapter(list, this);
        recyclerViewExpenses.setAdapter(adapter);



        // Obtener referencia a la base de datos
        reference = FirebaseDatabase.getInstance(REALTIME_PATH).getReference();

        // Obtener los gastos asociados al FairShare
        fetchExpenses();
        calcTotals();

        btnBalance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgBalance.setVisibility(View.VISIBLE);
                imgExpenses.setVisibility(View.GONE);

                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                BalanceFragment bf = new BalanceFragment();
                Bundle bundle = new Bundle();
                bundle.putString("id_fairshare", fairshareId);
                bf.setArguments(bundle); // Establece los argumentos en 'bf'.
                ft.replace(R.id.fragmentContainer, bf); // Usa 'bf' en la transacción.
                ft.addToBackStack(null);
                recyclerViewExpenses.setVisibility(View.GONE);
                getSupportFragmentManager().popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

                ft.commit();

            }
        });
        btnExpenses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgExpenses.setVisibility(View.VISIBLE);
                imgBalance.setVisibility(View.GONE);

                recyclerViewExpenses.setVisibility(View.VISIBLE);
                getSupportFragmentManager().popBackStackImmediate();

            }
        });

        btnNewExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ListExpenses.this, NewExpense.class);
                i.putExtra("fairshareId", fairshareId);
                startActivity(i);
            }
        });
    }

    private void calcTotals() {
        // Declarar final o efectivamente final para acceder dentro de la clase interna
        final String[] userEmail = {""};
        final String[] userId = {null};

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            userEmail[0] = user.getEmail();
        } else {
            Log.e("ListExpenses", "UNABLE TO FETCH USER EMAIL");
            return; // Salir del método si no se puede obtener el correo electrónico del usuario
        }

        DatabaseReference usersRef = FirebaseDatabase.getInstance(REALTIME_PATH).getReference().child("Users");
        usersRef.orderByChild("email").equalTo(userEmail[0]).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Verificar si se encontró un usuario con el correo electrónico proporcionado
                if (dataSnapshot.exists()) {
                    // Obtener el ID del usuario
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        userId[0] = snapshot.getKey();
                        break; // Solo necesitamos el primer usuario encontrado
                    }

                    // Verificar si se encontró el ID del usuario
                    if (userId[0] != null) {
                        // Obtener referencia a la ubicación de los balances en la base de datos
                        DatabaseReference balancesRef = FirebaseDatabase.getInstance(REALTIME_PATH).getReference().child("Balance");

                        // Consulta para obtener todos los balances en el FairShare
                        balancesRef.orderByChild("id_fairshare").equalTo(fairshareId).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                // Iterar sobre los balances encontrados
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    // Obtener el valor de "expenses"
                                    Double payments = snapshot.child("payments").getValue(Double.class);
                                    if (payments != null) {
                                        // Sumar al total de gastos en el FairShare
                                        totalFairShareExpenses += payments;

                                        Balance balance = new Balance();
                                        balance.setPayments(payments);
                                        balance.setIdFareshare(snapshot.child("id_fairshare").getValue(String.class));
                                        balance.setIdUser(snapshot.child("id_user").getValue(String.class));
                                        balance.setExpenses(snapshot.child("expenses").getValue(Double.class));

                                        System.out.println("EN FOR DE LISTEXPENSES Payments: " + balance.getPayments());
                                        System.out.println("EN FOR DE LISTEXPENSES Expenses: " + balance.getExpenses());
                                        System.out.println("EN FOR DE LISTEXPENSES UserId: " + balance.getIdUser());
                                        System.out.println("EN FOR DE LISTEXPENSES FairShareId: " + balance.getIdFareshare());

                                        // Verificar si el balance pertenece al usuario
                                        String idUser = snapshot.child("id_user").getValue(String.class);
                                        if (idUser != null && idUser.equals(userId[0])) {
                                            // Sumar al total de gastos del usuario
                                            userExpenses += payments;
                                        }
                                    }
                                }

                                // Imprimir los resultados
                                Log.d("USER_EXPENSES", "Total expenses for user: " + userExpenses);
                                Log.d("TOTAL_FAIRSHARE_EXPENSES", "Total expenses in FairShare: " + totalFairShareExpenses);

                                tvMyToal.setText(String.format("%.2f", userExpenses));
                                tvTotalExpenses.setText(String.format("%.2f", totalFairShareExpenses));
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                // Manejar errores de la base de datos
                                Log.e("CALC_TOTALS", "Firebase Database Error: " + databaseError.getMessage());
                                Toast.makeText(ListExpenses.this, R.string.db_error, Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        Log.e("CALC_TOTALS", "User ID not found");
                    }
                } else {
                    Log.e("CALC_TOTALS", "User with email " + userEmail[0] + " not found");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Manejar errores de la base de datos
                Log.e("CALC_TOTALS", "Firebase Database Error: " + databaseError.getMessage());
                Toast.makeText(ListExpenses.this, R.string.db_error, Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void fetchExpenses() {
        // Referencia a la ubicación de los gastos en la base de datos
        DatabaseReference expensesRef = reference.child("Expenses");

        // Consulta para filtrar los gastos por el ID del FairShare
        expensesRef.orderByChild("idFairshare").equalTo(fairshareId).
                addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Limpiar la lista actual de gastos
                list.clear();
                // Iterar sobre los gastos encontrados
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // Obtener cada gasto y añadirlo a la lista
                    Expense expense = snapshot.getValue(Expense.class);

                    //Set manually porque firebase no fufa bien
                    String userIdPayer = snapshot.child("idUserPayer").getValue(String.class);
                    expense.setIdUserPayer(userIdPayer);
                    System.out.println("ID PAYER: " + userIdPayer);
                    System.out.println("ID PAYER EXPENSE: " + expense.getIdUserPayer());

                    if (expense.getIdFairshare().equals(fairshareId)) {
                        // Agregar el gasto a la lista solo si coincide con el ID del FairShare actual
                        list.add(expense);
                        System.out.println(expense.getIdUserPayer());
                        Log.d("LISTEXPENSES", "Expense: " + expense.getName());
                    }
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
        if (actionBar != null) {
            actionBar.setDisplayShowHomeEnabled(false); // Desactiva el logo predeterminado
            actionBar.setDisplayShowTitleEnabled(false); // Desactiva el título predeterminado

            // Inflar la vista personalizada
            LayoutInflater inflater = LayoutInflater.from(this);
            View customView = inflater.inflate(R.layout.action_bar_logo, null);

            // Configura las opciones de layout para centrar la vista
            ActionBar.LayoutParams params = new ActionBar.LayoutParams(
                    ActionBar.LayoutParams.WRAP_CONTENT,
                    ActionBar.LayoutParams.WRAP_CONTENT,
                    Gravity.CENTER);

            actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#2B2B2B")));
            actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white); // Establecer el icono de la flecha blanca
            actionBar.setDisplayHomeAsUpEnabled(true); // Agregar el boton de flecha para ir atras
            actionBar.setTitle(Html.fromHtml("<font color=\"#2B2B2B\">" + getString(R.string.app_name) + "</font>"));

            actionBar.setCustomView(customView, params);
            actionBar.setDisplayShowCustomEnabled(true); // Habilita la vista personalizada
        }

    }


    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_activity_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // Finaliza la actividad actual para volver a MainActivity
            finish();
            return true;

        } else if (item.getItemId() == R.id.mnLogOut) {
            AlertDialog.Builder builder = new AlertDialog.Builder(ListExpenses.this);
            builder.setCancelable(false);
            builder.setTitle(R.string.tit_dialog_exit);
            builder.setMessage(R.string.message_dialog_exit);
            builder.setPositiveButton(R.string.btn_yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(ListExpenses.this, LoginActivity.class);

                    /* Se establecen los flags en el intent para limpiar la pila de actividades
                    y crear una nueva tarea para la actividad de inicio de sesion.*/
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish(); // Se finaliza la actividad actual
                }
            });
            builder.setNegativeButton(R.string.btn_no, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();

        } else if (item.getItemId() == R.id.mnExit) {
            AlertDialog.Builder builder = new AlertDialog.Builder(ListExpenses.this);
            builder.setCancelable(false);
            builder.setTitle(R.string.tit_dialog_exit);
            builder.setMessage(R.string.message_dialog_exit);
            builder.setPositiveButton(R.string.btn_yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // finalizara todas las actividades actualmente en ejecucion
                    finishAffinity();
                    System.exit(0);
                }
            });
            builder.setNegativeButton(R.string.btn_no, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
        return super.onOptionsItemSelected(item);
    }


}
