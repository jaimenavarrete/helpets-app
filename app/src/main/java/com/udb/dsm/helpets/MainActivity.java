package com.udb.dsm.helpets;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.udb.dsm.helpets.listElements.Post;
import com.udb.dsm.helpets.listElements.PostAdapter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    DatabaseReference pDatabase;
    FirebaseStorage storage;
    StorageReference storageRef;

    RecyclerView recyclerViewPosts;
    List<Post> posts = new ArrayList<>();

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        Toolbar toolbar = findViewById(R.id.mainToolbar);
        toolbar.setTitle("Publicaciones");
        setSupportActionBar(toolbar);

        initializeElements();
        initializeStorage();
        initializeDatabase();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.action_add) {
            Toast.makeText(MainActivity.this, "Has hecho click en el botón de agregar publicación", Toast.LENGTH_LONG).show();

            Intent in = new Intent(MainActivity.this, CreatePostActivity.class);
            startActivity(in);
        }
        else if(id == R.id.action_search) {
            Toast.makeText(MainActivity.this, "Has hecho click en el botón de búsqueda", Toast.LENGTH_LONG).show();
        }
        else if(id == R.id.action_notifications) {
            Toast.makeText(MainActivity.this, "Has hecho click en el botón de notificaciones", Toast.LENGTH_LONG).show();
        }
        else if(id == R.id.action_user) {
            Intent intent = new Intent(MainActivity.this, UserActivity.class);
            startActivity(intent);
        }
        else if(id == R.id.action_logout) {
            mAuth.signOut();

            Intent i = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(i);
        }

        return true;
    }

    protected void initializeElements() {
        recyclerViewPosts = findViewById(R.id.recyclerViewPosts);
        recyclerViewPosts.setHasFixedSize(false);
        recyclerViewPosts.setLayoutManager(new LinearLayoutManager(MainActivity.this));
    }

    protected void initializeStorage() {
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
    }

    protected void initializeDatabase() {
        pDatabase = FirebaseDatabase.getInstance().getReference();
        pDatabase.addValueEventListener(postListEvent());
    }

    protected ValueEventListener postListEvent() {
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                posts.clear();

                for (DataSnapshot productSnapshot : snapshot.child("posts").getChildren()) {
                    Post post = productSnapshot.getValue(Post.class);
                    posts.add(post);
                }

                PostAdapter listPostsAdapter = new PostAdapter(posts, MainActivity.this);
                recyclerViewPosts.setAdapter(listPostsAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
    }
}