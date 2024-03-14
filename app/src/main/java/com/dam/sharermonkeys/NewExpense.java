package com.dam.sharermonkeys;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.dam.sharermonkeys.R;
import com.dam.sharermonkeys.adapterutils.NewExpenseAdapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class NewExpense extends AppCompatActivity {
    RecyclerView recyclerView;
    NewExpenseAdapter adapter;
    EditText etDate;
    Button btnSave;
    Spinner spinnerUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_expense);

        etDate = findViewById(R.id.etDate);
        btnSave = findViewById(R.id.btnSave);
        spinnerUsers = findViewById(R.id.spinnerUsers);

        recyclerView = findViewById(R.id.rvCheckBox);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

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

        // Definir una lista de usuarios de prueba
        List<String> userList = new ArrayList<>();
        userList.add("User 1");
        userList.add("User 2");
        userList.add("User 3");
        userList.add("User 4");

        // Configurar el adaptador del Spinner con la lista de usuarios de prueba
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, userList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerUsers.setAdapter(adapter);

        // Configurar el Listener para el Spinner
        spinnerUsers.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedUser = parent.getItemAtPosition(position).toString();
                Toast.makeText(NewExpense.this, "Usuario seleccionado: " + selectedUser, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // No hacer nada cuando no se selecciona nada
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
}