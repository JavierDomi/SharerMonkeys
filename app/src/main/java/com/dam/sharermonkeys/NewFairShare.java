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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
    ArrayList<User> participantsList;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_fair_share);

        mAuth = FirebaseAuth.getInstance();

        reference = FirebaseDatabase.getInstance(REALTIME_PATH).getReference("Users");

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

        FirebaseUser currentUser = mAuth.getCurrentUser();
        String userEmail = currentUser.getEmail();

        DatabaseReference usersRef = FirebaseDatabase.getInstance(REALTIME_PATH).getReference().child("Users");
        usersRef.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        User user = userSnapshot.getValue(User.class);
                        if (user != null && user.getEmail().equals(email)) {
                            if (!user.getEmail().equals(userEmail) && !isUserAlreadyAdded(user)) {
                                participantsList.add(user); // Agregar el objeto User completo
                                adapter.notifyDataSetChanged();
                                Toast.makeText(NewFairShare.this, "Successfully added", Toast.LENGTH_SHORT).show();
                                return;
                            } else {
                                Toast.makeText(NewFairShare.this, R.string.cant_add_sef_or_existing, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
                Toast.makeText(NewFairShare.this, "User not registered", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(NewFairShare.this, "Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean isUserAlreadyAdded(User user) {
        for (User participant : participantsList) {
            if (participant.getEmail().equals(user.getEmail())) {
                return true;
            }
        }
        return false;
    }


    private boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }
}
