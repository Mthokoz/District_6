package com.example.signin;


import android.provider.MediaStore;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import android.content.*;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;


public class Site {
    private String name;
    private int vidName;
    private ArrayList<Integer> imgs ;


    public Site(String name){
        this.name = name;
        this.imgs = new ArrayList<>();
        this.vidName = 0;

    }

    public void setName(String name) {
        this.name = name;
    }

    public void setImages(String filepath) {

    }

    public void setVideo(MediaStore.Video video) {


    }



    public String getName() {
        return name;
    }

    public ArrayList<Integer> getImages() {
        if(name.equalsIgnoreCase("St Marks Church") ){
            imgs.add(R.drawable.stmarks2);
            imgs.add(R.drawable.stmarks3);
        }else if(name.equalsIgnoreCase("Star Cinema")){
            imgs.add(R.drawable.star);
            imgs.add(R.drawable.star_cinema);
            imgs.add(R.drawable.star_balcony);
        }else if(name.equalsIgnoreCase("Seven Steps")){
            imgs.add(R.drawable.seven_steps);
        }else if(name.equalsIgnoreCase("Hanover Street")){
            imgs.add(R.drawable.hanover1);
            imgs.add(R.drawable.hanover2);
            imgs.add(R.drawable.hanover3);
            imgs.add(R.drawable.hanover4);
        }else if(name.equalsIgnoreCase("Public Wash House")){
            imgs.add(R.drawable.pwh);
            imgs.add(R.drawable.pwh_greshoff);
        }else{}

        return imgs;
    }

    public int getVideo() {
        if(name.equalsIgnoreCase("St Marks Church")){
            vidName = R.raw.st_marks_church;
        }else if(name.equalsIgnoreCase("Seven Steps")){
            vidName = R.raw.st_marks_church;
        }else if(name.equalsIgnoreCase("Hanover Street")){
            vidName = R.raw.hanover_street;
        }else{}
        return vidName;
    }


}
