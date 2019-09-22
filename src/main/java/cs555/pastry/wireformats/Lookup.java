package cs555.pastry.wireformats;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Lookup implements Message {
    private int protocol;
    private String destinationHexId;
    private List<String> route = new ArrayList<>();

    public Lookup(String destinationHexId, List<String> route) {
        this(Protocol.LOOKUP, destinationHexId, route);
    }

    protected Lookup(int protocol, String destinationHexId, List<String> route) {
        this.protocol = protocol;
        this.destinationHexId = destinationHexId;
        this.route.addAll(route);
    }

    public Lookup(byte[] bytes) {
        try {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
            DataInputStream dataInputStream = new DataInputStream(new BufferedInputStream(byteArrayInputStream));

            deserialize(dataInputStream);

            byteArrayInputStream.close();
            dataInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Lookup() {
    }

    @Override
    public int getProtocol() {
        return protocol;
    }

    @Override
    public byte[] getBytes() {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            DataOutputStream dataOutputStream = new DataOutputStream(new BufferedOutputStream(byteArrayOutputStream));

            WireformatUtils.serializeInt(dataOutputStream, getProtocol());
            WireformatUtils.serializeString(dataOutputStream, destinationHexId);
            WireformatUtils.serializeInt(dataOutputStream, route.size());
            for (String peerId : route)
                WireformatUtils.serializeString(dataOutputStream, peerId);

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

    public void deserialize(DataInputStream dataInputStream) {
        protocol = WireformatUtils.deserializeInt(dataInputStream);
        destinationHexId = WireformatUtils.deserializeString(dataInputStream);
        int numHops = WireformatUtils.deserializeInt(dataInputStream);
        for (int i = 0; i < numHops; i++)
            route.add(WireformatUtils.deserializeString(dataInputStream));
    }

    public int getNumHops() {
        return route.size();
    }

    public List<String> getRoute() {
        return route;
    }
}
