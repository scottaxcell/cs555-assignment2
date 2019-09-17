package cs555.pastry.wireformats;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class MessageFactory {
    public static Message getMessageFromData(byte[] data) throws IOException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
        DataInputStream dataInputStream = new DataInputStream(new BufferedInputStream(byteArrayInputStream));

        int protocol = dataInputStream.readInt();
        switch (protocol) {
            default:
                throw new RuntimeException(String.format("received an unknown message with protocol %d", protocol));
        }
    }
}
