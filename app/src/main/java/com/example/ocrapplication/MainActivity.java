package com.example.ocrapplication;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.ocrapplication.Model.FirebaseModel;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    Button btn_recordVideo, btn_uploadData, btn_takephoto;
    VideoView videoView;
    String path = "";
    EditText editText;
    FirebaseModel firebaseModel;
    ProgressDialog mProgressDialog;
    TextView textView;
    private static final int VIDEO_CAPTURE = 101;
    private int PERMISSIONS = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseModel = FirebaseModel.getInstance();
        textView = findViewById(R.id.input_text);
        videoView = findViewById(R.id.video_view);
        editText = findViewById(R.id.name);
        btn_recordVideo = findViewById(R.id.btn_record);
        btn_uploadData = findViewById(R.id.btn_upload);
        btn_takephoto = findViewById(R.id.btn_captureImage);
        btn_takephoto.setOnClickListener(this);
        btn_recordVideo.setOnClickListener(this);
        btn_uploadData.setOnClickListener(this);
        // permissions

        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                    Manifest.permission.CAMERA
            }, PERMISSIONS);
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_record:
                Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 120);
                startActivityForResult(intent, VIDEO_CAPTURE);
                break;
            case R.id.btn_upload:
                validate();
                break;
            case R.id.btn_captureImage:
                imagepicker();
                break;
        }
    }

    public void imagepicker() {
        ImagePicker.with(this)
                .crop()                    //Crop image(Optional), Check Customization for more option
                .compress(1024)            //Final image size will be less than 1 MB(Optional)
                .maxResultSize(1080, 1080)    //Final image resolution will be less than 1080 x 1080(Optional)
                .start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (resultCode == RESULT_OK && requestCode == VIDEO_CAPTURE) {
            path = String.valueOf(data.getData());
            System.out.println("video path :" + path);
            videoView.setVideoPath(String.valueOf(data.getData()));
            MediaController mediaController = new MediaController(this);
            videoView.setMediaController(mediaController);
            mediaController.setAnchorView(videoView);
            videoView.pause();
            Toast.makeText(this, path, Toast.LENGTH_SHORT).show();
        }
//        // check image
        else if (resultCode == Activity.RESULT_OK && requestCode != VIDEO_CAPTURE) {
            //Image Uri will not be null for RESULT_OK
            Uri resultUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), resultUri);
                getTextFromImage(bitmap);
//
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private void getTextFromImage(Bitmap bitmap) {
        TextRecognizer textRecognize = new TextRecognizer.Builder(this).build();
        if (!textRecognize.isOperational()) {
            Toast.makeText(this, "Error Occurred", Toast.LENGTH_SHORT).show();
        } else {
            Frame frame = new Frame.Builder().setBitmap(bitmap).build();
            SparseArray<TextBlock> textBlockSparseArray = textRecognize.detect(frame);
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < textBlockSparseArray.size(); i++) {
                TextBlock textBlock = textBlockSparseArray.valueAt(i);
                stringBuilder.append(textBlock.getValue());
            }
            textView.setText(stringBuilder.toString());
        }
    }

    public void validate() {
        String name = editText.getText().toString();
        String videoPath = path;

        if (name.equals("") & path.equals("")) {
            Toast.makeText(this, "data not uploaded “\n” plz fill all fields", Toast.LENGTH_LONG).show();
        } else if (name.equals("") || videoPath.equals("")) {
            Toast.makeText(this, " plz fill all fields", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "good", Toast.LENGTH_SHORT).show();
            try {
                mProgressDialog = new ProgressDialog(this);
                mProgressDialog.setIndeterminate(true);
                mProgressDialog.setMessage("Uploading...");
                mProgressDialog.setProgressStyle(ProgressDialog.BUTTON_POSITIVE);
                mProgressDialog.show();
                firebaseModel.storeVideo(this, name, videoPath);
            } catch (Exception e) {
                System.out.println("alert :" + e);
            }

        }

    }

    public void updateUI(boolean isSuccess) {
        if (isSuccess) {
            mProgressDialog.dismiss();
            Toast.makeText(this, "Video Uploaded", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(MainActivity.this, "Network Error", Toast.LENGTH_SHORT).show();
        }
    }
}