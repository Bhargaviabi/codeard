package com.jansen.sander.arduinorgb;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.SeekBar;

import com.jansen.sander.arduinorgb.databinding.ActivityMainBinding;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private final static int VIBRATION_TIME = 100;

    private SharedPreferences sharedPref;
    private String macArduino;

    private ActivityMainBinding mainBinding;
    private Vibrator vibrator;

    private Snackbar snackbar;

    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothSocket mmSocket;
    private BluetoothDevice mmDevice;
    private OutputStream outputStream;
    private IntentFilter bluetoothFilter;
    private ArrayList<BTDevice> discoveredBluetoothDevices = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        snackbar = Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content), "", Snackbar.LENGTH_LONG);

        bindButtons();
        bindColorSliders();
    }

    protected void onStart() {
        super.onStart();
        sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        macArduino = sharedPref.getString(SettingsActivity.MAC_ARDUINO, "98:D3:32:11:02:9D");
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        // Register for broadcasts when a device is discovered.
        bluetoothFilter = new IntentFilter();
        bluetoothFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        bluetoothFilter.addAction(BluetoothDevice.ACTION_FOUND);
        bluetoothFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);

        registerReceiver(mReceiver, bluetoothFilter);

        initBluetooth();
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mReceiver, bluetoothFilter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        if (id == R.id.action_load_color) {
            startActivity(new Intent(this, ColorActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void bindButtons(){
        mainBinding.contentMain.fabBRUP.setOnClickListener(fabListener);
        mainBinding.contentMain.fabBRDO.setOnClickListener(fabListener);
        mainBinding.contentMain.fabOFF.setOnClickListener(fabListener);
        mainBinding.contentMain.fabON.setOnClickListener(fabListener);

        mainBinding.contentMain.fabRED.setOnClickListener(fabListener);
        mainBinding.contentMain.fabGREEN.setOnClickListener(fabListener);
        mainBinding.contentMain.fabBLUE.setOnClickListener(fabListener);
        mainBinding.contentMain.fabWHITE.setOnClickListener(fabListener);

        mainBinding.contentMain.fabORANGE.setOnClickListener(fabListener);
        mainBinding.contentMain.fabDARKYELLOW.setOnClickListener(fabListener);
        mainBinding.contentMain.fabYELLOW.setOnClickListener(fabListener);
        mainBinding.contentMain.fabSTRAWYELLOW.setOnClickListener(fabListener);

        mainBinding.contentMain.fabPEAGREEN.setOnClickListener(fabListener);
        mainBinding.contentMain.fabCYAN.setOnClickListener(fabListener);
        mainBinding.contentMain.fabLIGHTBLUE.setOnClickListener(fabListener);
        mainBinding.contentMain.fabSKYBLUE.setOnClickListener(fabListener);

        mainBinding.contentMain.fabDARKBLUE.setOnClickListener(fabListener);
        mainBinding.contentMain.fabDARKPINK.setOnClickListener(fabListener);
        mainBinding.contentMain.fabPINK.setOnClickListener(fabListener);
        mainBinding.contentMain.fabPURPLE.setOnClickListener(fabListener);

        mainBinding.contentMain.fabFLASH.setOnClickListener(fabListener);
        mainBinding.contentMain.fabSTROBE.setOnClickListener(fabListener);
        mainBinding.contentMain.fabFADE.setOnClickListener(fabListener);
        mainBinding.contentMain.fabSMOOTH.setOnClickListener(fabListener);

        mainBinding.contentMain.fabDelay.setOnClickListener(fabListener);
        mainBinding.contentMain.fabColor.setOnClickListener(fabListener);
        mainBinding.contentMain.fabBeat.setOnClickListener(fabListener);
    }

    private void bindColorSliders(){
        mainBinding.contentMain.sliderRed.setOnSeekBarChangeListener(slideListener);
        mainBinding.contentMain.sliderGreen.setOnSeekBarChangeListener(slideListener);
        mainBinding.contentMain.sliderBlue.setOnSeekBarChangeListener(slideListener);
    }

    private void vibrate(){
        if(sharedPref.getBoolean(SettingsActivity.ENABLE_HAPTIC_FEEDBACK, true)) {
            vibrator.vibrate(VIBRATION_TIME);
        }
    }

    protected OnClickListener fabListener = new FloatingActionButton.OnClickListener(){
        @Override
        public void onClick(View v) {
            if (v.getContentDescription() != null){
                try {
                    write(String.format(getResources().getString(R.string.ir_value), v.getContentDescription()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.d("IR-VALUE", v.getContentDescription().toString());
            }
            vibrate();
        }
    };

    protected SeekBar.OnSeekBarChangeListener slideListener = new SeekBar.OnSeekBarChangeListener() {
        int red, green, blue;

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            red     = mainBinding.contentMain.sliderRed.getProgress();
            green   = mainBinding.contentMain.sliderGreen.getProgress();
            blue    = mainBinding.contentMain.sliderBlue.getProgress();

            mainBinding.contentMain.fabColor.setBackgroundTintList(ColorStateList.valueOf(Color.rgb(red, green, blue)));
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            red     = mainBinding.contentMain.sliderRed.getProgress();
            green   = mainBinding.contentMain.sliderGreen.getProgress();
            blue    = mainBinding.contentMain.sliderBlue.getProgress();

            mainBinding.contentMain.fabColor.setBackgroundTintList(ColorStateList.valueOf(Color.rgb(red, green, blue)));

            Log.d("COLOR", String.format(getResources().getString(R.string.color_value), red, green, blue));
        }
    };


    private void listBluetoothDevices(){

    }

    private void initBluetooth(){
        if (checkBluetoothCompatibility()){
            enableBluetooth();
            if(queryPairedDevices(macArduino) != null) {
                new Thread(new Runnable() {
                    public void run() {
                        connectThread(mmDevice);
                    }
                }).start();
            }
            else {
                discoverBluetoothDevices();
            }
        } else {
            snackbar.setText(R.string.btNotSupported).show();
        }
    }

    private boolean checkBluetoothCompatibility(){
        if (mBluetoothAdapter != null){
            return true;
        }
        return false;
    }

    private void enableBluetooth(){
        final int REQUEST_ENABLE_BT = 1;
        if (!mBluetoothAdapter.isEnabled()){
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }

    private void enableDiscoverability(){
        Intent discoverableIntent =
                new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
        startActivity(discoverableIntent);
    }

    private BluetoothDevice queryPairedDevices(String macArduino){
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

        if (pairedDevices.size() > 0) {
            // There are paired devices. Get the name and address of each paired device.
            for (BluetoothDevice device : pairedDevices) {
                if(macArduino.equalsIgnoreCase(device.getAddress())){
                    mmDevice = device;
                    return device;
                }
            }
        }
        return null;
    }

    private void discoverBluetoothDevices(){
        if (mBluetoothAdapter.isDiscovering()) {
            // cancel the discovery if it has already started
            mBluetoothAdapter.cancelDiscovery();
        }
        enableDiscoverability();
        if (mBluetoothAdapter.startDiscovery()) {
            // bluetooth has started discovery
        }
    }

    // Create a BroadcastReceiver for ACTION_FOUND.
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Discovery has found a device. Get the BTDevice
                // object and its info from the Intent.
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
                discoveredBluetoothDevices.add(new BTDevice(deviceHardwareAddress, deviceName));
                Log.e("BT","found one");
            }
            else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {

                Log.d("Discovery","Finished");
                for (BTDevice devX : discoveredBluetoothDevices){
                    Log.e("mac", devX.getMacAddress());
                }

            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Don't forget to unregister the ACTION_FOUND receiver.
        unregisterReceiver(mReceiver);
    }

    public void connectThread (BluetoothDevice device){
        BluetoothSocket tmp = null;
        mmDevice = device;

        try {
            // Get a BluetoothSocket to connect with the given BluetoothDevice.
            // MY_UUID is the app's UUID string, also used in the server code.
            tmp = device.createRfcommSocketToServiceRecord(mmDevice.getUuids()[0].getUuid());
        } catch (IOException e) {
            Log.e("Bluetooth", "Socket's create() method failed", e);
        }
        mmSocket = tmp;
        run();
    }

    public void run() {
        // Cancel discovery because it otherwise slows down the connection.
        mBluetoothAdapter.cancelDiscovery();

        try {
            // Connect to the remote device through the socket. This call blocks
            // until it succeeds or throws an exception.
            mmSocket.connect();
            outputStream = mmSocket.getOutputStream();
            mmSocket.getInputStream();
            snackbar.setText(R.string.connected).show();
        } catch (IOException connectException) {
            // Unable to connect; close the socket and return.
            try {
                mmSocket.close();
            } catch (IOException closeException) {
                Log.e("Bluetooth", "Could not close the client socket", closeException);
            }
        }

        // The connection attempt succeeded. Perform work associated with
        // the connection in a separate thread.
    }

    public void write(String s) throws IOException {
        try {
            if (outputStream != null) {
                Log.v("Data", s);
                outputStream.write(s.getBytes());
            } else{
                //snackbar.setText(R.string.notPairedConnected).show();
                if (mmSocket != null){
                    cancel();
                }
                connectThread(mmDevice);
            }
        } catch (Exception e){
            if (mmSocket != null){
                cancel();
            }
        }
    }

    // Closes the client socket and causes the thread to finish.
    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) {
            Log.e("Bluetooth", "Could not close the client socket", e);
        }
    }
}
