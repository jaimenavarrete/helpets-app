package com.udb.dsm.helpets;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.udb.dsm.helpets.listElements.User;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditUserActivity extends AppCompatActivity {
    DatabaseReference pDatabase;
    FirebaseStorage storage;
    StorageReference storageRef;

    FirebaseUser firebaseUser;

    ImageView imageUserBackground;
    CircleImageView imageUserProfile;

    TextInputLayout textOldPassword, textNewPassword, textRepeatNewPassword;

    Button buttonUserBackgroundImage, buttonImageUserProfile, buttonEditUserPassword, buttonEditUserData;

    Uri uriImage;
    Boolean isUserImageProfile = false;

    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        Toolbar toolbar = findViewById(R.id.userToolbar);
        toolbar.setTitle("Editar perfil");
        setSupportActionBar(toolbar);

        initializeElements();
        initializeStorage();
        initializeDatabase();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_user, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.action_return) {
            finish();
        }
        else if(id == R.id.action_notifications) {
            Toast.makeText(EditUserActivity.this, "Has hecho click en el botón de notificaciones", Toast.LENGTH_LONG).show();
        }
        else if(id == R.id.action_edit_user) {
            Toast.makeText(EditUserActivity.this, "Has hecho click en el botón de editar usuario", Toast.LENGTH_LONG).show();
        }
        else if(id == R.id.action_logout) {
            Toast.makeText(EditUserActivity.this, "Has hecho click en el botón de cerrar sesión", Toast.LENGTH_LONG).show();
        }

        return true;
    }

    protected void initializeElements() {
        imageUserProfile = findViewById(R.id.imageUserProfile);
        imageUserBackground = findViewById(R.id.imageUserBackground);

        textOldPassword = findViewById(R.id.textOldPassword);
        textNewPassword = findViewById(R.id.textNewPassword);
        textRepeatNewPassword = findViewById(R.id.textRepeatNewPassword);

        buttonUserBackgroundImage = findViewById(R.id.buttonUserBackgroundImage);
        buttonUserBackgroundImage.setOnClickListener(v -> {
            isUserImageProfile = false;
            chooseImage();
        });

        buttonImageUserProfile = findViewById(R.id.buttonImageUserProfile);
        buttonImageUserProfile.setOnClickListener(v -> {
            isUserImageProfile = true;
            chooseImage();
        });

        buttonEditUserPassword = findViewById(R.id.buttonEditUserPassword);
        buttonEditUserPassword.setOnClickListener(v -> {
            changeUserPassword();
        });
    }

    protected void initializeStorage() {
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
    }

    protected void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            uriImage = data.getData();
            uploadImage();
        }
    }

    protected void uploadImage() {
        final ProgressDialog progress = new ProgressDialog(this);
        progress.setTitle("Subiendo imagen...");
        progress.show();

        String pathImage;

        if(isUserImageProfile) {
            pathImage = "users/profiles/" + "SxknLnmvVCMIxQfuHVOF2iiBOi63" + ".jpg";
        }
        else {
            pathImage = "users/backgrounds/" + "SxknLnmvVCMIxQfuHVOF2iiBOi63" + ".jpg";
        }

        StorageReference userImageRef = storageRef.child(pathImage);

        userImageRef.putFile(uriImage)
            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                    while(!uriTask.isSuccessful());
                    Uri uriDownload = uriTask.getResult();

                    if(uriTask.isSuccessful()) {
                        if(isUserImageProfile) {
                            pDatabase.child("users").child("SxknLnmvVCMIxQfuHVOF2iiBOi63").child("userImageProfile").setValue("image");
                            pDatabase.child("users").child("SxknLnmvVCMIxQfuHVOF2iiBOi63").child("userImageProfile").setValue(uriDownload.toString());
                        }
                        else {
                            pDatabase.child("users").child("SxknLnmvVCMIxQfuHVOF2iiBOi63").child("userImageBackground").setValue("image");
                            pDatabase.child("users").child("SxknLnmvVCMIxQfuHVOF2iiBOi63").child("userImageBackground").setValue(uriDownload.toString());
                        }

                        Snackbar.make(findViewById(android.R.id.content), "Imagen subida correctamente", Snackbar.LENGTH_LONG).show();
                    }

                    progress.dismiss();
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progress.dismiss();
                    Toast.makeText(EditUserActivity.this, "Fallo al subir la imagen", Toast.LENGTH_LONG).show();
                }
            })
            .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                    double progressPercent = (100.00 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                    progress.setMessage("Progreso: " + (int)progressPercent + "%");
                }
            });
    }

    protected void initializeDatabase() {
        pDatabase = FirebaseDatabase.getInstance().getReference();
        pDatabase.addValueEventListener(userEvent());
    }

    protected ValueEventListener userEvent() {
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Get user info
                user = snapshot.child("users").child("SxknLnmvVCMIxQfuHVOF2iiBOi63").getValue(User.class);

                Picasso.with(EditUserActivity.this).load(user.getUserImageBackground()).into(imageUserBackground);
                Picasso.with(EditUserActivity.this).load(user.getUserImageProfile()).into(imageUserProfile);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        };
    }

    protected void changeUserPassword() {
        String email = firebaseUser.getEmail();
        String oldPassword = textOldPassword.getEditText().getText().toString();
        String newPassword = textNewPassword.getEditText().getText().toString();
        String repeatNewPassword = textRepeatNewPassword.getEditText().getText().toString();

        if(!oldPassword.equals("") && !newPassword.equals("") && !repeatNewPassword.equals("")) {
            AuthCredential credential = EmailAuthProvider.getCredential(email, oldPassword);

            firebaseUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        if(newPassword.equals(repeatNewPassword)) {
                            firebaseUser.updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(!task.isSuccessful()){
                                        Toast.makeText(EditUserActivity.this, "Hubo un error al cambiar la contraseña. Inténtelo más tarde", Toast.LENGTH_LONG).show();
                                    }else {
                                        pDatabase.child("users").child("SxknLnmvVCMIxQfuHVOF2iiBOi63").child("userPassword").setValue(newPassword);
                                        Toast.makeText(EditUserActivity.this, "La contraseña ha sido cambiada correctamente", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        }
                        else {
                            Toast.makeText(EditUserActivity.this, "Las contraseñas no coinciden", Toast.LENGTH_LONG).show();
                        }
                    }else {
                        Toast.makeText(EditUserActivity.this, "La contraseña actual que ha colocado, es incorrecta", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
        else {
            Toast.makeText(EditUserActivity.this, "Debe rellenar todos los campos de contraseña", Toast.LENGTH_LONG).show();
        }
    }
}