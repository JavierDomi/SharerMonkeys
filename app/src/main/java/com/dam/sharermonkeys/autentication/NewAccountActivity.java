package com.dam.sharermonkeys.autentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dam.sharermonkeys.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class NewAccountActivity extends AppCompatActivity {
    TextInputEditText etEmail;
    TextInputEditText etPassword;
    TextInputEditText etConfPass;
    Button btnSignUp;
    TextView tvLogin;
    FirebaseAuth mAuth;
    ProgressBar progBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_account);

        mAuth = FirebaseAuth.getInstance();

        etConfPass= findViewById(R.id.etConfirmPasswordReg);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnSignUp = findViewById(R.id.btnSignUp);
        progBar = findViewById(R.id.progressBar);
        tvLogin = findViewById(R.id.tvLogin);

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //progBar.setVisibility(View.GONE);
                String email = String.valueOf(etEmail.getText());
                String password = String.valueOf(etPassword.getText());

                if(!validateEmail(email) || !validatePassword()){
                    return;
                }

                // ESTA EL METODO validateEmail y validatePassword
                /*if (TextUtils.isEmpty(email) && TextUtils.isEmpty(password)) {
                    // Ambos email y contraseña están vacíos
                    Toast.makeText(NewAccountActivity.this, R.string.enter_email_password, Toast.LENGTH_SHORT).show();
                    progBar.setVisibility(View.GONE);
                    return;
                }
                if (TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
                    // El email está vacío, pero la contraseña no
                    Toast.makeText(NewAccountActivity.this, R.string.enter_email, Toast.LENGTH_SHORT).show();
                    progBar.setVisibility(View.GONE);
                    return;
                }
                if (!TextUtils.isEmpty(email) && TextUtils.isEmpty(password)) {
                    // La contraseña está vacía, pero el email no
                    Toast.makeText(NewAccountActivity.this, R.string.enter_password, Toast.LENGTH_SHORT).show();
                    progBar.setVisibility(View.GONE);
                    return;
                }*/
                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            Toast.makeText(NewAccountActivity.this, R.string.account_created, Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                            startActivity(intent);
                            finish();

                        } else {
                            Toast.makeText(NewAccountActivity.this, R.string.account_creation_fail, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();

            }
        });
    }

    private boolean validatePassword() {
        String password;
        password = String.valueOf(etPassword.getText());
        String confPasswod;
        confPasswod = String.valueOf(etConfPass.getText());

        if(password.isEmpty()){
            Toast.makeText(NewAccountActivity.this, R.string.enter_password, Toast.LENGTH_SHORT).show();
            return false;
        }if (password.length()<5){
            Toast.makeText(NewAccountActivity.this, R.string.enter_password_char, Toast.LENGTH_SHORT).show();
            return false;

        }if(!password.equals(confPasswod)) {
            Toast.makeText(NewAccountActivity.this, R.string.enter_password_noMatch, Toast.LENGTH_SHORT).show();
            return false;

            //contraseña tiene que tener al menos un numero
        }if (!password.matches(".*\\d.*")) {
            // '.*' -> repetido cero o mas veces.
            // '\\d' -> verifica la presencia de un digito
            // '.*' -> repetido cero o mas veces.
            Toast.makeText(NewAccountActivity.this, R.string.et_enter_password_digit, Toast.LENGTH_SHORT).show();
            return false;
        }if (!password.matches(".*[a-zA-Z].*")) {
            // Esto verifica si la contraseña contiene al menos una letra
            Toast.makeText(NewAccountActivity.this, R.string.et_enter_password_letter, Toast.LENGTH_SHORT).show();
            return false;
        }
            return true;

    }

    private boolean validateEmail(String email) {
        if(email.isEmpty()){
            //saler error en editText hasta que no pones correo completo
            etEmail.setError(getString(R.string.et_error_email));
            return false;
        }else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            etEmail.setError(getString(R.string.set_error_email_invalid));
            return false;
        }else{
            etEmail.setError(null);
            return true;
        }
    }
}