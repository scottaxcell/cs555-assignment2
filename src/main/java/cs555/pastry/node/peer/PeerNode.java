package cs555.pastry.node.peer;

import cs555.pastry.node.Node;
import cs555.pastry.routing.DistributedHashTable;
import cs555.pastry.transport.TcpConnection;
import cs555.pastry.transport.TcpServer;
import cs555.pastry.util.Utils;
import cs555.pastry.wireformats.*;

import java.io.IOException;
import java.net.Socket;

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
            default:
                throw new RuntimeException(String.format("received an unknown message with protocol %d", protocol));
        }
    }

    private void handleJoinRequest(JoinRequest request) {

    }

    private void handleRegisterResponse(RegisterResponse response) {
        if (response.isRegistrationSuccess()) {
            Utils.info("registered with discovery node as " + response.getAssignedId());
            Utils.info("joining DHT via random peer " + response.getRandomPeerId());

            distributedHashTable = new DistributedHashTable(response.getAssignedId());
            String randomPeerId = response.getRandomPeerId();
            if (!randomPeerId.isEmpty() && !getHexId().equals(randomPeerId)) {
                // todo send special join message to random peer
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
