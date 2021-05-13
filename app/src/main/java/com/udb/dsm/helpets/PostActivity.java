package com.udb.dsm.helpets;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.udb.dsm.helpets.listElements.Comment;
import com.udb.dsm.helpets.listElements.CommentAdapter;
import com.udb.dsm.helpets.listElements.Post;
import com.udb.dsm.helpets.listElements.PostAdapter;
import com.udb.dsm.helpets.listElements.User;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class PostActivity extends AppCompatActivity {
    DatabaseReference pDatabaseUsers, pDatabasePosts, pDatabaseComments, pDatabaseLikes;

    RecyclerView recyclerViewComments;
    List<Comment> comments = new ArrayList<>();

    FirebaseUser firebaseUser;
    private FirebaseAuth mAuth;
    User user;

    TextView userName, postDate, postAddress, postTitle, postDescription, postCategory;
    EditText editTextComment;
    ImageView imageUserProfile, imagePost;
    Button buttonEditPost, buttonDeletePost, likeButton, commentButton, buttonAddComment;
    LinearLayout buttonsSection;

    String postId = "";
    Post post;
    boolean isLiked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        Toolbar toolbar = findViewById(R.id.postToolbar);
        toolbar.setTitle("Publicación");
        setSupportActionBar(toolbar);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            postId = bundle.getString("postId");
        }

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        mAuth = FirebaseAuth.getInstance();

        initializeElements();
        initializeDatabase();

        getButtonLikeState();
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
            Toast.makeText(PostActivity.this, "Has hecho click en el botón de notificaciones", Toast.LENGTH_LONG).show();
        }
        else if(id == R.id.action_user) {
            Intent intent = new Intent(PostActivity.this, UserActivity.class);
            startActivity(intent);

            finish();
        }
        else if(id == R.id.action_logout) {
            mAuth.signOut();

            Intent i = new Intent(PostActivity.this, LoginActivity.class);
            startActivity(i);

            finish();
        }

        return true;
    }

    public void initializeElements() {
        userName = findViewById(R.id.userName);
        postDate = findViewById(R.id.postDate);
        postAddress = findViewById(R.id.postAddress);
        postTitle = findViewById(R.id.postTitle);
        postDescription = findViewById(R.id.postDescription);
        postCategory = findViewById(R.id.postCategory);

        editTextComment = findViewById(R.id.editTextComment);

        imageUserProfile = findViewById(R.id.imageUserProfile);
        imagePost = findViewById(R.id.imagePost);

        buttonsSection = findViewById(R.id.buttonsSection);
        buttonEditPost = findViewById(R.id.buttonEditPost);
        buttonDeletePost = findViewById(R.id.buttonDeletePost);
        buttonAddComment = findViewById(R.id.buttonAddComment);

        likeButton = findViewById(R.id.likeButton);
        commentButton = findViewById(R.id.commentButton);

        recyclerViewComments = findViewById(R.id.recyclerViewComments);
        recyclerViewComments.setHasFixedSize(false);
        recyclerViewComments.setLayoutManager(new LinearLayoutManager(PostActivity.this));
    }

    public void defineElements() {
        userName.setText(post.getUserName());
        postDate.setText(post.getPostDate());
        postAddress.setText(post.getPostAddress());
        postTitle.setText(post.getPostTitle());
        postDescription.setText(post.getPostDescription());
        postCategory.setText(post.getPostCategory());

        Picasso.with(PostActivity.this).load(post.getUserImageProfile()).into(imageUserProfile);
        Picasso.with(PostActivity.this).load(post.getPostImage()).into(imagePost);

        buttonEditPost.setOnClickListener(v -> {
            buttonEditPostEvent();
        });

        buttonDeletePost.setOnClickListener(v -> {
            buttonDeletePostEvent();
        });

        likeButton.setOnClickListener(v -> {
            likeButtonEvent();
        });

        buttonAddComment.setOnClickListener(v -> {
            buttonAddCommentEvent();
        });

        likeButton.setText(post.getPostLikes() + " Me gusta");
        commentButton.setText(post.getPostComments() + " Comentarios");

        if(!post.getUserId().equals(firebaseUser.getUid())) {
            buttonsSection.setVisibility(View.GONE);
        }
    }

    public void initializeDatabase() {
        pDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("users").child(firebaseUser.getUid());
        pDatabaseUsers.addValueEventListener(userEvent());

        pDatabasePosts = FirebaseDatabase.getInstance().getReference().child("posts").child(postId);
        pDatabasePosts.addValueEventListener(postListEvent());

        pDatabaseLikes = FirebaseDatabase.getInstance().getReference().child("likes").child(postId);

        pDatabaseComments = FirebaseDatabase.getInstance().getReference().child("comments").child(postId);
        pDatabaseComments.addValueEventListener(commentListEvent());
    }

    public ValueEventListener userEvent() {
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Get user info
                user = snapshot.getValue(User.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        };
    }

    public ValueEventListener postListEvent() {
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                post = snapshot.getValue(Post.class);

                defineElements();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
    }

    public ValueEventListener commentListEvent() {
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                comments.clear();

                for (DataSnapshot productSnapshot : snapshot.getChildren()) {
                    Comment comment = productSnapshot.getValue(Comment.class);
                    comments.add(comment);
                }

                CommentAdapter listCommentAdapter = new CommentAdapter(comments, PostActivity.this);
                recyclerViewComments.setAdapter(listCommentAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
    }

    public void buttonEditPostEvent() {
        Intent intent = new Intent(PostActivity.this, CreatePostActivity.class);
        intent.putExtra("postId", postId);
        startActivity(intent);
    }

    public void buttonDeletePostEvent() {
        AlertDialog.Builder builder = new AlertDialog.Builder(PostActivity.this);

        builder.setMessage("¿Está seguro de eliminar esta publicación?").setTitle("Confirmación");

        builder.setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                try {
                    pDatabasePosts.removeValue();
                    pDatabaseComments.removeValue();
                    pDatabaseLikes.removeValue();

                    Toast.makeText(PostActivity.this, "La publicación ha sido eliminada correctamente", Toast.LENGTH_LONG).show();

                    finish();
                }
                catch (Exception e){
                    Toast.makeText(PostActivity.this, "Hubo un error al eliminar la publicación. Inténtelo más tarde", Toast.LENGTH_LONG).show();
                }
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {}
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void likeButtonEvent() {
        isLiked = true;

        pDatabaseLikes.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(isLiked) {
                    if(snapshot.hasChild(firebaseUser.getUid())) {
                        pDatabasePosts.child("postLikes").setValue(post.getPostLikes() - 1);
                        pDatabaseLikes.child(firebaseUser.getUid()).removeValue();
                    }
                    else {
                        pDatabasePosts.child("postLikes").setValue(post.getPostLikes() + 1);
                        pDatabaseLikes.child(firebaseUser.getUid()).setValue(true);
                    }

                    getButtonLikeState();
                    isLiked = false;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void getButtonLikeState() {
        pDatabaseLikes.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChild(firebaseUser.getUid())) {
                    likeButton.setTextColor(Color.parseColor("#F07573"));
                    likeButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.like_icon, 0, 0, 0);
                }
                else {
                    likeButton.setTextColor(Color.parseColor("#000000"));
                    likeButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.outline_like_icon, 0, 0, 0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void buttonAddCommentEvent() {
        if(!editTextComment.getText().toString().equals("")) {
            String dateFormat = "dd/MM/yyyy HH:mm:ss";
            String commentDate = new SimpleDateFormat(dateFormat).format(Calendar.getInstance().getTime());

            Comment comment = new Comment();
            comment.setCommentId(UUID.randomUUID().toString());
            comment.setCommentDate(commentDate);
            comment.setCommentText(editTextComment.getText().toString());
            comment.setUserId(firebaseUser.getUid());
            comment.setUserName(user.getUserName());
            comment.setUserImageProfile(user.getUserImageProfile());

            pDatabaseComments.child(comment.getCommentId()).setValue(comment);
            pDatabasePosts.child("postComments").setValue(post.getPostComments() + 1);
        }
        else {
            Toast.makeText(PostActivity.this, "No es posible agregar un comentario vacío", Toast.LENGTH_SHORT).show();
        }
    }
}