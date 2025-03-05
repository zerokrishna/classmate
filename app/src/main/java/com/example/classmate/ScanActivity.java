package com.example.classmate;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.Manifest;
import android.widget.TextView;

import com.example.classmate.ml.MobilenetV110224Quant;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;
import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class ScanActivity extends AppCompatActivity {
    private final int IMAGE_PICK = 100;
    private ImageView imageView;
    Bitmap bitmap;

    TextView result;
 // Declare labels at class level
     private String[] labels;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_scan);

        result = findViewById(R.id.textViewScan);


        loadLabels(); // Load the labels before using them



        // Get permission of camera
        getPermission();



        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        imageView = findViewById(R.id.imageView1); // Reference to the ImageView


    }
    private void loadLabels() {
        List<String> labelList = new ArrayList<>();
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(getAssets().open("labels.txt")));
            String line;

            while ((line = bufferedReader.readLine()) != null) {
                labelList.add(line);
            }

            bufferedReader.close(); // Always close the reader
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Convert List to Array and assign it to the class variable
        labels = labelList.toArray(new String[0]);
    }

    public void selectImage(View view){
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK);
    }
    public void captureImage(View view){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, 12);
    }
    public void scanImage(View view){

        try {

        MobilenetV110224Quant model = MobilenetV110224Quant.newInstance(ScanActivity.this);

        // Creates inputs for reference.
        TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 224, 224, 3}, DataType.UINT8);
        bitmap = Bitmap.createScaledBitmap(bitmap,224,224,true);
        inputFeature0.loadBuffer(TensorImage.fromBitmap(bitmap).getBuffer());

        // Runs model inference and gets result.
        MobilenetV110224Quant.Outputs outputs = model.process(inputFeature0);
        TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

            if (labels != null && labels.length > 0) {  // Check if labels are loaded
                int maxIndex = getMax(outputFeature0.getFloatArray());
                if (maxIndex >= 0 && maxIndex < labels.length) {  // Prevent index out of bounds
                    result.setText(labels[maxIndex]);
                } else {
                    result.setText("Unknown Label");
                }
            } else {
                result.setText("Labels not loaded");
            }


        // Releases model resources if no longer used.
        model.close();
    } catch (IOException e) {
        // TODO Handle the exception
    }

    }
    int getMax(float[] arr){
        int max =0;
        for (int i=0;i<arr.length;i++){
            if(arr[i]>arr[max]){
                max=i;
            }
        }
        return max;
    }
    void getPermission(){
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(ScanActivity.this , new String[]{Manifest.permission.CAMERA} , 11);
            }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults, int deviceId) {
        if(requestCode==11){
            if(grantResults.length > 0){
                if(grantResults[0] != PackageManager.PERMISSION_GRANTED){
                    this.getPermission();
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults, deviceId);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_PICK && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData(); // Get the image URI
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver() , uri);
                // Display the image in ImageView
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else if (requestCode == 12) {

            bitmap = (Bitmap) Objects.requireNonNull(data.getExtras()).get("data");
            imageView.setImageBitmap(bitmap);
        }
    }


}