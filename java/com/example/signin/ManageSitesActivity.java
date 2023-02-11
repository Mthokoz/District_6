package com.example.signin;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.esri.arcgisruntime.geometry.GeometryEngine;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.layers.KmlLayer;
import com.esri.arcgisruntime.ogc.kml.KmlAltitudeMode;
import com.esri.arcgisruntime.ogc.kml.KmlDataset;
import com.esri.arcgisruntime.ogc.kml.KmlDocument;
import com.esri.arcgisruntime.ogc.kml.KmlGeometry;
import com.esri.arcgisruntime.ogc.kml.KmlIcon;
import com.esri.arcgisruntime.ogc.kml.KmlIconStyle;
import com.esri.arcgisruntime.ogc.kml.KmlLineStyle;
import com.esri.arcgisruntime.ogc.kml.KmlNode;
import com.esri.arcgisruntime.ogc.kml.KmlPlacemark;
import com.esri.arcgisruntime.ogc.kml.KmlStyle;
import com.google.android.material.textfield.TextInputEditText;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;

public class ManageSitesActivity extends AppCompatActivity {


    private KmlDocument kmlDocument;
    private KmlPlacemark placemark;
    private Point point;

    private TextInputEditText placeName;
    private TextInputEditText snippet;
    private TextInputEditText description;
    private TextInputEditText x_coordinate;
    private TextInputEditText y_coordinate;

    private EditText imageInput;
    private EditText videoInput;

    private Button submit;
    private Button clearAll;
    private Button add;

    private String nameStr;
    private String snippetStr;
    private String descrStr;
    private String xstr;
    private String ystr;

    private File f;
    public static final String addedFileName = "additional.kmz";
    public static String path;

    private File external;
    public static AddedSites addedSite;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_sites);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        addedSite = new AddedSites("temp");
        submit = findViewById(R.id.submit);
        onClickSubmit(submit);

        add = findViewById(R.id.addButton);

        placeName = findViewById(R.id.placeName);
        snippet = findViewById(R.id.snippet);
        description = findViewById(R.id.description);
        x_coordinate = findViewById(R.id.x_coordinate);

        y_coordinate = findViewById(R.id.y_coordinate);

        imageInput = findViewById(R.id.imageInput);
        videoInput = findViewById(R.id.videoInput);


        setImageInput(imageInput);


        kmlDocument = new KmlDocument();


        onClickAdd(add);

    }

    private void setImageInput(View v){
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent,20);
            }
        });
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 20){
            if(resultCode == RESULT_OK){
                Uri image = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(image,filePathColumn, null,null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String picturePath = cursor.getString(columnIndex);
                cursor.close();
                path = picturePath;
                addedSite.setImages(picturePath);
                imageInput.setText(picturePath);
            }
        }
    }

    private void onClickSubmit(View v){
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nameStr = placeName.getText().toString();
                addedSite.setName(nameStr);
                snippetStr = snippet.getText().toString();
                descrStr = description.getText().toString();
                xstr = x_coordinate.getText().toString();
                ystr = y_coordinate.getText().toString();


            }
        });
        Toast.makeText(this, "submitted", Toast.LENGTH_SHORT).show();
    }

    private void onClickAdd(View v){
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //addedSite.
                addPlacement();
                MapActivity.additionalFileExists=true;

            }
        });
    }

    private void addPlacement(){

        Point wgs84Point = (Point) GeometryEngine.project(makePoint(xstr, ystr), SpatialReferences.getWgs84());
        placemark = new KmlPlacemark(new KmlGeometry(wgs84Point, KmlAltitudeMode.CLAMP_TO_GROUND));


        placemark.setName(nameStr);
        placemark.setSnippet(snippetStr);
        placemark.setDescription(descrStr);



        KmlStyle kmlStyle = new KmlStyle();
        Uri uri = Uri.parse("android.resource://com.example.signin/drawable/bluecircle.png");

        kmlStyle.setIconStyle(new KmlIconStyle(new KmlIcon(uri.toString()),1));

        kmlStyle.setLineStyle(new KmlLineStyle(Color.parseColor("Blue"),8));

        placemark.setStyle(kmlStyle);
        kmlDocument.getChildNodes().add(placemark);


        //System.out.println(Environment.DIRECTORY_DOCUMENTS+"/"+addedFileName);
        Log.d("Tag",Environment.DIRECTORY_DOCUMENTS+"/"+addedFileName+"  "+getExternalFilesDir(null)+"/"+addedFileName);
        //kmlDocument.saveAsAsync(Environment.DIRECTORY_DOCUMENTS+"/"+addedFileName);

        kmlDocument.saveAsAsync(getExternalFilesDir(null)+"/"+addedFileName);


//
       Toast.makeText(this, "Site added successfully", Toast.LENGTH_LONG).show();

    }

    private Point makePoint(String x, String y){
        Log.d("Tag",x+" "+y);
        Double dx = Double.valueOf(x);
        Double dy = Double.valueOf(y);
        return new Point(dx, dy);
    }

    private KmlDataset kmlFileOpener( String filename){
        f = new File(getCacheDir()+"/"+filename);
        if (!f.exists()) try {

            InputStream is = getAssets().open(filename);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();


            FileOutputStream fos = new FileOutputStream(f);
            fos.write(buffer);
            fos.close();
        } catch (Exception e) { throw new RuntimeException(e); }

        //mapView.setMapFile(f.getPath());



        KmlDataset kmlDataset = new KmlDataset(f.getPath());

        return kmlDataset;

    }
}