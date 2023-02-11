package com.example.signin;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class SpinnerAdapter extends ArrayAdapter<ItemData> {

    private final int grpId;
    private final ArrayList<ItemData> itemList;
    private  final LayoutInflater layoutInflater;

    public SpinnerAdapter(Activity context, int groupid, int id, ArrayList<ItemData> list){
        super(context, id, list);
        this.itemList = list;
        this.grpId = groupid;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public View getView(int position, View convertView, ViewGroup parent){
        View itemView = layoutInflater.inflate(grpId, parent, false);
        ImageView imgView = itemView.findViewById(R.id.img);
        imgView.setImageResource(itemList.get(position).getImgId());
        System.out.println(R.id.txt);
        TextView textView = itemView.findViewById(R.id.txt);
        textView.setText(itemList.get(position).getText());

        return itemView;
    }

    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getView(position, convertView, parent);

    }
}
