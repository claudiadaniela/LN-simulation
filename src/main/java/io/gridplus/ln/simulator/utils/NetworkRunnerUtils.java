package io.gridplus.ln.simulator.utils;

import java.util.Map;
import java.util.Set;

import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import io.gridplus.ln.generator.factory.TransfersSetup;
import io.gridplus.ln.model.LNEdge;
import io.gridplus.ln.model.LNVertex;
import io.gridplus.ln.network.utils.GraphIO;

public class NetworkRunnerUtils {

	public static void updateAndSaveTestNetworkGraph(SimpleDirectedWeightedGraph<LNVertex, LNEdge> networkGraph,
			Map<LNEdge, Integer> refunds) {
		Set<LNEdge> edges = networkGraph.edgeSet();
		for(LNEdge e: edges){
			int hopAmount = 0;
			if(refunds.containsKey(e)){
				hopAmount = refunds.get(e).intValue();
			}
			if(e.getSource().hop){
				e.setTokenAmount(hopAmount);
			}else{
				e.setTokenAmount((int)TransfersSetup.HOUSEHOLD_MAX_TOKEN_VALUE.value());
			}
			
		}
		GraphIO.writeGraphML(networkGraph,"network-topology.xml");
	}
}
