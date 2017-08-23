package io.gridplus.ln.simulator;

public class BlockCounterRunner implements Runnable {
    private volatile int currentBlock;
    private static BlockCounterRunner instance;
    private int simSteps;

    private BlockCounterRunner() {
    }

    public static BlockCounterRunner getInstance() {
        if (instance == null) {
            synchronized (BlockCounterRunner.class) {
                if (instance == null) {
                    System.out.println("Create Block Clock");
                    instance = new BlockCounterRunner();
                }
            }
        }
        return instance;
    }

    public int currentBlock() {
        return currentBlock< simSteps? currentBlock: simSteps-1;
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
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            currentBlock++;
            System.out.println("current time: " + currentBlock);
        }
        System.out.println("Finished block counter...");
    }
}
