package com.example.radrajith.laser_blu_upgraded;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.PointsGraphSeries;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by radrajith on 1/7/2017.
 */
public class SecondActivity extends Activity{
    PointsGraphSeries<DataPoint> series;
    private Button settingsButton;
    TextView objTemp;
    Thread thread;
    private int bytesRead;
    private int count = 0;
    private boolean run = true;
    double buff = 0;
    private Handler myHandler = new Handler();
    OutputStream myOutput;
    InputStream myInput;
    final byte[] buffer = new byte[9];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
//        settingsButton = (Button) findViewById(R.id.settingsButton);
//        settingsButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(SecondActivity.this, MainActivity.class);
//                startActivity(intent);
//            }
//        });
        objTemp = (TextView)findViewById(R.id.objTempText);
        //objTemp.setText(this.getIntent().getStringExtra("Temperature"));

        myInput = MainActivity.getStream();
        thread = new Thread(new Runnable() {
            public void run() {
                while (!Thread.currentThread().isInterrupted() && run) {
                    try {
                        bytesRead = myInput.read(buffer);
                        //readData = new String(buffer).split(" | ");
                        //buffer[bytesRead] = '\0';
                        myHandler.post(new Runnable() {
                            public void run() {
                                count++;
                                String bufferData =  new String(buffer).trim();
                                try {
                                    buff = Double.parseDouble(bufferData);
                                }catch (Exception e){ buff = 0;}
                                System.out.println(new String(buffer) + "  " + buff);
                                objTemp.setText(bufferData);
//                                secondActivityIntent.putExtra("Temperature", bufferData);
//                                startActivity(secondActivityIntent);
                                //System.out.println(readData[0].trim());
                                //System.out.println(readData[1].trim());
                                //series.appendData(new DataPoint(count, buff), true ,1000);

                            }
                        });
                        myHandler.obtainMessage(1, bytesRead, -1, buffer).sendToTarget();
                    } catch (IOException e) {
                        Toast.makeText(getApplicationContext(), "Error reading data", Toast.LENGTH_LONG).show();
                        run = false;
                        break;
                    }
                }

            }
        });
        //
        thread.start();
    }

}
