package cs555.pastry.wireformats;

public interface Message {
    int getProtocol();

    byte[] getBytes();
}
