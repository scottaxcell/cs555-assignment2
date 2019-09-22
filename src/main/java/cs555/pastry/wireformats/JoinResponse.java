package cs555.pastry.wireformats;

import cs555.pastry.routing.LeafSet;
import cs555.pastry.routing.Peer;

import java.io.*;
import java.util.List;

public class JoinResponse extends JoinRequest {
    private String[] leafSet = new String[2];

    public JoinResponse(String destinationHexId, List<String> route, LeafSet leafSet, Peer[][] routingTable) {
        super(Protocol.JOIN_RESPONSE, destinationHexId, route, routingTable);
        this.leafSet[0] = leafSet.getLeftNeighborId();
        this.leafSet[1] = leafSet.getRightNeighborId();
    }

    public JoinResponse(byte[] bytes) {
        super();

        try {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
            DataInputStream dataInputStream = new DataInputStream(new BufferedInputStream(byteArrayInputStream));

            super.deserialize(dataInputStream);

            leafSet[0] = WireformatUtils.deserializeString(dataInputStream);
            leafSet[1] = WireformatUtils.deserializeString(dataInputStream);

            byteArrayInputStream.close();
            dataInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @java.lang.Override
    public int getProtocol() {
        return super.getProtocol();
    }

    @java.lang.Override
    public byte[] getBytes() {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            DataOutputStream dataOutputStream = new DataOutputStream(new BufferedOutputStream(byteArrayOutputStream));

            byte[] joinRequestBytes = super.getBytes();
            WireformatUtils.serializeBytes(dataOutputStream, joinRequestBytes);

            WireformatUtils.serializeString(dataOutputStream, leafSet[0]);
            WireformatUtils.serializeString(dataOutputStream, leafSet[1]);

            dataOutputStream.flush();

            byte[] data = byteArrayOutputStream.toByteArray();

            byteArrayOutputStream.close();
            dataOutputStream.close();

            return data;
        } catch (IOException e) {
            e.printStackTrace();
            return new byte[0];
        }
    }

    public String[] getLeafSet() {
        return leafSet;
    }
}
