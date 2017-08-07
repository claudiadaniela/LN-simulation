package io.gridplus.ln.simulator;

public enum SimulationSetup {
	MAX_TOKEN_HOP(100),
	MAX_HTLC(5),
	MAX_TRANSFERS_PER_BLOCK(10),
	NO_HOPS(3),
	SAMPLE_RATE(3000),
	NO_NODES(40),
	NO_CLIENT_RUNNERS(1), 
	NO_TRANSFERS(70), 
	NO_SIM_STEPS(5);
	
	private int value;

	private SimulationSetup(int value) {
		this.value = value;
	}

	public int value() {
		return this.value;
	}
}
