package com.udb.dsm.helpets;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.udb.dsm.helpets.listElements.Post;

import java.util.ArrayList;
import java.util.UUID;

public class CreatePostActivity extends AppCompatActivity {
    private EditText etTitulo, etDescripcion;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private Button btnguardar;
    private AutoCompleteTextView act_categoria;
    private TextInputLayout til_menu;

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

        menu = new ArrayList<>();
        menu.add("Adopción");
        menu.add("Venta");
        menu.add("Perdido");

        i = new ArrayAdapter<>(getApplicationContext(), R.layout.support_simple_spinner_dropdown_item, menu);
        act_categoria.setAdapter(i);
        act_categoria.setThreshold(1);
        inicializarFirebase();

        btnguardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardar();
            }
        });

    }

    private void inicializarFirebase() {
        FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }



    private void guardar() {
        String titulo = etTitulo.getText().toString();
        String categoria = act_categoria.getText().toString();
        String descripcion = etDescripcion.getText().toString();

        if(titulo.equals("")){ //validacion
            validacion();
        }
        else{
            Post p = new Post();
            p.setPostId(UUID.randomUUID().toString());
            p.setPostTitulo(titulo);
            p.setPostCategoria(categoria);
            p.setPostDescripcion(descripcion);
            databaseReference.child("Post").child(p.getPostId()).setValue(p);
            Toast.makeText(this, "Agregado", Toast.LENGTH_LONG).show();
            limpiar();
        }
    }

    private void limpiar() {
        etTitulo.setText("");
        etDescripcion.setText("");
    }

    private void validacion() {
    }

}
