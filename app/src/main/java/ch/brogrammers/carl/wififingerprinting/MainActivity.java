package ch.brogrammers.carl.wififingerprinting;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private TextView textView;
    private Button toggleButton;

    private WifiManager wifiManager;
    private WifiScanReceiver wifiScanReceiver;
    private ScanResultsFilter scanResultsFilter;

    private int scanCount = 0;
    private ArrayList<String> results;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = (TextView) findViewById(R.id.textView);
        toggleButton = (Button) findViewById(R.id.button);
        results = new ArrayList();

        scanResultsFilter = new ScanResultsFilter();
        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        wifiScanReceiver = new WifiScanReceiver();
        toggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (scanCount == 0) {
                    toggleButton.setText("Scanning...");
                    wifiManager.startScan();
                }
            }
        });

        //scanResultsFilter.addAnchorNode("eduroam");
        //scanResultsFilter.addAnchorNode("public-unibe");
        scanResultsFilter.addAnchorNode("UPC0048103");
        textView.setText(scanResultsFilter.toString());
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
            scanCount++;
            toggleButton.setText( "Scan Count: " + Integer.toString(scanCount));
            scanResultsFilter.filterResults(wifiManager.getScanResults());
            results.add(scanResultsFilter.toString());
            textView.setText(scanResultsFilter.toString());
            if(results.size()<10){
                wifiManager.startScan();
            }
            else{
                toggleButton.setText("Scan Finished");
                textView.setText(results.toString());
                results.clear();
                scanCount = 0;
            }
        }
    }
}
