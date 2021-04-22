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
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.udb.dsm.helpets.listElements.User;

import java.util.UUID;


public class SignupActivity extends AppCompatActivity {
    EditText etName, etAddress, etEmail, etNumber, etPassword, etRepeatPassword;
    TextView tvLogin;
    Button btnSignup;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        initializeElements();
        initializeFirebase();
        tvLogin.setOnClickListener(v -> {
            Intent i = new Intent(SignupActivity.this, LoginActivity.class);
            startActivity(i);
        });
        btnSignup.setOnClickListener(v -> {
            insertData();
        });
    }

    public void insertData(){
        if (validateFields() == true){
            if (validatePassword()){
                User u = new User();
                u.setUserId(UUID.randomUUID().toString());
                u.setUserName(etName.getText().toString());
                u.setUserEmail(etEmail.getText().toString());
                u.setUserPhone(etNumber.getText().toString());
                u.setUserPassword(etPassword.getText().toString());
                u.setUserAddress(etAddress.getText().toString());
                u.setUserImageBackground("");
                u.setUserImageProfile("");
                databaseReference.child("users").child(u.getUserId()).setValue(u);
                Toast.makeText(this, "Sign Up Correct!", Toast.LENGTH_LONG).show();
                Intent i = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(i);
            }
        }
        else
            Toast.makeText(this, "Fill in the blanks!", Toast.LENGTH_LONG).show();
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
        btnSignup = this.findViewById(R.id.btnSignup);
    }

    protected boolean validatePassword() {
        int b = 0;
        String Password = etPassword.getText().toString().trim();
        String RepeatPassword = etRepeatPassword.getText().toString().trim();
        etPassword.setError(null);
        etRepeatPassword.setError(null);
        if (!Password.matches(".*[!@#$%^&*+=?-].*"))
            etPassword.setError("Debe contener caracteres especiales");
        else b++;
        if (!Password.matches(".*\\d.*"))
            etPassword.setError("Debe contener al menos un numero");
        else b++;
        if (!Password.matches(".*[a-z].*"))
            etPassword.setError("Debe contener minusculas");
        else b++;
        if (!Password.matches(".*[A-Z].*"))
            etPassword.setError("Debe contener mayusculas");
        else b++;
        if (!Password.matches(".{8,15}"))
            etPassword.setError("Debe contener 8 a 15 caracteres");
        else b++;
        if(!Password.equals(RepeatPassword))
            etRepeatPassword.setError("Contrasenas No Coinciden");
        else b++;
        if (b == 6) return true;
        else return false;
    }

    protected boolean validateFields() {
        if(etName.getText().toString().isEmpty())
            etName.setError("Required");
        else {
            if (etEmail.getText().toString().isEmpty())
                etEmail.setError("Required");
            else {
                if (etAddress.getText().toString().isEmpty())
                    etAddress.setError("Required");
                else {
                    if (etNumber.getText().toString().isEmpty())
                        etNumber.setError("Required");
                    else {
                        if(etPassword.getText().toString().isEmpty())
                            etPassword.setError("Required");
                        else {
                            if (etRepeatPassword.getText().toString().isEmpty())
                                etRepeatPassword.setError("Required");
                            else {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

}