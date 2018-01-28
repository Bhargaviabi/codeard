package com.jansen.sander.arduinorgb;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.SeekBar;

import com.jansen.sander.arduinorgb.databinding.ActivityMainBinding;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private static Context mContext;

    private final static int VIBRATION_TIME = 100;
    private int delay = 10;

    private SharedPreferences sharedPref;
    private String macArduino;

    private boolean beatsEnabled = false;

    private static ActivityMainBinding mainBinding;
    private Vibrator vibrator;

    private static Snackbar snackbar;
    private boolean fabLongPressed = false;

    private ListView listDiscoveredDevices;
    private static boolean connected = true;
    private static BluetoothAdapter mBluetoothAdapter;
    private static BluetoothSocket mmSocket;
    private static BluetoothDevice mmDevice;
    private static OutputStream outputStream;
    private IntentFilter bluetoothFilter;
    private ArrayList<BluetoothDevice> discoveredBluetoothDevices = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        snackbar = Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content), "", Snackbar.LENGTH_LONG);
        listDiscoveredDevices = mainBinding.bluetoothResults.list;
        bindButtons();
        bindColorSliders();
    }

    protected void onStart() {
        super.onStart();
        checkLocationPermission();

        sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        macArduino = sharedPref.getString(SettingsActivity.MAC_ARDUINO, "98:D3:32:11:02:9D");
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        // Register for broadcasts when a device is discovered.
        bluetoothFilter = new IntentFilter();
        bluetoothFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        bluetoothFilter.addAction(BluetoothDevice.ACTION_FOUND);
        bluetoothFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        bluetoothFilter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        bluetoothFilter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);

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
        if (id == R.id.action_discover) {
            discoverBluetoothDevices();
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
        mainBinding.contentMain.fabColor.setOnLongClickListener(longClickFabListener);
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

    protected FloatingActionButton.OnLongClickListener longClickFabListener = new FloatingActionButton.OnLongClickListener(){

        @Override
        public boolean onLongClick(View v) {
            if (v.getId() == mainBinding.contentMain.fabColor.getId()){
                fabLongPressed = true;
                saveColor();
            }
            return false;
        }
    };

    protected OnClickListener fabListener = new FloatingActionButton.OnClickListener(){
        @Override
        public void onClick(View v) {
            if (v.getId() == mainBinding.contentMain.fabColor.getId()){
                if (!fabLongPressed) {
                    snackbar.setText(R.string.longPressToSave).show();
                } else {
                    fabLongPressed = false;
                }
            } else if (v.getId() == mainBinding.contentMain.fabBeat.getId()){
                beatsEnabled = !beatsEnabled;
                try {
                    write(String.format(getResources().getString(R.string.beatsMessage), beatsEnabled));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (v.getId() == mainBinding.contentMain.fabDelay.getId()){
                showNumberPicker();
            }
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
            try {
                write(String.format(getResources().getString(R.string.colorMessage),red, green, blue));
            } catch (IOException e) {
                e.printStackTrace();
            }

            Log.d("COLOR", String.format(getResources().getString(R.string.color_value), red, green, blue));
        }
    };

    private void listBluetoothDevices(){
        //TODO list bt devices
        final ArrayAdapter<BluetoothDevice> arrayAdapter = new ArrayAdapter<BluetoothDevice>(
                this,
                android.R.layout.simple_list_item_1,
                discoveredBluetoothDevices
        );
        //listDiscoveredDevices.setAdapter(arrayAdapter);



        AlertDialog.Builder builderSingle = new AlertDialog.Builder(MainActivity.this);
        builderSingle.setTitle("Select Bluetooth Device");


        builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final int selectedDev = which;
                String devAddress = arrayAdapter.getItem(which).getAddress();
                String devName = arrayAdapter.getItem(which).getName();
                final AlertDialog.Builder builderInner = new AlertDialog.Builder(MainActivity.this);
                builderInner.setMessage(devName + " ("+devAddress + ")");
                builderInner.setTitle("Confirm this is the right device?");
                builderInner.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog,int which) {
                        //sharedPref.edit().putString(macArduino,arrayAdapter.getItem(which).getAddress());
                        Log.e("selected dev", arrayAdapter.getItem(selectedDev)+"");
                        sharedPref.edit().putString("pref_mac_arduino", arrayAdapter.getItem(selectedDev).getAddress()).commit();
                        initBluetooth();
                        dialog.dismiss();
                    }
                });
                builderInner.show();
            }
        });

        builderSingle.show();
    }

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle(R.string.title_location_permission)
                        .setMessage(R.string.text_location_permission)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(MainActivity.this,
                                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                                        1);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},1
                        );
            }
            return false;
        } else {
            return true;
        }
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
                snackbar.setText(R.string.discovering).show();
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
                discoveredBluetoothDevices.add(device);
                Log.e("BT","found bt dev");
            }
            else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                listBluetoothDevices();
                Log.d("Discoversize", ""+discoveredBluetoothDevices.size());
            } else if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)){
                snackbar.setText(R.string.connected).show();
            } else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)){
                connected = false;
                snackbar.setText(R.string.disconnected).show();
                snackbar.setText(R.string.reconnecting).show();

                new Thread(new Runnable() {
                    public void run() {
                        while (!connected){
                            Log.e("retry", "retry");
                            connectThread(mmDevice);
                        }
                    }
                }).start();

            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Don't forget to unregister the ACTION_FOUND receiver.
        unregisterReceiver(mReceiver);
    }

    public static void connectThread(BluetoothDevice device){
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

    public static void run() {
        // Cancel discovery because it otherwise slows down the connection.
        mBluetoothAdapter.cancelDiscovery();

        try {
            // Connect to the remote device through the socket. This call blocks
            // until it succeeds or throws an exception.
            mmSocket.connect();
            outputStream = mmSocket.getOutputStream();
            mmSocket.getInputStream();
            connected = true;
        } catch (IOException connectException) {
            // Unable to connect; close the socket and return.
            try {
                mmSocket.close();
            } catch (IOException closeException) {
                Log.e("Bluetooth", "Could not close the client socket", closeException);
            }
        }
    }

    public static void write(String s) throws IOException {
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
    public static void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) {
            Log.e("Bluetooth", "Could not close the client socket", e);
        }
    }

    protected Boolean saveColor(){
        try {
            new SaveNewColorTask(new CustomColor(   mainBinding.contentMain.sliderRed.getProgress(),
                                                    mainBinding.contentMain.sliderGreen.getProgress(),
                                                    mainBinding.contentMain.sliderBlue.getProgress()
                                                )).execute((Void)null);
            return true;
        } catch (Exception e){
            snackbar.setText(R.string.errorSave).show();
        }
        return false;
    }

    public class SaveNewColorTask extends AsyncTask<Void, Void, Boolean> {
        private final CustomColor newColor;
        private boolean success = false;
        private List<CustomColor> allSavedColors = new ArrayList<>();
        private boolean alreadySaved = false;

        SaveNewColorTask(CustomColor newColor){
            this.newColor = newColor;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            allSavedColors = AppDatabase.getInstance(getApplicationContext()).colorDB().getStoredColors();
            for (CustomColor colorX : allSavedColors){
                if ((colorX.getRed()==newColor.getRed()) &(colorX.getGreen()==newColor.getGreen()) &(colorX.getBlue()== newColor.getBlue())){
                    snackbar.setText(R.string.duplicate).show();
                    alreadySaved = true;
                    break;
                }
            }
            if(!alreadySaved){
                AppDatabase.getInstance(getApplicationContext()).colorDB().insertAllColors(newColor);
                return true;
            }
            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if (success) {
                snackbar.setText(R.string.saved).show();
            }
        }
    }

    public static Context getContext(){
        return mContext;
    }

    public static ActivityMainBinding getMainActivityBinding(){
        return mainBinding;
    }

    protected void showNumberPicker(){
        final AlertDialog.Builder d = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.number_picker_dialog, null);
        d.setTitle("Effect delay");
        d.setMessage("Try to stay under 500 ms");
        d.setView(dialogView);
        final NumberPicker numberPicker = (NumberPicker) dialogView.findViewById(R.id.dialog_number_picker);
        numberPicker.setMaxValue(65535);
        numberPicker.setMinValue(10);
        numberPicker.setValue(delay);
        numberPicker.setWrapSelectorWheel(false);
        numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                Log.d("lol", "onValueChange: ");
            }
        });
        d.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.d("lol", "onClick: " + numberPicker.getValue());
                delay = numberPicker.getValue();
                try {
                    write(String.format(getResources().getString(R.string.delayMessage), delay));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        d.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        AlertDialog alertDialog = d.create();
        alertDialog.show();
    }
}
