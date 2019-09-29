package cs555.pastry;

import cs555.pastry.routing.Peer;
import cs555.pastry.routing.RoutingTable;
import cs555.pastry.util.Utils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
        routingTable.printState();

        String lookup = routingTable.lookup("63ff");
        Assert.assertEquals("63df", lookup);

        lookup = routingTable.lookup("65af");
        Assert.assertEquals("65af", lookup);

        lookup = routingTable.lookup("1111");
        Assert.assertEquals("1111", lookup);

        lookup = routingTable.lookup("0032");
        Assert.assertEquals("004b", lookup);

        lookup = routingTable.lookup("65ef");
        Assert.assertEquals("65e3", lookup);
    }

    @Test
    public void toStringTest() {
        Utils.debug("\n" + routingTable);
    }

    @Test
    public void comparatorTest() {
        List<String> ids = new ArrayList<>();
        ids.add("1111");
        ids.add("0032");
        ids.add("65a1");
        Utils.debug(Arrays.toString(ids.toArray(new String[0])));
        Collections.sort(ids, (a, b) -> Utils.getHexIdDecimalDifference(a, b) > 0 ? 1 : Utils.getHexIdDecimalDifference(b, a) > 0 ? -1 : 0);
        Utils.debug(Arrays.toString(ids.toArray(new String[0])));

        ids.clear();
        ids.add("3635");
        ids.add("3937");
        ids.add("3218");
        Utils.debug(Arrays.toString(ids.toArray(new String[0])));
        Collections.sort(ids, (a, b) -> Utils.getHexIdDecimalDifference(a, b) > 0 ? 1 : Utils.getHexIdDecimalDifference(b, a) > 0 ? -1 : 0);
        Utils.debug(Arrays.toString(ids.toArray(new String[0])));
    }

    @Test
    public void closestComparatorTest() {
        List<String> peers = new ArrayList<>();
        peers.add("3739");
        peers.add("3534");
        peers.add("3939");
        Utils.debug(Arrays.toString(peers.toArray(new String[0])));
        Collections.sort(peers, (hexId1, hexId2) -> {
            int absoluteHexDiff1 = Utils.getAbsoluteHexIdDecimalDifference("3438", hexId1);
            int absoluteHexDiff2 = Utils.getAbsoluteHexIdDecimalDifference("3438", hexId2);
            if (absoluteHexDiff2 < absoluteHexDiff1)
                return 1;
            else if (absoluteHexDiff1 < absoluteHexDiff2)
                return -1;
            else
                return Utils.getHexIdDecimalDifference(hexId1, hexId2) > 0 ? -1 : 1;
        });
        Utils.debug(Arrays.toString(peers.toArray(new String[0])));
    }
}