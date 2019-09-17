package cs555.pastry.node.discovery;

import cs555.pastry.node.Node;
import cs555.pastry.routing.Peer;
import cs555.pastry.transport.TcpSender;
import cs555.pastry.transport.TcpServer;
import cs555.pastry.util.Utils;
import cs555.pastry.wireformats.Message;
import cs555.pastry.wireformats.Protocol;
import cs555.pastry.wireformats.RegisterRequest;
import cs555.pastry.wireformats.RegisterResponse;

import java.net.Socket;

public class DiscoveryNode implements Node {
    private final TcpServer tcpServer;
    private final Peers peers = new Peers();

    private DiscoveryNode(int port) {
        tcpServer = new TcpServer(port, this);
    }

    @Override
    public void onMessage(Message message) {
        int protocol = message.getProtocol();
        switch (protocol) {
            case Protocol.REGISTER_REQUEST:
                handleRegisterRequest((RegisterRequest) message);
                break;
            default:
                throw new RuntimeException(String.format("received an unknown message with protocol %d", protocol));
        }
    }

    private void handleRegisterRequest(RegisterRequest request) {
        Utils.debug("received: " + request);

        RegisterResponse registerResponse = new RegisterResponse();
        if (!peers.registerPeer(request.getPeer()))
            registerResponse.setRegistrationSuccess(false);
        else {
            registerResponse.setRegistrationSuccess(true);
            registerResponse.setAssignedId(request.getPeer().getId());
            Peer randomPeer = peers.getRandomPeer();
            if (randomPeer != null)
                registerResponse.setRandomPeerId(randomPeer.getId());
        }
        Socket socket = request.getSocket();
        TcpSender tcpSender = new TcpSender(socket);
        tcpSender.send(registerResponse.getBytes());
    }

    @Override
    public String getNodeTypeAsString() {
        return "DiscoveryNode";
    }

    public static void main(String[] args) {
        if (args.length != 1)
            printHelpAndExit();

        int port = Integer.parseInt(args[0]);

        new DiscoveryNode(port).run();
    }

    private void run() {
        new Thread(tcpServer).start();
        Utils.sleep(500);
    }

    private static void printHelpAndExit() {
        Utils.out("USAGE: java DiscoveryNode <port>\n");
        System.exit(-1);
    }
}
