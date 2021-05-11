package com.udb.dsm.helpets.listElements;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.udb.dsm.helpets.R;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {
    private List<Post> posts;
    private LayoutInflater pInflater;
    private Context context;

    public PostAdapter(List<Post> posts, Context context) {
        this.pInflater = LayoutInflater.from(context);
        this.context = context;
        this.posts = posts;
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
    }

    public void setItems(List<Post> items) {
        posts = items;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView userName, postDate, postAddress, postTitle, postDescription, postCategory;
        ImageView postImage, imageUserProfile;

        ViewHolder(View itemView) {
            super(itemView);

            postDate = itemView.findViewById(R.id.cardPostDate);
            postCategory = itemView.findViewById(R.id.cardPostCategory);
            postTitle = itemView.findViewById(R.id.cardPostTitle);
            postDescription = itemView.findViewById(R.id.cardPostDescription);
            postImage = itemView.findViewById(R.id.cardPostImage);

            userName = itemView.findViewById(R.id.cardUserName);
            postAddress = itemView.findViewById(R.id.cardPostAddress);
            imageUserProfile = itemView.findViewById(R.id.cardImageUserProfile);
        }

        void bindData(final Post item) {
            postDate.setText(item.getPostDate());
            postCategory.setText(item.getPostCategory());
            postTitle.setText(item.getPostTitle());
            postDescription.setText(item.getPostDescription());
            Picasso.with(context).load(item.getPostImage()).into(postImage);

            userName.setText(item.getUserName());
            postAddress.setText(item.getPostAddress());
            Picasso.with(context).load(item.getUserImageProfile()).into(imageUserProfile);
        }
    }
}
