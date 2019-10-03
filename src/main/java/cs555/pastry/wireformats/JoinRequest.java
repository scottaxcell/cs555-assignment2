package cs555.pastry.wireformats;

import cs555.pastry.routing.Peer;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class JoinRequest extends LookupRequest {
    private List<Peer> routingTablePeers = new ArrayList<>();

    public JoinRequest() {
    }

    public JoinRequest(String sourceAddress, String destinationHexId, List<String> route, List<Peer> routingTablePeers) {
        super(Protocol.JOIN_REQUEST, sourceAddress, destinationHexId, route);
        this.routingTablePeers = routingTablePeers;
    }

    protected JoinRequest(int protocol, String sourceAddress, String destinationHexId, List<String> route, List<Peer> routingTablePeers) {
        super(protocol, sourceAddress, destinationHexId, route);
        this.routingTablePeers = routingTablePeers;
    }

    public JoinRequest(byte[] bytes) {
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

        int numPeers = WireformatUtils.deserializeInt(dataInputStream);
        for (int i = 0; i < numPeers; i++) {
            routingTablePeers.add(Peer.deserialize(dataInputStream));
        }
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

        WireformatUtils.serializeInt(dataOutputStream, routingTablePeers.size());
        for (Peer peer : routingTablePeers)
            peer.serialize(dataOutputStream);
    }

    public List<Peer> getRoutingTablePeers() {
        return routingTablePeers;
    }

    @Override
    public String toString() {
        return "JoinRequest{" +
            "protocol=" + getProtocol() +
            ", sourceAddress='" + getSourceAddress() + '\'' +
            ", destinationHexId='" + getDestinationHexId() + '\'' +
            ", route=" + getRoute() +
            ", routingTablePeers=" + routingTablePeers +
            '}';
    }
}
