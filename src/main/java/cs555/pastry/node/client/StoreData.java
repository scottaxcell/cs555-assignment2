package cs555.pastry.node.client;

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
    private String hexId;
    private Path path;

    public StoreData(Client client) {
        this.client = client;
    }

    void storeFile(Path path) {
        setIsRunning(true);
        this.path = path;
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
        byte[] bytes = Utils.readFileToBytes(path);

        StoreFile storeFile = new StoreFile(Utils.getCanonicalPath(path), bytes);
        Utils.debug("sending: " + storeFile);
        TcpSender tcpSender = TcpSender.of(response.getPeer().getAddress() + ":" + client.getPeerPort());
        tcpSender.send(storeFile.getBytes());

        setIsRunning(false);
    }

    void handleRandomPeerResponse(RandomPeerResponse response) {
        LookupRequest lookupRequest = new LookupRequest(client.getIp(), hexId, Collections.emptyList());
        TcpSender tcpSender = TcpSender.of(response.getPeer().getAddress() + ":" + client.getPeerPort());
        tcpSender.send(lookupRequest.getBytes());
    }

    public void handleRetrieveFileResponse(RetrieveFileResponse response) {
        Path path = Paths.get(String.format("./%s", Paths.get(response.getFileName()).getFileName().toString()));
        Utils.writeBytesToFile(path, response.getData());
        Utils.info("File written to " + path.toAbsolutePath());
    }
}
