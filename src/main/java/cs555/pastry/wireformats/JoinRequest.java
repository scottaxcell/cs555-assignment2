package cs555.pastry.wireformats;

public class JoinRequest implements Message {


    @Override
    public int getProtocol() {
        return Protocol.JOIN_REQUEST;
    }

    @Override
    public byte[] getBytes() {
        return new byte[0];
    }
}
