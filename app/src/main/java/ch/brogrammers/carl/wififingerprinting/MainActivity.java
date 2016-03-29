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
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TextView textView;
    private Button toggleButton;

    private WifiManager wifiManager;
    private WifiScanReceiver wifiScanReceiver;
    private ScanResultsFilter scanResultsFilter;

    private Handler handler;
    private int scanCount = 0;
    private boolean isScanning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = (TextView) findViewById(R.id.textView);
        toggleButton = (Button) findViewById(R.id.button);

        scanResultsFilter = new ScanResultsFilter();
        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        wifiScanReceiver = new WifiScanReceiver();

        handler = new Handler();

        scanResultsFilter.addAnchorNode("Berntiger");

        final Runnable scanRepeater = new Runnable() {
            @Override
            public void run() {
                wifiManager.startScan();
                handler.postDelayed(this, 3000);
            }
        };

        toggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isScanning) {
                    isScanning = false;
                    toggleButton.setText("SCAN");
                    handler.removeCallbacks(scanRepeater);
                } else {
                    isScanning = true;
                    handler.post(scanRepeater);
                }

            }
        });

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
            toggleButton.setText("Scan Count: " + Integer.toString(scanCount++));
            scanResultsFilter.filterResults(wifiManager.getScanResults());
            textView.setText(scanResultsFilter.toString());
        }
    }
}
