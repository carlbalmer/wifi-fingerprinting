package ch.brogrammers.carl.wififingerprinting;


import android.net.wifi.ScanResult;

import java.util.ArrayList;

/**
 * Information about the anchor nodes. SSID is assumed to be unique.
 */
public class AnchorNode {
    private final String ssid;
    private ArrayList<Integer> rssi;

    public AnchorNode(String ssid) {
        this.ssid = ssid;
        rssi = new ArrayList<>();
    }

    public String getSsid() {
        return ssid;
    }

    public void addRssi(int rssi) {
        this.rssi.add(rssi);
    }

    public ArrayList<Integer> getRssis(){
        return rssi;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AnchorNode that = (AnchorNode) o;

        return ssid.equals(that.ssid);
    }

    @Override
    public int hashCode() {
        return ssid.hashCode();
    }

    @Override
    public String toString() {
        String allRssis = "";
        for(int value :rssi){
            allRssis = allRssis + " " + value;
        }
        return ssid + ": " + allRssis ;
    }
}
