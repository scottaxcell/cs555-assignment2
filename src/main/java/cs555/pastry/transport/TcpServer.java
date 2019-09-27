package cs555.pastry.transport;

import cs555.pastry.node.Node;
import cs555.pastry.util.Utils;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class TcpServer implements Runnable {
    private ServerSocket serverSocket;
    private Node node;

    public TcpServer(int port, Node node) {
        this.node = node;
        try {
            serverSocket = new ServerSocket(port);
            Utils.info(String.format("%s TCP server started on %s:%d (%s)", node.getNodeTypeAsString(), getIp(), getPort(), serverSocket.getInetAddress().getLocalHost().getHostAddress()));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getIp() {
        try {
            return serverSocket.getInetAddress().getLocalHost().getCanonicalHostName();
        }
        catch (UnknownHostException e) {
            e.printStackTrace();
            return "";
        }
    }

    public int getPort() {
        return serverSocket.getLocalPort();
    }

    @Override
    public void run() {
        try {
            while (!Thread.interrupted()) {
                Socket socket = serverSocket.accept();
                new Thread(new TcpReceiver(socket, node)).start();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
