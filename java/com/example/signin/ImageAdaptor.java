package com.example.signin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ImageAdaptor extends RecyclerView.Adapter<ImageAdaptor.ViewHolder> {
    ArrayList<Integer> imgs;
    Context context;

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.card_Imageview);
        }
    }

    public ImageAdaptor(Context context, ArrayList<Integer> imgs){
        this.context = context;
        this.imgs =imgs;

    }

    @NonNull
    @Override
    public ImageAdaptor.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.imageitem,parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ImageAdaptor.ViewHolder holder, int position) {
        holder.imageView.setImageResource(imgs.get(position));
    }

    @Override
    public int getItemCount() {
        return imgs.size();
    }
}
