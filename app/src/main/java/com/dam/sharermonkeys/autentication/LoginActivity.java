package com.dam.sharermonkeys.autentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dam.sharermonkeys.MainActivity;
import com.dam.sharermonkeys.R;
import com.dam.sharermonkeys.pojos.FairShare;
import com.dam.sharermonkeys.pojos.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity {
    TextInputEditText etEmail;
    TextInputEditText etPassword;
    Button btnLogin;
    ProgressBar progBar;
    TextView tvNewAccount;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        progBar = findViewById(R.id.progressBar);
        tvNewAccount = findViewById(R.id.tvNewAccount);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progBar.setVisibility(View.VISIBLE);
                String email = String.valueOf(etEmail.getText()).trim();
                String password = String.valueOf(etPassword.getText()).trim();


                if (TextUtils.isEmpty(email)) {
                    etEmail.setError(getString(R.string.enter_email));
                    progBar.setVisibility(View.GONE);
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(LoginActivity.this, "Enter the password", Toast.LENGTH_SHORT).show();
                    progBar.setVisibility(View.GONE);
                    return;
                }

                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            // Obtener datos del usuario desde la base de datos y pasarlos a la actividad principal
                            FirebaseUser currentUser = mAuth.getCurrentUser();
                            if (currentUser != null) {
                                String userEmail = currentUser.getEmail();
                                DatabaseReference userRef = FirebaseDatabase.getInstance(MainActivity.REALTIME_PATH).getReference("Users");
                                userRef.orderByChild("email").equalTo(userEmail).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
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
                                                    String userEmail = participantSnapshot.child("email").getValue(String.class);

                                                    // Crear objeto User
                                                    User user = new User(userUsername, userEmail, null); // Aquí puedes completar la lista de FairShares si es necesario
                                                    fairShareUsers.add(user);
                                                }


                                                // Crear objeto FairShare y completar la lista de usuarios
                                                FairShare fairShare = new FairShare(fairShareId, fairShareName, fairShareDescription, fairShareUsers);
                                                fairSharesList.add(fairShare);
                                            }

                                            // Crear objeto User
                                            User user = new User(username, email, fairSharesList);

                                            // Pasar el objeto User a la actividad principal
                                            Intent i = new Intent(getApplicationContext(), MainActivity.class);
                                            i.putExtra("user", user);
                                            startActivity(i);
                                            finish();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Toast.makeText(LoginActivity.this, "Error al obtener datos del usuario", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        } else {
                            Toast.makeText(LoginActivity.this, "Authentication failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        tvNewAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), NewAccountActivity.class);
                startActivity(i);
                finish();
            }
        });
    }
}