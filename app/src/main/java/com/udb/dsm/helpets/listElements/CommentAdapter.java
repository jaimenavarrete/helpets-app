package com.udb.dsm.helpets.listElements;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.udb.dsm.helpets.MainActivity;
import com.udb.dsm.helpets.R;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {
    private List<Comment> comments;
    private LayoutInflater pInflater;
    private Context context;
    private String postId;
    private int postComments;

    FirebaseUser firebaseUser;

    private DatabaseReference pDatabasePosts, pDatabaseComments;

    public CommentAdapter(List<Comment> comments, Context context, String postId) {
        this.pInflater = LayoutInflater.from(context);
        this.comments = comments;
        this.context = context;
        this.postId = postId;

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        pDatabasePosts = FirebaseDatabase.getInstance().getReference().child("posts");
        pDatabasePosts.addListenerForSingleValueEvent(postListEvent());

        pDatabaseComments = FirebaseDatabase.getInstance().getReference().child("comments");
    }

    public ValueEventListener postListEvent() {
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    postComments = snapshot.child(postId).child("postComments").getValue(Integer.class);
                }
                catch(Exception ignored) {}
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
    }

    @Override
    public int getItemCount() { return this.comments.size(); }

    @Override
    public CommentAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = pInflater.inflate(R.layout.layout_comment, parent, false);

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

            buttonDeleteComment.setOnClickListener(v -> {
                buttonDeleteCommentEvent(item);
            });

            if(!item.getUserId().equals(firebaseUser.getUid())) {
                buttonsSection.setVisibility(View.GONE);
            }
        }
    }

    public void buttonDeleteCommentEvent(final Comment item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setMessage("¿Está seguro de eliminar este comentario?").setTitle("Confirmación");

        builder.setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                try {
                    pDatabaseComments.child(postId).child(item.getCommentId()).removeValue();
                    pDatabasePosts.child(postId).child("postComments").setValue(postComments - 1);

                    Toast.makeText(context, "El comentario ha sido eliminado correctamente", Toast.LENGTH_LONG).show();
                }
                catch (Exception e){
                    Toast.makeText(context, "Hubo un error al eliminar el comentario. Inténtelo más tarde", Toast.LENGTH_LONG).show();
                }
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {}
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

}
