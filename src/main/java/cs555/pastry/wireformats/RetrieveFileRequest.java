package cs555.pastry.wireformats;

import java.net.Socket;

public class RetrieveFileRequest extends StoreFile {
    private Socket socket;

    public RetrieveFileRequest(String fileName) {
        super(fileName, new byte[0]);
    }

    public RetrieveFileRequest(byte[] bytes, Socket socket) {
        super(bytes);
        this.socket = socket;
    }

    @Override
    public int getProtocol() {
        return Protocol.RETRIEVE_FILE_REQUEST;
    }

    @Override
    public String toString() {
        return "RetrieveFileRequest{" +
            "fileName='" + getFileName() + '\'' +
            ", data.length=" + getData().length +
            '}';
    }

    public Socket getSocket() {
        return socket;
    }
}
