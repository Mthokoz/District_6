package com.example.signin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;

import org.w3c.dom.Document;

import java.util.Objects;

public class FeedbackActivity extends AppCompatActivity {
    EditText inputText;
    Button sendButton;
    private  UserModel data;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        sendButton = (Button) findViewById(R.id.sendButton);
        inputText = (EditText) findViewById(R.id.feedbackTextInput);

        GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(this);


        sendButton.setOnClickListener(v->{
            if(signInAccount != null){
                data= getIntent().getParcelableExtra("userData");
                data.setFeedback(inputText.getText().toString());
                saveFeedback(data);
                finish();
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
            }


        });


    }


    private void saveFeedback(UserModel data){
        DocumentReference documentReference = Utility.getCollectionReferenceForNotes();
        documentReference.set(data).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Utility.showToast(FeedbackActivity.this,"Thank You, For Your Feedback.");
                }
                else {
                    Utility.showToast(FeedbackActivity.this,"Failed while adding feedback");
                }
            }
        });
    }

}