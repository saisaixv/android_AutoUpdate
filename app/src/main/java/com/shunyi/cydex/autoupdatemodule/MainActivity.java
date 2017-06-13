package com.shunyi.cydex.autoupdatemodule;

import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnStart= (Button) findViewById(R.id.btn_start);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,DownloadService.class);
                intent.putExtra("path","http://dorecast.com/static2/cydex_download/cydex-android.apk");
                intent.putExtra("savepath", Environment.getExternalStorageDirectory().getAbsolutePath()+"/Cydex.apk");
                startService(intent);
            }
        });
        Button btnExit= (Button) findViewById(R.id.btn_exit);
        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.exit(0);
            }
        });


    }
}
