package ch.brogrammers.carl.wififingerprinting;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TextView textView;
    private Button button;
    private EditText editText;

    private WifiManager wifiManager;
    private WifiScanReceiver wifiScanReceiver;
    private ScanManager scanManager;

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

        handler = new Handler();
        scanRunner = new ScanRunner();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startScan();
            }
        });
    }

    private void startScan(){
        if(!isScanRunning){
            isScanRunning = true;
            scanManager = new ScanManager(editText.getText().toString(), 5);
            defineAnchorNodes();
            handler.post(scanRunner);
        }
    }

    private void defineAnchorNodes() {
        scanManager.addAnchorNode("UPC0048103");
    }

    private void finishScan(){
        textView.setText("Finished" + scanManager.toString());
        isScanRunning = false;
    }

    protected void onPause() {
        unregisterReceiver(wifiScanReceiver);
        super.onPause();
    }

    protected void onResume() {
        registerReceiver(wifiScanReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        super.onResume();
    }

    /**
     * Defines what happens when WifiManager.startScan() finishes.
     * Created by carl on 15.03.16.
     */
    public class WifiScanReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            scanManager.addScanResults(wifiManager.getScanResults());
            textView.setText(scanManager.toString());
            button.setText("Scan Count: " + Integer.toString(scanManager.getScanCount()));
        }
    }

    public class ScanRunner implements Runnable {
        @Override
        public void run() {
            if (!scanManager.enoughResults()) {
                wifiManager.startScan();
                handler.postDelayed(this, 3000);
            } else {
                finishScan();
                handler.removeCallbacks(this);
            }
        }
    }
}
