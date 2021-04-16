package com.udb.dsm.helpets;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.Menu;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.udb.dsm.helpets.listElements.ListAdapterPost;
import com.udb.dsm.helpets.listElements.ListElementPost;

import java.util.ArrayList;
import java.util.List;

public class UserActivity extends AppCompatActivity {
    DatabaseReference pDatabase;

    List<ListElementPost> posts = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        
        Toolbar toolbar = findViewById(R.id.mainToolbar);
        setSupportActionBar(toolbar);

        initializeElements();
        initializeDatabase();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    protected void initializeElements() {

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

//                for (DataSnapshot productSnapshot : snapshot.child("posts").getChildren()) {
//                    ListElementPost post = productSnapshot.getValue(ListElementPost.class);
////                    post.setId(productSnapshot.getKey());
//
//                    posts.add(post);
//                }

                posts.add(new ListElementPost("wefwefew", "Daniel Calderón", "15/04/2021", "Apopa, San Salvador", "Titulo", "Descripcion"));
                posts.add(new ListElementPost("wefwefew", "Daniel Calderón", "15/04/2021", "Apopa, San Salvador", "Titulo", "Descripcion"));
                posts.add(new ListElementPost("wefwefew", "Daniel Calderón", "15/04/2021", "Apopa, San Salvador", "Titulo", "Descripcion"));

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