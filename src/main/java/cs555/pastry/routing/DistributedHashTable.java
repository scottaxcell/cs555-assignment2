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
        // if L0 < D < Ll, return A (this node)
        if (leafSet.getLeftNeighbor() != null && leafSet.getRightNeighbor() != null) {
            if (Utils.getHexIdDecimalDifference(destHexId, leafSet.getLeftNeighbor().getId()) > 0
                    && Utils.getHexIdDecimalDifference(leafSet.getRightNeighbor().getId(), destHexId) > 0) {
                return hexId;
            }
        }
        // if L0 == D, return L0
        if (leafSet.getLeftNeighbor() != null && leafSet.getLeftNeighbor().getId().equals(destHexId))
            return leafSet.getLeftNeighbor().getId();

        // if L1 == D, return L1
        if (leafSet.getRightNeighbor() != null && leafSet.getRightNeighbor().getId().equals(destHexId))
            return leafSet.getRightNeighbor().getId();

        String nextPeerId = routingTable.lookup(destHexId);
        if (!nextPeerId.isEmpty())
            return nextPeerId;
        else {
            // return closest neighbor in leaf set
            if (leafSet.getLeftNeighbor() != null && leafSet.getRightNeighbor() != null) {
                int leftNeighborHexIdDecimalDifference = Utils.getAbsoluteHexIdDecimalDifference(destHexId, leafSet.getLeftNeighbor().getId());
                int rightNeighborHexIdDecimalDifference = Utils.getAbsoluteHexIdDecimalDifference(destHexId, leafSet.getRightNeighbor().getId());
                if (leftNeighborHexIdDecimalDifference < rightNeighborHexIdDecimalDifference)
                    return leafSet.getRightNeighbor().getId();
                else
                    return leafSet.getLeftNeighbor().getId();
            }
            else
                return "";
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

    public void printState() {
        Utils.out("Hex ID: " + hexId + "\n");
        leafSet.printState();
        routingTable.printState();
    }

    public String getHexId() {
        return hexId;
    }

    public Peer getPeer(String hexId) {
        return routingTable.getPeer(hexId);
    }

    public Peer[] getTableRow(String hexId) {
        return routingTable.getTableRow(hexId);
    }
}
