package com.example.signin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.method.ScrollingMovementMethod;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Objects;

public class SiteViewActivity extends AppCompatActivity {
    private TextView text;
    private VideoView videoView;
    private MediaController mediaController;
     private Site site;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layaoutMan;

    private static  AddedSites addedsite;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_view);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        text = findViewById(R.id.card_TextView);
        Bundle bundle = getIntent().getExtras();
        String name = "";

        if(bundle.getString("name") != null){
            name = bundle.getString("name");

            System.out.println("name: "+name);
            site = new Site(name);

            if(MapActivity.additionalFileExists){
                addedsite = new AddedSites(name);

            }
            text.setText(bundle.getString("description"));
            text.setMovementMethod(new ScrollingMovementMethod());
        }



        if(site.getImages().size() > 0){
            recyclerView = findViewById(R.id.recyclerView);
            recyclerView.setHasFixedSize(true);
            layaoutMan = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
            recyclerView.setLayoutManager(layaoutMan);
            recyclerView.setItemAnimator(new DefaultItemAnimator());

            System.out.println("Site name: " + site.getName() + " Image List: " + site.getImages() + " Video name: "+ site.getVideo());
            adapter = new ImageAdaptor(this, site.getImages());
            recyclerView.setAdapter(adapter);
        }





        if(site.getVideo() != 0){
            videoView = findViewById(R.id.videoView);
            mediaController = new MediaController(this);

            mediaController.setAnchorView(videoView);

            videoView.setMediaController(mediaController);
            videoView.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + site.getVideo()));
            videoView.requestFocus();
            videoView.start();
        }

    }

}