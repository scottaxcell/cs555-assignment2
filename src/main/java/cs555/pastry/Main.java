package cs555.pastry;

import cs555.pastry.util.Utils;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        Utils.debug(Utils.convertBytesToHex("hello".getBytes()));
        Utils.debug(Utils.convertBytesToHex("hello123".getBytes()));
        Utils.debug(Utils.convertBytesToHex("10:12:32".getBytes()));

        String hex = Utils.generateHexIdFromTimestamp();
        Utils.debug(hex);
    }
}
