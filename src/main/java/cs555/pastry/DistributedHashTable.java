package cs555.pastry;

import cs555.pastry.util.Utils;

public class DistributedHashTable {
    private final String hexId;
    private final LeafSet leafSet = new LeafSet();
    private final RoutingTable routingTable = new RoutingTable();

    public DistributedHashTable(String hexId) {
        this.hexId = hexId;
    }

    public String lookup(String destHexId) {
        // if L-l < D < Ll, return A (this node)
        if (Utils.getHexIdDecimalDifference(destHexId, leafSet.getLeftNeighbor().getId()) > 0
                && Utils.getHexIdDecimalDifference(leafSet.getRightNeghbor().getId(), destHexId) > 0) {
            return hexId;
        }

        // todo check routing table

        return null;
    }
}
