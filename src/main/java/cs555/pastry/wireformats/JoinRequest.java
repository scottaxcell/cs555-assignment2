package cs555.pastry.wireformats;

import cs555.pastry.routing.Peer;
import cs555.pastry.util.Utils;

import java.io.*;
import java.util.Arrays;
import java.util.List;

public class JoinRequest extends Lookup {
    private Peer[][] table = new Peer[Utils.NUM_16_BIT_ID_DIGITS][];


    public JoinRequest() {
    }

    public JoinRequest(String sourceAddress, String destinationHexId, List<String> route, Peer[][] routingTable) {
        super(Protocol.JOIN_REQUEST, sourceAddress, destinationHexId, route);
        table = routingTable;
    }

    protected JoinRequest(int protocol, String sourceAddress, String destinationHexId, List<String> route, Peer[][] routingTable) {
        super(protocol, sourceAddress, destinationHexId, route);
        table = routingTable;
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
            int row = WireformatUtils.deserializeInt(dataInputStream);
            int col = WireformatUtils.deserializeInt(dataInputStream);
            Peer peer = Peer.deserialize(dataInputStream);
            table[row][col] = peer;
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

        int numPeers = 0;
        for (int row = 0; row < table.length; row++) {
            if (table[row] == null)
                continue;
            for (int col = 0; col < table[row].length; col++) {
                Peer peer = table[row][col];
                if (peer != null)
                    numPeers++;
            }
        }
        WireformatUtils.serializeInt(dataOutputStream, numPeers);

        for (int row = 0; row < table.length; row++) {
            if (table[row] == null)
                continue;
            for (int col = 0; col < table[row].length; col++) {
                Peer peer = table[row][col];
                if (peer != null) {
                    WireformatUtils.serializeInt(dataOutputStream, row);
                    WireformatUtils.serializeInt(dataOutputStream, col);
                    peer.serialize(dataOutputStream);
                }
            }
        }
    }

    public Peer[][] getRoutingTable() {
        return table;
    }

    /**
     * ip for the peer that originated this join request
     */
    public String getInitPeerAddress() {
        return getRoute().get(0);
    }

    @Override
    public String toString() {
        return "JoinRequest{" +
            "protocol=" + getProtocol() +
            ", sourceAddress='" + getSourceAddress() + '\'' +
            ", destinationHexId='" + getDestinationHexId() + '\'' +
            ", route=" + getRoute() +
            ", table=" + Arrays.toString(table) +
            '}';
    }
}
