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
        TextView userName, postDate, userAddress, postTitle, postDescription;
        ImageView imageUserProfile;

        ViewHolder(View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.cardUserName);
            postDate = itemView.findViewById(R.id.cardPostDate);
            userAddress = itemView.findViewById(R.id.cardUserAddress);
            postTitle = itemView.findViewById(R.id.cardPostTitle);
            postDescription = itemView.findViewById(R.id.cardPostDescription);

            imageUserProfile = itemView.findViewById(R.id.cardImageUserProfile);
        }

        void bindData(final Post item) {
            userName.setText(item.getUserName());
            postDate.setText(item.getPostDate());
            userAddress.setText(item.getUserAddress());
            postTitle.setText(item.getPostTitle());
            postDescription.setText(item.getPostDescription());

            try {
                Picasso.with(context).load(item.getUserImageProfile()).into(imageUserProfile);
            } catch (Exception ignored) {
            }
        }
    }
}
