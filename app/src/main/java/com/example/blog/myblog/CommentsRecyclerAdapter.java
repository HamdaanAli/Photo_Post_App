package com.example.blog.myblog;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by User on 20-Jun-18.
 */

public class CommentsRecyclerAdapter extends RecyclerView.Adapter<CommentsRecyclerAdapter.ViewHolder> {
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
public List<Comments>commentsList;
    public Context context;

    public CommentsRecyclerAdapter(List<Comments> commentsList) {

        this.commentsList=commentsList;
    }

    @NonNull
    @Override
    public CommentsRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_list_item,parent,false);
        context=parent.getContext();
        firebaseFirestore= FirebaseFirestore.getInstance();
        firebaseAuth= FirebaseAuth.getInstance();
        return new CommentsRecyclerAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        holder.setIsRecyclable(false);
        final String user_comment=commentsList.get(position).getMessage();
        holder.setComment_message(user_comment);
        String user_id=commentsList.get(position).getUser_id();
        firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful())
                {
                    String userName=task.getResult().getString("name");
                    String usrImage=task.getResult().getString("image");
                    holder.setCommentUserData(userName,usrImage);
                }

            }
        });
    }



    @Override
    public int getItemCount() {
        if(commentsList != null) {

            return commentsList.size();

        } else {

            return 0;

        }
    }
    public class ViewHolder extends RecyclerView.ViewHolder{
        private View mView;
          private TextView comment_message,comment_userName;
          private CircleImageView commentuserImage;


        public ViewHolder(View itemView) {

            super(itemView);
            mView=itemView;


        }
        public void setComment_message(String message)
        {
            comment_message=mView.findViewById(R.id.comment_message);
            comment_message.setText(message);
        }
        public  void setCommentUserData(String name,String image)
        {
            commentuserImage=mView.findViewById(R.id.comment_image);
            comment_userName=mView.findViewById(R.id.comment_username);
            comment_userName.setText(name);
            Glide.with(context).load(image).into(commentuserImage);
        }
    }
}
