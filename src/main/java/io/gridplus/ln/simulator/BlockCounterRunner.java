package io.gridplus.ln.simulator;

import java.util.logging.Level;
import java.util.logging.Logger;

public class BlockCounterRunner implements Runnable {
    private static final Logger LOGGER = Logger.getLogger(BlockCounterRunner.class.getName());
    private volatile int currentBlock;
    private static BlockCounterRunner instance;
    private int simSteps;
    private volatile boolean running;

    private BlockCounterRunner() {
    }

    public static BlockCounterRunner getInstance() {
        if (instance == null) {
            synchronized (BlockCounterRunner.class) {
                if (instance == null) {
                    instance = new BlockCounterRunner();
                }
            }
        }
        return instance;
    }

    public int currentBlock() {
        return currentBlock < simSteps ? currentBlock : simSteps - 1;
    }

    public void setSimulationSteps(int steps) {
        running = true;
        this.simSteps = steps;
    }

    public boolean running() {
        return running;
    }

    public void run() {
        while (currentBlock < simSteps) {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            currentBlock++;
            LOGGER.log(Level.INFO, "current time: " + currentBlock);
        }
        running = false;
        LOGGER.log(Level.INFO, "Finished block counter...");
    }
}
