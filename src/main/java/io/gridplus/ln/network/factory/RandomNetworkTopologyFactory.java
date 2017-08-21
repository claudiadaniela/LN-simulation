 package io.gridplus.ln.network.factory;

import io.gridplus.ln.model.LNEdge;
import io.gridplus.ln.model.LNVertex;
import io.gridplus.ln.model.NetworkTopology;
import io.gridplus.ln.network.utils.RandomGaussian;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class RandomNetworkTopologyFactory extends NetworkTopologyAbstractFactory {

    @Override
    public NetworkTopology createTopology(int noHops, int noNodes) {
        NetworkTopology topology = new NetworkTopology();

        List<LNVertex> hops = new ArrayList<LNVertex>();
        Random rand = new Random();
        int initTokenHop = RandomGaussian.MEAN *24;

        for (int i = 0; i < noHops; i++) {
            LNVertex hop = topology.addNode(i, rand.nextDouble(), new LNVertex.NetworkStatus(1), true);
            hops.add(hop);
            if (i - 1 >= 0) {
                LNVertex hop0 = hops.get(i - 1);
                int tokenAmountV1 = initTokenHop;
                int tokenAmountV2 = initTokenHop;
                topology.addChannel(hop0, hop, LNEdge.ChannelStatus.OPENED, tokenAmountV1, tokenAmountV2);
            }
        }

        for (int i = noHops; i < noNodes + noHops; i++) {
            LNVertex v1 = topology.addNode(i, rand.nextDouble(), new LNVertex.NetworkStatus(1),false);
            LNVertex v2 = hops.get(rand.nextInt(noHops));

            int tokenAmountV1 = initTokenHop;
            int tokenAmountV2 = initTokenHop;
            topology.addChannel(v1, v2, LNEdge.ChannelStatus.OPENED, tokenAmountV1, tokenAmountV2);
        }

        return topology;
    }
}
