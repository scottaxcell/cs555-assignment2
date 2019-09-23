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

    public Peer getRandomPeer(String excludingId) {
        synchronized (peers) {
            if (peers.isEmpty() || peers.size() == 1)
                return null;

            Peer peer = peers.get(ThreadLocalRandom.current().nextInt(peers.size()));

            while (peer.getId().equals(excludingId))
                peer = peers.get(ThreadLocalRandom.current().nextInt(peers.size()));

            return peer;
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
