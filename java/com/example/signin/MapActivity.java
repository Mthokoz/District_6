package com.example.signin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.esri.arcgisruntime.ArcGISRuntimeEnvironment;
import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.geometry.Envelope;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.layers.KmlLayer;
import com.esri.arcgisruntime.layers.RasterLayer;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.BasemapStyle;
import com.esri.arcgisruntime.mapping.GeoElement;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.DefaultMapViewOnTouchListener;
import com.esri.arcgisruntime.mapping.view.IdentifyLayerResult;
import com.esri.arcgisruntime.mapping.view.LocationDisplay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.ogc.kml.KmlDataset;
import com.esri.arcgisruntime.ogc.kml.KmlDocument;
import com.esri.arcgisruntime.ogc.kml.KmlNode;
import com.esri.arcgisruntime.ogc.kml.KmlPlacemark;
import com.esri.arcgisruntime.raster.Raster;
import com.google.android.material.navigation.NavigationView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class MapActivity extends AppCompatActivity {
    private static final String TAG = MapActivity.class.getSimpleName();

    private MapView mMapView;

    private Switch oldMapSwitch;
    private RasterLayer oldMapRasterLayer;

    private LocationDisplay mLocationDisplay;
    private Spinner mSpinner;
    //private ConstraintLayout mSpinner;

    private final int requestCode = 2;
    private final String[] reqPermissions = { Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission
            .ACCESS_COARSE_LOCATION };

    public DrawerLayout drawerLayout;
    public ActionBarDrawerToggle actionBarDrawerToggle;
    public NavigationView navigationView;

    private static KmlLayer walkPoints;
    private KmlLayer kmlOutlines;
    private List<KmlNode> rootNodes;
    private KmlLayer additional;
    private KmlDocument kmlDoc;
    private static final String sTag = "Gesture";
    public static final String addedFileName = "additional.kmz";
    public static boolean additionalFileExists=false;

    ListenableFuture<IdentifyLayerResult> identify;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        //ArcGis Map functionalities
        //authentication with an API key or named user is required to access basemaps and other
        // location services
        ArcGISRuntimeEnvironment.setApiKey("AAPKc452b718d86a42478223daece4f6d230k2oCAVQVRYNcqhfhUNbcv0_z_88lTfU0aExSQeeoEGNEqlpco2T82YDDTAItgIKf");

        // inflate MapView from layout
        mMapView = findViewById(R.id.mapView);
        // create a map with the a topographic basemap
        ArcGISMap map = new ArcGISMap(BasemapStyle.ARCGIS_IMAGERY_STANDARD);

        // set the map to be displayed in this view
        mMapView.setMap(map);

        mMapView.setViewpoint(new Viewpoint(-33.93044358519987, 18.431169559143846, 3000));
        //-33.9798708144704, 18.46507358520209 Roscommon
        //-33.9314119603377, 18.428635754546022 District Six
        //-33.93044358519987, 18.431169559143846 center

        // Get the Spinner from layout
        mSpinner = findViewById(R.id.spinner);

        // create raster layer
        try {
            oldMapRasterLayer = createRasterLayer();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Add kml layers
        KmlDataset walkPointsDataset = kmlFileOpener("5walk_pts.kml");
        KmlDataset kmlOutlinesDataset = kmlFileOpener("CompSci_outlines.kml");
        walkPoints = new KmlLayer(walkPointsDataset);
        kmlOutlines = new KmlLayer(kmlOutlinesDataset);

        try {
            changeSourceToFileExternalStorage(walkPoints, kmlOutlines);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // add additional layers
        if(additionalFileExists) {
            KmlDataset additionalData = kmlFileOpener(getExternalFilesDir(null) + addedFileName);
            additional = new KmlLayer(additionalData);
            try {
                addAdditional(additional);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //changeSourceToURL();
        ServiceFeatureTable featureTable = new ServiceFeatureTable("https://services6.arcgis.com/rIDqjvQvP4CCdwUH/arcgis/rest/services/DSixFinTour/FeatureServer/0");
        FeatureLayer featureLayer = new FeatureLayer(featureTable);
        ServiceFeatureTable featureTable1 = new ServiceFeatureTable("https://services6.arcgis.com/rIDqjvQvP4CCdwUH/arcgis/rest/services/DSixFinTour/FeatureServer/1");
        FeatureLayer featureLayer1 = new FeatureLayer(featureTable1);
        ServiceFeatureTable featureTable2 = new ServiceFeatureTable("https://services6.arcgis.com/rIDqjvQvP4CCdwUH/arcgis/rest/services/DSixFinTour/FeatureServer/2");
        FeatureLayer featureLayer2 = new FeatureLayer(featureTable2);
        ServiceFeatureTable featureTable3 = new ServiceFeatureTable("https://services6.arcgis.com/rIDqjvQvP4CCdwUH/arcgis/rest/services/DSixFinTour/FeatureServer/3");
        FeatureLayer featureLayer3 = new FeatureLayer(featureTable3);
        mMapView.getMap().getOperationalLayers().add(featureLayer);
        mMapView.getMap().getOperationalLayers().add(featureLayer1);
        mMapView.getMap().getOperationalLayers().add(featureLayer2);
        mMapView.getMap().getOperationalLayers().add(featureLayer3);

        //Location services functionalities
        // get the MapView's LocationDisplay
        mLocationDisplay = mMapView.getLocationDisplay();

        // Listen to changes in the status of the location data source.
        mLocationDisplay.addDataSourceStatusChangedListener(dataSourceStatusChangedEvent -> {

            // If LocationDisplay started OK, then continue.
            if (dataSourceStatusChangedEvent.isStarted())
                return;

            // No error is reported, then continue.
            if (dataSourceStatusChangedEvent.getError() == null)
                return;

            // If an error is found, handle the failure to start.
            // Check permissions to see if failure may be due to lack of permissions.
            boolean permissionCheck1 = ContextCompat.checkSelfPermission(this, reqPermissions[0]) ==
                    PackageManager.PERMISSION_GRANTED;
            boolean permissionCheck2 = ContextCompat.checkSelfPermission(this, reqPermissions[1]) ==
                    PackageManager.PERMISSION_GRANTED;

            if (!(permissionCheck1 && permissionCheck2)) {
                // If permissions are not already granted, request permission from the user.
                ActivityCompat.requestPermissions(this, reqPermissions, requestCode);
            } else {
                // Report other unknown failure types to the user - for example, location services may not
                // be enabled on the device.
                String message = String.format("Error in DataSourceStatusChangedListener: %s", dataSourceStatusChangedEvent
                        .getSource().getLocationDataSource().getError().getMessage());
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();

                // Update UI to reflect that the location display did not actually start
                mSpinner.setSelection(0, true);
            }
        });

        // Populate the list for the Location display options for the spinner's Adapter
        ArrayList<ItemData> list = new ArrayList<>();
        list.add(new ItemData("Stop", R.drawable.locationdisplaydisabled));
        list.add(new ItemData("On", R.drawable.locationdisplayon));
        list.add(new ItemData("Re-Center", R.drawable.locationdisplayrecenter));
        list.add(new ItemData("Navigation", R.drawable.locationdisplaynavigation));
        list.add(new ItemData("Compass", R.drawable.locationdisplayheading));

        SpinnerAdapter adapter = new SpinnerAdapter(this, R.layout.spinner_layout, R.id.txt, list);
        mSpinner.setAdapter(adapter);
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                switch (position) {
                    case 0:
                        // Stop Location Display
                        if (mLocationDisplay.isStarted())
                            mLocationDisplay.stop();
                        break;
                    case 1:
                        // Start Location Display
                        if (!mLocationDisplay.isStarted())
                            mLocationDisplay.startAsync();
                        break;
                    case 2:
                        // Re-Center MapView on Location
                        // AutoPanMode - Default: In this mode, the MapView attempts to keep the location symbol on-screen by
                        // re-centering the location symbol when the symbol moves outside a "wander extent". The location symbol
                        // may move freely within the wander extent, but as soon as the symbol exits the wander extent, the MapView
                        // re-centers the map on the symbol.
                        mLocationDisplay.setAutoPanMode(LocationDisplay.AutoPanMode.RECENTER);
                        if (!mLocationDisplay.isStarted())
                            mLocationDisplay.startAsync();
                        break;
                    case 3:
                        // Start Navigation Mode
                        // This mode is best suited for in-vehicle navigation.
                        mLocationDisplay.setAutoPanMode(LocationDisplay.AutoPanMode.NAVIGATION);
                        if (!mLocationDisplay.isStarted())
                            mLocationDisplay.startAsync();
                        break;
                    case 4:
                        // Start Compass Mode
                        // This mode is better suited for waypoint navigation when the user is walking.
                        mLocationDisplay.setAutoPanMode(LocationDisplay.AutoPanMode.COMPASS_NAVIGATION);
                        if (!mLocationDisplay.isStarted())
                            mLocationDisplay.startAsync();
                        break;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }

        });

        oldMapSwitch = new Switch(this);
        oldMapSwitch = (Switch) findViewById(R.id.switch1);
        oldMapSwitch.setOnClickListener(v -> {
            if (oldMapSwitch.isChecked()) {
                mMapView.getMap().getOperationalLayers().add(0,oldMapRasterLayer);
            }else {
                mMapView.getMap().getOperationalLayers().remove(0);
            }
        });


        mMapView.setOnTouchListener(new DefaultMapViewOnTouchListener(this, mMapView){
            @Override
            public boolean onSingleTapConfirmed(MotionEvent motionEvent){
                Log.d(sTag, "onSingleTapConfirmed: " + motionEvent.toString());

                // get the point that was clicked and convert it to a point in map coordinates
                android.graphics.Point screenPoint = new android.graphics.Point(Math.round(motionEvent.getX()),
                        Math.round(motionEvent.getY()));

                // create a map point from screen point
                //Point mapPoint = mMapView.screenToLocation(screenPoint);

                ListenableFuture<IdentifyLayerResult> identify = mMapView.identifyLayerAsync(walkPoints, screenPoint, 15, false);
                identify.addDoneListener(doneListener(identify));

                if(additionalFileExists){
                    ListenableFuture<IdentifyLayerResult> identify2 = mMapView.identifyLayerAsync(additional, screenPoint, 15, false);

                    identify2.addDoneListener(doneListener(identify2));
                }

                return true;


            }


        });

    }


    public Runnable doneListener(ListenableFuture<IdentifyLayerResult> identify){
        return new Runnable(){
            @Override
            public void run() {
                try {
                    IdentifyLayerResult result = identify.get();
                    // find the first geoElement that is a KML placemark

                    for (GeoElement geoElement : result.getElements()) {
                        if (geoElement instanceof KmlPlacemark) {
                            // show a callout at the placemark with custom content using the placemark's "balloon content"
                            KmlPlacemark placemark = (KmlPlacemark) geoElement;
                            System.out.println(placemark.getSnippet());


                            // Google Earth only displays the placemarks with description or extended data. To
                            // match its behavior, add a description placeholder if the data source is empty
                            if (placemark.getDescription().isEmpty()) {
                                placemark.setDescription("Point description here");
                            }

                            AlertDialog.Builder builder = new AlertDialog.Builder(MapActivity.this);
                            ImageView image = new ImageView(MapActivity.this);
                            image.setImageResource(R.drawable.hanover1);
                            builder.setMessage(placemark.getSnippet())
                                    .setTitle(placemark.getName())
                                    .setIcon(R.drawable.hanover1)
                                    .setNegativeButton("Back", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {

                                        }
                                    }).setPositiveButton("View", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            if(additionalFileExists){
                                                if(isNotFromAdded(placemark.getName())){
                                                    Intent cardViewIntent = new Intent(MapActivity.this, SiteViewActivity.class);

                                                    String name = placemark.getName();
                                                    cardViewIntent.putExtra("name",name);
                                                    cardViewIntent.putExtra("description",placemark.getDescription());
                                                    startActivity(cardViewIntent);
                                                }else{
                                                    Intent cardViewIntent = new Intent(MapActivity.this, AddedSiteView.class);
                                                    String name = placemark.getName();
                                                    cardViewIntent.putExtra("name",name);
                                                    cardViewIntent.putExtra("description",placemark.getDescription());
                                                    startActivity(cardViewIntent);
                                                }

                                            }else{
                                                Intent cardViewIntent = new Intent(MapActivity.this, SiteViewActivity.class);

                                                String name = placemark.getName();
                                                cardViewIntent.putExtra("name",name);
                                                cardViewIntent.putExtra("description",placemark.getDescription());
                                                startActivity(cardViewIntent);
                                            }


                                        }
                                    });
                            AlertDialog dialog = builder.create();
                            dialog.show();

                            break;
                        }
                    }
                } catch (InterruptedException | ExecutionException ex) {
                    //new Alert(Alert.AlertType.ERROR, "Error identifying features in layer").show();
                    Log.d("Alert","Error identifying features in layer");
                }
            }
        };
    }

    private boolean isNotFromAdded(String plname){
        boolean isNotFromAdded = false;
        String[] names = {"St Marks Church", "Star Cinema", "Seven Steps", "Hanover Street", "Public Wash House"};
        for(String name:names){
            if(plname.equalsIgnoreCase(name)){
                isNotFromAdded = true;
            }
        }
        return isNotFromAdded;
    }
    /**
     * Permission to use location services
    * */

    @Override
    public void onRequestPermissionsResult (int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // If request is cancelled, the result arrays are empty.
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Location permission was granted. This would have been triggered in response to failing to start the
            // LocationDisplay, so try starting this again.
            mLocationDisplay.startAsync();
        } else {
            // If permission was denied, show toast to inform user what was chosen. If LocationDisplay is started again,
            // request permission UX will be shown again, option should be shown to allow never showing the UX again.
            // Alternative would be to disable functionality so request is not shown again.
            Toast.makeText(this, getString(R.string.location_permission_denied), Toast.LENGTH_SHORT).show();

            // Update UI to reflect that the location display did not actually start
            mSpinner.setSelection(0, true);
        }
    }

    /**
     * create raster layer to add old map
     * */
    private RasterLayer createRasterLayer() throws IOException {
        // Load the raster file
        Raster myRasterFile = rasterFileOpener("D6_Georef_1968.tif");
        assert myRasterFile != null;
        // Create the layer
        RasterLayer myRasterLayer = new RasterLayer(myRasterFile);
        assert myRasterLayer != null;

        //mMapView.getMap().getOperationalLayers().add(myRasterLayer);
        return myRasterLayer;
    }
    /**
     * opens the tiff required for the old map where it is stored in the apk within the cellphone
     */
    private Raster rasterFileOpener( String filename){
        File f = new File(getCacheDir()+"/"+filename);
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

        Raster myRaster = new Raster(f.getPath());
        return myRaster;
    }


    /**
     * Creates a list of stops along a route.
     * change point to district six points
     */

    private void display(KmlLayer kmlLayer){
        // clear the existing layers from the map
        //mMapView.getMap().getOperationalLayers().clear();

        // add the KML layer to the map
        mMapView.getMap().getOperationalLayers().add(kmlLayer);
    }


    /***
     * Display a kml layer from external storage. downloads
     */
    private void changeSourceToFileExternalStorage(KmlLayer kmlWalk, KmlLayer kmlOutlines) throws IOException {

        display(kmlOutlines);
        display(kmlWalk);

        // report errors if failed to load
        kmlWalk.addDoneLoadingListener(() -> {
            if (kmlWalk.getLoadStatus() != LoadStatus.LOADED) {
                String error ="failed to load layers";
//                        "Failed to load kml data set from external storage: " + kmlDataset.getLoadError().getCause().getMessage();
                Toast.makeText(this, error, Toast.LENGTH_LONG).show();
                Log.e(TAG, error);
            }
        });
    }

    private void addAdditional(KmlLayer kmlAdditional) throws IOException{
        display(kmlAdditional);

        // report errors if failed to load
        kmlAdditional.addDoneLoadingListener(() -> {
            if (kmlAdditional.getLoadStatus() != LoadStatus.LOADED) {
                String error ="failed to load layers";
//                        "Failed to load kml data set from external storage: " + kmlDataset.getLoadError().getCause().getMessage();
                Toast.makeText(this, error, Toast.LENGTH_LONG).show();
                Log.e(TAG, error);
            }Toast.makeText(this, "Loaded", Toast.LENGTH_LONG).show();
        });
    }

    private KmlDataset kmlFileOpener( String filename){
        File f;
        Log.d("Tag", "filename: "+filename);
        if(filename.equalsIgnoreCase("/storage/emulated/0/Android/data/com.example.signin/filesadditional.kmz")){
            f = new File("/storage/emulated/0/Android/data/com.example.signin/files/additional.kmz");
            additionalFileExists= f.exists();
        }else {f = new File(getCacheDir()+"/"+filename);}

        Log.d("Tag", Environment.getExternalStorageState()+" "+f.getName()+" "+f.exists());

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
        //System.out.println(f.getPath());
        //Toast.makeText(this, f.getPath(), Toast.LENGTH_LONG).show();
        KmlDataset kmlDataset = new KmlDataset(f.getPath());
        return kmlDataset;
    }

    private KmlNode nodeAnalyser(KmlDataset walkPoints, Point point){

        rootNodes = walkPoints.getRootNodes();
        for (KmlNode n: rootNodes){
            System.out.println();
            Envelope env = n.getExtent();
            if((env.getXMin() <= point.getX() && point.getX() <= env.getXMax()) && (env.getYMin() <= point.getY() && point.getY() <= env.getYMax())){
                return n;
            }
        }
        return null;
    }


    @Override
    protected void onPause() {
        mMapView.pause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.resume();

    }

    @Override
    protected void onDestroy() {
        mMapView.dispose();
        super.onDestroy();
    }



    public static KmlLayer getWalkPoints() {
        return walkPoints;
    }

}