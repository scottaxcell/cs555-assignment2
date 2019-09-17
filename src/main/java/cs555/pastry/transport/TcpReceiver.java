package cs555.pastry.transport;

import cs555.pastry.node.Node;
import cs555.pastry.wireformats.Message;
import cs555.pastry.wireformats.MessageFactory;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

public class TcpReceiver implements Runnable {
    private Socket socket;
    private DataInputStream dataInputStream;
    private Node node;

    public TcpReceiver(Socket socket, Node node) {
        this.socket = socket;
        this.node = node;
        try {
            dataInputStream = new DataInputStream(socket.getInputStream());
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Socket getSocket() {
        return socket;
    }

    @Override
    public void run() {
        while (socket != null) {
            try {
                int dataLength = dataInputStream.readInt();
                byte[] data = new byte[dataLength];
                dataInputStream.readFully(data, 0, dataLength);
                Message message = MessageFactory.getMessageFromData(data);
                node.onMessage(message);
            }
            catch (IOException e) {
                break;
            }
        }
    }
}
