package com.example.ocrapplication.Model;



import static android.content.ContentValues.TAG;
import static com.example.ocrapplication.Model.Immutable.COLLECTION_VIDEOS;
import static com.example.ocrapplication.Model.Immutable.STORAGE_VIDEOS;

import android.net.Uri;
import android.util.Log;
import android.widget.Toast;


import androidx.annotation.NonNull;

import com.example.ocrapplication.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

public class FirebaseModel {
    private static FirebaseModel single_instance = null;
    private final FirebaseFirestore db;
    private final StorageReference storageRef;
    private final FirebaseStorage firebaseStorage;
    String videoUrl;

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
    public void getVideoFromVideoName(MainActivity mainActivity, String name){

        db.collection(COLLECTION_VIDEOS).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                if (document.get("name").equals(name)){
                                    videoUrl=(document.getString("videourl"));

                                }
                            }
                            mainActivity.updateUISuccess(task.isSuccessful(),videoUrl);
                        }else{
                            System.out.println("not found");
                        }
                    }
                });
    }
}
