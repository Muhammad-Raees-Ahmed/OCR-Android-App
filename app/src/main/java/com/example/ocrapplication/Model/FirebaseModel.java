package com.example.ocrapplication.Model;



import static com.example.ocrapplication.Model.Immutable.COLLECTION_VIDEOS;
import static com.example.ocrapplication.Model.Immutable.STORAGE_VIDEOS;

import android.net.Uri;


import com.example.ocrapplication.MainActivity;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Date;
import java.util.UUID;

public class FirebaseModel {
    private static FirebaseModel single_instance = null;
    private final FirebaseFirestore db;
    private final StorageReference storageRef;
    private final FirebaseStorage firebaseStorage;


    // check  instance
    public FirebaseModel() {
        db = FirebaseFirestore.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        storageRef = firebaseStorage.getReference();
    }

    public static FirebaseModel getInstance() {
        if (single_instance == null)
            single_instance = new FirebaseModel();
        return single_instance;
    }

    public void storeVideo(MainActivity mainActivity, String name, String videopath){
        // firebase storage (bucket)
        String uploadedPath = STORAGE_VIDEOS + "/" + UUID.randomUUID().toString();
        StorageReference storageReference = storageRef.child(uploadedPath);

        storageReference.putFile(Uri.parse(videopath))
                .addOnSuccessListener(
                        taskSnapshot -> storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                            // Firestore firebase
                            // imagePath..getPath() will get & save image path where yr image save in phone
                            DocumentReference doc = FirebaseFirestore.getInstance().collection(COLLECTION_VIDEOS).document();
                            doc.set(new Detail(doc.getId(), name, storageReference.getPath(), uri.toString(), new Date().getTime()))
                                    .addOnCompleteListener(task1 -> mainActivity.updateUI(task1.isSuccessful()));
                        }));
    }
}
