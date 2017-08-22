 package io.gridplus.ln.network.factory;

import io.gridplus.ln.model.LNEdge;
import io.gridplus.ln.model.LNVertex;
import io.gridplus.ln.model.NetworkTopology;
import io.gridplus.ln.generator.utils.GaussianConsumptionGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class RandomNetworkTopologyFactory extends NetworkTopologyAbstractFactory {

    private static double hop_fee=0.01;
    @Override
    public NetworkTopology createTopology(int noHops, int noNodes) {
        NetworkTopology topology = new NetworkTopology();

        List<LNVertex> hops = new ArrayList<LNVertex>();
        Random rand = new Random();
        int initTokenHop = GaussianConsumptionGenerator.MEAN *24;

        for (int i = 0; i < noHops; i++) {
            LNVertex hop = topology.addNode(i, hop_fee, new LNVertex.NetworkStatus(1), true);
            hops.add(hop);
            if (i - 1 >= 0) {
                LNVertex hop0 = hops.get(i - 1);
                int tokenAmountV1 = 0;
                int tokenAmountV2 = 0;
                topology.addChannel(hop0, hop, LNEdge.ChannelStatus.OPENED, tokenAmountV1, tokenAmountV2);
            }
        }

        for (int i = noHops; i < noNodes + noHops; i++) {
            LNVertex v1 = topology.addNode(i, 0, new LNVertex.NetworkStatus(1),false);
            LNVertex v2 = hops.get(rand.nextInt(noHops));

            int tokenAmountV1 = initTokenHop;
            int tokenAmountV2 = 0;
            topology.addChannel(v1, v2, LNEdge.ChannelStatus.OPENED, tokenAmountV1, tokenAmountV2);
        }

        return topology;
    }

    @Override
    public NetworkTopology createTopology(String file) {
        return new NetworkTopology();
    }
}
