package cs555.pastry.routing;

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

        if (leafSet.getLeftNeighbor().getId().equals(destHexId))
            return leafSet.getLeftNeighbor().getId();

        if (leafSet.getRightNeighbor().getId().equals(destHexId))
            return leafSet.getRightNeighbor().getId();

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

    public void updateRoutingTable(Peer peer) {
        routingTable.update(peer);
    }

    public void setLeftNeighbor(Peer peer) {
        leafSet.setLeftNeighbor(peer);
    }

    public void setRightNeighbor(Peer peer) {
        leafSet.setRightNeighbor(peer);
    }
}
