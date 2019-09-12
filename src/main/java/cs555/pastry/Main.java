package cs555.pastry;

import cs555.pastry.util.Utils;

import java.sql.Timestamp;
import java.time.Instant;

public class Main {
    private static final int NUM_16_BIT_ID_DIGITS = 4;

    public static void main(String[] args) throws InterruptedException {
        Utils.debug(Utils.convertBytesToHex("hello".getBytes()));
        Utils.debug(Utils.convertBytesToHex("hello123".getBytes()));
        Utils.debug(Utils.convertBytesToHex("10:12:32".getBytes()));
//        Instant.now().getNano()
//        Utils.debug(Utils.convertBytesToHex(Timestamp.from(Instant.now()).toString().getBytes()));
//        Thread.sleep(1000);
//        Utils.debug(Utils.convertBytesToHex(Timestamp.from(Instant.now()).toString().getBytes()));
//        Thread.sleep(3400);
//        Utils.debug(Utils.convertBytesToHex(Timestamp.from(Instant.now()).toString().getBytes()));

        String hex = Utils.convertBytesToHex(Timestamp.from(Instant.now()).toString().getBytes());
        Utils.debug(hex);
        Utils.debug(hex.substring(hex.length() - NUM_16_BIT_ID_DIGITS));
    }

}
