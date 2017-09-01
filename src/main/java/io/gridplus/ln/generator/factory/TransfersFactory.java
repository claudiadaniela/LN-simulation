package io.gridplus.ln.generator.factory;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import java.util.logging.Logger;

import io.gridplus.ln.generator.utils.GaussianConsumptionGenerator;
import io.gridplus.ln.model.LNVertex;
import io.gridplus.ln.model.Transfer;
import io.gridplus.ln.simulator.utils.CSVReader;

public class TransfersFactory {
    private static final double[] HOURLY_PROFILE = {0.03, 0.01, 0.01, 0.01, 0.01, 0.02, 0.03, 0.04, 0.05, 0.06, 0.06,
            0.05, 0.04, 0.03, 0.03, 0.04, 0.05, 0.06, 0.07, 0.07, 0.07, 0.06, 0.06, 0.04};
    private static final String PROFILE_FILE = "./src/main/resources/dailyProfileClients.csv";
    private static final Logger LOGGER = Logger.getLogger(TransfersFactory.class.getName());

    protected LNVertex[] vertices;
    private int[] dailyClientsProfile;
    private double[][] hourlyClientProfile;
    private int consumers;
    private TransfersFactory() {
    }

    public static TransfersFactory getInstance(LNVertex[] vertices, TransfersInput input, int consumers) {
        TransfersFactory tFactory = new TransfersFactory();
        tFactory.vertices = vertices;
        tFactory.consumers = consumers;
        if (TransfersInput.FILE.equals(input)) {
            tFactory.dailyClientsProfile = CSVReader.readConsumptionData(PROFILE_FILE);
        } else {
            tFactory.dailyClientsProfile = GaussianConsumptionGenerator.generateDailyProfile(consumers);
        }
        tFactory.hourlyClientProfile = new double[consumers][24];
        for (int i = 0; i < consumers; i++) {
            tFactory.hourlyClientProfile[i] = getHourlyProfile(tFactory.dailyClientsProfile[i]);
        }
        return tFactory;
    }

    public List<Transfer> generate(int startBlock) {
        LOGGER.log(Level.INFO, "Generate Transfers for time: " + startBlock +"  and consumers: " + consumers);
        List<Transfer> transfers = new ArrayList<>();

        for (int i = 0; i < consumers; i++) {
            if (vertices[i].hop) {
                continue;
            }
            double energy = hourlyClientProfile[i][startBlock];
            int token = getBolts(energy);
            LNVertex source = vertices[i];
            int index =  ThreadLocalRandom.current().nextInt(consumers, vertices.length);
            LNVertex recipient = vertices[index];
            while (source.equals(recipient) || recipient.hop) {
                index = ThreadLocalRandom.current().nextInt(consumers, vertices.length);
                recipient =  vertices[index];
            }
            Transfer transfer = new Transfer(source, recipient, token);
            transfer.setBlockOfDeploymentTime(startBlock);
            transfer.setEnergy(energy);
            transfers.add(transfer);
        }
        return transfers;
    }

    public int getBolts(double energy) {
        return (int) ((energy* TransfersSetup.TOKEN_SCALE.value()) / TransfersSetup.ENERGY_PRICE.value());
    }

    public double[][] getEnergyValues() {
        return hourlyClientProfile;
    }

    public int[] getClientsDailyProfile() {
        return dailyClientsProfile;
    }

    public int[] getClientsConsumptionProfileHistogram() {
        return GaussianConsumptionGenerator.histogram(dailyClientsProfile);
    }

    private static double[] getHourlyProfile(int valuePerDay) {
        double[] values = new double[24];
        for (int i = 0; i < HOURLY_PROFILE.length; i++) {
            values[i] = HOURLY_PROFILE[i] * valuePerDay;
        }
        return values;
    }

    public enum TransfersInput {
        GAUSSIAN, FILE
    }

}
