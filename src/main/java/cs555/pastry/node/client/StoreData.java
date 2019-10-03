package cs555.pastry.node.client;

import cs555.pastry.transport.TcpSender;
import cs555.pastry.util.Utils;
import cs555.pastry.wireformats.LookupRequest;
import cs555.pastry.wireformats.LookupResponse;
import cs555.pastry.wireformats.RandomPeerRequest;
import cs555.pastry.wireformats.RandomPeerResponse;

import java.nio.file.Path;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicBoolean;

public class StoreData {
    private final Client client;
    private AtomicBoolean isRunning = new AtomicBoolean();
    private String hexId;

    public StoreData(Client client) {
        this.client = client;
    }

    void storeFile(Path path) {
        setIsRunning(true);
        hexId = Utils.generateHexIdFromFileName(path.getFileName().toString());
        RandomPeerRequest randomPeerRequest = new RandomPeerRequest();
        client.sendDiscoveryNodeMessage(randomPeerRequest);
    }

    void retrieveFile(Path path) {
        // todo
    }

    boolean isRunning() {
        return isRunning.get();
    }

    private void setIsRunning(boolean isRunning) {
        this.isRunning.set(isRunning);
    }

    void handleLookupResponse(LookupResponse response) {
        // todo
    


        setIsRunning(false);
    }

    void handleRandomPeerResponse(RandomPeerResponse response) {
        LookupRequest lookupRequest = new LookupRequest(client.getIp(), hexId, Collections.emptyList());
        TcpSender tcpSender = TcpSender.of(response.getPeer().getAddress() + ":" + client.getPeerPort());
        tcpSender.send(lookupRequest.getBytes());
    }
}
