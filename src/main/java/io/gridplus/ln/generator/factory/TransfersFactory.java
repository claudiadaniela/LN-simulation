package io.gridplus.ln.generator.factory;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import io.gridplus.ln.generator.utils.GaussianConsumptionGenerator;
import io.gridplus.ln.model.LNVertex;
import io.gridplus.ln.model.Transfer;
import io.gridplus.ln.simulator.utils.CSVReader;
import io.gridplus.ln.simulator.utils.CSVWriter;

public class TransfersFactory {
	private static final double[] HOURLY_PROFILE = { 0.03, 0.01, 0.01, 0.01, 0.01, 0.02, 0.03, 0.04, 0.05, 0.06, 0.06,
			0.05, 0.04, 0.03, 0.03, 0.04, 0.05, 0.06, 0.07, 0.07, 0.07, 0.06, 0.06, 0.04 };
	private static final String PROFILE_FILE = "./src/main/resources/dailyProfileClients.csv";
	protected LNVertex[] vertices;
	private int[] dailyClientsProfile;
	private double[][] hourlyClientProfile;

	private TransfersFactory() {
	
	}
	public static TransfersFactory getInstance(LNVertex[] vertices, TransfersInput input){
		TransfersFactory tFactory = new TransfersFactory();
		tFactory.vertices = vertices;
		
		if(TransfersInput.FILE.equals(input)){
			tFactory.dailyClientsProfile = CSVReader.readConsumptionData(PROFILE_FILE);
		}else{
			tFactory.dailyClientsProfile = GaussianConsumptionGenerator.generateDailyProfile(vertices.length);
		}	
		tFactory.hourlyClientProfile = new double[vertices.length][24];
		for (int i = 0; i < vertices.length; i++) {
			tFactory.hourlyClientProfile[i] = getHourlyProfile(tFactory.dailyClientsProfile[i]);
		}
		return tFactory;
	}

	public List<Transfer> generate(int startBlock) {
		List<Transfer> transfers = new ArrayList<>();
		Random rand = new Random();

		for (int i = 0; i < vertices.length; i++) {
			if(vertices[i].hop){continue;}
			double energy = hourlyClientProfile[i][startBlock];
			int token = getBolts(energy);
			LNVertex source = vertices[i];

			LNVertex recipient = vertices[rand.nextInt(vertices.length)];
			while (source.equals(recipient) || recipient.hop) {
				recipient = new LNVertex(rand.nextInt(vertices.length));
			}
			Transfer transfer = new Transfer(source, recipient, token);
			transfer.setBlockOfDeploymentTime(startBlock);
			transfer.setEnergy(energy);
			transfers.add(transfer);
		}
		CSVWriter.writeTransfers("transfers.csv", transfers);
		return transfers;
	}
	
	public int getBolts(double energy){	
		return (int) ((energy /TransfersSetup.ENERGY_PRICE.value())* TransfersSetup.TOKEN_SCALE.value());
	}
	
	public double[][] getEnergyValues(){
		return hourlyClientProfile;
	}
	
	public int[] getClientsDailyProfile(){
		return dailyClientsProfile;
	}
	public int[] getClientsConsumptionProfileHistogram(){
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
