package cs555.pastry.node.client;

import cs555.pastry.node.peer.PeerNode;
import cs555.pastry.transport.TcpSender;
import cs555.pastry.util.Utils;
import cs555.pastry.wireformats.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicBoolean;

public class StoreData {
    private final Client client;
    private AtomicBoolean isRunning = new AtomicBoolean();
    private AtomicBoolean isReading = new AtomicBoolean();
    private String hexId;
    private Path path;

    public StoreData(Client client) {
        this.client = client;
    }

    void storeFile(Path path) {
        setIsRunning(true);

        this.path = path;
        hexId = Utils.generateHexIdFromFileName(path.getFileName().toString());
        Utils.debug("fileName hex ID: " + hexId);
        RandomPeerRequest randomPeerRequest = new RandomPeerRequest();
        Utils.debug("sending: " + randomPeerRequest);
        client.sendDiscoveryNodeMessage(randomPeerRequest);
    }

    void retrieveFile(Path path) {
        setIsRunning(true);
        setIsReading(true);

        this.path = path;
        hexId = Utils.generateHexIdFromFileName(path.getFileName().toString());
        RandomPeerRequest randomPeerRequest = new RandomPeerRequest();
        Utils.debug("sending: " + randomPeerRequest);
        client.sendDiscoveryNodeMessage(randomPeerRequest);
    }

    boolean isRunning() {
        return isRunning.get();
    }

    private void setIsRunning(boolean isRunning) {
        this.isRunning.set(isRunning);
    }

    private boolean isReading() {
        return isReading.get();
    }

    private void setIsReading(boolean isReading) {
        this.isReading.set(isReading);
    }

    void handleLookupResponse(LookupResponse response) {
        StringBuilder sb = new StringBuilder("Lookup response route (" + (response.getRoute().size() + 1) + "): ");
        for (String hop :response.getRoute())
            sb.append(PeerNode.getHopId(hop)).append(" ");
        Utils.info(sb.toString());

        if (isReading()) {
            Utils.info("Retrieving file " + hexId + " from peer " + response.getPeer().getId());

            RetrieveFileRequest request = new RetrieveFileRequest(Utils.getCanonicalPath(path));
            Utils.debug("sending: " + request);
            TcpSender tcpSender = TcpSender.of(response.getPeer().getAddress() + ":" + client.getPeerPort());
            tcpSender.send(request.getBytes());
        }
        else {
            Utils.info("Storing file " + hexId + " at peer " + response.getPeer().getId());

            byte[] bytes = Utils.readFileToBytes(path);
            StoreFile storeFile = new StoreFile(Utils.getCanonicalPath(path), bytes);
            Utils.debug("sending: " + storeFile);
            TcpSender tcpSender = TcpSender.of(response.getPeer().getAddress() + ":" + client.getPeerPort());
            tcpSender.send(storeFile.getBytes());

            setIsRunning(false);
        }
    }

    void handleRandomPeerResponse(RandomPeerResponse response) {
        Utils.info("Sending lookup request for " + hexId + " to random peer " + response.getPeer().getId());
        LookupRequest lookupRequest = new LookupRequest(client.getIp(), hexId, Collections.emptyList());
        TcpSender tcpSender = TcpSender.of(response.getPeer().getAddress() + ":" + client.getPeerPort());
        tcpSender.send(lookupRequest.getBytes());
    }

    void handleRetrieveFileResponse(RetrieveFileResponse response) {
        Path path = Paths.get(String.format("./%s", Paths.get(response.getFileName()).getFileName().toString()));
        Utils.writeBytesToFile(path, response.getData());
        Utils.sleep(1500);
        Utils.info("File written to " + path.toAbsolutePath());

        setIsRunning(false);
        setIsReading(false);
    }
}
