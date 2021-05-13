package com.udb.dsm.helpets.listElements;

import android.content.Context;
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

import androidx.annotation.NonNull;
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
import com.udb.dsm.helpets.PostActivity;
import com.udb.dsm.helpets.R;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {
    private List<Post> posts;
    private LayoutInflater pInflater;
    private Context context;

    private DatabaseReference pDatabaseLikes;
    private DatabaseReference pDatabasePosts;

    FirebaseUser firebaseUser;

    boolean isLiked = false;

    public PostAdapter(List<Post> posts, Context context) {
        this.pInflater = LayoutInflater.from(context);
        this.context = context;
        this.posts = posts;

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        pDatabaseLikes = FirebaseDatabase.getInstance().getReference().child("likes");
        pDatabasePosts = FirebaseDatabase.getInstance().getReference().child("posts");
    }

    @Override
    public int getItemCount() { return posts.size(); }

    @Override
    public PostAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = pInflater.inflate(R.layout.layout_post, null);

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
        Button postLikeButton, postCommentButton;
        TextView userName, postDate, postAddress, postTitle, postDescription, postCategory;
        ImageView postImage, imageUserProfile;
        LinearLayout clickableSection;

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

            clickableSection = itemView.findViewById(R.id.cardClickableSection);
        }

        void bindData(final Post item) {
            postDate.setText(item.getPostDate());
            postCategory.setText(item.getPostCategory());
            postTitle.setText(item.getPostTitle());
            postDescription.setText(item.getPostDescription());
            Picasso.with(context).load(item.getPostImage()).into(postImage);
            postLikeButton.setText(item.getPostLikes() + " Me gusta");

            userName.setText(item.getUserName());
            postAddress.setText(item.getPostAddress());
            Picasso.with(context).load(item.getUserImageProfile()).into(imageUserProfile);

            postLikeButton.setOnClickListener(v -> {
                likeButtonEvent(item);
            });

            clickableSection.setOnClickListener(v -> {
                clickableSectionEvent(item);
            });
        }
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
