package ch.brogrammers.carl.wififingerprinting;

import android.content.Context;
import android.os.Environment;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Saves the raw and average data stored in the ScanManager to separate files.
 * Created by carl on 05.04.16.
 */
public class FileSaver {

    public static void save(Context c, ScanManager scanManager, String filename) {
        saveAverages(c, scanManager, filename);
        saveRaw(c, scanManager, filename);
    }

    public static void saveRaw(Context c, ScanManager scanManager, String filename) {
        assert isExternalStorageWritable();
        //Creates a new file
        File file = new File(c.getExternalFilesDir(null), "raw" + filename);
        //Saves the AnchorNodes as separate lines
        for (String key : scanManager.getAnchorNodes().keySet()) {
            String line = scanManager.getLabel();
            line += "," + key;
            for (int value : scanManager.getAnchorNodes().get(key).getRssis()) {
                line += "," + value;
            }
            line += "\n";
            writeLineToFile(line, file);
        }
        //saves the Magnetometer as separate lines
        String xAxis = scanManager.getLabel() + ",X-Axis";
        String yAxis = scanManager.getLabel() + ",Y-Axis";
        String zAxis = scanManager.getLabel() + ",Z-Axis";
        for (float[] values : scanManager.getMagneticFieldValues()) {
            xAxis += "," + values[0];
            yAxis += "," + values[1];
            zAxis += "," + values[2];
        }
        xAxis += "\n";
        yAxis += "\n";
        zAxis += "\n";
        writeLineToFile(xAxis, file);
        writeLineToFile(yAxis, file);
        writeLineToFile(zAxis, file);
    }

    public static void saveAverages(Context c, ScanManager scanManager, String filename) {
        assert isExternalStorageWritable();
        File file = new File(c.getExternalFilesDir(null), "averages" + filename);
        for (AnchorNode anchorNode : scanManager.calculateAverageRssi()) {
            String line = scanManager.getLabel();
            line += "," + anchorNode.getSsid();
            for (int value : anchorNode.getRssis()) {
                line += "," + value;
            }
            line += "\n";
            writeLineToFile(line, file);
        }
        //saves the Magnetometer as separate lines
        String xAxis = scanManager.getLabel() + ",X-Axis" + "," + scanManager.calculateAverageMagnetometer()[0] + "\n";
        String yAxis = scanManager.getLabel() + ",Y-Axis" + "," + scanManager.calculateAverageMagnetometer()[1] + "\n";
        String zAxis = scanManager.getLabel() + ",Z-Axis" + "," + scanManager.calculateAverageMagnetometer()[2] + "\n";
        writeLineToFile(xAxis, file);
        writeLineToFile(yAxis, file);
        writeLineToFile(zAxis, file);

    }

    /**
     * Checks if external storage is available for read and write.
     */
    private static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    private static void writeLineToFile(String line, File file) {
        try {
            BufferedWriter buf = new BufferedWriter(new FileWriter(file, true));
            buf.append(line);
            buf.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
