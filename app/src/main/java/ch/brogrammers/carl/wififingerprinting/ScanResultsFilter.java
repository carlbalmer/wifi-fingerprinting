package ch.brogrammers.carl.wififingerprinting;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;

import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

/**
 * Scans for and Tracks the RSSI of a given set of AnchorNodes
 */
public class ScanResultsFilter {

    private Hashtable<String, AnchorNode> anchorNodes;

    public ScanResultsFilter() {
        this.anchorNodes = new Hashtable();
    }

    public void filterResults(List<ScanResult> scanResults){
        for(ScanResult scanResult : scanResults){
            if(anchorNodes.containsKey(scanResult.SSID)){
                anchorNodes.get(scanResult.SSID).setRssi(scanResult.level);
            }
        }
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

    @Override
    public String toString() {
        String output = "";
        for(String key : anchorNodes.keySet()){
            output = output + anchorNodes.get(key) + "\n";
        }
        return output;
    }
}
