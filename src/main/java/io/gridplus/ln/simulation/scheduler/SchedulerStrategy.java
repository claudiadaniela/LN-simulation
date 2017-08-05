package io.gridplus.ln.simulation.scheduler;

import java.util.List;

import io.gridplus.ln.simulation.model.Transfer;
import io.gridplus.ln.simulation.network.ClientRunner;

public interface SchedulerStrategy {
	public void dispatchTransfer(List<Transfer> transfer, List<ClientRunner> clients);
}
