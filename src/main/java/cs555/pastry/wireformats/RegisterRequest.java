package cs555.pastry.wireformats;

import cs555.pastry.node.peer.routing.Peer;

import java.io.*;
import java.net.Socket;

public class RegisterRequest implements Message {
    private Socket socket;
    private String id;
    private String address;

    public RegisterRequest(String id, String address) {
        this.id = id;
        this.address = address;
    }

    @Override
    public String toString() {
        return "RegisterRequest{" +
            "id='" + id + '\'' +
            ", address='" + address + '\'' +
            '}';
    }

    @Override
    public int getProtocol() {
        return Protocol.REGISTER_REQUEST;
    }

    @Override
    public byte[] getBytes() {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            DataOutputStream dataOutputStream = new DataOutputStream(new BufferedOutputStream(byteArrayOutputStream));

            WireformatUtils.serializeInt(dataOutputStream, getProtocol());
            WireformatUtils.serializeString(dataOutputStream, id);
            WireformatUtils.serializeString(dataOutputStream, address);

            dataOutputStream.flush();

            byte[] data = byteArrayOutputStream.toByteArray();

            byteArrayOutputStream.close();
            dataOutputStream.close();

            return data;
        }
        catch (IOException e) {
            e.printStackTrace();
            return new byte[0];
        }
    }

    public RegisterRequest(byte[] bytes, Socket socket) {
        this.socket = socket;

        try {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
            DataInputStream dataInputStream = new DataInputStream(new BufferedInputStream(byteArrayInputStream));

            int protocol = WireformatUtils.deserializeInt(dataInputStream);
            id = WireformatUtils.deserializeString(dataInputStream);
            address = WireformatUtils.deserializeString(dataInputStream);

            byteArrayInputStream.close();
            dataInputStream.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Socket getSocket() {
        return socket;
    }

    public Peer getPeer() {
        return new Peer(id, address);
    }
}
