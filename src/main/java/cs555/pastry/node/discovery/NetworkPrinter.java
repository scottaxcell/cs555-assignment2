package cs555.pastry.node.discovery;

import cs555.pastry.routing.LeafSet;
import cs555.pastry.routing.Peer;
import cs555.pastry.transport.TcpSender;
import cs555.pastry.util.Utils;
import cs555.pastry.wireformats.debug.LeafSetRequest;
import cs555.pastry.wireformats.debug.LeafSetResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class NetworkPrinter {
    private final List<Peer> pendingPeers = new ArrayList<>();
    private final Map<Peer, LeafSet> peerToLeafSet = new ConcurrentHashMap<>();

    public void print(List<Peer> peers) {
        synchronized (pendingPeers) {
            pendingPeers.clear();
            pendingPeers.addAll(peers);

            for (Peer peer : pendingPeers) {
                TcpSender tcpSender = TcpSender.of(String.format("%s:%d", peer.getAddress(), 1328));
                tcpSender.send(new LeafSetRequest().getBytes());
            }
        }
    }

    public void handleLeafSetResponse(LeafSetResponse response) {
        synchronized (pendingPeers) {
            LeafSet leafSet = response.getLeafSet();
            Peer peer = response.getPeer();
            pendingPeers.remove(peer);

            peerToLeafSet.put(peer, leafSet);

            if (pendingPeers.isEmpty()) {
                Utils.debug("RECEIVED ALL LEAFSET RESPONSES");
                // todo print network

                synchronized (peerToLeafSet) {
                    Utils.info("Network Topology");
                    Utils.out("      ================\n");
                    peerToLeafSet.entrySet().stream()
                        .sorted((a, b) -> Utils.getHexIdDecimalDifference(a.getKey().getId(), b.getKey().getId()) > 0 ? 1 : Utils.getHexIdDecimalDifference(b.getKey().getId(), a.getKey().getId()) > 0 ? -1 : 0)
                        .forEach(e -> {
                            Utils.out(
                                String.format("      %s <-- %s --> %s\n",
                                    e.getValue().getLeftNeighborId(),
                                    e.getKey().getId() + " (" + e.getKey().getAddress() + ")",
                                    e.getValue().getRightNeighborId()
                                ));
                        });
                }
            }
        }
    }

}
