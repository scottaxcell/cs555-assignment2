package cs555.pastry;

import cs555.pastry.util.Utils;

public class DistributedHashTable {
    private final String hexId;
    private final LeafSet leafSet;
    private final RoutingTable routingTable;

    public DistributedHashTable(String hexId) {
        this.hexId = hexId;
        leafSet = new LeafSet();
        routingTable = new RoutingTable(hexId);
    }

    public String lookup(String destHexId) {
        // if L-l < D < Ll, return A (this node)
        if (Utils.getHexIdDecimalDifference(destHexId, leafSet.getLeftNeighbor().getId()) > 0
                && Utils.getHexIdDecimalDifference(leafSet.getRightNeighbor().getId(), destHexId) > 0) {
            return hexId;
        }

        String nextPeerId = routingTable.lookup(destHexId);
        if (!nextPeerId.isEmpty())
            return nextPeerId;
        else {
            int leftNeighborHexIdDecimalDifference = Utils.getAbsoluteHexIdDecimalDifference(destHexId, leafSet.getLeftNeighbor().getId());
            int rightNeighborHexIdDecimalDifference = Utils.getAbsoluteHexIdDecimalDifference(destHexId, leafSet.getRightNeighbor().getId());
            if (leftNeighborHexIdDecimalDifference < rightNeighborHexIdDecimalDifference)
                return leafSet.getRightNeighbor().getId();
            else
                return leafSet.getLeftNeighbor().getId();
        }
    }
}
