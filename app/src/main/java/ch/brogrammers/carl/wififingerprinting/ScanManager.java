package ch.brogrammers.carl.wififingerprinting;

import android.net.wifi.ScanResult;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 * Scans for and Tracks the RSSI of a given set of AnchorNodes
 */
public class ScanManager {

    private Hashtable<String, AnchorNode> anchorNodes;
    private final String label;
    private final int numberOfScans;
    private int scanCount;

    public ScanManager(String label, int numberOfScans) {
        this.label = label;
        this.numberOfScans = numberOfScans;
        this.anchorNodes = new Hashtable<>();
        this.scanCount = 0;
    }

    public void addScanResults(List<ScanResult> scanResults){
        for(ScanResult scanResult : scanResults){
            if(anchorNodes.containsKey(scanResult.SSID)){
                anchorNodes.get(scanResult.SSID).addRssi(scanResult.level);
            }
        }
        scanCount++;
    }

    public void addAnchorNode(String ssid){
        anchorNodes.put(ssid, new AnchorNode(ssid));
    }

    public void removeAnchorNode(String ssid){
        anchorNodes.remove(ssid);
    }

    public AnchorNode getAnchorNode(String ssid) {
        return anchorNodes.get(ssid);
    }

    public int getScanCount() {
        return scanCount;
    }

    @Override
    public String toString() {
        String output = label + "\n";
        for(String key : anchorNodes.keySet()){
            output = output + anchorNodes.get(key) + "\n";
        }
        return output;
    }

    public boolean enoughResults(){
        return scanCount >= numberOfScans;
    }

    public String getLabel() {
        return label;
    }
}
