package ch.brogrammers.carl.wififingerprinting;

import android.hardware.SensorEvent;
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
    private ArrayList<float[]> magneticFieldValues;

    public ScanManager(String label, int numberOfScans) {
        this.label = label;
        this.numberOfScans = numberOfScans;
        this.anchorNodes = new Hashtable<>();
        this.magneticFieldValues = new ArrayList<>();
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

    public int[] calculateAverageMagnetometer() {
        assert (!magneticFieldValues.isEmpty());
        int[] avg = new int[3];
        float[] sum = new float[3];
        for (float[] value : magneticFieldValues) {
            for (int i = 0; i < 3; i++) {
                sum[i] += value[i];
            }
        }
        for (int i = 0; i < 3; i++) {
            avg[i] = Math.round(sum[i] / magneticFieldValues.size());
        }
        return avg;
    }

    public List<AnchorNode> calculateAverageRssi() {
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

    public Hashtable<String, AnchorNode> getAnchorNodes() {
        return anchorNodes;
    }

    public int getScanCount() {
        return scanCount;
    }

    public String averagesToString() {
        String output = label;
        output += " Averages";
        for (AnchorNode node : calculateAverageRssi()) {
            output += "\n" + node.toString();
        }
        output += "\nX-Axis:" + calculateAverageMagnetometer()[0]
                + "\nY-Axis:" + calculateAverageMagnetometer()[1]
                + "\nZ-Axis:" + calculateAverageMagnetometer()[2];
        return output;
    }

    @Override
    public String toString() {
        String output = label;

        for (String key : anchorNodes.keySet()) {
            output += "\n" + anchorNodes.get(key).toString();
        }
        output += "\nX-Axis:" + magneticFieldValues.get(magneticFieldValues.size() - 1)[0]
                + "\nY-Axis:" + magneticFieldValues.get(magneticFieldValues.size() - 1)[1]
                + "\nZ-Axis:" + magneticFieldValues.get(magneticFieldValues.size() - 1)[2];
        return output;
    }

    public boolean enoughResults() {
        return scanCount >= numberOfScans;
    }

    public String getLabel() {
        return label;
    }

    public void addMagnetometerValues(SensorEvent magnetometerEvent) {
        this.magneticFieldValues.add(magnetometerEvent.values);
    }

    public ArrayList<float[]> getMagneticFieldValues() {
        return magneticFieldValues;
    }
}
