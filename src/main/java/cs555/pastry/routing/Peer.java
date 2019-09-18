package cs555.pastry.routing;

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
}
