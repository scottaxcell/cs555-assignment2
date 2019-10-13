package cs555.pastry.wireformats;

import cs555.pastry.node.peer.routing.Peer;

import java.io.*;

public class RandomPeerResponse implements Message {
    private Peer peer;

    public RandomPeerResponse(Peer peer) {
        this.peer = peer;
    }

    public RandomPeerResponse(byte[] bytes) {
        try {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
            DataInputStream dataInputStream = new DataInputStream(new BufferedInputStream(byteArrayInputStream));

            deserialize(dataInputStream);

            byteArrayInputStream.close();
            dataInputStream.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deserialize(DataInputStream dataInputStream) {
        int protocol = WireformatUtils.deserializeInt(dataInputStream);
        peer = Peer.deserialize(dataInputStream);
    }

    @Override
    public int getProtocol() {
        return Protocol.RANDOM_PEER_RESPONSE;
    }

    @Override
    public byte[] getBytes() {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            DataOutputStream dataOutputStream = new DataOutputStream(new BufferedOutputStream(byteArrayOutputStream));

            serialize(dataOutputStream);

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

    @Override
    public String toString() {
        return "RandomPeerResponse{" +
            "peer=" + peer +
            '}';
    }

    protected void serialize(DataOutputStream dataOutputStream) {
        WireformatUtils.serializeInt(dataOutputStream, getProtocol());
        peer.serialize(dataOutputStream);
    }

    public Peer getPeer() {
        return peer;
    }
}
