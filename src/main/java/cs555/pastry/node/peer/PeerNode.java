package cs555.pastry.node.peer;

import cs555.pastry.node.Node;
import cs555.pastry.routing.DistributedHashTable;
import cs555.pastry.routing.LeafSet;
import cs555.pastry.routing.Peer;
import cs555.pastry.transport.TcpConnection;
import cs555.pastry.transport.TcpSender;
import cs555.pastry.transport.TcpServer;
import cs555.pastry.util.Utils;
import cs555.pastry.wireformats.*;
import cs555.pastry.wireformats.debug.LeafSetRequest;
import cs555.pastry.wireformats.debug.LeafSetResponse;

import java.io.IOException;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

public class PeerNode implements Node {
    private static final int FORGET_ME_TTL = 25;
    private static final String TMP_DIR = "/tmp";
    private static final String USER_NAME = System.getProperty("user.name");
    private final int port;
    private final TcpServer tcpServer;
    private final TcpConnections tcpConnections;
    private final Path storageDir;
    private TcpConnection discoveryNodeTcpConnection;
    private DistributedHashTable distributedHashTable;

    public PeerNode(int port, String discoveryNodeIp, int discoveryNodePort) {
        this.port = port;
        tcpServer = new TcpServer(port, this);
        tcpConnections = new TcpConnections(this);
        storageDir = Paths.get(TMP_DIR, USER_NAME, "pastry");

        registerWithDiscoveryNode(discoveryNodeIp, discoveryNodePort);
    }

