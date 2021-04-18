package com.udb.dsm.helpets.listElements;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.udb.dsm.helpets.EditUserActivity;
import com.udb.dsm.helpets.R;
import com.udb.dsm.helpets.UserActivity;

import java.util.ArrayList;
import java.util.List;

public class ListAdapterPost extends RecyclerView.Adapter<ListAdapterPost.ViewHolder> {
    private List<ListElementPost> pData;
    private LayoutInflater pInflater;
    private Context context;

    public ListAdapterPost(List<ListElementPost> itemList, Context context) {
        this.pInflater = LayoutInflater.from(context);
        this.context = context;
        this.pData = itemList;
    }

    @Override
    public int getItemCount() { return pData.size(); }

    @Override
    public ListAdapterPost.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = pInflater.inflate(R.layout.layout_post, null);

        return new ListAdapterPost.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ListAdapterPost.ViewHolder holder, final int position) {
        holder.bindData(pData.get(position));
    }

    public void setItems(List<ListElementPost> items) {
        pData = items;
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

        void bindData(final ListElementPost item) {
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
