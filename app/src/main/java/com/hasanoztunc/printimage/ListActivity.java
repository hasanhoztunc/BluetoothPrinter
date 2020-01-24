package com.hasanoztunc.printimage;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.UUID;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class ListActivity extends AppCompatActivity {

    BluetoothDevice device;
    BluetoothAdapter mbluetoothAdapter;
    BluetoothSocket mbtSocket;
    ListView lwDevices;
    ArrayAdapter arrayAdapter;
    final ArrayList<String> arrayDevice=new ArrayList<>();
    final ArrayList<BluetoothDevice> deviceList=new ArrayList<>();
    Context context = this;
    private static OutputStream outputStream;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        lwDevices=(ListView)findViewById(R.id.lwDevices);

        mbluetoothAdapter=BluetoothAdapter.getDefaultAdapter();

        arrayAdapter=new ArrayAdapter(this,android.R.layout.simple_list_item_1,arrayDevice);
        lwDevices.setAdapter(arrayAdapter);

        try {
            bluetoothScanning();
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        lwDevices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view,final int i, long l) {

                if(mbluetoothAdapter.isDiscovering()){
                    mbluetoothAdapter.cancelDiscovery();
                }

                Toast.makeText(getApplicationContext(),
                        "Connecting to "+arrayDevice.get(i).toString(),
                        Toast.LENGTH_SHORT
                        ).show();

                Thread connectThread=new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            boolean gotuuid = deviceList.get(i)
                                    .fetchUuidsWithSdp();
                            UUID uuid = deviceList.get(i).getUuids()[0]
                                    .getUuid();
                            mbtSocket = deviceList.get(i)
                                    .createRfcommSocketToServiceRecord(uuid);

                            mbtSocket.connect();
                        }catch (IOException e){
                            runOnUiThread(socketErrorRunnable);
                            try{
                                mbtSocket.close();
                            }catch (IOException ex){
                                ex.printStackTrace();
                            }
                            mbtSocket=null;
                        }finally{
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    finish();
                                }
                            });
                        }
                    }
                });
                connectThread.start();

                AlertDialog.Builder alertPrint=new AlertDialog.Builder(getApplicationContext());
                alertPrint.setTitle("Print Image");
                alertPrint.setMessage("Do you want to print image?");
                alertPrint.setNegativeButton("No",null);
                alertPrint.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // TODO: 23.01.2020 Image print edilip edilmeyecegini sor
                        printPhoto(R.drawable.gib);
                    }
                });
            }
        });
    }

    private Runnable socketErrorRunnable = new Runnable() {

        @Override
        public void run() {
            Toast.makeText(getApplicationContext(),
                    "Cannot establish connection", Toast.LENGTH_SHORT).show();
            mbluetoothAdapter.startDiscovery();
        }
    };

    void bluetoothScanning() {
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        context.registerReceiver(receiver, filter);
        mbluetoothAdapter.startDiscovery();
    }

    public final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address

                Log.i("Device Name: ", "device " + deviceName);
                Log.i("deviceHardwareAddress ", "hard" + deviceHardwareAddress);

                if(!arrayDevice.contains(deviceName)&& deviceName!=null){
                    arrayDevice.add(deviceName);
                    deviceList.add(device);
                    arrayAdapter.notifyDataSetChanged();
                }
            }
        }
    };

    public void printPhoto(int img) {
        try {
            Bitmap bmp = BitmapFactory.decodeResource(getResources(),
                    img);
            if(bmp!=null){
                byte[] command = Utils.decodeBitmap(bmp);
                outputStream.write(PrinterCommands.ESC_ALIGN_CENTER);
                printText(command);
            }else{
                Log.e("Print Photo error", "the file isn't exists");
                Toast.makeText(getApplicationContext(), "DANGERRRRR", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("PrintTools", "the file isn't exists");
        }
    }

    private void printText(byte[] msg) {
        try {
            // Print normal text
            outputStream.write(msg);
            printNewLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void printNewLine() {
        try {
            outputStream.write(PrinterCommands.FEED_LINE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
