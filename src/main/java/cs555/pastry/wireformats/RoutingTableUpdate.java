package cs555.pastry.wireformats;

import cs555.pastry.routing.Peer;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class RoutingTableUpdate implements Message {
    private List<Peer> peers = new ArrayList<>();

    public RoutingTableUpdate(List<Peer> peers) {
        this.peers = peers;
    }

    public RoutingTableUpdate(byte[] bytes) {
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

        int numPeers = WireformatUtils.deserializeInt(dataInputStream);
        for (int i = 0; i < numPeers; i++)
            peers.add(Peer.deserialize(dataInputStream));
    }

    @Override
    public int getProtocol() {
        return Protocol.ROUTING_TABLE_UPDATE;
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
        return "RoutingTableUpdate{" +
            "peers=" + peers +
            '}';
    }

    protected void serialize(DataOutputStream dataOutputStream) {
        WireformatUtils.serializeInt(dataOutputStream, getProtocol());

        WireformatUtils.serializeInt(dataOutputStream, peers.size());
        for (Peer peer : peers)
            peer.serialize(dataOutputStream);
    }

    public List<Peer> getPeers() {
        return peers;
    }
}
