package com.dam.sharermonkeys;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dam.sharermonkeys.adapterutils.ParticipantListAdapter;
import com.dam.sharermonkeys.pojos.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;

public class NewFairShare extends AppCompatActivity {

    public static final String REALTIME_PATH = "https://fairshare-ae0be-default-rtdb.europe-west1.firebasedatabase.app/";
    DatabaseReference reference;
    EditText etNewParticipant;
    Button btnAddParticipant;
    RecyclerView recyclerView;
    ParticipantListAdapter adapter;
    ArrayList<String> participantsList; //TODO REVISAR SI ESTA BIEN

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_fair_share);

        reference = FirebaseDatabase.getInstance(REALTIME_PATH).getReference("FairShares");

        etNewParticipant = findViewById(R.id.etNewParticipant);
        btnAddParticipant = findViewById(R.id.btnAddParticipant);
        recyclerView = findViewById(R.id.rvParticipants);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        participantsList = new ArrayList<>();

        adapter = new ParticipantListAdapter(participantsList);

        recyclerView.setAdapter(adapter);

        btnAddParticipant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addParticipant();
            }
        });
    }

    private void addParticipant() {
        final String email = etNewParticipant.getText().toString().trim();
        if (!isValidEmail(email)) {
            Toast.makeText(this, "Invalid email address", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("users");
        usersRef.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // El correo electrónico existe en Firebase
                    // Agregar el correo electrónico a la lista
                    participantsList.add(email);
                    // Notificar al RecyclerView que los datos han cambiado
                    adapter.notifyDataSetChanged();
                    // Mostrar mensaje de éxito
                    Toast.makeText(NewFairShare.this, "Successfully added", Toast.LENGTH_SHORT).show();
                } else {
                    // El correo electrónico no existe en Firebase
                    Toast.makeText(NewFairShare.this, "Participant is not registered in the application", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Manejar el error si ocurre
                Toast.makeText(NewFairShare.this, "Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }
}
