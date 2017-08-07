package io.gridplus.ln.simulator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import io.gridplus.ln.model.LNEdge;
import io.gridplus.ln.model.LNVertex;
import io.gridplus.ln.model.NetworkTopology;

public class NetworkTopologyGenerator {
	
	private NetworkTopologyGenerator(){}
	
	public static NetworkTopology generateRandomTopology(int noHops, int noNodes, int initTokenHop) {
		NetworkTopology topology = NetworkTopology.getInstance();

		List<LNVertex> hops = new ArrayList<LNVertex>();
		Random rand = new Random();

		for (int i = 0; i < noHops; i++) {
			LNVertex hop = topology.addNode(i, rand.nextDouble(), new LNVertex.NetworkStatus(1));
			hops.add(hop);
			if (i - 1 >= 0) {
				LNVertex hop0 = hops.get(i - 1);
				int tokenAmountV1 = rand.nextInt(initTokenHop) + initTokenHop * noNodes / noHops;
				int tokenAmountV2 = rand.nextInt(initTokenHop) + initTokenHop* noNodes / noHops;
				topology.addChannel(hop0, hop, LNEdge.ChannelStatus.OPENED, tokenAmountV1, tokenAmountV2);
			}
		}

		for (int i = noHops; i < noNodes + noHops; i++) {
			LNVertex v1 = topology.addNode(i, rand.nextDouble(), new LNVertex.NetworkStatus(1));
			LNVertex v2 = hops.get(rand.nextInt(noHops));

			int tokenAmountV1 = rand.nextInt(initTokenHop/2) + initTokenHop/2;
			int tokenAmountV2 = rand.nextInt(initTokenHop/2) + initTokenHop/2;
			topology.addChannel(v1, v2, LNEdge.ChannelStatus.OPENED, tokenAmountV1, tokenAmountV2);
		}
		topology.setHops(hops);
		return topology;
	}
}
