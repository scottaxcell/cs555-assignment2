package cs555.pastry.wireformats;

import cs555.pastry.routing.Peer;

import java.io.*;

public class ForgetMe implements Message {
    private int ttl;
    private Peer peer;

    public ForgetMe(int ttl, Peer peer) {
        this.ttl = ttl;
        this.peer = peer;
    }

    public ForgetMe(byte[] bytes) {
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
        int protocol = WireformatUtils.deserializeInt(dataInputStream);
        ttl = WireformatUtils.deserializeInt(dataInputStream);
        peer = Peer.deserialize(dataInputStream);
    }

    @Override
    public int getProtocol() {
        return Protocol.FORGET_ME;
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

    @Override
    public String toString() {
        return "ForgetMe{" +
            "ttl=" + ttl +
            ", peer=" + peer +
            '}';
    }

    protected void serialize(DataOutputStream dataOutputStream) {
        WireformatUtils.serializeInt(dataOutputStream, getProtocol());
        WireformatUtils.serializeInt(dataOutputStream, ttl);
        peer.serialize(dataOutputStream);
    }

    public Peer getPeer() {
        return peer;
    }

    public int getTtl() {
        return ttl;
    }

    public void setTtl(int ttl) {
        this.ttl = ttl;
    }
}
