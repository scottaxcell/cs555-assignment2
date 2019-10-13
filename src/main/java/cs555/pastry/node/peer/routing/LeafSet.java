package cs555.pastry.node.peer.routing;

import cs555.pastry.util.Utils;

public class LeafSet {
    private Peer leftNeighbor;
    private Peer rightNeghbor;

    public LeafSet() {
    }

    public LeafSet(Peer leftNeighbor, Peer rightNeghbor) {
        this.leftNeighbor = leftNeighbor;
        this.rightNeghbor = rightNeghbor;
    }

    public Peer getLeftNeighbor() {
        return leftNeighbor;
    }

    public void setLeftNeighbor(Peer leftNeighbor) {
        this.leftNeighbor = leftNeighbor;
    }

    public Peer getRightNeighbor() {
        return rightNeghbor;
    }

    public void setRightNeighbor(Peer rightNeghbor) {
        this.rightNeghbor = rightNeghbor;
    }

    public String getRightNeighborId() {
        return getRightNeighbor() != null ? getRightNeighbor().getId() : "";
    }

    public String getLeftNeighborId() {
        return getLeftNeighbor() != null ? getLeftNeighbor().getId() : "";
    }

    public String getRightNeighborAddress() {
        return getRightNeighbor() != null ? getRightNeighbor().getAddress() : "";
    }

    public String getLeftNeighborAddress() {
        return getLeftNeighbor() != null ? getLeftNeighbor().getAddress() : "";
    }

    @Override
    public String toString() {
        return "LeafSet{" +
            "leftNeighbor=" + leftNeighbor +
            ", rightNeghbor=" + rightNeghbor +
            '}';
    }

    public void printState() {
        StringBuilder stringBuilder = new StringBuilder("Leaf Set\n");
        stringBuilder.append("   left  peer: ").append(leftNeighbor).append("\n");
        stringBuilder.append("   right peer: ").append(rightNeghbor).append("\n");
        stringBuilder.append("\n");
        Utils.out(stringBuilder);
    }

    public Peer getPeer(String hexId) {
        if (getLeftNeighborId().equals(hexId))
            return getLeftNeighbor();
        else if (getRightNeighborId().equals(hexId))
            return getRightNeighbor();
        return null;
    }
}
