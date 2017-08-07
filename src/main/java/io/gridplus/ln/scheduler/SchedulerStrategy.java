package io.gridplus.ln.scheduler;

import java.util.List;

import io.gridplus.ln.model.Transfer;
import io.gridplus.ln.simulator.NetworkClientRunner;

public interface SchedulerStrategy {
	public void dispatchTransfer(List<Transfer> transfer, List<NetworkClientRunner> clients);
}
