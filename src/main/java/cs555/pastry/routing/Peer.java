package cs555.pastry.routing;

import cs555.pastry.wireformats.WireformatUtils;

import java.io.DataInputStream;
import java.io.DataOutputStream;

public class Peer {
    private final String id;
    private final String address;

    public Peer(String id, String address) {
        this.id = id;
        this.address = address;
    }

    public String getId() {
        return id;
    }

    public String getAddress() {
        return address;
    }

    @Override
    public String toString() {
        return "Peer{" +
            "id='" + id + '\'' +
            ", address='" + address + '\'' +
            '}';
    }

    public static Peer deserialize(DataInputStream dataInputStream) {
        String id = WireformatUtils.deserializeString(dataInputStream);
        String address = WireformatUtils.deserializeString(dataInputStream);
        return new Peer(id, address);
    }

    public void serialize(DataOutputStream dataOutputStream) {
        WireformatUtils.serializeString(dataOutputStream, getId());
        WireformatUtils.serializeString(dataOutputStream, getAddress());
    }
}
