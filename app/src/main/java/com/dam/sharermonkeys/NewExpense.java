package com.dam.sharermonkeys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.dam.sharermonkeys.R;
import com.dam.sharermonkeys.adapterutils.ExpenseListAdapter;
import com.dam.sharermonkeys.adapterutils.NewExpenseAdapter;
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
    NewExpenseAdapter adapter;
    EditText etDate;
    Button btnSave;
    Spinner spinnerUsers;
    String fairShairId;
    ArrayList<User> userList;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_expense);

        databaseReference = FirebaseDatabase.getInstance(MainActivity.REALTIME_PATH).getReference();

        fairShairId = getIntent().getStringExtra("fairshareId");
        System.out.println(fairShairId);

        fetchUsers(fairShairId);

        etDate = findViewById(R.id.etDate);
        btnSave = findViewById(R.id.btnSave);
        spinnerUsers = findViewById(R.id.spinnerUsers);

        recyclerView = findViewById(R.id.rvCheckBox);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // TODO PASAR LA LISTA AL ADAPTER
        adapter = new NewExpenseAdapter(userList, this);
        recyclerView.setAdapter(adapter);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(NewExpense.this, "Guardando PRUEBA", Toast.LENGTH_SHORT).show();
            }
        });

        etDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarDialogoSelectorFecha();
            }
        });

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
                            if (fairshareId.equals(fairshareId)) {
                                // Si coincide, agrega el usuario a la lista y sal del bucle
                                User user = new User(username, email, userId, null);
                                userList.add(user);
                                break;
                            }
                        }
                    }
                }

                System.out.println(userList.size());
                setUpSpinnerAdapter();
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
                String selectedUser = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(NewExpense.this, R.string.must_select_user_spinner, Toast.LENGTH_SHORT).show();
            }
        });

    }

}