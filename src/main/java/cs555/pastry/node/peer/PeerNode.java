package cs555.pastry.node.peer;

import cs555.pastry.node.Node;
import cs555.pastry.routing.DistributedHashTable;
import cs555.pastry.routing.Peer;
import cs555.pastry.transport.TcpConnection;
import cs555.pastry.transport.TcpSender;
import cs555.pastry.transport.TcpServer;
import cs555.pastry.util.Utils;
import cs555.pastry.wireformats.*;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PeerNode implements Node {
    private final TcpServer tcpServer;
    private TcpConnection discoveryNodeTcpConnection;
    private DistributedHashTable distributedHashTable;

    public PeerNode(int port, String discoveryNodeIp, int discoveryNodePort) {
        tcpServer = new TcpServer(port, this);

        registerWithDiscoveryNode(discoveryNodeIp, discoveryNodePort);
    }

    private void registerWithDiscoveryNode(String discoveryNodeIp, int discoveryNodePort) {
        try {
            Socket socket = new Socket(discoveryNodeIp, discoveryNodePort);
            discoveryNodeTcpConnection = new TcpConnection(socket, this);
            RegisterRequest request = new RegisterRequest(Utils.generateHexIdFromTimestamp(), Utils.getServerAddress(tcpServer));
            discoveryNodeTcpConnection.send(request.getBytes());
        } catch (IOException e) {
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
            case Protocol.JOIN_RESPONSE:
                handleJoinResponse((JoinResponse) message);
                break;
            default:
                throw new RuntimeException(String.format("received an unknown message with protocol %d", protocol));
        }
    }

    private void handleJoinRequest(JoinRequest request) {
        if (distributedHashTable.getLeafSet().getLeftNeighborId().isEmpty() &&
                distributedHashTable.getLeafSet().getRightNeighborId().isEmpty()) {
            // we are the second node to enter the network
            sendJoinResponse(request);
            return;
        }
        else if (distributedHashTable.getLeafSet().getLeftNeighborId().equals(distributedHashTable.getLeafSet().getRightNeighborId())) {
            // we are the thrid node to enter the network
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

            JoinRequest joinRequest = new JoinRequest(destinationHexId, createUpdatedRoute(route), routingTable);
            Peer peer = distributedHashTable.getPeer(lookup);
            TcpSender tcpSender = TcpSender.of(peer.getIp());
            if (tcpSender != null)
                tcpSender.send(joinRequest.getBytes());
        }
        else
            sendJoinResponse(request);
    }

    private void sendJoinResponse(JoinRequest request) {
        JoinResponse joinResponse = new JoinResponse(getHexId(), createUpdatedRoute(request.getRoute()), distributedHashTable.getLeafSet(), request.getRoutingTable());
        TcpSender tcpSender = TcpSender.of(request.getInitPeerIp());
        if (tcpSender != null)
            tcpSender.send(joinResponse.getBytes());
    }

    private void handleJoinResponse(JoinResponse response) {
        String destinationHexId = response.getDestinationHexId();
        String lookup = distributedHashTable.lookup(destinationHexId);
        List<String> route = response.getRoute();
        Peer[][] routingTable = response.getRoutingTable();
        String[] leafSet = response.getLeafSet();

        // todo only node in network
        // todo second node in network
        //


    }

    private List<String> createUpdatedRoute(List<String> route) {
        List<String> r = new ArrayList<>();
        r.addAll(route);
        r.add(getHexId());
        return r;
    }

    private void handleRegisterResponse(RegisterResponse response) {
        if (response.isRegistrationSuccess()) {
            Utils.info("registered with discovery node as " + response.getAssignedId());
            Utils.info("joining DHT via random peer " + response.getRandomPeerId());

            distributedHashTable = new DistributedHashTable(response.getAssignedId());
            String randomPeerId = response.getRandomPeerId();
            if (!randomPeerId.isEmpty() && !getHexId().equals(randomPeerId)) {
                JoinRequest joinRequest = new JoinRequest(getHexId(), Collections.emptyList(), distributedHashTable.getRoutingTable());
                String[] splitServerAddress = Utils.splitServerAddress(randomPeerId);
                try {
                    Socket socket = new Socket(splitServerAddress[0], Integer.parseInt(splitServerAddress[1]));
                    TcpSender tcpSender = new TcpSender(socket);
                    tcpSender.send(joinRequest.getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }
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
}
