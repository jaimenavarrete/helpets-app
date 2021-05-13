package com.udb.dsm.helpets.listElements;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.squareup.picasso.Picasso;
import com.udb.dsm.helpets.R;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {
    private List<Comment> comments;
    private LayoutInflater pInflater;
    private Context context;

    FirebaseUser firebaseUser;

    private DatabaseReference pDatabaseUsers;

    public CommentAdapter(List<Comment> comments, Context context) {
        this.pInflater = LayoutInflater.from(context);
        this.comments = comments;
        this.context = context;

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    @Override
    public int getItemCount() { return this.comments.size(); }

    @Override
    public CommentAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = pInflater.inflate(R.layout.layout_comment, null);

        return new CommentAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final CommentAdapter.ViewHolder holder, final int position) {
        holder.bindData(comments.get(position));
    }

    public void setItems(List<Comment> items) {
        comments = items;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout buttonsSection;
        Button buttonEditComment, buttonDeleteComment;
        TextView userName, commentDate, commentText;
        ImageView userImageProfile;

        ViewHolder(View itemView) {
            super(itemView);

            buttonsSection = itemView.findViewById(R.id.cardButtonsSection);

            buttonEditComment = itemView.findViewById(R.id.cardButtonEditComment);
            buttonDeleteComment = itemView.findViewById(R.id.cardButtonDeleteComment);

            userName = itemView.findViewById(R.id.cardUserName);
            userImageProfile = itemView.findViewById(R.id.cardImageUserProfile);

            commentDate = itemView.findViewById(R.id.cardCommentDate);
            commentText = itemView.findViewById(R.id.cardCommentText);
        }

        void bindData(final Comment item) {
            Picasso.with(context).load(item.getUserImageProfile()).into(userImageProfile);
            userName.setText(item.getUserName());
            commentDate.setText(item.getCommentDate());
            commentText.setText(item.getCommentText());

            if(!item.getUserId().equals(firebaseUser.getUid())) {
                buttonsSection.setVisibility(View.GONE);
            }
        }
    }

}
