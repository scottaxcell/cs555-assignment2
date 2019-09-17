package cs555.pastry.node.peer;

import cs555.pastry.node.Node;
import cs555.pastry.routing.DistributedHashTable;
import cs555.pastry.transport.TcpConnection;
import cs555.pastry.transport.TcpServer;
import cs555.pastry.util.Utils;
import cs555.pastry.wireformats.Message;
import cs555.pastry.wireformats.Protocol;
import cs555.pastry.wireformats.RegisterRequest;
import cs555.pastry.wireformats.RegisterResponse;

public class PeerNode implements Node {
    private final TcpServer tcpServer;
    private TcpConnection discoveryNodeTcpConnection;
    private DistributedHashTable distributedHashTable;

    public PeerNode(int port) {
        tcpServer = new TcpServer(port, this);
    }

    @Override
    public void onMessage(Message message) {
        int protocol = message.getProtocol();
        switch (protocol) {
            case Protocol.REGISTER_RESPONSE:
                handleRegisterResponse((RegisterResponse) message);
                break;
            default:
                throw new RuntimeException(String.format("received an unknown message with protocol %d", protocol));
        }
    }

    private void handleRegisterResponse(RegisterResponse response) {
        if (response.isRegistrationSuccess()) {
            distributedHashTable = new DistributedHashTable(response.getAssignedId());
            String randomPeerId = response.getRandomPeerId();
            if (!randomPeerId.isEmpty()) {
                // todo send special join message to random peer
            }
        }
        else {
            RegisterRequest registerRequest = new RegisterRequest(Utils.generateHexIdFromTimestamp(), Utils.getServerAddress(tcpServer));
            discoveryNodeTcpConnection.send(registerRequest.getBytes());
        }
    }

    @Override
    public String getNodeTypeAsString() {
        return "PeerNode";
    }

    public static void main(String[] args) {
        if (args.length != 1)
            printHelpAndExit();

        int port = Integer.parseInt(args[0]);

        new PeerNode(port).run();
    }

    private void run() {
        new Thread(tcpServer).start();
        Utils.sleep(500);
    }

    private static void printHelpAndExit() {
        Utils.out("USAGE: java PeerNode <port>\n");
        System.exit(-1);
    }
}
