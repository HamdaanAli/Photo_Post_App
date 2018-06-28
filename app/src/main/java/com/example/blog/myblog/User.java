package com.example.blog.myblog;

/**
 * Created by User on 22-Jun-18.
 */

public class User {

   public String image,name;

    public User() {
    }
    public User(String image, String name) {
        this.image = image;
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}
