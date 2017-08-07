package io.gridplus.ln.scheduler;

import java.util.List;

import io.gridplus.ln.model.Transfer;
import io.gridplus.ln.simulator.NetworkClientRunner;

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
