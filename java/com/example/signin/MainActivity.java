package com.example.signin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import androidx.core.view.GravityCompat;
import android.app.Activity;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.squareup.picasso.Picasso;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.Objects;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener, NavigationView.OnNavigationItemSelectedListener, FirebaseAuth.AuthStateListener {

    private SignInButton signInButton;
    private Button signOutButton;
    private FloatingActionButton floatToMapBtn;
    private TextView textView;
    private TextView youtubeTextView;
    private TextView museumTextView;
    private Switch dataCollectionSwitch;
    private static TextView headerEmail;
    private static TextView headerName;
    private static ImageView headerImage;
    private GoogleApiClient mGoogleClient;
    private static final String TAG = "tax";
    private static final int RC_SIGN_IN = 123;

    public DrawerLayout drawerLayout;
    public ActionBarDrawerToggle actionBarDrawerToggle;

    UserModel userData;
    SharedPreferences sharedPreferences;
    private FirebaseAuth mAuth;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Header text, home page links and image
        headerName = findViewById(R.id.header_name);
        headerEmail = findViewById(R.id.header_email);
        headerImage = findViewById(R.id.header_imageView);
        youtubeTextView = findViewById(R.id.youtubeTextView);
        museumTextView= findViewById(R.id.museumTextView);

        //enable the links to be clickable
        youtubeTextView.setMovementMethod(LinkMovementMethod.getInstance());
        museumTextView.setMovementMethod(LinkMovementMethod.getInstance());

        mAuth = FirebaseAuth.getInstance();
        createRequest();

        userData=new UserModel();

        signInButton = findViewById(R.id.signIn);
        textView = findViewById(R.id.textview);
        findViewById(R.id.signIn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });

        signOutButton = findViewById(R.id.signOut);
        signOutButton.setOnClickListener(this);

        floatToMapBtn = findViewById(R.id.floatToMapBtn);

        floatToMapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                naviToMap();
            }
        });

        //initialize switch
        dataCollectionSwitch = findViewById(R.id.dataCollection);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isDataCollectionOn =  sharedPreferences.getBoolean("dataCollection",true);

        dataCollectionSwitch.setChecked(isDataCollectionOn);

        dataCollectionSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    //turn on data collection
                    sharedPreferences.edit().putBoolean("dataCollection",true).apply();
                    FirebaseAnalytics.getInstance(getApplicationContext()).setAnalyticsCollectionEnabled(true);
                }else {
                    //turn off data collection
                    sharedPreferences.edit().putBoolean("dataCollection",false).apply();
                    FirebaseAnalytics.getInstance(getApplicationContext()).setAnalyticsCollectionEnabled(false);
                }
            }
        });

        //initialize profile


        onCreateDrawer();


    }

    private void createRequest()  {

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .requestIdToken("1027967924906-37p0urq0antcijpdko2rtet9lcisv1al.apps.googleusercontent.com")
                .build();

        mGoogleClient = new GoogleApiClient.Builder(this).enableAutoManage(this,this).addApi(Auth.GOOGLE_SIGN_IN_API,gso).build();

    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {


        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public void onClick(View v){
        switch (v.getId()){
            case R.id.signIn:
                signIn();
                break;
            case R.id.signOut:
                signOut();
                break;
        }

    }

    private void signIn(){
        Intent signinIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleClient);
        startActivityForResult(signinIntent, RC_SIGN_IN);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);


        }
    }

        public  void updateNavProfile(GoogleSignInAccount acct){
            headerImage=findViewById(R.id.header_imageView);
            headerEmail= findViewById(R.id.header_email);
            headerName= findViewById(R.id.header_name);
            headerName.setText(acct.getDisplayName());
            headerEmail.setText(acct.getEmail());
            Picasso.get().load(acct.getPhotoUrl()).into(headerImage);

    }




    private void handleSignInResult(GoogleSignInResult result){
        String accName ="";
        if(result.isSuccess()){
            GoogleSignInAccount account = result.getSignInAccount();
            firebaseAuthWithGoogle(account);
            updateNavProfile(account);


            textView.setText("Welcome, "+ account.getDisplayName());
            if((headerName!= null && headerEmail != null) ){
                headerName.setText(account.getDisplayName());
                headerEmail.setText(account.getEmail());
            }

            userData.setEmail(account.getEmail());
            userData.setUsername(account.getDisplayName());

            //naviToMap();

        }else{
            textView.setText("Sign in unsuccessful");
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the  signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            //Toast.makeText(MainActivity.this, "Firebase signed in.", Toast.LENGTH_SHORT).show();
                            Log.d("firebase","Firebase signed in");


                        } else {
                            Toast.makeText(MainActivity.this, "Sorry auth failed.", Toast.LENGTH_SHORT).show();



                        }

                    }
                });
    }

    public void onConnectionFailed(ConnectionResult connectionResult){
        Log.d(TAG,"onConnectionFailed" + connectionResult);
    }

    private void signOut(){
        Auth.GoogleSignInApi.signOut(mGoogleClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                textView.setText("Signed Out");
            }
        });
        mAuth.signOut();
        finish();
        startActivity(getIntent());

    }

    private void navToMap(MenuItem item){
        actionBarDrawerToggle.onOptionsItemSelected(item);
        Intent mapViewIntent = new Intent(getApplicationContext(), MapActivity.class);
        startActivity(mapViewIntent);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }

    private void naviToHome(MenuItem item){
        actionBarDrawerToggle.onOptionsItemSelected(item);
        Intent mapViewIntent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(mapViewIntent);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }
    private void naviToMap(){
        Intent mapViewIntent = new Intent(getApplicationContext(), MapActivity.class);
        startActivity(mapViewIntent);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }


    private void navToFeedback(MenuItem item){
        actionBarDrawerToggle.onOptionsItemSelected(item);
        Intent mainViewIntent = new Intent(getApplicationContext(), FeedbackActivity.class);
        mainViewIntent.putExtra("userData",userData);
        startActivity(mainViewIntent);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }
    private void navToManSites(MenuItem item){
        actionBarDrawerToggle.onOptionsItemSelected(item);
        Intent mainViewIntent = new Intent(getApplicationContext(), ManageSitesActivity.class);
        startActivity(mainViewIntent);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }
    private void navToAppInstructions(MenuItem item){
        actionBarDrawerToggle.onOptionsItemSelected(item);
        Intent mainViewIntent = new Intent(getApplicationContext(), AppInstructionsActivity.class);
        startActivity(mainViewIntent);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }

    private void navToRateUs(MenuItem item) {
        actionBarDrawerToggle.onOptionsItemSelected(item);
        Intent mainViewIntent = new Intent(getApplicationContext(), RateUs.class);
        mainViewIntent.putExtra("userData",userData);
        startActivity(mainViewIntent);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.nav_home :
                naviToHome(item);
                drawerLayout.closeDrawer(GravityCompat.START);
                break;

//            case  R.id.nav_map:
//                navToMap(item);
//                drawerLayout.closeDrawer(GravityCompat.START);
//                break;

            case R.id.nav_feedback :
                navToFeedback(item);
                drawerLayout.closeDrawer(GravityCompat.START);
                break;

            case R.id.nav_managesites:
                navToManSites(item);
                drawerLayout.closeDrawer(GravityCompat.START);
                break;

            case R.id.nav_instructions:
                navToAppInstructions(item);
                drawerLayout.closeDrawer(GravityCompat.START);
                break;
                
            case R.id.activity_rate_us:
                navToRateUs(item);
                drawerLayout.closeDrawer(GravityCompat.START);
                break;

        }
        return true;
    }


    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        FirebaseAuth.getInstance().addAuthStateListener(this);
        if(user != null){
            textView.setText("Welcome Back, "+ user.getDisplayName());
        }


    }

    @Override
    protected void onStop() {
        super.onStop();
        FirebaseAuth.getInstance().removeAuthStateListener(this);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        if(mAuth.getCurrentUser()==null){
            signInButton.setVisibility(View.VISIBLE);
            floatToMapBtn.setVisibility(View.INVISIBLE);

        }
        else {
            signInButton.setVisibility(View.INVISIBLE);
            floatToMapBtn.setVisibility(View.VISIBLE);

        }

    }

    protected void onCreateDrawer() {

        // Drawer layout functionalities
        // drawer layout instance to toggle the menu icon to open
        // drawer and back button to close drawer
        drawerLayout = findViewById(R.id.my_drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close);

        // pass the Open and Close toggle for the drawer layout listener
        // to toggle the button
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        // to make the Navigation drawer icon always appear on the action bar
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);}

}