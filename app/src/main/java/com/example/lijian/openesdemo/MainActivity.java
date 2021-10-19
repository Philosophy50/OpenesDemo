package com.example.lijian.openesdemo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.lijian.openesdemo.ui.Animation2dActivity;

public class MainActivity extends AppCompatActivity {


    Button btn1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // ActionInstance.getInstance().setContext(getApplicationContext());
        setContentView(R.layout.activity_main);
        BitmapList.init(this);

        btn1 = (Button) findViewById(R.id.main_btn_2Hello);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             //   Intent j = new Intent(MainActivity.this,HelloTriangle2.class);

            Intent j = new Intent(MainActivity.this,Animation2dActivity.class);
                startActivity(j);
            }
        });
//        rewardCompareThread.run();
    }
}
