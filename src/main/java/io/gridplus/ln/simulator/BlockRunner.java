package io.gridplus.ln.simulator;

public class BlockRunner implements Runnable {
    private volatile int currentBlock;
    private static BlockRunner instance;
    private int simSteps;

    private BlockRunner() {
    }

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

    public void setSimulationSteps(int steps) {
        this.simSteps = steps;
    }

    public boolean running() {
        return currentBlock < simSteps;
    }

    public void run() {
        while (currentBlock < simSteps) {
            try {
                Thread.sleep(SimulationSetup.SAMPLE_RATE.value());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            currentBlock++;
            System.out.println("current time: " + currentBlock);
        }
        System.out.println("Finished simulation...");
    }
}
