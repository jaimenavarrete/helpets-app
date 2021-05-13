package com.udb.dsm.helpets;

import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Address;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.format.Time;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.udb.dsm.helpets.listElements.Post;
import com.udb.dsm.helpets.listElements.User;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.UUID;

public class CreatePostActivity extends AppCompatActivity {
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private StorageReference myStorage;

    private EditText etTitulo, etDireccion, etDescripcion;

    private Button btnguardar, btnUpload;

    private AutoCompleteTextView act_categoria;
    private TextInputLayout til_menu;

    private static  final int Galery_intent = 1;
    private ImageView myImageView;
    private ProgressDialog myProgressDialog;
    private TextView urlImage;

    private  User users;

    private ValueEventListener eventListener;
    private DatabaseReference UserData;

    ArrayList<String> menu;
    ArrayAdapter<String> i;

    String uName, uAddress, uImageUrl;

    private FirebaseUser user;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        Toolbar toolbar = findViewById(R.id.createPostToolbar);
        toolbar.setTitle("Crear publicación");
        setSupportActionBar(toolbar);

//        Bundle bundle = getIntent().getExtras();
//        if(bundle != null) {
//
//        }

        mAuth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        etTitulo = findViewById(R.id.etTitulo);
        etDescripcion = findViewById(R.id.etDescripcion);
        etDireccion = findViewById(R.id.etDireccion);

        til_menu = (TextInputLayout) findViewById(R.id.til_menu);
        act_categoria = (AutoCompleteTextView) findViewById(R.id.act_Categoria);

        btnguardar = (Button) findViewById(R.id.buttonCreatePost);
        btnUpload = (Button) findViewById(R.id.buttonImagePost);

        myImageView = (ImageView) findViewById(R.id.imagePost);
        myProgressDialog = new ProgressDialog(this);
        urlImage = (TextView) findViewById(R.id.txtUrlImage);

        menu = new ArrayList<>();
        menu.add("Adopción");
        menu.add("Venta");
        menu.add("Perdido");

        i = new ArrayAdapter<>(getApplicationContext(), R.layout.support_simple_spinner_dropdown_item, menu);
        act_categoria.setAdapter(i);
        act_categoria.setThreshold(1);

        inicializarFirebase();
        iniciarStorage();

        UserData = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid());

        eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                uName = snapshot.child("userName").getValue().toString();
                uImageUrl = snapshot.child("userImageProfile").getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        };

        UserData.addValueEventListener(eventListener);

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, Galery_intent);

            }
        });

        btnguardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardar();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_create_post, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.action_return) {
            finish();
        }
        else if(id == R.id.action_notifications) {
            Toast.makeText(CreatePostActivity.this, "Has hecho click en el botón de notificaciones", Toast.LENGTH_LONG).show();
        }
        else if(id == R.id.action_user) {
            Intent intent = new Intent(CreatePostActivity.this, UserActivity.class);
            startActivity(intent);

            finish();
        }
        else if(id == R.id.action_logout) {
            mAuth.signOut();

            Intent i = new Intent(CreatePostActivity.this, LoginActivity.class);
            startActivity(i);

            finish();
        }

        return true;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Verificamos se obtenga la foto de la galeria
        if (requestCode == Galery_intent && resultCode == RESULT_OK) {

            myProgressDialog.setTitle("Subiendo");
            myProgressDialog.setMessage("Cargando foto...");
            myProgressDialog.setCancelable(false);
            myProgressDialog.show();

            Uri uri = data.getData();

            //Carpeta dentro del storage
            StorageReference filePath = myStorage.child("posts").child(uri.getLastPathSegment());

            filePath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    //myProgressDialog.dismiss();
                    Task<Uri> Download = taskSnapshot.getStorage().getDownloadUrl();

                    while(!Download.isSuccessful());
                    Uri url = Download.getResult();
                    urlImage.setText(url.toString());

                    Glide.with(CreatePostActivity.this)
                            .load(url.toString())
                            .apply(new RequestOptions().override(600, 200))
                            .apply(new RequestOptions().fitCenter())
                            .into(myImageView);

                    myProgressDialog.dismiss();
                    Toast.makeText(CreatePostActivity.this, "Imagen Cargada", Toast.LENGTH_SHORT).show();
                }
            });


        }
    }

    private void iniciarStorage() {
        myStorage = FirebaseStorage.getInstance().getReference();
    }

    private void inicializarFirebase() {
        FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }

    private void guardar() {
        if (user != null){

            String id = user.getUid();

            String dateFormat = "dd/MM/yyyy HH:mm:ss";
            String Date = new SimpleDateFormat(dateFormat).format(Calendar.getInstance().getTime());

            String titulo = etTitulo.getText().toString();
            String direccion = etDireccion.getText().toString();
            String categoria = act_categoria.getText().toString();
            String descripcion = etDescripcion.getText().toString();
            String url = urlImage.getText().toString();

            Post post = new Post();
            post.setPostId(UUID.randomUUID().toString());
            post.setPostDate(Date);
            post.setPostTitle(titulo);
            post.setPostCategory(categoria);
            post.setPostDescription(descripcion);
            post.setPostImage(url);
            post.setPostAddress(direccion);
            post.setPostLikes(0);
            post.setPostComments(0);
            post.setUserId(id);
            post.setUserName(uName);
            post.setUserImageProfile(uImageUrl);

            databaseReference.child("posts").child(post.getPostId()).setValue(post);
            Toast.makeText(this, "La publicación se ha creado correctamente", Toast.LENGTH_LONG).show();
            limpiar();
        }
        else{ Toast.makeText(this, "Usuario no encontrado", Toast.LENGTH_LONG).show();}
    }

    private void limpiar() {
        etTitulo.setText("");
        etDireccion.setText("");
        etDescripcion.setText("");
    }
}
