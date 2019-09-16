package cs555.pastry;

import cs555.pastry.util.Utils;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        Utils.debug(Utils.convertBytesToHex("hello".getBytes()));
        Utils.debug(Utils.generateHexIdFromTimestamp());
        Utils.debug(Utils.generateHexIdFromTimestamp());

        String hexId1 = Utils.generateHexIdFromTimestamp();
        Utils.debug("hex1: " + hexId1);
        Thread.sleep(1000);
        String hexId2 = Utils.generateHexIdFromTimestamp();
        Utils.debug("hex2: " + hexId2);
        String longestCommonPrefix = Utils.getLongestCommonPrefix(hexId1, hexId2);
        Utils.debug("longest common prefix: " + longestCommonPrefix);

        hexId1 = "65a1";
        Utils.debug("hex1: " + hexId1);
        Thread.sleep(1000);
        hexId2 = "65b1";
        Utils.debug("hex2: " + hexId2);
        longestCommonPrefix = Utils.getLongestCommonPrefix(hexId1, hexId2);
        Utils.debug("longest common prefix: " + longestCommonPrefix);

        int hexIdDifference = Utils.getHexIdDecimalDifference(hexId1, hexId2);
        Utils.debug(hexIdDifference);
        hexIdDifference = Utils.getHexIdDecimalDifference(hexId2, hexId1);
        Utils.debug(hexIdDifference);
        hexIdDifference = Utils.getHexIdDecimalDifference("1AF3", "88AE");
        Utils.debug(hexIdDifference);}
}
