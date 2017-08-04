package io.gridplus.ln.simulation.network;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import io.gridplus.ln.simulation.model.LNEdge;
import io.gridplus.ln.simulation.model.LNVertex;
import io.gridplus.ln.simulation.model.NetworkTopology;

public class NetworkTopologyGenerator {
	private static final int V_TOKEN = 100;
	
	private NetworkTopologyGenerator(){}
	
	public static NetworkTopology generateRandomTopology(int noHops, int size) {
		NetworkTopology topology = new NetworkTopology();

		List<LNVertex> hops = new ArrayList<LNVertex>();
		Random rand = new Random();

		for (int i = 0; i < noHops; i++) {
			LNVertex hop = topology.addNode(i, rand.nextDouble(), new LNVertex.NetworkStatus(1));
			hops.add(hop);
			if (i - 1 >= 0) {
				LNVertex hop0 = hops.get(i - 1);
				int tokenAmountV1 = rand.nextInt(100) + V_TOKEN * size / noHops;
				int tokenAmountV2 = rand.nextInt(100) + V_TOKEN * size / noHops;
				topology.addChannel(hop0, hop, LNEdge.ChannelStatus.OPENED, tokenAmountV1, tokenAmountV2);
				System.out.println("Hop Channel: " + hop0 + "-" + hop);
			}
		}

		for (int i = noHops; i < size + noHops; i++) {
			LNVertex v1 = topology.addNode(i, rand.nextDouble(), new LNVertex.NetworkStatus(1));
			LNVertex v2 = hops.get(rand.nextInt(noHops));

			int tokenAmountV1 = rand.nextInt(50) + V_TOKEN;
			int tokenAmountV2 = rand.nextInt(50) + V_TOKEN;
			topology.addChannel(v1, v2, LNEdge.ChannelStatus.OPENED, tokenAmountV1, tokenAmountV2);
			System.out.println("Channel: " + v1 + "-" + v2);
		}
		return topology;
	}
}
