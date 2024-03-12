package com.dam.sharermonkeys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.dam.sharermonkeys.adapterutils.FairShareListAdapter;
import com.dam.sharermonkeys.autentication.LoginActivity;
import com.dam.sharermonkeys.pojos.User;

public class MainActivity extends AppCompatActivity {

    public static final String REALTIME_PATH = "https://fairshare-ae0be-default-rtdb.europe-west1.firebasedatabase.app/";

    RecyclerView recyclerView;
    FairShareListAdapter adapter;
    Button btnAddNew;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
                Intent i = new Intent(MainActivity.this, NewExpense.class);
                startActivity(i);
            }
        });
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
                    Intent i = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(i);
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
        // Actualizar la lista de FairShares si es necesario
        if (user != null && adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }
}
