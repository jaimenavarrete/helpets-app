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

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.udb.dsm.helpets.listElements.User;

import java.util.ArrayList;
import java.util.List;

public class RecoveryActivity extends AppCompatActivity {

    private boolean exists = false;
    private EditText etEmail;
    private Button btnRemember, btnBack;
    private List<String> listEmail = new ArrayList<String>();
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recovery);
        initializeUI();
        initializeFirebase();
        btnRemember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendRecoveryEmail();
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { finish(); }
        });
    }

    private void sendRecoveryEmail() {
        String email = etEmail.getText().toString();
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(), "Please Enter Email...", Toast.LENGTH_LONG).show();
            return;
        }
        else {
              getEmail(email);
              if (exists) {
                  Intent emailIntent = new Intent(Intent.ACTION_SEND);
                  emailIntent.setData(Uri.parse("mailto:"));
                  emailIntent.setType("text/plain");
                  emailIntent.putExtra(Intent.EXTRA_EMAIL, email);
                  emailIntent.putExtra(Intent.EXTRA_SUBJECT, "HelPets Password Recovery");
                  emailIntent.putExtra(Intent.EXTRA_TEXT, "This is your recovery code: " + (int)(Math.random()*9999)+1);
                  try {
                      startActivity(Intent.createChooser(emailIntent, "Sending Email"));
                      Toast.makeText(this, "Sending Recovery Email ...", Toast.LENGTH_SHORT).show();
                  }
                  catch (android.content.ActivityNotFoundException e) {
                      Toast.makeText(this, "NO email client installed.", Toast.LENGTH_SHORT).show();
                  }
              }
              else {
                  Toast.makeText(getApplicationContext(), "User " + email + " doesn't exist", Toast.LENGTH_LONG).show();
              }
        }

    }

    protected void getEmail(String email){
        databaseReference.child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot objSnapshot: snapshot.getChildren()){
                    User user = objSnapshot.getValue(User.class);
                    listEmail.add(user.getUserEmail());
                    for (String o : listEmail){
                        if (email.equals(o)) exists = true;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void initializeUI() {
        etEmail= findViewById(R.id.etUser);
        btnBack = findViewById(R.id.btnBack);
        btnRemember = findViewById(R.id.btnRemember);
    }

    public void initializeFirebase(){
        FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }
}