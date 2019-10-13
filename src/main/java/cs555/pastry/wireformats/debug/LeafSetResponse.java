package cs555.pastry.wireformats.debug;

import cs555.pastry.node.peer.routing.LeafSet;
import cs555.pastry.node.peer.routing.Peer;
import cs555.pastry.wireformats.Message;
import cs555.pastry.wireformats.Protocol;
import cs555.pastry.wireformats.WireformatUtils;

import java.io.*;

public class LeafSetResponse implements Message {
    private Peer peer;
    private LeafSet leafSet = new LeafSet();

    public LeafSetResponse(Peer peer, LeafSet leafSet) {
        this.peer = peer;
        this.leafSet = leafSet;
    }

    public LeafSetResponse(byte[] bytes) {
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

        String leftNeighborId = WireformatUtils.deserializeString(dataInputStream);
        String leftNeighborAddress = WireformatUtils.deserializeString(dataInputStream);
        String rightNeighborId = WireformatUtils.deserializeString(dataInputStream);
        String rightNeighborAddress = WireformatUtils.deserializeString(dataInputStream);

        leafSet.setLeftNeighbor(new Peer(leftNeighborId, leftNeighborAddress));
        leafSet.setRightNeighbor(new Peer(rightNeighborId, rightNeighborAddress));
    }

    @Override
    public int getProtocol() {
        return Protocol.LEAF_SET_RESPONSE;
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

    protected void serialize(DataOutputStream dataOutputStream) {
        WireformatUtils.serializeInt(dataOutputStream, getProtocol());

        peer.serialize(dataOutputStream);

        WireformatUtils.serializeString(dataOutputStream, leafSet.getLeftNeighborId());
        WireformatUtils.serializeString(dataOutputStream, leafSet.getLeftNeighborAddress());
        WireformatUtils.serializeString(dataOutputStream, leafSet.getRightNeighborId());
        WireformatUtils.serializeString(dataOutputStream, leafSet.getRightNeighborAddress());
    }

    public LeafSet getLeafSet() {
        return leafSet;
    }

    public Peer getPeer() {
        return peer;
    }

    @Override
    public String toString() {
        return "LeafSetResponse{" +
            "peer=" + peer +
            ", leafSet=" + leafSet +
            '}';
    }
}
