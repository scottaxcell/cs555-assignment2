package cs555.pastry.wireformats;

import cs555.pastry.routing.Peer;

import java.io.*;

public class LeafSetUpdate implements Message {
    private Peer peer;
    private boolean isLeftNeighbor;

    public LeafSetUpdate(Peer peer, boolean isLeftNeighbor) {
        this.peer = peer;
        this.isLeftNeighbor = isLeftNeighbor;
    }

    public LeafSetUpdate(byte[] bytes) {
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
        String id = WireformatUtils.deserializeString(dataInputStream);
        String address = WireformatUtils.deserializeString(dataInputStream);
        peer = new Peer(id, address);
        isLeftNeighbor = WireformatUtils.deserializeBoolean(dataInputStream);
    }

    @Override
    public int getProtocol() {
        return Protocol.LEAF_SET_UPDATE;
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
        return "LeafSetUpdate{" +
            "peer=" + peer +
            ", isLeftNeighbor=" + isLeftNeighbor +
            '}';
    }

    protected void serialize(DataOutputStream dataOutputStream) {
        WireformatUtils.serializeInt(dataOutputStream, getProtocol());
        WireformatUtils.serializeString(dataOutputStream, peer.getId());
        WireformatUtils.serializeString(dataOutputStream, peer.getAddress());
        WireformatUtils.serializeBoolean(dataOutputStream, isLeftNeighbor);
    }

    public Peer getPeer() {
        return peer;
    }

    public boolean isLeftNeighbor() {
        return isLeftNeighbor;
    }
}
