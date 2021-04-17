package com.udb.dsm.helpets;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.udb.dsm.helpets.listElements.ListAdapterPost;
import com.udb.dsm.helpets.listElements.ListElementPost;
import com.udb.dsm.helpets.listElements.User;

import java.util.ArrayList;
import java.util.List;

public class UserActivity extends AppCompatActivity {
    DatabaseReference pDatabase;

    TextView textUserName, textUserEmail, textUserAddress, textUserPhone;
    User user;

    Button buttonEditUser;

    List<ListElementPost> posts = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        Toolbar toolbar = findViewById(R.id.userToolbar);
        setSupportActionBar(toolbar);

        initializeElements();
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
            Toast.makeText(UserActivity.this, "Has hecho click en el botón de retorno", Toast.LENGTH_LONG).show();
        }
        else if(id == R.id.action_notifications) {
            Toast.makeText(UserActivity.this, "Has hecho click en el botón de notificaciones", Toast.LENGTH_LONG).show();
        }
        else if(id == R.id.action_edit_user) {
            Toast.makeText(UserActivity.this, "Has hecho click en el botón de editar usuario", Toast.LENGTH_LONG).show();
        }
        else if(id == R.id.action_logout) {
            Toast.makeText(UserActivity.this, "Has hecho click en el botón de cerrar sesión", Toast.LENGTH_LONG).show();
        }

        return true;
    }

    protected void initializeElements() {
        textUserName = findViewById(R.id.textUserName);
        textUserAddress = findViewById(R.id.textUserAddress);
        textUserPhone = findViewById(R.id.textUserPhone);
        textUserEmail = findViewById(R.id.textUserEmail);

        buttonEditUser = findViewById(R.id.buttonEditUser);
        buttonEditUser.setOnClickListener(v -> {
            Intent intent = new Intent(UserActivity.this, EditUserActivity.class);
            startActivity(intent);
        });
    }

    protected void initializeDatabase() {
        pDatabase = FirebaseDatabase.getInstance().getReference();

        pDatabase.addValueEventListener(userEvent());
        pDatabase.addValueEventListener(postListEvent());
    }

    protected ValueEventListener userEvent() {
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Get user info
                user = snapshot.child("users").child("SxknLnmvVCMIxQfuHVOF2iiBOi63").getValue(User.class);

                // Set user info into the textViews
                textUserName.setText(user.getUserName());
                textUserEmail.setText(user.getUserEmail());
                textUserAddress.setText(user.getUserAddress());
                textUserPhone.setText(user.getUserPhone());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
    }

    protected ValueEventListener postListEvent() {
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                posts.clear();

                for (DataSnapshot productSnapshot : snapshot.child("posts").getChildren()) {
                    ListElementPost post = productSnapshot.getValue(ListElementPost.class);
                    post.setPostId(productSnapshot.getKey());

                    // Set the user info into the post object
                    post.setUserName(user.getUserName());
                    post.setUserAddress(user.getUserAddress());
                    post.setUserId(user.getUserId());

                    posts.add(post);
                }

                ListAdapterPost listAdapter = new ListAdapterPost(posts, UserActivity.this);

                RecyclerView recyclerViewPosts = findViewById(R.id.recyclerViewPosts);
                recyclerViewPosts.setHasFixedSize(false);
                recyclerViewPosts.setLayoutManager(new LinearLayoutManager(UserActivity.this));
                recyclerViewPosts.setAdapter(listAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
    }
}