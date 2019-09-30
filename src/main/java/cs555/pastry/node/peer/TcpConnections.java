package cs555.pastry.node.peer;

import cs555.pastry.transport.TcpConnection;
import cs555.pastry.util.Utils;

import java.io.IOException;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TcpConnections {
    private final PeerNode peerNode;
    private Map<String, TcpConnection> connections = new ConcurrentHashMap<>();

    public TcpConnections(PeerNode peerNode) {
        this.peerNode = peerNode;
    }

    public TcpConnection getTcpConnection(String address) {
        String ip = Utils.getIpFromAddress(address);

        synchronized (connections) {
            if (connections.containsKey(ip))
                return connections.get(ip);

            try {
                return new TcpConnection(new Socket(ip, peerNode.getPort()), peerNode);
            }
            catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
    }
}
