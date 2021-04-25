package com.udb.dsm.helpets;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.udb.dsm.helpets.listElements.User;

import java.util.ArrayList;
import java.util.List;

public class RecoveryActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    private TextInputLayout etEmail;
    private Button btnRemember, btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recovery);

        mAuth = FirebaseAuth.getInstance();

        initializeElements();
    }

    private void initializeElements() {
        etEmail= findViewById(R.id.etEmail);

        btnRemember = findViewById(R.id.btnRemember);
        btnRemember.setOnClickListener(v -> {
            sendRecoveryEmail(etEmail.getEditText().getText().toString());
        });

        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            finish();
        });
    }

    private void sendRecoveryEmail(String email) {
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(RecoveryActivity.this, "Se le ha enviado un correo para modificar su contraseña", Toast.LENGTH_SHORT).show();
            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }
}