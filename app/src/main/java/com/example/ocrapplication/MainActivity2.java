package com.example.ocrapplication;

import static com.example.ocrapplication.MainActivity.url;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.VideoView;

public class MainActivity2 extends AppCompatActivity {

    VideoView videoView;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        try {
            this.getSupportActionBar().hide();
        } catch (NullPointerException e) {
        }

        progressBar = findViewById(R.id.progress);
        progressBar.setVisibility(View.VISIBLE);
        playVideo();


    }

    public void playVideo() {
        videoView = findViewById(R.id.fullscreen_videoview);
        videoView.setVideoPath(url);
        MediaController mediaController = new MediaController(this);
        videoView.setMediaController(mediaController);
        mediaController.setAnchorView(videoView);
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            public void onPrepared(MediaPlayer arg0) {
                progressBar.setVisibility(View.INVISIBLE);
                videoView.pause();
            }
        });
        videoView.pause();
//            progressBar.setVisibility(View.INVISIBLE);
    }
}