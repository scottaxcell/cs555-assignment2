package cs555.pastry;

import cs555.pastry.util.Utils;

import java.util.ArrayList;
import java.util.List;

public class RoutingTable {
    private final List<List<Peer>> table = new ArrayList<>(Utils.NUM_16_BIT_ID_DIGITS);

    public RoutingTable() {
        for (int i = 0; i < Utils.NUM_16_BIT_ID_DIGITS; i++)
            table.add(new ArrayList<>());
    }

    public Peer getDestination(String srcHexId, String destHexId) {
        String longestCommonPrefix = Utils.getLongestCommonPrefix(srcHexId, destHexId);

        return null;
    }
}
