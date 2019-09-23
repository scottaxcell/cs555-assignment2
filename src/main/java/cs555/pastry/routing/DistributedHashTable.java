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
        if (Utils.getHexIdDecimalDifference(destHexId, leafSet.getLeftNeighborId()) > 0
            && Utils.getHexIdDecimalDifference(leafSet.getRightNeighborId(), destHexId) > 0) {
            return hexId;
        }
        // if L0 == D, return L0
        if (leafSet.getLeftNeighborId().equals(destHexId))
            return leafSet.getLeftNeighborId();

        // if L1 == D, return L1
        if (leafSet.getRightNeighborId().equals(destHexId))
            return leafSet.getRightNeighborId();

        String nextPeerId = routingTable.lookup(destHexId);
        if (!nextPeerId.isEmpty())
            return nextPeerId;
        else {
            // return closest neighbor in leaf set
            int leftNeighborHexIdDecimalDifference = Utils.getAbsoluteHexIdDecimalDifference(destHexId, leafSet.getLeftNeighborId());
            int rightNeighborHexIdDecimalDifference = Utils.getAbsoluteHexIdDecimalDifference(destHexId, leafSet.getRightNeighborId());
            if (leftNeighborHexIdDecimalDifference < rightNeighborHexIdDecimalDifference)
                return leafSet.getRightNeighborId();
            else
                return leafSet.getLeftNeighborId();
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

    public LeafSet getLeafSet() {
        return leafSet;
    }

    public Peer[][] getRoutingTable() {
        return routingTable.getTable();
    }
}
