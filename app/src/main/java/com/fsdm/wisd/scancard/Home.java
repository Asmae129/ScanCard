package com.fsdm.wisd.scancard;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

import static android.content.ContentValues.TAG;

public class Home extends AppCompatActivity {

    DatabaseHelper databaseHelper;
    private String deviceName = null;
    private String deviceAddress;
    String userUid=null;
    public static Handler handler;
    public static BluetoothSocket mmSocket;
    public static ConnectedThread connectedThread;
    public static CreateConnectThread createConnectThread;
    RecyclerView recyclerView;
    ArrayList<UserLog> userLogList;
    UserLogAdapter userLogAdapter;
    private final static int CONNECTING_STATUS = 1; // used in bluetooth handler to identify message status
    private final static int MESSAGE_READ = 2; // used in bluetooth handler to identify message update

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        recyclerView=findViewById(R.id.list);
        //******************* init data*******************
        databaseHelper=new DatabaseHelper(this,DatabaseHelper.DATABASE_NAME,null,1);
        userLogList=new ArrayList<UserLog>();

        userLogList.addAll(databaseHelper.getUsersLog());
        //  System.out.println("****************************************************************"+l.get(10).getDate());
        //  System.out.println("****************************************************************"+l.get(11).getDate());
        userLogAdapter=new UserLogAdapter(Home.this,userLogList);
        recyclerView.setAdapter(userLogAdapter);
        userLogAdapter.notifyDataSetChanged();
        // UI Initialization
        final Button buttonConnect = findViewById(R.id.buttonConnect);
        final Button addUser = findViewById(R.id.user);
        final Toolbar toolbar = findViewById(R.id.toolbar);
        final ProgressBar progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
        final TextView textViewInfo = findViewById(R.id.textViewInfo);

        // If a bluetooth device has been selected from SelectDeviceActivity
        deviceName = getIntent().getStringExtra("deviceName");
        Log.d("Status","name is : " + deviceName);
        if (deviceName != null){
            // Get the device address to make BT Connection
            deviceAddress = getIntent().getStringExtra("deviceAddress");
            // Show progree and connection status
            toolbar.setSubtitle("Connecting to " + deviceName + "...");
            progressBar.setVisibility(View.VISIBLE);
            buttonConnect.setEnabled(false);
            Log.d("Status","address is : " + deviceAddress);
            /*
            This is the most important piece of code. When "deviceName" is found
            the code will call a new thread to create a bluetooth connection to the
            selected device (see the thread code below)
             */
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            createConnectThread = new CreateConnectThread(bluetoothAdapter,deviceAddress);
            createConnectThread.start();
        }


        /*
        Second most important piece of Code
         */
        handler = new Handler(Looper.getMainLooper()) {



            Date date=new Date();
            String dateString=date.toString();
            @SuppressLint("SetTextI18n")
            @Override
            public void handleMessage(Message msg){
                switch (msg.what){
                    case CONNECTING_STATUS:
                        switch(msg.arg1){
                            case 1:
                                toolbar.setSubtitle("Connected to " + deviceName);
                                progressBar.setVisibility(View.GONE);
                                buttonConnect.setEnabled(true);
                                break;
                            case -1:
                                toolbar.setSubtitle("Device fails to connect");
                                progressBar.setVisibility(View.GONE);
                                buttonConnect.setEnabled(true);
                                break;
                        }
                        break;
/************************************  uid from rfid   *************
 ****************************************************
 **************************************************/
                    case MESSAGE_READ:
                        String deviceMsg = msg.obj.toString();
                        textViewInfo.setText("UID : " + deviceMsg);
                        userUid=deviceMsg;
                        User user=databaseHelper.validUser(userUid);
                        if(user!=null) {
                            databaseHelper.addNewLog(userUid,user.getName(),dateString,1);
                            //write to bluetooth
                        }
                        else {
                            databaseHelper.addNewLog(userUid,user.getName(),dateString,0);
                        }
                        break;
                }
            }
        };

