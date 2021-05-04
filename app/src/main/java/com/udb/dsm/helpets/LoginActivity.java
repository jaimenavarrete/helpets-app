package com.udb.dsm.helpets;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.udb.dsm.helpets.listElements.Post;
import com.udb.dsm.helpets.listElements.User;

import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends AppCompatActivity {
    DatabaseReference pDatabase;

    FirebaseUser firebaseUser;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private int RC_SIGN_IN = 1;

    private EditText etUser, etPassword;
    private TextView tvSignup, tvForget;
    private Button btnLogin, btnLoginGoogle;

    List<User> users = new ArrayList<>();
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        initializeElements();
        initializeDatabase();
    }

    private void initializeElements() {
        etUser= findViewById(R.id.etUser);
        etPassword = findViewById(R.id.etPassword);

        btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUserAccount();
            }
        });

        btnLoginGoogle = findViewById(R.id.btnLoginGoogle);
        btnLoginGoogle.setOnClickListener(v -> {
            loginGoogle();
        });

        tvSignup = findViewById(R.id.tvSignup);
        tvSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                Intent i = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(i);
            }
        });

        tvForget = findViewById(R.id.tvForget);
        tvForget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                Intent i = new Intent(LoginActivity.this, RecoveryActivity.class);
                startActivity(i);
            }
        });
    }

    private void initializeDatabase() {
        pDatabase = FirebaseDatabase.getInstance().getReference();
        pDatabase.addValueEventListener(userEvent());
    }

    private ValueEventListener userEvent() {
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                User userFirebase = snapshot.child("users").child(firebaseUser.getUid()).getValue(User.class);

                // Get users info
                for (DataSnapshot productSnapshot : snapshot.child("users").getChildren()) {
                    User user = productSnapshot.getValue(User.class);
                    users.add(user);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        };
    }

    private void loginUserAccount() {

        String user, password;
        user = etUser.getText().toString();
        password = etPassword.getText().toString();

        if (TextUtils.isEmpty(user)) {
            Toast.makeText(getApplicationContext(), "Por favor ingrese un email...", Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(), "Por favor ingrese una contraseña!", Toast.LENGTH_LONG).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(user, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(getApplicationContext(), "Inicio de sesión exitoso!", Toast.LENGTH_LONG).show();
                        Intent i = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(i);
                    }
                    else {
                        Toast.makeText(getApplicationContext(), "El inicio de sesión falló! Por favor inténtelo más tarde", Toast.LENGTH_LONG).show();
                    }
                });
    }

    // LOGIN CON GOOGLE

    private void loginGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount acc = completedTask.getResult(ApiException.class);
            FirebaseGoogleAuth(acc);
        }
        catch (ApiException e) {
            Toast.makeText(LoginActivity.this, "El inicio de sesión falló! Por favor inténtelo más tarde", Toast.LENGTH_LONG).show();
            FirebaseGoogleAuth(null);
        }
    }

    private void FirebaseGoogleAuth(GoogleSignInAccount acct) {
        AuthCredential authCredential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);

        mAuth.signInWithCredential(authCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Inicio de sesión exitoso!", Toast.LENGTH_LONG).show();

                    firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

                    for(User userElement : users) {
                        if(userElement.getUserId().equals(firebaseUser.getUid())) {
                            user = userElement;
                            break;
                        }
                    }

                    if(user == null) {
                        user = new User();
                        user.setUserId(firebaseUser.getUid());
                        user.setUserName(firebaseUser.getDisplayName());
                        user.setUserPhone("");
                        user.setUserAddress("");
                        user.setUserImageBackground("https://firebasestorage.googleapis.com/v0/b/helpets-ccc8d.appspot.com/o/users%2Fbackgrounds%2Fdefault.jpeg?alt=media&token=f7dec4de-f02c-4e77-b9b6-0566b1058794");
                        user.setUserImageProfile(firebaseUser.getPhotoUrl().toString());
                    }

                    insertData();

                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), "El inicio de sesión falló! Por favor inténtelo más tarde", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void insertData(){
        pDatabase.child("users").child(user.getUserId()).setValue(user);
    }
}