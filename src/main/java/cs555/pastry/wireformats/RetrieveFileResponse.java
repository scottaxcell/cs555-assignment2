package cs555.pastry.wireformats;

public class RetrieveFileResponse extends StoreFile {

    public RetrieveFileResponse(String fileName, byte[] data) {
        super(fileName, data);
    }

    public RetrieveFileResponse(byte[] bytes) {
        super(bytes);
    }

    @Override
    public int getProtocol() {
        return Protocol.RETRIEVE_FILE_RESPONSE;
    }

    @Override
    public String toString() {
        return "RetrieveFileResponse{" +
            "fileName='" + getFileName() + '\'' +
            ", data.length=" + getData().length +
            '}';
    }
}
