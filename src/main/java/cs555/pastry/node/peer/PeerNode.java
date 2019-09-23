package cs555.pastry.node.peer;

import cs555.pastry.node.Node;
import cs555.pastry.routing.DistributedHashTable;
import cs555.pastry.routing.Peer;
import cs555.pastry.transport.TcpConnection;
import cs555.pastry.transport.TcpServer;
import cs555.pastry.util.Utils;
import cs555.pastry.wireformats.*;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PeerNode implements Node {
    private final int port;
    private final TcpServer tcpServer;
    private TcpConnections tcpConnections;
    private TcpConnection discoveryNodeTcpConnection;
    private DistributedHashTable distributedHashTable;

    public PeerNode(int port, String discoveryNodeIp, int discoveryNodePort) {
        this.port = port;
        tcpServer = new TcpServer(port, this);
        tcpConnections = new TcpConnections(this);

        registerWithDiscoveryNode(discoveryNodeIp, discoveryNodePort);
    }

    private void registerWithDiscoveryNode(String discoveryNodeIp, int discoveryNodePort) {
        try {
            Socket socket = new Socket(discoveryNodeIp, discoveryNodePort);
            discoveryNodeTcpConnection = new TcpConnection(socket, this);
            RegisterRequest request = new RegisterRequest(Utils.generateHexIdFromTimestamp(), Utils.getServerAddress(tcpServer));
            discoveryNodeTcpConnection.send(request.getBytes());
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void run() {
        new Thread(tcpServer).start();
        Utils.sleep(500);
    }

    @Override
    public void onMessage(Message message) {
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
            default:
                throw new RuntimeException(String.format("received an unknown message with protocol %d", protocol));
        }
    }

    private void handleJoinRequest(JoinRequest request) {
        Utils.debug("recevied: " + request);

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
        Peer[][] routingTable = request.getRoutingTable();

        String lookup = distributedHashTable.lookup(destinationHexId);
        if (lookup.isEmpty()) {
            Utils.error("lookup failed for: " + destinationHexId);
            new Exception().printStackTrace();
            return;
        }

        if (!destinationHexId.equals(lookup)) {
            Peer[] nextTableRow = distributedHashTable.getTableRow(destinationHexId);
            int tableRowIndex = route.size() - 1;
            routingTable[tableRowIndex] = nextTableRow;

            JoinRequest joinRequest = new JoinRequest(request.getSourceAddress(), destinationHexId, createUpdatedRoute(route), routingTable);
            Peer peer = distributedHashTable.getPeer(lookup);
            TcpConnection tcpConnection = getTcpConnection(peer.getAddress());
            if (tcpConnection != null) {
                tcpConnection.send(joinRequest.getBytes());
                // todo print join request
            }
        }
        else
            sendJoinResponse(request);
    }

    private void sendJoinResponse(JoinRequest request) {
        JoinResponse joinResponse = new JoinResponse(getHexId(), request.getSourceAddress(), createUpdatedRoute(request.getRoute()), distributedHashTable.getLeafSet(), request.getRoutingTable());
        TcpConnection tcpConnection = getTcpConnection(request.getSourceAddress());
        if (tcpConnection != null) {
            Utils.debug("sending: " + joinResponse);
            tcpConnection.send(joinResponse.getBytes());
            // todo print join response
        }
    }

    private void handleJoinResponse(JoinResponse response) {
        Utils.debug("received: " + response);

        String[] leafSet = response.getLeafSet();

        if (leafSet[0].isEmpty() && leafSet[1].isEmpty()) {
            // we are the second node to enter the network
            // todo update and send leafset update
        }
        else if (leafSet[0].equals(leafSet[1])) {
            // we are the third node to enter the network
            // todo update and send leafset update
        }
        else {
            // todo update and send leafset update
            // todo update routing table and send routing table update
        }

        // todo print diagnostics
        distributedHashTable.printState();
    }

    private List<String> createUpdatedRoute(List<String> route) {
        List<String> r = new ArrayList<>();
        r.addAll(route);
        r.add(getHexId());
        return r;
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
                JoinRequest joinRequest = new JoinRequest(Utils.getServerAddress(tcpServer), getHexId(), Collections.singletonList(getHexId()), distributedHashTable.getRoutingTable());
                TcpConnection tcpConnection = getTcpConnection(response.getRandomPeerAddress());
                tcpConnection.send(joinRequest.getBytes());
            }
        }
        else {
            RegisterRequest registerRequest = new RegisterRequest(Utils.generateHexIdFromTimestamp(), Utils.getServerAddress(tcpServer));
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
}
