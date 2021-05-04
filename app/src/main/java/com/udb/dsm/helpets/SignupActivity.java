package com.udb.dsm.helpets;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.udb.dsm.helpets.listElements.User;

import java.util.UUID;


public class SignupActivity extends AppCompatActivity {
    TextInputLayout etEmail, etPassword, etName, etAddress, etNumber, etRepeatPassword;
    TextView tvLogin;
    Button btnSignup;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mAuth = FirebaseAuth.getInstance();

        initializeElements();
        initializeFirebase();
    }

    private void createUser(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Se ha registrado correctamente!", Toast.LENGTH_LONG).show();
                            insertData();

                            Intent i = new Intent(SignupActivity.this, MainActivity.class);
                            startActivity(i);

                            finish();
                        }
                        else {
                            Toast.makeText(getApplicationContext(), "Ha fallado el registro! Por favor, inténtelo más tarde", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    public void insertData(){
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        User u = new User();
        u.setUserId(firebaseUser.getUid());
        u.setUserName(etName.getEditText().getText().toString());
        u.setUserPhone(etNumber.getEditText().getText().toString());
        u.setUserAddress(etAddress.getEditText().getText().toString());
        // Default images
        u.setUserImageBackground("https://firebasestorage.googleapis.com/v0/b/helpets-ccc8d.appspot.com/o/users%2Fbackgrounds%2Fdefault.jpeg?alt=media&token=f7dec4de-f02c-4e77-b9b6-0566b1058794");
        u.setUserImageProfile("https://firebasestorage.googleapis.com/v0/b/helpets-ccc8d.appspot.com/o/users%2Fprofiles%2Fdefault.jpg?alt=media&token=c112b593-3d69-4d0b-bc80-570acf6e9b34");

        databaseReference.child("users").child(u.getUserId()).setValue(u);
        Toast.makeText(this, "Se ha registrado correctamente!", Toast.LENGTH_LONG).show();
    }

    public void initializeFirebase(){
        FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }

    protected void initializeElements() {
        etName = this.findViewById(R.id.etName);
        etEmail = this.findViewById(R.id.etEmail);
        etAddress = this.findViewById(R.id.etAddress);
        etNumber = this.findViewById(R.id.etNumber);
        etPassword = this.findViewById(R.id.etPassword);
        etRepeatPassword = this.findViewById(R.id.etRepeatPassword);

        tvLogin = this.findViewById(R.id.tvLogin);
        tvLogin.setOnClickListener(v -> {
            Intent i = new Intent(SignupActivity.this, LoginActivity.class);
            startActivity(i);
        });

        btnSignup = this.findViewById(R.id.btnSignup);
        btnSignup.setOnClickListener(v -> {
            if(validateFields() && validatePassword()) {
                createUser(etEmail.getEditText().getText().toString(), etPassword.getEditText().getText().toString());
            }
            else {
                Toast.makeText(this, "Debe rellenar todos los campos requeridos!", Toast.LENGTH_LONG).show();
            }
        });
    }

    protected boolean validatePassword() {
        int b = 0;
        String Password = etPassword.getEditText().getText().toString().trim();
        String RepeatPassword = etRepeatPassword.getEditText().getText().toString().trim();
        etPassword.setError(null);
        etRepeatPassword.setError(null);
        if (!Password.matches(".*[!@#$%^&*+=?-].*"))
            etPassword.setError("Debe contener caracteres especiales");
        else b++;
        if (!Password.matches(".*\\d.*"))
            etPassword.setError("Debe contener al menos un número");
        else b++;
        if (!Password.matches(".*[a-z].*"))
            etPassword.setError("Debe contener minúsculas");
        else b++;
        if (!Password.matches(".*[A-Z].*"))
            etPassword.setError("Debe contener mayúsculas");
        else b++;
        if (!Password.matches(".{8,15}"))
            etPassword.setError("Debe contener de 8 a 15 caracteres");
        else b++;
        if(!Password.equals(RepeatPassword))
            etRepeatPassword.setError("Las contraseñas no Coinciden");
        else b++;
        if (b == 6) return true;
        else return false;
    }

    protected boolean validateFields() {
        if(etName.getEditText().getText().toString().isEmpty())
            etName.setError("Required");
        else if (etEmail.getEditText().getText().toString().isEmpty())
            etEmail.setError("Required");
        else if(etPassword.getEditText().getText().toString().isEmpty())
            etPassword.setError("Required");
        else if (etRepeatPassword.getEditText().getText().toString().isEmpty())
            etRepeatPassword.setError("Required");
        else return true;

        return false;
    }
}