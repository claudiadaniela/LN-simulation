package io.gridplus.ln.simulation.scheduler;

import java.util.List;

import io.gridplus.ln.simulation.model.Transfer;
import io.gridplus.ln.simulation.network.ClientRunner;

public class ShortestQueueStrategy implements SchedulerStrategy {

	public void dispatchTransfer(List<Transfer> transfers, List<ClientRunner> clients) {
		int minSize = Integer.MAX_VALUE;
		ClientRunner minRunner = null;
		for (Transfer t : transfers) {
			for (ClientRunner runner : clients) {
				if (runner.getSize() < minSize) {
					minSize = runner.getSize();
					minRunner = runner;
				}
			}
			minRunner.addTransfer(t);
		}
	}

}
