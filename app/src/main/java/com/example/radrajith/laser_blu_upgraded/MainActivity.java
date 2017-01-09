package com.example.radrajith.laser_blu_upgraded;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    private Button closeButton, restartButton, sendButton, loadButton, graphButton, saveButton1;
    private BluetoothAdapter btAdapter;
    private BluetoothSocket mySocket;
    private BluetoothDevice myDevice;
    private String fileoutput = "laserSetting.txt";
    OutputStream myOutput;
    private static InputStream myInput;
    private Handler myHandler = new Handler();
    String period ="10", pulses="2", duty="50", peak="2";
    private EditText periodVal, dutyVal, pulsesVal, peakVal, objectTemp;
    Button graph;
    int periodLimit = 1000;
    int peakLimit = 5;
    int pulsesLimit = 10000;
    int dutyLimit = 100;
    private TextView headingText;
    String deviceName = "HC-06";
    Thread thread;
    private int bytesRead;
    private int count = 0;
    private boolean run = true;
    double buff = 0;
    Intent secondActivityIntent = new Intent(MainActivity.this, SecondActivity.class);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        headingText = (TextView)findViewById(R.id.headingTextView);
        headingText.setText("Disconnected!!!");
        periodVal = ((EditText)findViewById(R.id.periodText));
        periodVal.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                period =  periodVal.getText().toString();
                int temp = Integer.parseInt(period);
                if(temp>periodLimit){
                    periodVal.setText(""+periodLimit);
                    period = ""+periodLimit;
                }

            }
            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        dutyVal = ((EditText)findViewById(R.id.dutyText));
        dutyVal.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                duty =  dutyVal.getText().toString();
                int temp = Integer.parseInt(duty);
                if(temp>dutyLimit){
                    dutyVal.setText(""+dutyLimit);
                    duty = ""+dutyLimit;
                }

            }
            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        pulsesVal = ((EditText)findViewById(R.id.pulsesText));
        pulsesVal.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                pulses =  pulsesVal.getText().toString();
                int temp = Integer.parseInt(pulses);
                if(temp>pulsesLimit){
                    pulsesVal.setText(""+pulsesLimit);
                    pulses = ""+pulsesLimit;
                }

            }
            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        peakVal = ((EditText)findViewById(R.id.peakText));
        peakVal.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                peak =  peakVal.getText().toString();
                int temp = Integer.parseInt(peak);
                if(temp>peakLimit){
                    peakVal.setText(""+peakLimit);
                    peak = ""+peakLimit;
                }

            }
            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        graphButton = (Button) findViewById(R.id.graphButton);
        graphButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SecondActivity.class);
                startActivity(intent);
            }
        });
        sendButton = (Button) findViewById(R.id.sendButtton);
        sendButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sendData(period,duty, pulses, peak);

            }
        });
        saveButton1 = (Button) findViewById(R.id.saveButton1);
        saveButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    OutputStreamWriter fileOut = new OutputStreamWriter(openFileOutput(fileoutput,0));
                    fileOut.write(period+","+duty+","+pulses+","+peak);
                    fileOut.close();
                    Toast.makeText(getApplicationContext(), "Settings Saved", Toast.LENGTH_LONG).show();
                }
                catch (Throwable t){
                    Toast.makeText(getApplicationContext(), "Cannot save setting", Toast.LENGTH_LONG).show();
                }
            }
        });
        loadButton = (Button) findViewById(R.id.loadButton);
        loadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    InputStream fileIn = openFileInput(fileoutput);
                    if (fileIn != null) {
                        InputStreamReader input=new InputStreamReader(fileIn);
                        BufferedReader reader=new BufferedReader(input);
                        String setting = reader.readLine();
                        fileIn.close();
                        int temp1 = setting.indexOf(',');
                        int temp2 = setting.indexOf(',',temp1+1);
                        int temp3 = setting.indexOf(',',temp2+1);
                        periodVal.setText(setting.substring(0,temp1));
                        dutyVal.setText(setting.substring(temp1+1, temp2));
                        pulsesVal.setText(setting.substring(temp2+1, temp3));
                        peakVal.setText(setting.substring(temp3+1, setting.length()));

                    }

                    Toast.makeText(getApplicationContext(), "Settings obtained", Toast.LENGTH_LONG).show();
                }
                catch (Throwable t){
                    Toast.makeText(getApplicationContext(), "Cannot load setting", Toast.LENGTH_LONG).show();
                }
            }
        });
        btConfigure();

    }
    public static InputStream getStream(){
        return myInput;
    }
    public void btConfigure() {
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        if (btAdapter == null) {
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setMessage("Bluetooth adapter not present");
            alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {}
            });
        }

        if (!btAdapter.isEnabled()) {
            Intent btEnable = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(btEnable, 1);
        }
        Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();
        ArrayList devicesArray = new ArrayList();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                devicesArray.add(device.getName() + "\n" + device.getAddress());
                if (device.getName().equals(deviceName)) {
                    myDevice = device;
                    Toast.makeText(getApplicationContext(), "connected to "+deviceName, Toast.LENGTH_LONG).show();
                    openBt();
                    break;
                }
            }
        }
        else{
            dummy();
        }
    }
    public void dummy(){
        //objectTemp.setText("bluetooth not connected");
    }
    public void receiveData(){
        final byte[] buffer = new byte[9];
        run = true;
        headingText.setText("Bluetooth Connected");
        //Toast.makeText(getApplicationContext(), "was here", Toast.LENGTH_LONG).show();
//        thread = new Thread(new Runnable() {
//            public void run() {
//                while (!Thread.currentThread().isInterrupted() && run) {
//                    try {
//                        bytesRead = myInput.read(buffer);
//                        //readData = new String(buffer).split(" | ");
//                        //buffer[bytesRead] = '\0';
//                        myHandler.post(new Runnable() {
//                            public void run() {
//                                count++;
//                                String bufferData =  new String(buffer).trim();
//                                try {
//                                    buff = Double.parseDouble(bufferData);
//                                }catch (Exception e){ buff = 0;}
//                                System.out.println(new String(buffer) + "  " + buff);
//                                secondActivityIntent.putExtra("Temperature", bufferData);
//                                startActivity(secondActivityIntent);
//                                //System.out.println(readData[0].trim());
//                                //System.out.println(readData[1].trim());
//                                //objectTemp.setText(bufferData);
//                                //series.appendData(new DataPoint(count, buff), true ,1000);
//                            }
//                        });
//                        myHandler.obtainMessage(1, bytesRead, -1, buffer).sendToTarget();
//                    } catch (IOException e) {
//                        Toast.makeText(getApplicationContext(), "Error reading data", Toast.LENGTH_LONG).show();
//                        run = false;
//                        break;
//                    }
//                }
//
//            }
//        });
//        //
//        thread.start();
    }
    public void openBt() {
        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
        try {
            mySocket = myDevice.createRfcommSocketToServiceRecord(uuid);
            mySocket.connect();
            try {
                myInput = mySocket.getInputStream();
                myOutput = mySocket.getOutputStream();
                Toast.makeText(getApplicationContext(), "BT communication established", Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                System.out.println("error at stream");
            }
            receiveData();
        } catch (IOException e) {
            System.out.println("problem at serversocket creation");
            System.out.println("didnt connect");
            Toast.makeText(getApplicationContext(), "cannot connect", Toast.LENGTH_LONG).show();
            dummy();
            //sendData.setText("Didnt Connect restart app");
        }

    }
    public void sendData(String period, String duty, String pulses, String peak){
        String writeData = period +"," + duty +","+ pulses + ","+peak;
        try {
            myOutput.write(writeData.getBytes());
            Toast.makeText(getApplicationContext(), "Data Sent", Toast.LENGTH_LONG).show();
        }catch (Exception e){
            Toast.makeText(getApplicationContext(), "Error sending data", Toast.LENGTH_LONG).show();
        }
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if(newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){}
        else if(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE){}
    }
}
