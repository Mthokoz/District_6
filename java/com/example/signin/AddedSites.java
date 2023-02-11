package com.example.signin;

import android.provider.MediaStore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class AddedSites extends Site{
    private static ArrayList<String> filepaths = new ArrayList<String>();
    private static HashMap<String,ArrayList<String>> nameImgPair = new HashMap<String,ArrayList<String>>();
    public AddedSites(String name) {
        super(name);

    }

    @Override
    public void setImages(String filepath) {

        if(nameImgPair.containsKey(super.getName())){
            filepaths = nameImgPair.get(super.getName());
            filepaths.add(filepath);
            System.out.println(filepath+" "+filepaths.toString());
            nameImgPair.put(super.getName(),filepaths);
        }else{
            filepaths.add(filepath);
            nameImgPair.put(super.getName(),filepaths);
            System.out.println(filepath+" "+filepaths.toString());
        }
    }


    public String getImage() {
        return Objects.requireNonNull(nameImgPair.get(super.getName())).get(0);
    }
}
