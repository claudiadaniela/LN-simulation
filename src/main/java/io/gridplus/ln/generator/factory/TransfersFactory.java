package io.gridplus.ln.generator.factory;

import io.gridplus.ln.generator.utils.GaussianConsumptionGenerator;
import io.gridplus.ln.model.LNVertex;
import io.gridplus.ln.model.Transfer;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public  class TransfersFactory {
    protected LNVertex[] vertices;
    protected int maxHTLC;

    public TransfersFactory(LNVertex[] vertices, int maxHTLC) {
        this.vertices = vertices;
        this.maxHTLC = maxHTLC;
    }

    public  List<Transfer> generate(int startBlock,int size){
        int[] values = GaussianConsumptionGenerator.generate(size);
        List<Transfer> transfers = new ArrayList<>();
        Random rand = new Random();

        for (int i = 0; i < size; i++) {
            int amount = values[i];

            LNVertex source = vertices[rand.nextInt(vertices.length)];

            LNVertex recipient = vertices[rand.nextInt(vertices.length)];
            while (source.equals(recipient)) {
                recipient = new LNVertex(rand.nextInt(vertices.length));
            }
            int htlc = rand.nextInt(maxHTLC) + 1;
            Transfer transfer = new Transfer(source, recipient, amount, htlc, rand.nextInt(htlc));
            transfer.setBlockOfDeploymentTime(startBlock);

            transfers.add(transfer);
        }
        return  transfers;
    }

}
