package cs555.pastry.routing;

import cs555.pastry.node.peer.PeerNode;
import cs555.pastry.util.Utils;

import java.util.ArrayList;
import java.util.List;

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
        else {
            List<String> peers = new ArrayList<>();
            peers.add(hexId);

            if (!leafSet.getLeftNeighborId().isEmpty())
                peers.add(leafSet.getLeftNeighborId());

            if (!leafSet.getRightNeighborId().isEmpty())
                peers.add(leafSet.getRightNeighborId());

            String nextPeerId = routingTable.lookup(destHexId);
            if (!nextPeerId.isEmpty())
                peers.add(nextPeerId);

            // sort peers by closest hex id value
            peers.sort((hexId1, hexId2) -> {
                int absoluteHexDiff1 = Utils.getAbsoluteHexIdDecimalDifference(destHexId, hexId1);
                int absoluteHexDiff2 = Utils.getAbsoluteHexIdDecimalDifference(destHexId, hexId2);
                if (absoluteHexDiff2 < absoluteHexDiff1)
                    return 1;
                else if (absoluteHexDiff1 < absoluteHexDiff2)
                    return -1;
                else
                    return Utils.getHexIdDecimalDifference(hexId1, hexId2) > 0 ? -1 : 1;
            });

            return peers.get(0);
        }
    }
//        // if L0 == D, return L0
//        if (leafSet.getLeftNeighborId().equals(destHexId))
//            return leafSet.getLeftNeighborId();
//
//        // if L1 == D, return L1
//        if (leafSet.getRightNeighborId().equals(destHexId))
//            return leafSet.getRightNeighborId();
//
//        String nextPeerId = routingTable.lookup(destHexId);
//        if (!nextPeerId.isEmpty())
//            return nextPeerId;
//        else {
//            // return closest neighbor in leaf set
//            int leftNeighborHexIdDecimalDifference = Utils.getAbsoluteHexIdDecimalDifference(destHexId, leafSet.getLeftNeighborId());
//            int rightNeighborHexIdDecimalDifference = Utils.getAbsoluteHexIdDecimalDifference(destHexId, leafSet.getRightNeighborId());
//            if (leftNeighborHexIdDecimalDifference < rightNeighborHexIdDecimalDifference)
//                return leafSet.getRightNeighborId();
//            else
//                return leafSet.getLeftNeighborId();
//        }
//        }

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
        Peer peer = routingTable.getPeer(hexId);
        if (peer != null)
            return peer;
        return leafSet.getPeer(hexId);
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

    public void updateRoutingTable(Peer[][] table) {
        for (int row = 0; row < table.length; row++) {
            if (table[row] == null)
                continue;
            for (int col = 0; col < table[row].length; col++) {
                Peer peer = table[row][col];
                if (peer != null)
                    this.routingTable.update(peer);
            }
        }
    }

    public List<Peer> getPeers() {
        return routingTable.getPeers();
    }

    public void updateRoutingTableFromRoute(List<String> route) {
        for (String hop : route) {
            String hopId = PeerNode.getHopId(hop);
            if (hexId.equals(hopId))
                continue;
            String hopIp = PeerNode.getHopIp(hop);
            routingTable.update(new Peer(hopId, hopIp));
        }
    }
}
