package cs555.pastry.wireformats;

import cs555.pastry.routing.Peer;

import java.io.*;

public class RegisterRequest implements Message {
    private String id;
    private String ip;

    public RegisterRequest(String id, String ip) {
        this.id = id;
        this.ip = ip;
    }

    @Override
    public String toString() {
        return "RegisterRequest{" +
                "id='" + id + '\'' +
                ", ip='" + ip + '\'' +
                '}';
    }

    @Override
    public int getProtocol() {
        return Protocol.REGISTER_REQUEST;
    }

    @Override
    public byte[] getBytes() {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            DataOutputStream dataOutputStream = new DataOutputStream(new BufferedOutputStream(byteArrayOutputStream));

            WireformatUtils.serializeString(dataOutputStream, id);
            WireformatUtils.serializeString(dataOutputStream, ip);

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

    public RegisterRequest(byte[] bytes) {
        try {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
            DataInputStream dataInputStream = new DataInputStream(new BufferedInputStream(byteArrayInputStream));

            id = WireformatUtils.deserializeString(dataInputStream);
            ip = WireformatUtils.deserializeString(dataInputStream);

            byteArrayInputStream.close();
            dataInputStream.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Peer getPeer() {
        return new Peer(id, ip);
    }
}
