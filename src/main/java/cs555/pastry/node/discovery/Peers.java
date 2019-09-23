package cs555.pastry.node.discovery;

import cs555.pastry.routing.Peer;
import cs555.pastry.util.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Peers {
    private final List<Peer> peers = Collections.synchronizedList(new ArrayList<>());

    public boolean peerIdAlreadyRegistered(String peerId) {
        synchronized (peers) {
            return peers.stream()
                    .anyMatch(p -> p.getId().equals(peerId));
        }
    }

    public Peer getRandomPeer() {
        synchronized (peers) {
            if (peers.isEmpty())
                return null;
            int randomPeerIdx = ThreadLocalRandom.current().nextInt(peers.size());
            return peers.get(randomPeerIdx);
        }
    }

    public boolean registerPeer(Peer peer) {
        if (peerIdAlreadyRegistered(peer.getId()))
            return false;

        synchronized (peers) {
            Utils.info("registered peer " + peer.getId() + " @ " + peer.getAddress());
            return peers.add(peer);
        }
    }
}
