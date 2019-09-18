package cs555.pastry.routing;

import cs555.pastry.util.Utils;

public class LeafSet {
    private Peer leftNeighbor;
    private Peer rightNeghbor;

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
}
