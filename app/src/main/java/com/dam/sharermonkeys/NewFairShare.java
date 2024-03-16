    package com.dam.sharermonkeys;

    import android.content.Intent;
    import android.graphics.Color;
    import android.graphics.drawable.ColorDrawable;
    import android.os.Bundle;
    import android.text.Html;
    import android.text.TextUtils;
    import android.util.Patterns;
    import android.view.MenuItem;
    import android.view.View;
    import android.widget.Button;
    import android.widget.EditText;
    import android.widget.Toast;
    import androidx.annotation.NonNull;
    import androidx.appcompat.app.ActionBar;
    import androidx.appcompat.app.AppCompatActivity;
    import androidx.recyclerview.widget.LinearLayoutManager;
    import androidx.recyclerview.widget.RecyclerView;

    import com.dam.sharermonkeys.adapterutils.ParticipantListAdapter;
    import com.dam.sharermonkeys.pojos.FairShare;
    import com.dam.sharermonkeys.pojos.User;
    import com.google.android.gms.tasks.OnFailureListener;
    import com.google.android.gms.tasks.OnSuccessListener;
    import com.google.firebase.auth.FirebaseAuth;
    import com.google.firebase.auth.FirebaseUser;
    import com.google.firebase.database.DataSnapshot;
    import com.google.firebase.database.DatabaseError;
    import com.google.firebase.database.DatabaseReference;
    import com.google.firebase.database.FirebaseDatabase;
    import com.google.firebase.database.Query;
    import com.google.firebase.database.ValueEventListener;
    import java.util.ArrayList;
    import java.util.HashMap;
    import java.util.List;
    import java.util.Map;

    public class NewFairShare extends AppCompatActivity {

        public static final String REALTIME_PATH = "https://fairshare-ae0be-default-rtdb.europe-west1.firebasedatabase.app/";
        DatabaseReference reference;
        EditText etNewParticipant, etTitle, etDesc;
        Button btnAddParticipant, btnSave;
        RecyclerView recyclerView;
        ParticipantListAdapter adapter;
        ArrayList<User> participantsList;
        FirebaseAuth mAuth;
        FairShare newFairShare;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_new_fair_share);
            // Inicializar UI
            initializeUI();
            mAuth = FirebaseAuth.getInstance();

            reference = FirebaseDatabase.getInstance(REALTIME_PATH).getReference("Users");

            etNewParticipant = findViewById(R.id.etNewParticipant);
            etTitle = findViewById(R.id.etTitle);
            etDesc = findViewById(R.id.etDescription);

            btnAddParticipant = findViewById(R.id.btnAddParticipant);
            btnSave = findViewById(R.id.btnSave);

            recyclerView = findViewById(R.id.rvParticipants);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));

            participantsList = new ArrayList<>();

            adapter = new ParticipantListAdapter(participantsList);
            recyclerView.setAdapter(adapter);

            newFairShare = new FairShare();

            addParticipantCreator();

            btnAddParticipant.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addParticipant();
                }
            });

            btnSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    FirebaseDatabase database = FirebaseDatabase.getInstance(MainActivity.REALTIME_PATH);
                    DatabaseReference usersRef = database.getReference("FairShares");

                    String title = etTitle.getText().toString(),
                            desc = etDesc.getText().toString();

                    if (!title.equals("") && !desc.equals("") && !participantsList.isEmpty()) {

                        DatabaseReference newFairShareRef = usersRef.push(); // Crear una nueva referencia para el nuevo fairShare
                        String fairShareId = newFairShareRef.getKey();

                        /*newFairShare.setName(title);
                        newFairShare.setDescription(desc);
                        newFairShare.setUserList(participantsList);*/

                        Map<String, Object> newFairShare = new HashMap<>();
                        newFairShare.put("name", title);
                        newFairShare.put("description", desc);
                        newFairShare.put("participants", participantsList);

                        newFairShareRef.setValue(newFairShare).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {

                                clearFields();

                                Toast.makeText(NewFairShare.this, R.string.new_fairShare_uploaded, Toast.LENGTH_SHORT).show();

                                for (User participant : participantsList) {
                                    Query query = reference.orderByChild("email").equalTo(participant.getEmail());
                                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.exists()) {
                                                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                                                    String userId = userSnapshot.getKey();
                                                    DatabaseReference userRef = reference.child(userId).child("participa_fairshares");
                                                    Map<String, Object> fairShareMap = new HashMap<>();
                                                    fairShareMap.put("id_fairshare", fairShareId); // El ID del nuevo fairShare
                                                    fairShareMap.put("name", title); // Nombre del fairShare
                                                    fairShareMap.put("description", desc); // Descripción del fairShare
                                                    userRef.push().setValue(fairShareMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void unused) {
                                                            // Crear el objeto Balance después de crear el FairShare
                                                            DatabaseReference balanceRef = FirebaseDatabase.getInstance(REALTIME_PATH)
                                                                    .getReference("Balance");
                                                            Map<String, Object> balanceMap = new HashMap<>();
                                                            balanceMap.put("id_fairshare", fairShareId);
                                                            balanceMap.put("id_user", userId);
                                                            balanceMap.put("expenses", 0); // set default value to 0
                                                            balanceMap.put("payments", 0);
                                                            balanceRef.push().setValue(balanceMap);

                                                            balanceRef.addValueEventListener(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(@NonNull DataSnapshot snapshot) {

                                                                    refreshAndLaunchMainActivity();

                                                                }

                                                                @Override
                                                                public void onCancelled(@NonNull DatabaseError error) {

                                                                    Toast.makeText(NewFairShare.this, R.string.unable_to_upload_balance, Toast.LENGTH_SHORT).show();

                                                                }
                                                            });



                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Toast.makeText(NewFairShare.this, "Error creating FairShare: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                            Toast.makeText(NewFairShare.this, "Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(NewFairShare.this, R.string.unable_to_upload_fairShare, Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        Toast.makeText(NewFairShare.this, R.string.complete_all_fields, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        private void addParticipant() {
            final String email = etNewParticipant.getText().toString().trim();
            if (!isValidEmail(email)) {
                Toast.makeText(this, R.string.invalid_email, Toast.LENGTH_SHORT).show();
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
                                    participantsList.add(user);
                                    adapter.notifyDataSetChanged();
                                    etNewParticipant.setText("");
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

        public void clearFields() {
            etTitle.setText("");
            etDesc.setText("");
            etNewParticipant.setText("");
        }

        public void addParticipantCreator() {
            Intent intent = getIntent();
            if(intent != null && intent.hasExtra("userLogin")) {
                User user = (User) intent.getSerializableExtra("userLogin");
                participantsList.add(user);
                adapter.notifyDataSetChanged();
            }
        }

        public void refreshAndLaunchMainActivity() {
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser != null) {
                String userEmail = currentUser.getEmail();
                DatabaseReference userRef = FirebaseDatabase.getInstance(REALTIME_PATH).getReference("Users");
                userRef.orderByChild("email").equalTo(userEmail).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        List<FairShare> fairShares = new ArrayList<>();

                        if (snapshot.exists()) {
                            DataSnapshot userSnapshot = snapshot.getChildren().iterator().next(); // Obtiene el primer resultado (el único resultado esperado)
                            String username = userSnapshot.child("username").getValue(String.class);
                            String email = userSnapshot.child("email").getValue(String.class);

                            // Obtener la lista de FairShares
                            ArrayList<FairShare> fairSharesList = new ArrayList<>();
                            DataSnapshot fairSharesSnapshot = userSnapshot.child("participa_fairshares");
                            for (DataSnapshot fairShareSnapshot : fairSharesSnapshot.getChildren()) {
                                String fairShareId = fairShareSnapshot.child("id_fairshare").getValue(String.class);
                                String fairShareName = fairShareSnapshot.child("name").getValue(String.class);
                                String fairShareDescription = fairShareSnapshot.child("description").getValue(String.class);

                                // Obtener la lista de usuarios participantes en este FairShare
                                ArrayList<User> fairShareUsers = new ArrayList<>();
                                DataSnapshot fairShareUsersSnapshot = fairShareSnapshot.child("participants");
                                for (DataSnapshot participantSnapshot : fairShareUsersSnapshot.getChildren()) {
                                    String userUsername = participantSnapshot.child("username").getValue(String.class);
                                    String participantEmail = participantSnapshot.child("email").getValue(String.class);

                                    // Crear objeto User
                                    User participantUser = new User(userUsername, participantEmail, null);
                                    fairShareUsers.add(participantUser);
                                }

                                // Crear objeto FairShare y completar la lista de usuarios
                                FairShare fairShare = new FairShare(fairShareId, fairShareName, fairShareDescription, fairShareUsers);
                                fairSharesList.add(fairShare);
                            }

                            //fairSharesList.add(newFairShare);

                            // Crear objeto User
                            User user = new User(username, email, fairSharesList);

                            // Pasar el objeto User a la actividad principal
                            Intent intent = new Intent(NewFairShare.this, MainActivity.class);
                            intent.putExtra("user", user);
                            startActivity(intent);
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(NewFairShare.this, "Error al obtener datos del usuario", Toast.LENGTH_SHORT).show();
                    }
                });
            }
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