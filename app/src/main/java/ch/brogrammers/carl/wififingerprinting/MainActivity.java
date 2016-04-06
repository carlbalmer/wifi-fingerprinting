package ch.brogrammers.carl.wififingerprinting;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements SensorEventListener{

    private TextView textView;
    private Button button;
    private EditText editText;

    private WifiManager wifiManager;
    private WifiScanReceiver wifiScanReceiver;
    private ScanManager scanManager;

    private SensorManager sensorManager;
    private Sensor magnetometer;
    private SensorEvent magnetometerEvent;

    private Handler handler;
    private Runnable scanRunner;
    private boolean isScanRunning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = (TextView) findViewById(R.id.textView);
        button = (Button) findViewById(R.id.button);
        editText = (EditText) findViewById(R.id.editText);

        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        wifiScanReceiver = new WifiScanReceiver();

        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        handler = new Handler();
        scanRunner = new ScanRunner();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startScan();
            }
        });
    }

    private void startScan() {
        if (!isScanRunning) {
            isScanRunning = true;
            scanManager = new ScanManager(editText.getText().toString(), 8);
            defineAnchorNodes();
            handler.post(scanRunner);
        }
    }

    private void defineAnchorNodes() {
        scanManager.addAnchorNode("UPC0048103");
        scanManager.addAnchorNode("NETGEAR31");
        scanManager.addAnchorNode("devolo-000B3B9BC9A9");
        scanManager.addAnchorNode("489-652");
        scanManager.addAnchorNode("Beatevents_WLAN");
        scanManager.addAnchorNode("Berntiger");
        scanManager.addAnchorNode("TP-LINK_BCC3A8");
    }

    private void finishScan() {
        textView.setText(scanManager.averagesToString());
        FileSaver.save(this, scanManager, "testingDoors.csv");
        button.setText(R.string.button1);
        isScanRunning = false;
    }

    protected void onPause() {
        unregisterReceiver(wifiScanReceiver);
        sensorManager.unregisterListener(this);
        super.onPause();
    }

    protected void onResume() {
        registerReceiver(wifiScanReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_NORMAL);
        super.onResume();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        this.magnetometerEvent = event;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    /**
     * Defines what happens when WifiManager.startScan() finishes.
     * Created by carl on 15.03.16.
     */
    public class WifiScanReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (!scanManager.enoughResults()) {
                scanManager.addScanResults(wifiManager.getScanResults());
                scanManager.addMagnetometerValues(magnetometerEvent);
                textView.setText(scanManager.toString());
                button.setText(getString(R.string.scanCount) + Integer.toString(scanManager.getScanCount()));
            }
        }
    }

    public class ScanRunner implements Runnable {
        @Override
        public void run() {
            if (!scanManager.enoughResults()) {
                wifiManager.startScan();
                handler.postDelayed(this, 2000);
            } else {
                finishScan();
                handler.removeCallbacks(this);
            }
        }
    }
}
