package cs555.pastry.node;

import cs555.pastry.transport.TcpServer;
import cs555.pastry.util.Utils;
import cs555.pastry.wireformats.Message;
import cs555.pastry.wireformats.RegisterRequest;

public class DiscoveryNode implements Node {
    private final TcpServer tcpServer;
    private final Peers peers = new Peers();

    public DiscoveryNode(int port) {
        tcpServer = new TcpServer(port, this);
    }

    private void handleRegisterRequest(RegisterRequest request) {
        Utils.debug("received: " + request);
        if (!peers.registerPeer(request.getPeer())) {
            // todo send request_failed back to peer to try again
        }
    }

    @Override
    public void onMessage(Message message) {

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

    void run() {
        new Thread(tcpServer).start();
        Utils.sleep(500);
    }

    private static void printHelpAndExit() {
        Utils.out("USAGE: java DiscoveryNode <port>\n");
        System.exit(-1);
    }

}
