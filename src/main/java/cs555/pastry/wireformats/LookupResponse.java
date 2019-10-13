package cs555.pastry.wireformats;

import cs555.pastry.node.peer.routing.Peer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.List;

public class LookupResponse extends LookupRequest {
    private Peer peer;

    public LookupResponse(String sourceAddress, String destinationHexId, List<String> route, Peer peer) {
        super(Protocol.LOOKUP_RESPONSE, sourceAddress, destinationHexId, route);
        this.peer = peer;
    }

    public LookupResponse(byte[] bytes) {
        super(bytes);
    }

    @Override
    public void deserialize(DataInputStream dataInputStream) {
        super.deserialize(dataInputStream);

        peer = Peer.deserialize(dataInputStream);
    }

    @Override
    protected void serialize(DataOutputStream dataOutputStream) {
        super.serialize(dataOutputStream);

        peer.serialize(dataOutputStream);
    }

    public Peer getPeer() {
        return peer;
    }

    @Override
    public String toString() {
        return "LookupResponse{" +
            "protocol=" + getProtocol() +
            ", sourceAddress='" + getSourceAddress() + '\'' +
            ", destinationHexId='" + getDestinationHexId() + '\'' +
            ", route=" + getRoute() +
            ", peer=" + peer +
            '}';
    }
}
