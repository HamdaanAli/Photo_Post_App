package com.example.blog.myblog;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;


import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by User on 26-May-18.
 */


public class BlogRecyclerAdapter extends RecyclerView.Adapter<BlogRecyclerAdapter.ViewHolder>  {
    public List<BlogPost> blog_list;
    public Context context;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    public BlogRecyclerAdapter(List<BlogPost> blog_list){

        this.blog_list=blog_list;
    }
    @NonNull
    @Override
    public BlogRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.blog_list_item, parent, false);
        context=parent.getContext();
        firebaseFirestore=FirebaseFirestore.getInstance();
        firebaseAuth=FirebaseAuth.getInstance();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final BlogRecyclerAdapter.ViewHolder holder, int position) {
        holder.setIsRecyclable(false);
        final String blogPostId=blog_list.get(position).BlogPostId;
        final String currentUserId=firebaseAuth.getCurrentUser().getUid();
        String desc_data=blog_list.get(position).getDesc();
        holder.setDescText(desc_data);
        String image_url=blog_list.get(position).getImage_url();
        String thumbUrl=blog_list.get(position).getImage_thumb();
        holder.setBlogImage(image_url,thumbUrl);
        String user_id=blog_list.get(position).getUser_id();
        firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful())
                {
                    String userName=task.getResult().getString("name");
                    String usrImage=task.getResult().getString("image");
                    holder.setUserData(userName,usrImage);
                }
                else
                {
                    String errorMessage=task.getException().getMessage();
                    Toast.makeText(context,errorMessage,Toast.LENGTH_LONG).show();
                }
            }
        });

        try {
            long millisecond = blog_list.get(position).getTimestamp().getTime();
            String dateString = DateFormat.format("MM/dd/yyyy", new Date(millisecond)).toString();
            holder.setTime(dateString);
        } catch (Exception e) {

            Toast.makeText(context, "Exception : " + e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.d("Date Exception",e.getMessage());

        }
        //Likes Count
        firebaseFirestore.collection("Post/" + blogPostId + "/Likes").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if(!queryDocumentSnapshots.isEmpty())
                {
                    int count=queryDocumentSnapshots.size();
                    holder.updateLikesCount(count);
                }else {
                    holder.updateLikesCount(0);
                }

            }
        });
        //get likes
        firebaseFirestore.collection("Post/" + blogPostId + "/Likes").document(currentUserId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if(documentSnapshot.exists()) {

                    holder.blogLikeBtn.setImageDrawable(context.getDrawable(R.mipmap.action_like_accent));
                }else {
                    holder.blogLikeBtn.setImageDrawable(context.getDrawable(R.mipmap.action_like_gray));
                }
            }
        });
        //Likes Features
        holder.blogLikeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseFirestore.collection("Post/" + blogPostId + "/Likes").document(currentUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (!task.getResult().exists())
                        {
                            Map<String,Object> likesMap=new HashMap<>();
                            likesMap.put("timestamp", FieldValue.serverTimestamp());
                            firebaseFirestore.collection("Post/" + blogPostId + "/Likes").document(currentUserId).set(likesMap);
                        }else {
                            firebaseFirestore.collection("Post/" + blogPostId + "/Likes").document(currentUserId).delete();
                        }
                    }
                });


            }
        });

        holder.blogCommnetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent commentIntent=new Intent(context,CommentActivity.class);
                commentIntent.putExtra("blog_post_id",blogPostId);
                context.startActivity(commentIntent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return blog_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private View mView;
        private TextView blogDate;
        private TextView desView,userName;
        private ImageView blogImageView;
        private CircleImageView bloguserImage;
        private ImageView blogLikeBtn;
        private ImageView blogCommnetBtn;
        private TextView blogLikeCount;
        public ViewHolder(View itemView) {
            super(itemView);
            mView=itemView;
            blogLikeBtn=mView.findViewById(R.id.blog_like_btn);
            blogCommnetBtn=mView.findViewById(R.id.blog_comment_icon);

        }
        public void setDescText(String descText)
        {
            desView=mView.findViewById(R.id.blog_desc);
            desView.setText(descText);
        }
        public void setBlogImage(String downloadUri,String thumbUri){
            blogImageView=mView.findViewById(R.id.blog_image);
            Glide.with(context).load(downloadUri).thumbnail(Glide.with(context).load(thumbUri)).into(blogImageView);
        }

        public void setTime(String date) {

            blogDate = mView.findViewById(R.id.blog_date);
            blogDate.setText(date);

        }
        public  void setUserData(String name,String image)
        {
            bloguserImage=mView.findViewById(R.id.blog_user_image);
            userName=mView.findViewById(R.id.blog_user_name);
            userName.setText(name);
            Glide.with(context).load(image).into(bloguserImage);
        }
        public void updateLikesCount(int count)
        {
            blogLikeCount=mView.findViewById(R.id.blog_like_count);
            blogLikeCount.setText(count+"Likes");
        }
    }
}
