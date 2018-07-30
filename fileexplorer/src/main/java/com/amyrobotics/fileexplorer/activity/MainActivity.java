package com.amyrobotics.fileexplorer.activity;


import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v13.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import com.amyrobotics.fileexplorer.R;

public class MainActivity extends AppCompatActivity {

    private ImageView imageView;

    private int orientation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        orientation=getResources().getConfiguration().orientation;
        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[] { android.Manifest.permission.WRITE_EXTERNAL_STORAGE }, 1);
        }else{
            startApp();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                startApp();
            }else{
                Toast.makeText(this, "本程序需要读写磁盘权限", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private void startApp(){

        imageView = findViewById(R.id.main_img);
//        if (orientation == Configuration.ORIENTATION_LANDSCAPE){
//            imageView.setImageResource(R.drawable.main_img2);
//        }else{
//            imageView.setImageResource(R.drawable.xiaoan);
//        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(MainActivity.this, FileExplorerTabActivity.class);
                startActivity(i);
                finish();
            }
        },2000);

    }
}
