package com.example.blog.myblog;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by User on 26-May-18.
 */


public class BlogRecyclerAdapter extends RecyclerView.Adapter<BlogRecyclerAdapter.ViewHolder>  {
    public List<BlogPost> blog_list;
    public Context context;
    private FirebaseFirestore firebaseFirestore;
    public BlogRecyclerAdapter(List<BlogPost> blog_list){

        this.blog_list=blog_list;
    }
    @NonNull
    @Override
    public BlogRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.blog_list_item, parent, false);
        context=parent.getContext();
        firebaseFirestore=FirebaseFirestore.getInstance();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final BlogRecyclerAdapter.ViewHolder holder, int position) {
              String desc_data=blog_list.get(position).getDesc();
              holder.setDescText(desc_data);
              String image_url=blog_list.get(position).getImage_url();
              holder.setBlogImage(image_url);
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

        }


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
        public ViewHolder(View itemView) {
            super(itemView);
          mView=itemView;
        }
        public void setDescText(String descText)
        {
           desView=mView.findViewById(R.id.blog_desc);
           desView.setText(descText);
        }
        public void setBlogImage(String downloadUri){
            blogImageView=mView.findViewById(R.id.blog_image);
            Glide.with(context).load(downloadUri).into(blogImageView);
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

    }
}
