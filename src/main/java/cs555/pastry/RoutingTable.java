package cs555.pastry;

import cs555.pastry.util.Utils;

public class RoutingTable {
    private final String hexId;
    private final Peer[][] table = new Peer[Utils.NUM_16_BIT_ID_DIGITS][];

    public RoutingTable(String hexId) {
        this.hexId = hexId;
        for (int i = 0; i < Utils.NUM_16_BIT_ID_DIGITS; i++)
            table[i] = new Peer[Utils.HEXADECIMAL_RADIX];
        Utils.debug(table);
    }

    public String lookup(String destHexId) {
        String longestCommonPrefix = Utils.getLongestCommonPrefix(hexId, destHexId);
        int p = longestCommonPrefix.length();
        char firstNonMatchingHexDigit = destHexId.charAt(p);
        int i = Integer.parseInt(String.valueOf(firstNonMatchingHexDigit), Utils.HEXADECIMAL_RADIX);
        Peer peer = table[p][i];
        if (peer != null)
            return peer.getId();

        for (int col = i; col >= 0; col--) {
            peer = table[p][col];
            if (peer != null)
                return peer.getId();
        }

        for (int row = p; row >= 0; row--) {
            for (int col = table[row].length - 1; col >= 0; col--) {
                peer = table[p][col];
                if (peer != null)
                    return peer.getId();
            }
        }

        Utils.debug("routing table exhausted -- unable to find next peer");
        return "";
    }

    public void update(Peer peer) {
        String peerId = peer.getId();
        String longestCommonPrefix = Utils.getLongestCommonPrefix(hexId, peerId);
        int p = longestCommonPrefix.length();
        char firstNonMatchingHexDigit = peerId.charAt(p);
        int i = Integer.parseInt(String.valueOf(firstNonMatchingHexDigit), Utils.HEXADECIMAL_RADIX);
        Peer oldPeer = table[p][i];
        if (oldPeer == null)
            table[p][i] = peer;
        else {
            int origHexIdDecimalDifference = Utils.getAbsoluteHexIdDecimalDifference(hexId, oldPeer.getId());
            int myHexIdDecimalDifference = Utils.getAbsoluteHexIdDecimalDifference(hexId, peerId);
            if (origHexIdDecimalDifference > myHexIdDecimalDifference)
                table[p][i] = peer;
        }
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("Routing Table\n");
        stringBuilder.append("   ");
        for (int col = 0; col < Utils.HEXADECIMAL_RADIX ; col++)
            stringBuilder.append(String.format("%-5s", Integer.toHexString(col)));
        stringBuilder.append("\n");

        for (int row = 0; row < table.length; row++) {
            stringBuilder.append(String.format("%-3s", row));
            for (int col = 0; col < table[row].length; col++) {
                Peer peer = table[row][col];
                if (peer != null)
                    stringBuilder.append(String.format("%-5s", peer.getId()));
                else
                    stringBuilder.append(String.format("%-5s", "n"));
            }
            stringBuilder.append("\n");
        }
        stringBuilder.append("\n");
        return stringBuilder.toString();
    }
}
