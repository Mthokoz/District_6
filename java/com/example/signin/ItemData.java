package com.example.signin;
//this code was referenced from arcgis sample code
public class ItemData {
    private final String text;
    private final Integer imgId;

    public ItemData(String text, Integer imgId){
        this.text = text;
        this.imgId = imgId;
    }

    public String getText(){
        return text;
    }

    public Integer getImgId(){
        return  imgId;
    }
}
