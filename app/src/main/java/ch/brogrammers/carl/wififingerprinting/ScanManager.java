package ch.brogrammers.carl.wififingerprinting;

import android.net.wifi.ScanResult;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 * Scans for and Tracks the RSSI of a given set of AnchorNodes
 */
public class ScanManager {

    private final String label;
    private final int numberOfScans;
    private Hashtable<String, AnchorNode> anchorNodes;
    private int scanCount;

    public ScanManager(String label, int numberOfScans) {
        this.label = label;
        this.numberOfScans = numberOfScans;
        this.anchorNodes = new Hashtable<>();
        this.scanCount = 0;
    }

    public void addScanResults(List<ScanResult> scanResults) {
        assert !enoughResults();
        for (ScanResult scanResult : scanResults) {
            if (anchorNodes.containsKey(scanResult.SSID)) {
                anchorNodes.get(scanResult.SSID).addRssi(scanResult.level);
            }
        }
        scanCount++;
    }

    public List<AnchorNode> getAverages() {
        return calculateAverages();
    }

    private List<AnchorNode> calculateAverages() {
        List<AnchorNode> averages = new ArrayList<>();
        for (String key : anchorNodes.keySet()) {
            float avg = 0;
            if (!anchorNodes.get(key).getRssis().isEmpty()) {
                for (Integer rssi : anchorNodes.get(key).getRssis()) {
                    avg += rssi;
                }
                avg = Math.round(avg / anchorNodes.get(key).getRssis().size());
            }
            AnchorNode node = new AnchorNode(key);
            node.addRssi((int) avg);
            averages.add(node);
        }

        return averages;
    }

    public void addAnchorNode(String ssid) {
        anchorNodes.put(ssid, new AnchorNode(ssid));
    }

    public void removeAnchorNode(String ssid) {
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
        String output = label;
        if (!enoughResults()){
            for (String key : anchorNodes.keySet()) {
                output += "\n" + anchorNodes.get(key).toString();
            }
        }else{
            output += " Averages";
            for (AnchorNode node : getAverages()){
                output += "\n" + node.toString();
            }
        }
        return output;
    }

    public boolean enoughResults() {
        return scanCount >= numberOfScans;
    }

    public String getLabel() {
        return label;
    }
}
