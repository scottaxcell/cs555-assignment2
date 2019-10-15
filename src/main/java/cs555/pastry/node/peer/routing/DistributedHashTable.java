package cs555.pastry.node.peer.routing;

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
//        Utils.info("lookup for: " + destHexId + " " + peers);

        return peers.get(0);
    }

    public void setLeftNeighbor(Peer peer) {
        leafSet.setLeftNeighbor(peer);
    }

    public void setRightNeighbor(Peer peer) {
        leafSet.setRightNeighbor(peer);
    }

    public void printState() {
        Utils.info("Hex ID: " + hexId);
        Utils.out("      ============\n");
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

    public List<Peer> getTableRow(String hexId) {
        List<Peer> peers = new ArrayList<>();
        for (Peer peer : routingTable.getTableRow(hexId))
            if (peer != null)
                peers.add(peer);
        return peers;
    }

    public LeafSet getLeafSet() {
        return leafSet;
    }

    public List<Peer> getPeers() {
        return routingTable.getPeers();
    }

    public void updateRoutingTableFromRoute(List<String> route) {
        for (String hop : route) {
            String hopId = PeerNode.getHopId(hop);
            if (hexId.equals(hopId))
                continue;
            if (leafSet.getLeftNeighborId().equals(hop) || leafSet.getRightNeighborId().equals(hop))
                continue;
            String hopIp = PeerNode.getHopIp(hop);
            routingTable.update(new Peer(hopId, hopIp));
        }
    }

    public void updateRoutingTable(List<Peer> peers) {
        for (Peer peer : peers) {
            if (!hexId.equals(peer.getId()))
                routingTable.update(peer);
        }
    }

    public void removePeer(Peer peer) {
        routingTable.removePeer(peer);
    }
}
