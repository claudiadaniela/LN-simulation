package io.gridplus.ln.simulation.network;

public class BlockRunner implements Runnable {
	private volatile int currentBlock;
	private static BlockRunner instance;

	private BlockRunner(){}
	public static BlockRunner getInstance() {
		if (instance == null) {
			synchronized (BlockRunner.class) {
				if (instance == null) {
					System.out.println("Create Block Clock");
					instance = new BlockRunner();
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
