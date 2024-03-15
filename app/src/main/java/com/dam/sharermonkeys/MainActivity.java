package com.dam.sharermonkeys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.dam.sharermonkeys.adapterutils.FairShareListAdapter;
import com.dam.sharermonkeys.autentication.LoginActivity;
import com.dam.sharermonkeys.fragments.BalanceFragment;
import com.dam.sharermonkeys.pojos.User;


public class MainActivity extends AppCompatActivity{

    public static final String REALTIME_PATH = "https://fairshare-ae0be-default-rtdb.europe-west1.firebasedatabase.app/";

    RecyclerView recyclerView;
    FairShareListAdapter adapter;
    Button btnAddNew;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inicializeUI();

        btnAddNew = findViewById(R.id.btnAddNew);
        recyclerView = findViewById(R.id.rvList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Intent intent = getIntent();
        user = (User) intent.getSerializableExtra("user");

        if (user != null) {
            adapter = new FairShareListAdapter(user.getFairSharesList(), this);
            recyclerView.setAdapter(adapter);
        } else {
            Toast.makeText(MainActivity.this, R.string.error_fetch_user_main, Toast.LENGTH_SHORT).show();
        }

        btnAddNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, NewFairShare.class);
                i.putExtra("userLogin", user);
                startActivity(i);
            }
        });
    }

    private void inicializeUI() {

        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#2B2B2B")));
        actionBar.setLogo(R.drawable.fairshare2_small);
        actionBar.setDisplayUseLogoEnabled(true); // Habilita el uso del logo en lugar del título
        actionBar.setDisplayShowHomeEnabled(true); // Muestra el logo en la barra de acción
        actionBar.setTitle((Html.fromHtml("<font color=\"#2B2B2B\">" + getString(R.string.app_name) + "</font>")));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_activity_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.mnLogOut) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setCancelable(false);
            builder.setTitle(R.string.tit_dialog_exit);
            builder.setMessage(R.string.message_dialog_exit);
            builder.setPositiveButton(R.string.btn_yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);

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
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setCancelable(false);
            builder.setTitle(R.string.tit_dialog_exit);
            builder.setMessage(R.string.message_dialog_exit);
            builder.setPositiveButton(R.string.btn_yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
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
        } else if (item.getItemId() == R.id.mnDelete) {
            Toast.makeText(this, R.string.hold_to_delete, Toast.LENGTH_LONG).show();
            //TODO: Implementar acción para borrar un grupo de gastos (Al dejar pulsado?)
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (user != null && adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }


}
