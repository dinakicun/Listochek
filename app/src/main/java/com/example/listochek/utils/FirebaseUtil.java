package com.example.listochek.utils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class FirebaseUtil {

    public static String currentUserId(){
        return FirebaseAuth.getInstance().getUid();
    }
    public static DocumentReference currentUserDetails(){

        return FirebaseFirestore.getInstance().collection("users").document(currentUserId());
    }
    public static DocumentReference currentCharacteristicsDetails(){

        return FirebaseFirestore.getInstance().collection("characteristics").document(currentUserId());
    }
    public static String currentUserEmail() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            return user.getEmail();
        } else {
            return ""; 
        }
    }

    public static CollectionReference allMealsCollectionReference(){
        return FirebaseFirestore.getInstance().collection("meal");
    }
}
