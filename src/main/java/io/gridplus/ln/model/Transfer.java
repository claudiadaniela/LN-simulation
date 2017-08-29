package io.gridplus.ln.model;

import java.util.Comparator;

public class Transfer {
    private LNVertex source;
    private LNVertex recipient;
    private int amount;
    /**
     * Simulate the time that a transfer locks the tokens lockTime< HTLC-time
     */
    //private int lockTime;
    //private int htlcTime;
    /**
     * Field used for scheduling transfers
     */
    private int blockOfDeploymentTime;
    private double energy;

    public Transfer(LNVertex source, LNVertex recipient, int amount) {
        super();
        this.source = source;
        this.recipient = recipient;
        this.amount = amount;
        //this.htlcTime = htlc;
        //this.lockTime =lockTime;
    }

    public LNVertex getSource() {
        return source;
    }

    public LNVertex getRecipient() {
        return recipient;
    }

    public int getAmount() {
        return amount;
    }

    public int getBlockOfDeploymentTime() {
        return blockOfDeploymentTime;
    }

    public void setBlockOfDeploymentTime(int blockOfDeploymentTime) {
        this.blockOfDeploymentTime = blockOfDeploymentTime;
    }

    public double getEnergy() {
        return energy;
    }

    public void setEnergy(double energy) {
        this.energy = energy;
    }

    @Override
    public String toString() {
        return "Transfer [source=" + source + ", recipient=" + recipient + ", amount=" + amount + ", deploy time=" + blockOfDeploymentTime + ", energy=" + energy + "]";
    }

    public static class TransferComparator implements Comparator<Transfer> {
        public int compare(Transfer o1, Transfer o2) {
            return o1.blockOfDeploymentTime - o2.blockOfDeploymentTime;
        }

    }
}
