package cs555.pastry.wireformats;

import java.io.*;

public class StoreFile implements Message {
    private String fileName;
    private byte[] data;

    public StoreFile(String fileName, byte[] data) {
        this.fileName = fileName;
        this.data = data;
    }

    public StoreFile(byte[] bytes) {
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
        fileName = WireformatUtils.deserializeString(dataInputStream);
        data = WireformatUtils.deserializeBytes(dataInputStream);
    }

    @Override
    public int getProtocol() {
        return Protocol.STORE_FILE;
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
        return "StoreFile{" +
            "fileName='" + fileName + '\'' +
            ", data.length=" + data.length +
            '}';
    }

    protected void serialize(DataOutputStream dataOutputStream) {
        WireformatUtils.serializeInt(dataOutputStream, getProtocol());
        WireformatUtils.serializeString(dataOutputStream, fileName);
        WireformatUtils.serializeBytes(dataOutputStream, data);
    }

    public String getFileName() {
        return fileName;
    }

    public byte[] getData() {
        return data;
    }
}
