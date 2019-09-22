package cs555.pastry.wireformats;

import cs555.pastry.routing.Peer;
import cs555.pastry.util.Utils;

import java.io.*;
import java.util.List;

public class JoinRequest extends Lookup {
    private Peer[][] table = new Peer[Utils.NUM_16_BIT_ID_DIGITS][];

    public JoinRequest(String destinationHexId, List<String> route, Peer[][] routingTable) {
        super(Protocol.JOIN_REQUEST, destinationHexId, route);
        table = routingTable;
    }

    protected JoinRequest(int protocol, String destinationHexId, List<String> route, Peer[][] routingTable) {
        super(protocol, destinationHexId, route);
        table = routingTable;
    }

    public JoinRequest(byte[] bytes) {
        super();

        try {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
            DataInputStream dataInputStream = new DataInputStream(new BufferedInputStream(byteArrayInputStream));

            super.deserialize(dataInputStream);

            int numPeers = WireformatUtils.deserializeInt(dataInputStream);
            for (int i = 0; i < numPeers; i++) {
                int row = WireformatUtils.deserializeInt(dataInputStream);
                int col = WireformatUtils.deserializeInt(dataInputStream);
                Peer peer = Peer.deserialize(dataInputStream);
                table[row][col] = peer;
            }

            byteArrayInputStream.close();
            dataInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public JoinRequest() {
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

            byte[] lookupBytes = super.getBytes();
            WireformatUtils.serializeBytes(dataOutputStream, lookupBytes);

            int numPeers = 0;
            for (int row = 0; row < table.length; row++) {
                for (int col = 0; col < table[row].length; col++) {
                    Peer peer = table[row][col];
                    if (peer != null)
                        numPeers++;
                }
            }
            WireformatUtils.serializeInt(dataOutputStream, numPeers);

            for (int row = 0; row < table.length; row++) {
                for (int col = 0; col < table[row].length; col++) {
                    Peer peer = table[row][col];
                    if (peer != null) {
                        WireformatUtils.serializeInt(dataOutputStream, row);
                        WireformatUtils.serializeInt(dataOutputStream, col);
                        peer.serialize(dataOutputStream);
                    }
                }
            }

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

    public Peer[][] getRoutingTable() {
        return table;
    }
}
