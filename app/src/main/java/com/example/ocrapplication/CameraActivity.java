package com.example.ocrapplication;

import static com.example.ocrapplication.MainActivity.url;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.ocrapplication.Model.FirebaseModel;

public class CameraActivity extends AppCompatActivity {

    VideoView videoView;
    ProgressBar progressBar;
    String name;
    FirebaseModel firebaseModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);


        try {
            this.getSupportActionBar().hide();
        } catch (NullPointerException e) {
        }

        firebaseModel = FirebaseModel.getInstance();

        progressBar=findViewById(R.id.progress3);
        try{
            name = getIntent().getExtras().getString("path");
            searchVideo_Play(name);
        }
        catch (Exception e){
            System.out.println(e);
        }

//        Toast.makeText(this, name, Toast.LENGTH_SHORT).show();


    }
    private void searchVideo_Play(String videoName) {
        if (videoName == "") {
            Toast.makeText(this, "Plz fill fields", Toast.LENGTH_SHORT).show();
        } else {
            progressBar.setVisibility(View.VISIBLE);
            firebaseModel.getVideoFromVideoName2(this, videoName);
        }
    }


    public void updateUISuccess2(boolean isSuccessfull, String videoUrl) {

        if (isSuccessfull == true && videoUrl != null) {
            Toast.makeText(this, "Found", Toast.LENGTH_SHORT).show();
            playVideo(videoUrl);
        } else {
            progressBar.setVisibility(View.INVISIBLE);
            Toast.makeText(this, "Not found", Toast.LENGTH_SHORT).show();
        }
    }
    public void playVideo(String videourl) {
        videoView=findViewById(R.id.fullscreen_videoview2);
        System.out.println("path check :"+ name);
        videoView.setVideoPath(videourl);
        MediaController mediaController = new MediaController(this);
        videoView.setMediaController(mediaController);
        mediaController.setAnchorView(videoView);
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            public void onPrepared(MediaPlayer arg0) {
                progressBar.setVisibility(View.INVISIBLE);
                videoView.start();

            }
        });

//            progressBar.setVisibility(View.INVISIBLE);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // perform your action here
        Intent intent=new Intent(CameraActivity.this,ClientWorkActivity.class);
        startActivity(intent);

    }
}