        // Select Bluetooth Device
        buttonConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Home.this, SelectDeviceActivity.class);
                startActivity(intent);
            }
        });
        //******** add  user to db

        addUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Home.this, AddUserActivity.class);
                startActivity(intent);
            }
        });

    }

    /* ============================ Thread to Create Bluetooth Connection =================================== */
    public static class CreateConnectThread extends Thread {

        public CreateConnectThread(BluetoothAdapter bluetoothAdapter, String address) {
            // Use a temporary object that is later assigned to mmSocket
            // because mmSocket is final.
            BluetoothDevice bluetoothDevice = bluetoothAdapter.getRemoteDevice(address);
            BluetoothSocket tmp = null;
            UUID uuid = bluetoothDevice.getUuids()[0].getUuid();
////////////////////////aaaaaaaaaaaaaaaa
            try {
                // Get a BluetoothSocket to connect with the given BluetoothDevice.
                // MY_UUID is the app's UUID string, also used in the server code.
                /*
                Due to Android device varieties,the method below may not work fo different devices.
                You should try using other methods i.e. :
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
                 */
                tmp = bluetoothDevice.createInsecureRfcommSocketToServiceRecord(uuid);
                if(tmp!=null){
                    Log.d("Status","uuid : " + uuid);
                }

            } catch (IOException e) {
                Log.e("Status", "Socket's create() method failed", e);
            }
            mmSocket = tmp;
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        public void run() {
            // Cancel discovery because it otherwise slows down the connection.
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            bluetoothAdapter.cancelDiscovery();
            try {
                // Connect to the remote device through the socket. This call blocks
                // until it succeeds or throws an exception.
                Log.e("Status", "Connecting...");
                mmSocket.connect();
                Log.e("Status", "Device connected");
                handler.obtainMessage(CONNECTING_STATUS, 1, -1).sendToTarget();
            } catch (IOException connectException) {
                // Unable to connect; close the socket and return.
                try {
                    mmSocket.close();
                    Log.e("Status", "Cannot connect to device..");
                    handler.obtainMessage(CONNECTING_STATUS, -1, -1).sendToTarget();
                } catch (IOException closeException) {
                    Log.e("Status", "Could not close the client socket", closeException);
                }
                return;
            }

            // The connection attempt succeeded. Perform work associated with
            // the connection in a separate thread.
            connectedThread = new ConnectedThread(mmSocket);
            connectedThread.run();
        }

        // Closes the client socket and causes the thread to finish.
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Could not close the client socket", e);
            }
        }
    }

    /* =============================== Thread for Data Transfer =========================================== */
    public static class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;
        private  boolean createString = false;
        private String readMessage;


        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        public void run() {
            byte[] buffer = new byte[1024];  // buffer store for the stream
            int bytes = 0; // bytes returned from read()
            // Keep listening to the InputStream until an exception occurs
            while (true) {
                try {
                    // Read from the InputStream
                    buffer[bytes] = (byte) mmInStream.read();
                    String readByte = new String(buffer, StandardCharsets.UTF_8).substring(0,1);

                    //**   here we can add state byte
                    switch (readByte){
                        case "<":
                            createString = true;
                            readMessage = "";
                            break;
                        case ">":
                            createString = false;
                            break;
                    }

                    //
                    if (createString){
                        readMessage = readMessage + readByte;
                    } else {
                        Log.e("UID Length", readMessage.length() + " characters");
                        String readUID = readMessage.substring(1).trim();
                        handler.obtainMessage(MESSAGE_READ,readUID).sendToTarget();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
            }
        }

        /* Call this from the main activity to send data to the remote device */
        public void write(String input) {
            byte[] bytes = input.getBytes(); //converts entered String into bytes
            try {
                mmOutStream.write(bytes);
            } catch (IOException e) {
                Log.e("Send Error","Unable to send message",e);
            }
        }

        /* Call this from the main activity to shutdown the connection */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }
    }

    /* ============================ Terminate Connection at BackPress ====================== */
    @Override
    public void onBackPressed() {
        if (createConnectThread != null){
            createConnectThread.cancel();
        }
        finish();
    }
}
