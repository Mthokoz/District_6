package com.example.signin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;

import java.util.Objects;

public class RateUs extends AppCompatActivity {

    RatingBar ratingBar;
    Button submitBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_us);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        ratingBar = findViewById(R.id.rating_bar);
        submitBtn =  findViewById(R.id.rateUs_sbmt_btn);

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String rate ="" + ratingBar.getRating();
                //Toast.makeText(RateUs.this, rate+ " Star", Toast.LENGTH_SHORT).show();
                UserModel user = getIntent().getParcelableExtra("userData");
                user.setRating(rate);
                upload(user);
                finish();
            }
        });
    }

    private void upload(UserModel data){
        DocumentReference documentReference = Utility.getCollectionReferenceForNotes();
        documentReference.set(data).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Utility.showToast(RateUs.this,"Thank You, For Your Rating.");
                }
                else {
                    Utility.showToast(RateUs.this,"Failed while adding Rating");
                }
            }
        });
    }

}