package io.gridplus.ln.simulation.network;

public class BlockClock implements Runnable {
	private volatile int currentBlock;
	private static BlockClock instance;

	private BlockClock(){}
	public static BlockClock getInstance() {
		if (instance == null) {
			synchronized (BlockClock.class) {
				if (instance == null) {
					System.out.println("Create Block Clock");
					instance = new BlockClock();
				}
			}
		}
		return instance;
	}

	public int currentBlock() {
		return currentBlock;
	}

	public void run() {
		while (currentBlock < SimulationSetup.NO_SIM_STEPS.value()) {
			try {
				Thread.sleep(SimulationSetup.SAMPLE_RATE.value());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			currentBlock++;
			System.out.println("current time: "+ currentBlock);
		}
	}
}
