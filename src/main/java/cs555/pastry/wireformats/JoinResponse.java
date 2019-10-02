package cs555.pastry.wireformats;

import cs555.pastry.routing.LeafSet;
import cs555.pastry.routing.Peer;
import cs555.pastry.util.Utils;

import java.io.*;
import java.net.Socket;
import java.util.List;

public class JoinResponse extends JoinRequest {
    private Socket socket;
    private LeafSet leafSet = new LeafSet();

    public JoinResponse(String sourceAddress, String destinationHexId, List<String> route, LeafSet leafSet, List<Peer> routingTablePeers) {
        super(Protocol.JOIN_RESPONSE, sourceAddress, destinationHexId, route, routingTablePeers);
        this.leafSet = leafSet;
    }

    public JoinResponse(byte[] bytes, Socket socket) {
        super();
        this.socket = socket;

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

        String leftNeighborId = WireformatUtils.deserializeString(dataInputStream);
        String leftNeighborAddress = WireformatUtils.deserializeString(dataInputStream);
        String rightNeighborId = WireformatUtils.deserializeString(dataInputStream);
        String rightNeighborAddress = WireformatUtils.deserializeString(dataInputStream);

        leafSet.setLeftNeighbor(new Peer(leftNeighborId, leftNeighborAddress));
        leafSet.setRightNeighbor(new Peer(rightNeighborId, rightNeighborAddress));
    }

    @Override
    public int getProtocol() {
        return super.getProtocol();
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
        super.serialize(dataOutputStream);

        WireformatUtils.serializeString(dataOutputStream, leafSet.getLeftNeighborId());
        WireformatUtils.serializeString(dataOutputStream, leafSet.getLeftNeighborAddress());
        WireformatUtils.serializeString(dataOutputStream, leafSet.getRightNeighborId());
        WireformatUtils.serializeString(dataOutputStream, leafSet.getRightNeighborAddress());
    }

    public LeafSet getLeafSet() {
        return leafSet;
    }

    @Override
    public String toString() {
        return "JoinResponse{" +
            "protocol=" + getProtocol() +
            ", sourceAddress='" + getSourceAddress() + '\'' +
            ", destinationHexId='" + getDestinationHexId() + '\'' +
            ", route=" + getRoute() +
            ", routingTablePeers=" + getRoutingTablePeers() +
            ", leafSet=" + leafSet +
            '}';
    }

    public Socket getSocket() {
        return socket;
    }

    public String getRemoteSocketAddress() {
        return Utils.getIpFromAddress(getSocket().getRemoteSocketAddress().toString());
    }
}
