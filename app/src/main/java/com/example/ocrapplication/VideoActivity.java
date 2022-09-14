package com.example.ocrapplication;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.ocrapplication.Model.FirebaseModel;

import java.io.IOException;

public class VideoActivity extends AppCompatActivity {

    Button btnr, btns;
    private static final int VIDEO_CAPTURE = 101;
    String path = "";
    EditText editText1;
    ProgressBar progressBar;
    FirebaseModel firebaseModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        btns = findViewById(R.id.save2);
        btnr = findViewById(R.id.record2);
        editText1 = findViewById(R.id.name1);
        progressBar = findViewById(R.id.progress2);
        firebaseModel = FirebaseModel.getInstance();


        btnr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               chooseOption();
            }
        });
        btns.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validate();
            }
        });


    }
    private void chooseOption() {
        final CharSequence[] options = {"Record Video", "Choose from Gallery", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(VideoActivity.this);
        builder.setItems(options, (dialog, item) -> {
            if (options[item].equals("Record Video")) {
//                Intent intent = new Intent("android.media.action.VIDEO_CAPTURE");
//                intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 120);
//                startActivityForResult(intent, 1);
                Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 120);
                startActivityForResult(intent, VIDEO_CAPTURE);
            } else if (options[item].equals("Choose from Gallery")) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
                intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 120);
                startActivityForResult(intent, 2);
            } else if (options[item].equals("Cancel")) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (resultCode == RESULT_OK && requestCode == VIDEO_CAPTURE) {
            path = String.valueOf(data.getData());
            System.out.println("video path :" + path);

//            Toast.makeText(this, path, Toast.LENGTH_SHORT).show();
        }
        else if (resultCode == RESULT_OK && requestCode == 2){
            path = String.valueOf(data.getData());
            System.out.println("video path :" + path);
        }

    }

    public void validate() {
        String name = editText1.getText().toString().toLowerCase();
        String videoPath = path;

//        Toast.makeText(this, name, Toast.LENGTH_SHORT).show();
//        Toast.makeText(this, videoPath, Toast.LENGTH_SHORT).show();

        if (name.equals("") & path.equals("")) {
            Toast.makeText(this, "data not uploaded “\n” plz fill all fields", Toast.LENGTH_LONG).show();
        } else if (name.equals("") || videoPath.equals("")) {
            Toast.makeText(this, " plz fill all fields", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Plz Wait", Toast.LENGTH_LONG).show();
            try {

                progressBar.setVisibility(View.VISIBLE);
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                firebaseModel.storeVideo2(this, name, videoPath);
            } catch (Exception e) {
                System.out.println("alert :" + e);
            }

        }

    }

    public void updateUI(boolean isSuccess) {
        if (isSuccess) {
            progressBar.setVisibility(View.INVISIBLE);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            Toast.makeText(this, "Video Uploaded", Toast.LENGTH_SHORT).show();

        } else {
            Toast.makeText(VideoActivity.this, "Network Error", Toast.LENGTH_SHORT).show();
        }
    }
}