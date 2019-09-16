package cs555.pastry;

public class LeafSet {
    private Peer leftNeighbor;
    private Peer rightNeghbor;

    public Peer getLeftNeighbor() {
        return leftNeighbor;
    }

    public void setLeftNeighbor(Peer leftNeighbor) {
        this.leftNeighbor = leftNeighbor;
    }

    public Peer getRightNeghbor() {
        return rightNeghbor;
    }

    public void setRightNeghbor(Peer rightNeghbor) {
        this.rightNeghbor = rightNeghbor;
    }
}