    private void registerWithDiscoveryNode(String discoveryNodeIp, int discoveryNodePort) {
        try {
            Socket socket = new Socket(discoveryNodeIp, discoveryNodePort);
            discoveryNodeTcpConnection = new TcpConnection(socket, this);
            RegisterRequest request = new RegisterRequest(Utils.generateHexIdFromTimestamp(), Utils.getIpFromAddress(socket.getLocalSocketAddress().toString()));
            discoveryNodeTcpConnection.send(request.getBytes());
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void run() {
        new Thread(tcpServer).start();
        Utils.sleep(500);

        handleCmdLineInput();
    }

    @Override
    public synchronized void onMessage(Message message) {
        int protocol = message.getProtocol();
        switch (protocol) {
            case Protocol.REGISTER_RESPONSE:
                handleRegisterResponse((RegisterResponse) message);
                break;
            case Protocol.JOIN_REQUEST:
                handleJoinRequest((JoinRequest) message);
                break;
            case Protocol.JOIN_RESPONSE:
                handleJoinResponse((JoinResponse) message);
                break;
            case Protocol.LEAF_SET_UPDATE:
                handleLeafSetUpdate((LeafSetUpdate) message);
                break;
            case Protocol.ROUTING_TABLE_UPDATE:
                handleRoutingTableUpdate((RoutingTableUpdate) message);
                break;
            case Protocol.LEAF_SET_REQUEST:
                handleLeafSetRequset((LeafSetRequest) message);
                break;
            case Protocol.FORGET_ME:
                handleForgetMe((ForgetMe) message);
                break;
            case Protocol.LOOKUP_REQUEST:
                handleLookupRequest((LookupRequest) message);
                break;
            case Protocol.STORE_FILE:
                handleStoreFile((StoreFile) message);
                break;
            case Protocol.RETRIEVE_FILE_REQUEST:
                handleRetrieveFileRequest((RetrieveFileRequest) message);
                break;
            default:
                throw new RuntimeException(String.format("received an unknown message with protocol %d", protocol));
        }
    }

    private void handleRetrieveFileRequest(RetrieveFileRequest request) {
        Utils.debug("received: " + request);
        Path writePath = generateWritePath(request.getFileName());
        if (!writePath.toFile().exists())
            return;

        Utils.debug("reading: " + writePath);
        byte[] data = Utils.readFileToBytes(writePath);
        RetrieveFileResponse response = new RetrieveFileResponse(request.getFileName(), data);
        Utils.debug("sending: " + response + " to " + request.getSocket().getRemoteSocketAddress());
        TcpSender tcpSender = TcpSender.of(Utils.getIpFromAddress(request.getSocket().getRemoteSocketAddress().toString()) + ":" + getPort());
        tcpSender.send(response.getBytes());
    }

    private void handleStoreFile(StoreFile message) {
        Utils.debug("received: " + message);
        Path writePath = generateWritePath(message.getFileName());
        Utils.debug("writing: " + writePath);
        Utils.writeBytesToFile(writePath, message.getData());
    }

    private Path generateWritePath(String fileName) {
        return Paths.get(storageDir.toString(), fileName);
    }

    private void handleLookupRequest(LookupRequest request) {
        Utils.debug("received: " + request);

        String destinationHexId = request.getDestinationHexId();
        List<String> route = request.getRoute();

        String lookup = distributedHashTable.lookup(destinationHexId);
        if (!getHexId().equals(lookup)) {
            LookupRequest lookupRequest = new LookupRequest(request.getSourceAddress(), destinationHexId, createUpdatedRoute(route));
            Peer peer = distributedHashTable.getPeer(lookup);
            if (peer == null) {
                Utils.error("failed to get peer using hex id: " + lookup);
                distributedHashTable.printState();
                return;
            }
            TcpConnection tcpConnection = getTcpConnection(peer.getAddress());
            if (tcpConnection != null) {
                Utils.debug("sending: " + lookupRequest);
                tcpConnection.send(lookupRequest.getBytes());
                // todo print join request
            }
        }
        else {
            LookupResponse lookupResponse = new LookupResponse(request.getSourceAddress(), destinationHexId, createUpdatedRoute(request.getRoute()), new Peer(getHexId(), getIp()));
            TcpConnection tcpConnection = getTcpConnection(request.getSourceAddress());
            if (tcpConnection != null) {
                Utils.debug("sending: " + lookupResponse);
                tcpConnection.send(lookupResponse.getBytes());
                // todo print join response
            }
        }
    }

    private void handleForgetMe(ForgetMe message) {
        Utils.debug("received: " + message);
        distributedHashTable.removePeer(message.getPeer());
        distributedHashTable.printState();

        int ttl = message.getTtl() - 1;
        if (ttl <= 0)
            return;

        message.setTtl(ttl);

        TcpConnection rightTcpConnection = getTcpConnection(distributedHashTable.getLeafSet().getRightNeighborAddress());
        rightTcpConnection.send(message.getBytes());
    }

    private void handleLeafSetRequset(LeafSetRequest request) {
        Utils.debug("received: " + request);
        LeafSetResponse response = new LeafSetResponse(new Peer(getHexId(), getIp()), distributedHashTable.getLeafSet());
        Utils.debug("sending: " + response);
        discoveryNodeTcpConnection.send(response.getBytes());
    }

    private void handleRoutingTableUpdate(RoutingTableUpdate update) {
        Utils.debug("received: " + update);
        distributedHashTable.updateRoutingTable(update.getPeers());
        distributedHashTable.printState();
    }

    private void handleLeafSetUpdate(LeafSetUpdate update) {
        Utils.debug("received: " + update);

        if (update.isLeftNeighbor())
            distributedHashTable.setLeftNeighbor(update.getPeer());
        else
            distributedHashTable.setRightNeighbor(update.getPeer());

        distributedHashTable.printState();
    }

    private void handleJoinRequest(JoinRequest request) {
        Utils.debug("received: " + request);

        if (distributedHashTable.getLeafSet().getLeftNeighborId().isEmpty() &&
            distributedHashTable.getLeafSet().getRightNeighborId().isEmpty()) {
            // request is for the second node to enter the network
            sendJoinResponse(request);
            return;
        }
        else if (distributedHashTable.getLeafSet().getLeftNeighborId().equals(distributedHashTable.getLeafSet().getRightNeighborId())) {
            // request is for the third node to enter the network
            sendJoinResponse(request);
            return;
        }

        String destinationHexId = request.getDestinationHexId();
        List<String> route = request.getRoute();
        List<Peer> routingTablePeers = request.getRoutingTablePeers();

        if (route.contains(createMyHopString())) {
            Utils.error("i've seen this message before!");
            new Exception().printStackTrace();
            return;
        }

        String lookup = distributedHashTable.lookup(destinationHexId);
        Utils.debug("lookup for destination (" + destinationHexId + "): " + lookup);
        if (lookup.isEmpty()) {
            Utils.error("lookup failed for: " + destinationHexId);
            new Exception().printStackTrace();
            return;
        }

        if (!getHexId().equals(lookup)) {
            List<Peer> tableRowPeers = distributedHashTable.getTableRow(destinationHexId);
            for (Peer peer : tableRowPeers) {
                if (!routingTablePeers.contains(peer))
                    routingTablePeers.add(peer);
            }

            JoinRequest joinRequest = new JoinRequest(request.getSourceAddress(), destinationHexId, createUpdatedRoute(route), routingTablePeers);
            Peer peer = distributedHashTable.getPeer(lookup);
            if (peer == null) {
                Utils.error("failed to get peer using hex id: " + lookup);
                distributedHashTable.printState();
                return;
            }
            TcpConnection tcpConnection = getTcpConnection(peer.getAddress());
            if (tcpConnection != null) {
                Utils.debug("sending: " + joinRequest);
                tcpConnection.send(joinRequest.getBytes());
                // todo print join request
            }
        }
        else
            sendJoinResponse(request);
    }

    private void sendJoinResponse(JoinRequest request) {
        JoinResponse joinResponse = new JoinResponse(request.getSourceAddress(), getHopId(request.getInitHop()), createUpdatedRoute(request.getRoute()), distributedHashTable.getLeafSet(), request.getRoutingTablePeers());
        TcpConnection tcpConnection = getTcpConnection(request.getSourceAddress());
        if (tcpConnection != null) {
            Utils.debug("sending: " + joinResponse);
            tcpConnection.send(joinResponse.getBytes());
            // todo print join response
        }
    }

    private void handleJoinResponse(JoinResponse response) {
        Utils.debug("received: " + response);

        Peer me = new Peer(getHexId(), getIp());

        LeafSet leafSet = response.getLeafSet();

        if (leafSet.getLeftNeighborId().isEmpty() && leafSet.getRightNeighborId().isEmpty()) {
            // we are the second node to enter the network
            String sourceAddress = response.getRemoteSocketAddress();
            Peer peer = new Peer(getHopId(response.getLastHop()), sourceAddress);
            distributedHashTable.setLeftNeighbor(peer);
            distributedHashTable.setRightNeighbor(peer);

            TcpConnection tcpConnection = tcpConnections.getTcpConnection(sourceAddress);
            if (tcpConnection != null) {
                tcpConnection.send(new LeafSetUpdate(me, true).getBytes());
                tcpConnection.send(new LeafSetUpdate(me, false).getBytes());
            }
        }
        else if (leafSet.getLeftNeighborId().equals(leafSet.getRightNeighborId())) {
            // we are the third node to enter the network
            String sourceId = getHopId(response.getLastHop());
            String otherId = leafSet.getLeftNeighborId();

            TcpConnection sourceTcpConnection = getTcpConnection(response.getRemoteSocketAddress());
            TcpConnection otherTcpConnection = getTcpConnection(leafSet.getLeftNeighborAddress());

            Peer otherPeer = new Peer(leafSet.getLeftNeighborId(), leafSet.getLeftNeighborAddress());
            Peer sourcePeer = new Peer(sourceId, response.getRemoteSocketAddress());

            boolean isSourceLowerThanOther = Utils.getHexIdDecimalDifference(otherId, sourceId) > 0;
            if ((Utils.getHexIdDecimalDifference(getHexId(), sourceId) > 0 && Utils.getHexIdDecimalDifference(otherId, getHexId()) > 0) ||
                (Utils.getHexIdDecimalDifference(getHexId(), otherId) > 0 && Utils.getHexIdDecimalDifference(sourceId, getHexId()) > 0)) {
                if (isSourceLowerThanOther) {
                    // S < X < O
                    Utils.debug("S < X < O");
                    Utils.debug(sourceId + " < " + getHexId() + " < " + otherId);
                    distributedHashTable.setLeftNeighbor(sourcePeer);
                    distributedHashTable.setRightNeighbor(otherPeer);
                    sourceTcpConnection.send(new LeafSetUpdate(me, false).getBytes());
                    otherTcpConnection.send(new LeafSetUpdate(me, true).getBytes());
                }
                else {
                    // O < X < S
                    Utils.debug("O < X < S");
                    Utils.debug(otherId + " < " + getHexId() + " < " + sourceId);
                    distributedHashTable.setLeftNeighbor(otherPeer);
                    distributedHashTable.setRightNeighbor(sourcePeer);
                    otherTcpConnection.send(new LeafSetUpdate(me, false).getBytes());
                    sourceTcpConnection.send(new LeafSetUpdate(me, true).getBytes());
                }
            }
            else {
                if (isSourceLowerThanOther) {
                    // S < O < X || X < S < O
                    Utils.debug("S < O < X || X < S < O");
                    Utils.debug(sourceId + " < " + otherId + " < " + getHexId() + " || " + getHexId() + " < " + sourceId + " < " + otherId);
                    distributedHashTable.setLeftNeighbor(otherPeer);
                    distributedHashTable.setRightNeighbor(sourcePeer);
                    otherTcpConnection.send(new LeafSetUpdate(me, false).getBytes());
                    sourceTcpConnection.send(new LeafSetUpdate(me, true).getBytes());
                }
                else {
                    // O < S < X || X < O < S
                    Utils.debug("O < S < X || X < O < S");
                    Utils.debug(otherId + " < " + sourceId + " < " + getHexId() + " || " + getHexId() + " < " + otherId + " < " + sourceId);
                    distributedHashTable.setLeftNeighbor(sourcePeer);
                    distributedHashTable.setRightNeighbor(otherPeer);
                    sourceTcpConnection.send(new LeafSetUpdate(me, false).getBytes());
                    otherTcpConnection.send(new LeafSetUpdate(me, true).getBytes());
                }
            }
        }
        else {
            String sourceId = getHopId(response.getLastHop());
            Peer sourcePeer = new Peer(sourceId, response.getRemoteSocketAddress());

            TcpConnection sourceTcpConnection = getTcpConnection(response.getRemoteSocketAddress());
            TcpConnection otherTcpConnection;
            boolean isSourceLowerThanMe = Utils.getHexIdDecimalDifference(getHexId(), sourceId) > 0;
            if (isSourceLowerThanMe) {
                otherTcpConnection = getTcpConnection(leafSet.getRightNeighborAddress());
                distributedHashTable.setLeftNeighbor(sourcePeer);
                distributedHashTable.setRightNeighbor(new Peer(leafSet.getRightNeighborId(), leafSet.getRightNeighborAddress()));

                sourceTcpConnection.send(new LeafSetUpdate(me, false).getBytes());
                otherTcpConnection.send(new LeafSetUpdate(me, true).getBytes());
            }
            else {
                otherTcpConnection = getTcpConnection(leafSet.getLeftNeighborAddress());
                distributedHashTable.setLeftNeighbor(leafSet.getLeftNeighbor());
                distributedHashTable.setRightNeighbor(sourcePeer);

                sourceTcpConnection.send(new LeafSetUpdate(me, true).getBytes());
                otherTcpConnection.send(new LeafSetUpdate(me, false).getBytes());
            }

            distributedHashTable.updateRoutingTable(response.getRoutingTablePeers());

            distributedHashTable.updateRoutingTableFromRoute(response.getRoute());

            RoutingTableUpdate routingTableUpdate = new RoutingTableUpdate(distributedHashTable.getPeers());

            sourceTcpConnection.send(routingTableUpdate.getBytes());
            otherTcpConnection.send(routingTableUpdate.getBytes());

            for (Peer peer : distributedHashTable.getPeers()) {
                TcpConnection tcpConnection = tcpConnections.getTcpConnection(peer.getAddress());
                tcpConnection.send(routingTableUpdate.getBytes());
            }
        }

        // todo print diagnostics
        distributedHashTable.printState();

        Utils.debug("sending: " + new JoinComplete(me));
        discoveryNodeTcpConnection.send(new JoinComplete(me).getBytes());
    }

    private List<String> createUpdatedRoute(List<String> route) {
        List<String> r = new ArrayList<>(route);
        r.add(createMyHopString());
        return r;
    }

    private String createMyHopString() {
        return String.format("%s:%s", getHexId(), getIp());
    }

    public static String getHopIp(String hop) {
        return hop.split(":")[1];
    }

    public static String getHopId(String hop) {
        return hop.split(":")[0];
    }

    private void handleRegisterResponse(RegisterResponse response) {
        Utils.debug("received: " + response);

        if (response.isRegistrationSuccess()) {
            Utils.info("registered with discovery node as " + response.getAssignedId());
            if (!response.getRandomPeerId().isEmpty())
                Utils.info("joining DHT via random peer " + response.getRandomPeerId() + " @ " + response.getRandomPeerAddress());

            distributedHashTable = new DistributedHashTable(response.getAssignedId());
            String randomPeerId = response.getRandomPeerId();
            if (!randomPeerId.isEmpty()) {
                JoinRequest joinRequest = new JoinRequest(getIp(), getHexId(), Collections.singletonList(createMyHopString()), Collections.emptyList());
                Utils.debug("sending: " + joinRequest);
                TcpConnection tcpConnection = getTcpConnection(response.getRandomPeerAddress());
                tcpConnection.send(joinRequest.getBytes());
            }
            else
                discoveryNodeTcpConnection.send(new JoinComplete(new Peer(getHexId(), getIp())).getBytes());
        }
        else {
            RegisterRequest registerRequest = new RegisterRequest(Utils.generateHexIdFromTimestamp(), getIp());
            discoveryNodeTcpConnection.send(registerRequest.getBytes());
        }
    }

    private String getHexId() {
        return distributedHashTable.getHexId();
    }

    private TcpConnection getTcpConnection(String ip) {
        return tcpConnections.getTcpConnection(ip);
    }

    @Override
    public String getNodeTypeAsString() {
        return "PeerNode";
    }

    public static void main(String[] args) {
        if (args.length != 3)
            printHelpAndExit();

        int port = Integer.parseInt(args[0]);
        String discoveryNodeIp = args[1];
        int discoveryNodePort = Integer.parseInt(args[2]);

        new PeerNode(port, discoveryNodeIp, discoveryNodePort).run();
    }

    private static void printHelpAndExit() {
        Utils.out("USAGE: java PeerNode <port> <discovery-node-host> <discovery-node-port>\n");
        System.exit(-1);
    }

    public int getPort() {
        return port;
    }

    public String getIp() {
        return Utils.getIpFromAddress(discoveryNodeTcpConnection.getLocalSocketAddress());
    }

    private void handleCmdLineInput() {
        printMenu();

        String input;
        Scanner scanner = new Scanner(System.in);
        scanner.useDelimiter(Pattern.compile("[\\r\\n;]+"));

        while (true) {
            Utils.out("\n");

            input = scanner.next();
            if (input.startsWith("e")) {
                Utils.info("Auf Wiedersehen");
                System.exit(0);
            }
            else if (input.startsWith("h")) {
                printMenu();
            }
            else if (input.startsWith("rn")) {
                removeNodeFromNetwork();
            }
            else if (input.startsWith("prt")) {
                printRoutingTable();
            }
            else if (input.startsWith("lf")) {
                printStoredFiles();
            }
        }
    }

    private void printStoredFiles() {
        // todo
    }

    private void printRoutingTable() {
        distributedHashTable.printState();
    }

    private void removeNodeFromNetwork() {
        ForgetMe forgetMe = new ForgetMe(FORGET_ME_TTL, new Peer(getHexId(), getIp()));

        discoveryNodeTcpConnection.send(forgetMe.getBytes());

        TcpConnection leftTcpConnection = getTcpConnection(distributedHashTable.getLeafSet().getLeftNeighborAddress());
        leftTcpConnection.send(new LeafSetUpdate(distributedHashTable.getLeafSet().getRightNeighbor(), false).getBytes());

        TcpConnection rightTcpConnection = getTcpConnection(distributedHashTable.getLeafSet().getRightNeighborAddress());
        rightTcpConnection.send(new LeafSetUpdate(distributedHashTable.getLeafSet().getLeftNeighbor(), true).getBytes());
        rightTcpConnection.send(forgetMe.getBytes());
    }

    private static void printMenu() {
        Utils.out("\n*************************************\n");
        Utils.out("h   -- print this menu\n");
        Utils.out("rn  -- remove node from network\n");
        Utils.out("prt -- print routing table and leaf set\n");
        Utils.out("lf  -- print stored files\n");
        Utils.out("***************************************\n");
    }
}
