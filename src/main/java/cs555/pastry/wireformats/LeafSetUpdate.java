package cs555.pastry.wireformats;

import cs555.pastry.routing.LeafSet;
import cs555.pastry.routing.Peer;

import java.io.*;

public class LeafSetUpdate implements Message {
    private LeafSet leafSet = new LeafSet();

    public LeafSetUpdate(LeafSet leafSet) {
        this.leafSet = leafSet;
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
        String leftNeighborId = WireformatUtils.deserializeString(dataInputStream);
        String leftNeighborAddress = WireformatUtils.deserializeString(dataInputStream);
        String rightNeighborId = WireformatUtils.deserializeString(dataInputStream);
        String rightNeighborAddress = WireformatUtils.deserializeString(dataInputStream);

        leafSet.setLeftNeighbor(new Peer(leftNeighborId, leftNeighborAddress));
        leafSet.setRightNeighbor(new Peer(rightNeighborId, rightNeighborAddress));
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
            "leafSet=" + leafSet +
            '}';
    }

    protected void serialize(DataOutputStream dataOutputStream) {
        WireformatUtils.serializeInt(dataOutputStream, getProtocol());
        WireformatUtils.serializeString(dataOutputStream, leafSet.getLeftNeighborId());
        WireformatUtils.serializeString(dataOutputStream, leafSet.getLeftNeighborAddress());
        WireformatUtils.serializeString(dataOutputStream, leafSet.getRightNeighborId());
        WireformatUtils.serializeString(dataOutputStream, leafSet.getRightNeighborAddress());
    }

    public LeafSet getLeafSet() {
        return leafSet;
    }
}
