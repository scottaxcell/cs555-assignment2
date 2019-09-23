package cs555.pastry.wireformats;

import cs555.pastry.routing.LeafSet;
import cs555.pastry.routing.Peer;

import java.io.*;
import java.util.Arrays;
import java.util.List;

public class JoinResponse extends JoinRequest {
    private String[] leafSet = new String[2];

    public JoinResponse(String sourceAddress, String destinationHexId, List<String> route, LeafSet leafSet, Peer[][] routingTable) {
        super(Protocol.JOIN_RESPONSE, sourceAddress, destinationHexId, route, routingTable);
        this.leafSet[0] = leafSet.getLeftNeighborId();
        this.leafSet[1] = leafSet.getRightNeighborId();
    }

    public JoinResponse(byte[] bytes) {
        super();

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
        super.deserialize(dataInputStream);

        leafSet[0] = WireformatUtils.deserializeString(dataInputStream);
        leafSet[1] = WireformatUtils.deserializeString(dataInputStream);
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
        super.serialize(dataOutputStream);

        WireformatUtils.serializeString(dataOutputStream, leafSet[0]);
        WireformatUtils.serializeString(dataOutputStream, leafSet[1]);
    }

    public String[] getLeafSet() {
        return leafSet;
    }

    @Override
    public String toString() {
        return "JoinResponse{" +
            "protocol=" + getProtocol() +
            ", sourceAddress='" + getSourceAddress() + '\'' +
            ", destinationHexId='" + getDestinationHexId() + '\'' +
            ", route=" + getRoute() +
            ", table=" + Arrays.toString(getRoutingTable()) +
            ", leafSet=" + Arrays.toString(leafSet) +
            '}';
    }
}
