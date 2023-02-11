package com.example.signin;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class Utility {
    static void showToast(Context context, String message){
        Toast.makeText(context,message, Toast.LENGTH_SHORT).show();
    }

    static DocumentReference getCollectionReferenceForNotes(){
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if(currentUser == null){
            Log.d("usr","null user");
        }
       return FirebaseFirestore.getInstance().collection("users").document(currentUser.getUid());
    }
}
