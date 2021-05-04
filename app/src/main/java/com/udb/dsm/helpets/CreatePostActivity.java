package com.udb.dsm.helpets;

import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Address;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.format.Time;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.udb.dsm.helpets.listElements.Post;
import com.udb.dsm.helpets.listElements.User;

import java.util.ArrayList;
import java.util.UUID;

public class CreatePostActivity extends AppCompatActivity {
    private EditText etTitulo, etDescripcion;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private StorageReference myStorage;
    private Button btnguardar, btnUpload;
    private AutoCompleteTextView act_categoria;
    private TextInputLayout til_menu;
    private static  final int Galery_intent = 1;
    private ImageView myImageView;
    private ProgressDialog myProgressDialog;
    private TextView urlImage;
    private  User users;

    ArrayList<String> menu;
    ArrayAdapter<String> i;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);
        etTitulo = (EditText) findViewById(R.id.etTitulo);
        etDescripcion = (EditText) findViewById(R.id.etDescripcion);
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
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null){


            String id = user.getUid();
            String name = user.getDisplayName();

            String address = user.getEmail();
          // Uri UserImage = user.getPhotoUrl();


        Time now =  new Time (Time.getCurrentTimezone());
        now.setToNow();
        int dia = now.monthDay;
        int mes = now.month;
        int anio = now.year;
        mes= mes+1;
        String Date = dia + "/" + mes + "/" + anio;
        String titulo = etTitulo.getText().toString();
        String categoria = act_categoria.getText().toString();
        String descripcion = etDescripcion.getText().toString();
        String url = urlImage.getText().toString();


        if(titulo.equals("")){ //validacion
            validacion();
        }
        else{
            Post p = new Post();
            p.setPostId(UUID.randomUUID().toString());
            p.setPostDate(Date);
            p.setPostTitle(titulo);
            p.setPostCategoria(categoria);
            p.setPostDescription(descripcion);
            p.setPostImage(url);
            p.setUserId(id);
            p.setUserName(name);
            p.setUserAddress(address);
           // p.setUserImageProfile(UserImage.toString());
            databaseReference.child("Post").child(p.getPostId()).setValue(p);
            Toast.makeText(this, "Agregado", Toast.LENGTH_LONG).show();
            limpiar();
        }
        }
        else{ Toast.makeText(this, "Usuario no Encontrado", Toast.LENGTH_LONG).show();}
    }


    private void limpiar() {
        etTitulo.setText("");
        etDescripcion.setText("");
    }

    private void validacion() {
    }

}
