package io.gridplus.ln.simulation.test;

import io.gridplus.ln.simulation.model.LNEdge;
import io.gridplus.ln.simulation.model.LNVertex;
import io.gridplus.ln.simulation.model.NetworkTopology;
import org.junit.Before;

public class NetworkTopologyTest {
    protected static NetworkTopology networkTop;
    protected static final double EPSILON = 0.000001;
    @Before
    public  void init() {
        networkTop = NetworkTopology.getInstance();
        networkTop.resetGraph();
        LNVertex v0 = networkTop.addNode(0, 0.01, new LNVertex.NetworkStatus(1));
        LNVertex v1 = networkTop.addNode(1, 0, new LNVertex.NetworkStatus(1));
        LNVertex v2 = networkTop.addNode(2, 0, new LNVertex.NetworkStatus(1));
        LNVertex v3 = networkTop.addNode(3, 0, new LNVertex.NetworkStatus(1));
        LNVertex v4 = networkTop.addNode(4, 0, new LNVertex.NetworkStatus(1));

        networkTop.addChannel(v0, v1, LNEdge.ChannelStatus.OPENED, 3, 0);
        networkTop.addChannel(v0, v2, LNEdge.ChannelStatus.OPENED, 4, 0);
        networkTop.addChannel(v0, v3, LNEdge.ChannelStatus.OPENED, 5, 0);

        networkTop.addChannel(v1, v2, LNEdge.ChannelStatus.OPENED, 2, 0);

        networkTop.addChannel(v2, v3, LNEdge.ChannelStatus.OPENED, 4, 0);
        networkTop.addChannel(v2, v4, LNEdge.ChannelStatus.OPENED, 1, 0);

        networkTop.addChannel(v3, v4, LNEdge.ChannelStatus.OPENED, 10, 0);
    }
}
