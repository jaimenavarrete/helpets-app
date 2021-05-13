package com.udb.dsm.helpets.listElements;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.udb.dsm.helpets.CreatePostActivity;
import com.udb.dsm.helpets.MainActivity;
import com.udb.dsm.helpets.PostActivity;
import com.udb.dsm.helpets.R;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {
    private List<Post> posts;
    private LayoutInflater pInflater;
    private Context context;

    private DatabaseReference pDatabaseLikes, pDatabaseComments, pDatabasePosts;

    FirebaseUser firebaseUser;

    boolean isLiked = false;

    public PostAdapter(List<Post> posts, Context context) {
        this.pInflater = LayoutInflater.from(context);
        this.context = context;
        this.posts = posts;

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        pDatabaseLikes = FirebaseDatabase.getInstance().getReference().child("likes");
        pDatabasePosts = FirebaseDatabase.getInstance().getReference().child("posts");
        pDatabaseComments = FirebaseDatabase.getInstance().getReference().child("comments");
    }

    @Override
    public int getItemCount() { return posts.size(); }

    @Override
    public PostAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = pInflater.inflate(R.layout.layout_post, parent, false);

        return new PostAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final PostAdapter.ViewHolder holder, final int position) {
        holder.bindData(posts.get(position));

        setLikes(holder, posts.get(position).getPostId());
    }

    public void setLikes(final PostAdapter.ViewHolder holder, final String postId) {
        pDatabaseLikes.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(postId).hasChild(firebaseUser.getUid())) {
                    holder.postLikeButton.setTextColor(Color.parseColor("#F07573"));
                    holder.postLikeButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.like_icon, 0, 0, 0);
                }
                else {
                    holder.postLikeButton.setTextColor(Color.parseColor("#000000"));
                    holder.postLikeButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.outline_like_icon, 0, 0, 0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void setItems(List<Post> items) {
        posts = items;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        Button buttonEditPost, buttonDeletePost, postLikeButton, postCommentButton;
        TextView userName, postDate, postAddress, postTitle, postDescription, postCategory;
        ImageView postImage, imageUserProfile;
        LinearLayout clickableSection, buttonsSection;

        ViewHolder(View itemView) {
            super(itemView);

            postDate = itemView.findViewById(R.id.cardPostDate);
            postCategory = itemView.findViewById(R.id.cardPostCategory);
            postTitle = itemView.findViewById(R.id.cardPostTitle);
            postDescription = itemView.findViewById(R.id.cardPostDescription);
            postImage = itemView.findViewById(R.id.cardPostImage);
            postLikeButton = itemView.findViewById(R.id.cardLikeButton);
            postCommentButton = itemView.findViewById(R.id.cardCommentButton);

            userName = itemView.findViewById(R.id.cardUserName);
            postAddress = itemView.findViewById(R.id.cardPostAddress);
            imageUserProfile = itemView.findViewById(R.id.cardImageUserProfile);

            buttonsSection = itemView.findViewById(R.id.cardButtonsSection);
            buttonEditPost = itemView.findViewById(R.id.cardButtonEditPost);
            buttonDeletePost = itemView.findViewById(R.id.cardButtonDeletePost);

            clickableSection = itemView.findViewById(R.id.cardClickableSection);
        }

        void bindData(final Post item) {
            postDate.setText(item.getPostDate());
            postCategory.setText(item.getPostCategory());
            postTitle.setText(item.getPostTitle());
            postDescription.setText(item.getPostDescription());
            Picasso.with(context).load(item.getPostImage()).into(postImage);
            postLikeButton.setText(item.getPostLikes() + " Me gusta");
            postCommentButton.setText(item.getPostComments() + " Comentarios");

            userName.setText(item.getUserName());
            postAddress.setText(item.getPostAddress());
            Picasso.with(context).load(item.getUserImageProfile()).into(imageUserProfile);

            postLikeButton.setOnClickListener(v -> {
                likeButtonEvent(item);
            });

            postCommentButton.setOnClickListener(v -> {
                clickableSectionEvent(item);
            });

            buttonEditPost.setOnClickListener(v -> {
                buttonEditPostEvent(item);
            });

            buttonDeletePost.setOnClickListener(v -> {
                buttonDeletePostEvent(item);
            });

            clickableSection.setOnClickListener(v -> {
                clickableSectionEvent(item);
            });

            if(!item.getUserId().equals(firebaseUser.getUid())) {
                buttonsSection.setVisibility(View.GONE);
            }
        }
    }

    public void buttonEditPostEvent(final Post item) {
        Intent intent = new Intent(context, CreatePostActivity.class);
        intent.putExtra("postId", item.getPostId());
        intent.putExtra("postTitle", item.getPostTitle());
        intent.putExtra("postAddress", item.getPostAddress());
        intent.putExtra("postDescription", item.getPostDescription());
        intent.putExtra("postCategory", item.getPostCategory());
        intent.putExtra("postImage", item.getPostImage());
        context.startActivity(intent);
    }

    public void buttonDeletePostEvent(final Post item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setMessage("¿Está seguro de eliminar esta publicación?").setTitle("Confirmación");

        builder.setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                try {
                    pDatabasePosts.child(item.getPostId()).removeValue();
                    pDatabaseLikes.child(item.getPostId()).removeValue();
                    pDatabaseComments.child(item.getPostId()).removeValue();

                    Toast.makeText(context, "La publicación ha sido eliminada correctamente", Toast.LENGTH_LONG).show();

                    Intent intent = new Intent(context, MainActivity.class);
                    context.startActivity(intent);

                    ((AppCompatActivity)context).finish();
                }
                catch (Exception e){
                    Toast.makeText(context, "Hubo un error al eliminar la publicación. Inténtelo más tarde", Toast.LENGTH_LONG).show();
                }
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {}
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void likeButtonEvent(final Post item) {
        isLiked = true;

        pDatabaseLikes.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(isLiked) {
                    if(snapshot.child(item.getPostId()).hasChild(firebaseUser.getUid())) {
                        pDatabasePosts.child(item.getPostId()).child("postLikes").setValue(item.getPostLikes() - 1);
                        pDatabaseLikes.child(item.getPostId()).child(firebaseUser.getUid()).removeValue();
                    }
                    else {
                        pDatabasePosts.child(item.getPostId()).child("postLikes").setValue(item.getPostLikes() + 1);
                        pDatabaseLikes.child(item.getPostId()).child(firebaseUser.getUid()).setValue(true);
                    }

                    isLiked = false;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void clickableSectionEvent(final Post item) {
        Intent intent = new Intent(context, PostActivity.class);
        intent.putExtra("postId", item.getPostId());
        context.startActivity(intent);
    }
}
