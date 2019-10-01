package cs555.pastry.wireformats;

import cs555.pastry.wireformats.debug.LeafSetRequest;
import cs555.pastry.wireformats.debug.LeafSetResponse;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

public class MessageFactory {
    public static Message getMessageFromData(byte[] data, Socket socket) throws IOException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
        DataInputStream dataInputStream = new DataInputStream(new BufferedInputStream(byteArrayInputStream));

        int protocol = WireformatUtils.deserializeInt(dataInputStream);
        switch (protocol) {
            case Protocol.REGISTER_REQUEST:
                return new RegisterRequest(data, socket);
            case Protocol.REGISTER_RESPONSE:
                return new RegisterResponse(data);
            case Protocol.JOIN_REQUEST:
                return new JoinRequest(data);
            case Protocol.JOIN_RESPONSE:
                return new JoinResponse(data, socket);
            case Protocol.LEAF_SET_UPDATE:
                return new LeafSetUpdate(data);
            case Protocol.ROUTING_TABLE_UPDATE:
                return new RoutingTableUpdate(data);
            case Protocol.JOIN_COMPLETE:
                return new JoinComplete(data);
            case Protocol.LEAF_SET_REQUEST:
                return new LeafSetRequest(data);
            case Protocol.LEAF_SET_RESPONSE:
                return new LeafSetResponse(data);
            default:
                throw new RuntimeException(String.format("received an unknown message with protocol %d", protocol));
        }
    }
}
