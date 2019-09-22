package cs555.pastry.routing;

import cs555.pastry.wireformats.WireformatUtils;

import java.io.DataInputStream;
import java.io.DataOutputStream;

public class Peer {
    private final String id;
    private final String ip;

    public Peer(String id, String ip) {
        this.id = id;
        this.ip = ip;
    }

    public String getId() {
        return id;
    }

    public String getIp() {
        return ip;
    }

    @Override
    public String toString() {
        return "Peer{" +
                "id='" + id + '\'' +
                ", ip='" + ip + '\'' +
                '}';
    }

    public static Peer deserialize(DataInputStream dataInputStream) {
        String id = WireformatUtils.deserializeString(dataInputStream);
        String ip = WireformatUtils.deserializeString(dataInputStream);
        return new Peer(id, ip);
    }

    public void serialize(DataOutputStream dataOutputStream) {
        WireformatUtils.serializeString(dataOutputStream, getId());
        WireformatUtils.serializeString(dataOutputStream, getIp());
    }
}
