package com.example.signin;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

public class AddedSiteView extends AppCompatActivity {
    private TextView textView;
    private ImageView imageView;
    private VideoView videoView;
    private MediaController mediaController;

    private AddedSites addedsite;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_added_site_view);

        textView = findViewById(R.id.card_TextViewAdded);
        Bundle bundle = getIntent().getExtras();
        String name = "";

        if(bundle.getString("name") != null){
            name = bundle.getString("name");

            System.out.println("name: "+name);

            if(MapActivity.additionalFileExists){
                //addedsite = new AddedSites(name);
                System.out.println("Added site created in added site view");
                textView.setText(bundle.getString("description"));
                textView.setMovementMethod(new ScrollingMovementMethod());
            }

        }
        addedsite = ManageSitesActivity.addedSite;

        imageView = findViewById(R.id.addedImageView);


        imageView.setImageBitmap(BitmapFactory.decodeFile(ManageSitesActivity.path));




        if(addedsite.getVideo() != 0){
            videoView = findViewById(R.id.videoView);
            mediaController = new MediaController(this);

            mediaController.setAnchorView(videoView);

            videoView.setMediaController(mediaController);
            videoView.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + addedsite.getVideo()));
            videoView.requestFocus();
            videoView.start();
        }
    }
}