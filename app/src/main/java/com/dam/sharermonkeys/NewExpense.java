package com.dam.sharermonkeys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.dam.sharermonkeys.adapterutils.NewExpenseAdapter;
import com.dam.sharermonkeys.pojos.Expense;
import com.dam.sharermonkeys.pojos.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class NewExpense extends AppCompatActivity {
    RecyclerView recyclerView;
    NewExpenseAdapter adapterNewExpense;
    EditText etTitle, etAmount, etDate;

    Button btnSave;
    Spinner spinnerUsers;
    String fairShareId;
    User selectedPayer;
    ArrayList<User> userList;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_expense);

        // Inicializar UI
        initializeUI();

        databaseReference = FirebaseDatabase.getInstance(MainActivity.REALTIME_PATH).getReference();

        fairShareId = getIntent().getStringExtra("fairshareId");
        System.out.println(fairShareId);

        fetchUsers(fairShareId);

        etTitle = findViewById(R.id.etTitle);
        etAmount = findViewById(R.id.etAmount);
        etDate = findViewById(R.id.etDate);

        btnSave = findViewById(R.id.btnSave);
        spinnerUsers = findViewById(R.id.spinnerUsers);

        recyclerView = findViewById(R.id.rvCheckBox);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapterNewExpense = new NewExpenseAdapter(userList, NewExpense.this);
        recyclerView.setAdapter(adapterNewExpense);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String title = etTitle.getText().toString();
                String date = etDate.getText().toString();
                String sAmount = etAmount.getText().toString();
                // payer = selectedPayer
                ArrayList<User> participants = adapterNewExpense.getSelectedUsers();

                if (!title.equals("") && !date.equals("") && !sAmount.equals("") && participants.size() > 0) {

                    double amount = Double.parseDouble(sAmount);

                    double toPay = amount / (participants.size());

                        if (amount >= participants.size()) {

                            Expense expense = new Expense(title, selectedPayer.getId(), fairShareId, date, amount, participants);
                            //PUSH NEW EXPENSE TO FIREBASE
                            databaseReference.child("Expenses").push().setValue(expense);

                            //UPDATE BALANCES
                            updatePayerBalance(amount);
                            updateParticipantsBalance(toPay, participants);

                            clearFields();

                            Toast.makeText(NewExpense.this, R.string.expese_created, Toast.LENGTH_SHORT).show();

                            Intent i = new Intent(NewExpense.this, ListExpenses.class);
                            i.putExtra("id_fairshare", fairShareId);
                            startActivity(i);

                        } else {
                            Toast.makeText(NewExpense.this, R.string.minimun_amount, Toast.LENGTH_SHORT).show();
                        }
                } else {
                    Toast.makeText(NewExpense.this, R.string.complete_all_fields, Toast.LENGTH_SHORT).show();
                }
            }
        });

        etDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarDialogoSelectorFecha();
            }
        });

    }

    private void clearFields() {

        etTitle.setText("");
        etDate.setText("");
        etAmount.setText("");

    }


    private void fetchUsers(String fairShairId) {

        userList = new ArrayList<User>();
        databaseReference.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    // Obtén los datos de cada usuario
                    String username = userSnapshot.child("username").getValue(String.class);
                    String email = userSnapshot.child("email").getValue(String.class);
                    String userId = userSnapshot.getKey();

                    // Verifica si el usuario tiene participación en el fairshare deseado
                    if (userSnapshot.child("participa_fairshares").exists()) {
                        for (DataSnapshot fairshareSnapshot : userSnapshot.child("participa_fairshares").getChildren()) {
                            String fairshareId = fairshareSnapshot.child("id_fairshare").getValue(String.class);

                            // Verifica si el fairshareId coincide con el que estás buscando
                            if (fairshareId.equals(fairShairId)) {
                                // Si coincide, agrega el usuario a la lista y sal del bucle
                                User user = new User(username, userId, email, null);
                                user.setSelected(true);
                                userList.add(user);
                                break;
                            }
                        }
                    }
                }

                System.out.println(userList.size());
                setUpSpinnerAdapter();
                adapterNewExpense.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Maneja cualquier error que ocurra al obtener los datos
                Log.e("NewExpense", "Error al obtener usuarios: " + databaseError.getMessage());
            }
        });

    }

    private void mostrarDialogoSelectorFecha() {
        // Obtener la fecha actual
        final Calendar miCalendario = Calendar.getInstance();
        int year = miCalendario.get(Calendar.YEAR);
        int month = miCalendario.get(Calendar.MONTH);
        int day = miCalendario.get(Calendar.DAY_OF_MONTH);

        // Crear el DatePickerDialog
        DatePickerDialog dialogoSelectorFecha = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
                        // Actualizar el EditText con la fecha seleccionada en el formato deseado
                        String fechaFormateada = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                        etDate.setText(fechaFormateada);
                    }
                }, year, month, day);

        // Mostrar el DatePickerDialog
        dialogoSelectorFecha.show();
    }

    private void setUpSpinnerAdapter() {

        List<String> dropList = new ArrayList<>();
        for (User user : userList) {

            dropList.add(user.getUsername());

        }

        // Configurar el adaptador del Spinner con la lista de usuarios de prueba
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, dropList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerUsers.setAdapter(adapter);

        // Configurar el Listener para el Spinner
        spinnerUsers.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedUsername = parent.getItemAtPosition(position).toString();
                selectedPayer = null;

                for (User user : userList) {
                    if (user.getUsername().equals(selectedUsername)) {
                        selectedPayer = user;
                        break;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(NewExpense.this, R.string.must_select_user_spinner, Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void updateParticipantsBalance(Double toPay, ArrayList<User> participants) {

        for(User participant : participants) {
            databaseReference.child("Balance").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for(DataSnapshot balanceSnapshot : dataSnapshot.getChildren()) {
                        String userId = balanceSnapshot.child("id_user").getValue(String.class);
                        String fairshareId = balanceSnapshot.child("id_fairshare").getValue(String.class);
                        if(userId != null && fairshareId != null && userId.equals(participant.getId()) && fairshareId.equals(fairShareId)) {
                            // Encontrado el balance correcto, actualizar el campo "expenses"
                            double currentExpenses = balanceSnapshot.child("expenses").getValue(Double.class);
                            currentExpenses += toPay;
                            // Actualizar el campo "expenses" del usuario en la base de datos
                            databaseReference.child("Balance").child(balanceSnapshot.getKey()).child("expenses").setValue(currentExpenses);
                            // Salir del bucle ya que hemos encontrado el balance correcto
                            break;
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Manejar cualquier error que ocurra al acceder a los datos
                    Log.e("NewExpense", "Error al acceder al saldo del usuario: " + databaseError.getMessage());
                }
            });
        }


    }

    public void updatePayerBalance (Double amount) {

        databaseReference.child("Balance").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot balanceSnapshot : dataSnapshot.getChildren()) {
                    String userId = balanceSnapshot.child("id_user").getValue(String.class);
                    String fairshareId = balanceSnapshot.child("id_fairshare").getValue(String.class);
                    if(userId != null && fairshareId != null && userId.equals(selectedPayer.getId()) && fairshareId.equals(fairShareId)) {
                        // Encontrado el balance correcto, actualizar el saldo
                        double currentBalance = balanceSnapshot.child("payments").getValue(Double.class);
                        currentBalance += amount;
                        // Actualizar el saldo del usuario en la base de datos
                        databaseReference.child("Balance").child(balanceSnapshot.getKey()).child("payments").setValue(currentBalance);
                        // Salir del bucle ya que hemos encontrado el balance correcto
                        return;
                    }
                }
                // Si llega aquí, significa que no se encontró el balance adecuado
                Toast.makeText(NewExpense.this, R.string.unable_find_balance, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Manejar cualquier error que ocurra al acceder a los datos
                Toast.makeText(NewExpense.this, R.string.unable_fetch_balance, Toast.LENGTH_SHORT).show();
            }
        });

    }
    private void initializeUI() {
        // Configurar la barra de acción
        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#2B2B2B")));
        actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white); // Establecer el icono de la flecha blanca
        actionBar.setDisplayHomeAsUpEnabled(true); // Agregar el boton de flecha para ir atras
        actionBar.setTitle(Html.fromHtml("<font color=\"#2B2B2B\">" + getString(R.string.app_name) + "</font>"));

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // ID del botón de retroceso en la ActionBar
        if (item.getItemId() == android.R.id.home) {
            // Finaliza la actividad actual para volver a MainActivity
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}