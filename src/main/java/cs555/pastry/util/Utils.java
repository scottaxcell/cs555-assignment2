package cs555.pastry.util;

import java.sql.Timestamp;
import java.time.Instant;

public class Utils {
    public static final int NUM_16_BIT_ID_DIGITS = 4;
    private static boolean debug = true;

    public static void out(Object o) {
        System.out.print(o);
    }

    public static void info(Object o) {
        info(o, true);
    }

    public static void info(Object o, boolean newLine) {
        if (newLine)
            System.out.println("\nINFO: " + o);
        else
            System.out.print("\nINFO: " + o);
    }

    public static void debug(Object o) {
        if (debug)
            System.out.println("DEBUG: " + o);
    }

    public static void error(Object o) {
        System.err.println("\nERROR: " + o);
    }

    public static String generateHexIdFromTimestamp() {
        String hexId = convertBytesToHex(Timestamp.from(Instant.now()).toString().getBytes());
        return hexId.substring(hexId.length() - NUM_16_BIT_ID_DIGITS);
    }

    /**
     * This method converts a set of bytes into a Hexadecimal representation.
     *
     * @param buf
     * @return
     */
    public static String convertBytesToHex(byte[] buf) {
        StringBuffer strBuf = new StringBuffer();
        for (int i = 0; i < buf.length; i++) {
            int byteValue = (int) buf[i] & 0xff;
            if (byteValue <= 15) {
                strBuf.append("0");
            }
            strBuf.append(Integer.toString(byteValue, 16));
        }
        return strBuf.toString();
    }

    /**
     * This method converts a specified hexadecimal String into a set of bytes.
     *
     * @param hexString
     * @return
     */
    public static byte[] convertHexToBytes(String hexString) {
        int size = hexString.length();
        byte[] buf = new byte[size / 2];
        int j = 0;
        for (int i = 0; i < size; i++) {
            String a = hexString.substring(i, i + 2);
            int valA = Integer.parseInt(a, 16);
            i++;
            buf[j] = (byte) valA;
            j++;
        }
        return buf;
    }
}
