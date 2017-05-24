package com.michouze;

/**
 * Created by MIC7X4 on 5/2/2017.
 */
public class Blog {

    private String title;
    private String descript;
    private String image;
    private String username;
    private String comment;





    public Blog(){


    }


    public Blog(String title, String descript, String image, String comment) {
        this.title = title;
        this.descript = descript;
        this.image = image;
        this.username = username;
        this.comment= comment;
    }
    public String getComment() {return comment;}

    public void setComment(String comment) {this.comment = comment;}

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescript() {
        return descript;
    }

    public void setDescript(String descript) {
        this.descript = descript;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }



}


