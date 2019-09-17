package cs555.pastry;

import cs555.pastry.routing.Peer;
import cs555.pastry.routing.RoutingTable;
import cs555.pastry.util.Utils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class RoutingTableTest {
    private RoutingTable routingTable = new RoutingTable("65a1");

    @Before
    public void setUp() throws Exception {
        routingTable.update(new Peer("65a3", ""));
        routingTable.update(new Peer("3f45", ""));
        routingTable.update(new Peer("65a2", ""));
        routingTable.update(new Peer("63df", ""));
        routingTable.update(new Peer("6000", ""));
        routingTable.update(new Peer("65af", ""));
        routingTable.update(new Peer("1111", ""));
        routingTable.update(new Peer("6fff", ""));
        routingTable.update(new Peer("004b", ""));
        routingTable.update(new Peer("65e3", ""));
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void lookup() {
        String lookup = routingTable.lookup("63ff");
        Assert.assertEquals("63df", lookup);

        lookup = routingTable.lookup("65af");
        Assert.assertEquals("65af", lookup);

        lookup = routingTable.lookup("1111");
        Assert.assertEquals("1111", lookup);

        lookup = routingTable.lookup("0032");
        Assert.assertEquals("004b", lookup);
    }

    @Test
    public void toStringTest() {
        Utils.debug("\n" + routingTable);
    }
}