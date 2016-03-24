package ch.brogrammers.carl.wififingerprinting;


import android.net.wifi.ScanResult;

/**
 * Information about the anchor nodes. SSID is assumed to be unique.
 */
public class AnchorNode {
    private final String ssid;
    private int rssi;

    public AnchorNode(String ssid) {
        this.ssid = ssid;
    }

    public String getSsid() {
        return ssid;
    }

    public int getRssi() {
        return rssi;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
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
        return ssid + ": " + rssi ;
    }
}
