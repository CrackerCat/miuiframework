package android.drm;

import android.net.wifi.WifiEnterpriseConfig;
import java.io.BufferedInputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

public class DrmUtils {

    public static class ExtendedMetadataParser {
        HashMap<String, String> mMap;

        private int readByte(byte[] constraintData, int arrayIndex) {
            return constraintData[arrayIndex];
        }

        private String readMultipleBytes(byte[] constraintData, int numberOfBytes, int arrayIndex) {
            byte[] returnBytes = new byte[numberOfBytes];
            int j = arrayIndex;
            int i = 0;
            while (j < arrayIndex + numberOfBytes) {
                returnBytes[i] = constraintData[j];
                j++;
                i++;
            }
            return new String(returnBytes);
        }

        private ExtendedMetadataParser(byte[] constraintData) {
            this.mMap = new HashMap();
            int index = 0;
            while (index < constraintData.length) {
                int keyLength = readByte(constraintData, index);
                index++;
                int valueLength = readByte(constraintData, index);
                index++;
                String strKey = readMultipleBytes(constraintData, keyLength, index);
                index += keyLength;
                String strValue = readMultipleBytes(constraintData, valueLength, index);
                if (strValue.equals(WifiEnterpriseConfig.CA_CERT_ALIAS_DELIMITER)) {
                    strValue = "";
                }
                index += valueLength;
                this.mMap.put(strKey, strValue);
            }
        }

        public Iterator<String> iterator() {
            return this.mMap.values().iterator();
        }

        public Iterator<String> keyIterator() {
            return this.mMap.keySet().iterator();
        }

        public String get(String key) {
            return (String) this.mMap.get(key);
        }
    }

    static byte[] readBytes(String path) throws IOException {
        return readBytes(new File(path));
    }

    static byte[] readBytes(File file) throws IOException {
        FileInputStream inputStream = new FileInputStream(file);
        BufferedInputStream bufferedStream = new BufferedInputStream(inputStream);
        byte[] data = null;
        try {
            int length = bufferedStream.available();
            if (length > 0) {
                data = new byte[length];
                bufferedStream.read(data);
            }
            quietlyDispose(bufferedStream);
            quietlyDispose(inputStream);
            return data;
        } catch (Throwable th) {
            quietlyDispose(bufferedStream);
            quietlyDispose(inputStream);
        }
    }

    static void writeToFile(String path, byte[] data) throws IOException {
        FileOutputStream outputStream = null;
        if (path != null && data != null) {
            try {
                outputStream = new FileOutputStream(path);
                outputStream.write(data);
            } finally {
                quietlyDispose(outputStream);
            }
        }
    }

    static void removeFile(String path) throws IOException {
        new File(path).delete();
    }

    private static void quietlyDispose(Closeable closable) {
        if (closable != null) {
            try {
                closable.close();
            } catch (IOException e) {
            }
        }
    }

    public static ExtendedMetadataParser getExtendedMetadataParser(byte[] extendedMetadata) {
        return new ExtendedMetadataParser(extendedMetadata);
    }
}
