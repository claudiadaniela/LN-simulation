package io.gridplus.ln.simulation.scheduler;

import java.util.List;

import io.gridplus.ln.simulation.model.Transfer;
import io.gridplus.ln.simulation.network.NetworkClientRunner;

public class ShortestQueueStrategy implements SchedulerStrategy {

	public void dispatchTransfer(List<Transfer> transfers, List<NetworkClientRunner> clients) {
		int minSize = Integer.MAX_VALUE;
		NetworkClientRunner minRunner = null;
		for (Transfer t : transfers) {
			for (NetworkClientRunner runner : clients) {
				if (runner.getSize() < minSize) {
					minSize = runner.getSize();
					minRunner = runner;
				}
			}
			minRunner.addTransfer(t);
		}
	}

}
