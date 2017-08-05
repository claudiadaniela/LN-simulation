package io.gridplus.ln.simulation.network;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import io.gridplus.ln.simulation.model.LNEdge;
import io.gridplus.ln.simulation.model.LNVertex;
import io.gridplus.ln.simulation.model.NetworkTopology;

public class NetworkTopologyGenerator {
	
	private NetworkTopologyGenerator(){}
	
	public static NetworkTopology generateRandomTopology() {
		NetworkTopology topology = NetworkTopology.getInstance();

		List<LNVertex> hops = new ArrayList<LNVertex>();
		Random rand = new Random();

		for (int i = 0; i < SimulationSetup.NO_HOPS.value(); i++) {
			LNVertex hop = topology.addNode(i, rand.nextDouble(), new LNVertex.NetworkStatus(1));
			hops.add(hop);
			if (i - 1 >= 0) {
				LNVertex hop0 = hops.get(i - 1);
				int tokenAmountV1 = rand.nextInt(SimulationSetup.MAX_TOKEN_HOP.value()) + SimulationSetup.MAX_TOKEN_HOP.value() * SimulationSetup.NO_NODES.value() / SimulationSetup.NO_HOPS.value();
				int tokenAmountV2 = rand.nextInt(SimulationSetup.MAX_TOKEN_HOP.value()) + SimulationSetup.MAX_TOKEN_HOP.value() * SimulationSetup.NO_NODES.value() / SimulationSetup.NO_HOPS.value();
				topology.addChannel(hop0, hop, LNEdge.ChannelStatus.OPENED, tokenAmountV1, tokenAmountV2);
			}
		}

		for (int i = SimulationSetup.NO_HOPS.value(); i < SimulationSetup.NO_NODES.value() + SimulationSetup.NO_HOPS.value(); i++) {
			LNVertex v1 = topology.addNode(i, rand.nextDouble(), new LNVertex.NetworkStatus(1));
			LNVertex v2 = hops.get(rand.nextInt(SimulationSetup.NO_HOPS.value()));

			int tokenAmountV1 = rand.nextInt(SimulationSetup.MAX_TOKEN_HOP.value()/2) + SimulationSetup.MAX_TOKEN_HOP.value()/2;
			int tokenAmountV2 = rand.nextInt(SimulationSetup.MAX_TOKEN_HOP.value()/2) + SimulationSetup.MAX_TOKEN_HOP.value()/2;
			topology.addChannel(v1, v2, LNEdge.ChannelStatus.OPENED, tokenAmountV1, tokenAmountV2);
		}
		topology.setHops(hops);
		return topology;
	}
}
