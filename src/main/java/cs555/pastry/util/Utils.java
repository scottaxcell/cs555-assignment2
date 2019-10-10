package cs555.pastry.util;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.Random;

public class Utils {
    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("HH:mm:ss.SSS");
    public static final int HEXADECIMAL_RADIX = 16;
    public static final int NUM_16_BIT_ID_DIGITS = 4;
    private static boolean debug = true;

    public static void main(String[] args) {
        for (int i = 0; i < 15; i++) {
            Timestamp timestamp = Timestamp.from(Instant.now());
//        String sha1FromBytes1 = createSha1FromBytes(timeStamp.getTime());
            String timestampS = Timestamp.from(Instant.now()).toString();
            String sha1 = createSha1FromBytes(timestampS.getBytes());
            Utils.debug(sha1.substring(sha1.length() - 4));
            sleep(new Random().nextInt(1000));
        }
    }

    public static void out(Object o) {
        System.out.print(o);
    }

    public static void info(Object o) {
        info(o, true);
    }

    public static void info(Object o, boolean newLine) {
        if (newLine)
            System.out.println("INFO: " + o);
        else
            System.out.print("INFO: " + o);
    }

    public static void debug(Object o) {
        if (debug)
            System.out.println(String.format("\nDEBUG %s %s", SIMPLE_DATE_FORMAT.format(new Date()), o));
    }

    public static void error(Object o) {
        System.err.println("\nERROR: " + o);
    }

    public static String generateHexIdFromTimestamp() {
        String hexId = convertBytesToHex(Timestamp.from(Instant.now()).toString().getBytes());
        return hexId.substring(hexId.length() - NUM_16_BIT_ID_DIGITS);
    }

    public static String generateHexIdFromFileName(String fileName) {
        String hexId = convertBytesToHex(fileName.getBytes());
        return hexId.substring(hexId.length() - NUM_16_BIT_ID_DIGITS);
    }

    public static String getLongestCommonPrefix(String hexId1, String hexId2) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < hexId1.length(); i++) {
            if (hexId1.charAt(i) != hexId2.charAt(i))
                break;
            stringBuilder.append(hexId1.charAt(i));
        }
        return stringBuilder.toString();
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

    public static int getHexIdDecimalDifference(String hexId1, String hexId2) {
        return Integer.parseInt(hexId1, HEXADECIMAL_RADIX) - Integer.parseInt(hexId2, HEXADECIMAL_RADIX);
    }

    public static int getAbsoluteHexIdDecimalDifference(String hexId1, String hexId2) {
        return Math.abs(getHexIdDecimalDifference(hexId1, hexId2));
    }

//    public static String getServerAddress(TcpServer tcpServer) {
//        if (tcpServer == null)
//            return "";
//        return String.format("%s:%d", tcpServer.getIp(), tcpServer.getPort());
//    }

    public static String[] splitAddress(String address) {
        if (address == null)
            return new String[0];
        return address.split(":");
    }

    public static String getIpFromAddress(String address) {
        if (address == null)
            return "";
        if (address.startsWith("/"))
            return splitAddress(address.substring(1))[0];
        return splitAddress(address)[0];
    }

    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static byte[] readFileToBytes(Path path) {
        byte[] bytes = new byte[0];
        try {
            bytes = Files.readAllBytes(path);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return bytes;
    }

    public static void writeBytesToFile(Path path, byte[] bytes) {
        try {
            Files.createDirectories(path.getParent());
            Files.write(path, bytes);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getCanonicalPath(Path path) {
        try {
            return path.toFile().getCanonicalPath();
        }
        catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String createSha1FromBytes(byte[] data) {
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA1");
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "";
        }
        byte[] hash = digest.digest(data);
        BigInteger hashInt = new BigInteger(1, hash);
        return hashInt.toString(16);
    }
}
