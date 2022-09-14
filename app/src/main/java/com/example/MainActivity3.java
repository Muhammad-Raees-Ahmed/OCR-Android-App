package com.example;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.SparseArray;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.ocrapplication.CameraActivity;
import com.example.ocrapplication.MainActivity;
import com.example.ocrapplication.R;
import com.example.ocrapplication.VideoActivity;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.IOException;

public class MainActivity3 extends AppCompatActivity {

    Button video,camera;
    private static final int VIDEO_CAPTURE = 101;
    ProgressBar progressBar;
    private int PERMISSIONS = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);


        // permissions

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.CAMERA
            }, PERMISSIONS);
        }

        video=findViewById(R.id.btn_video);
        camera=findViewById(R.id.btn_camera);
        progressBar=findViewById(R.id.progress3);

        video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent send = new Intent(MainActivity3.this, VideoActivity.class);
                startActivity(send);
            }
        });

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               imagepicker();

            }
        });
    }
    public void imagepicker() {
        ImagePicker.with(this)
                                //Crop image(Optional), Check Customization for more option
                .compress(1024)            //Final image size will be less than 1 MB(Optional)
                .maxResultSize(1080, 1080)    //Final image resolution will be less than 1080 x 1080(Optional)
                .start();

        progressBar.setVisibility(View.VISIBLE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (resultCode == Activity.RESULT_OK) {
            //Image Uri will not be null for RESULT_OK
            Uri resultUri = data.getData();
//            Toast.makeText(this, resultUri.toString(), Toast.LENGTH_SHORT).show();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), resultUri);
//                Toast.makeText(this, bitmap.toString(), Toast.LENGTH_SHORT).show();
                getTextFromImage(bitmap);
//
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.INVISIBLE);
        }


    }
    private void getTextFromImage(Bitmap bitmap) {

        try {


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
                String name = stringBuilder.toString().toLowerCase();
//            editText2.setText(stringBuilder.toString());
//            textView.setText(stringBuilder.toString());
                System.out.println("name : " + name);
//                Toast.makeText(this, name, Toast.LENGTH_SHORT).show();
                try{
                    Intent send = new Intent(MainActivity3.this, CameraActivity.class);
                    send.putExtra("path", name);
                    progressBar.setVisibility(View.INVISIBLE);
                startActivity(send);
                }catch (Exception e){
                    Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
                }

            }

        } catch (Exception e) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }



}