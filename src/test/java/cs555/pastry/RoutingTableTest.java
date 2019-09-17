package cs555.pastry;

import cs555.pastry.util.Utils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class RoutingTableTest {
    private RoutingTable routingTable = new RoutingTable("1234");

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void lookup() {
    }

    @Test
    public void toStringTest() {
        RoutingTable routingTable = new RoutingTable("65a1");
        routingTable.update(new Peer("65a3", ""));
        routingTable.update(new Peer("3f45", ""));
        routingTable.update(new Peer("65a2", ""));
        routingTable.update(new Peer("63df", ""));
        routingTable.update(new Peer("6000", ""));
        routingTable.update(new Peer("65af", ""));
        routingTable.update(new Peer("1111", ""));
        routingTable.update(new Peer("1112", ""));
        routingTable.update(new Peer("6fff", ""));
        routingTable.update(new Peer("65e3", ""));
        Utils.debug("\n" + routingTable);
    }
}