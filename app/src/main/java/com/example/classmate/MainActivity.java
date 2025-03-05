package com.example.classmate;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.opencv.android.OpenCVLoader;


public class MainActivity extends AppCompatActivity {

    static{
        if(OpenCVLoader.initDebug()){
            Log.d("MainActivity:","OpenCV is loaded");
        }else{
            Log.d("MainActivity:","OpenCV failed to loaded");

        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    /*
     *  Launch Different Activity from Dashboard
     */
    public void launchScan(View v){
        Intent i = new Intent(this,ScanActivity.class);
        startActivity(i);
    }
    public void launchView(View v){
        Intent i = new Intent(this,ViewActivity.class);
        startActivity(i);
    }
    public void launchEdit(View v){
        Intent i = new Intent(this,EditActivity.class);
        startActivity(i);
    }


}