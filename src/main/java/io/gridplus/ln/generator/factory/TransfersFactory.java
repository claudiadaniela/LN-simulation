package io.gridplus.ln.generator.factory;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import io.gridplus.ln.generator.utils.GaussianConsumptionGenerator;
import io.gridplus.ln.model.LNVertex;
import io.gridplus.ln.model.Transfer;

public class TransfersFactory {
	protected LNVertex[] vertices;
	private int[] dailyClientsProfile;
	private double[][] hourlyClientProfile;

	public TransfersFactory(LNVertex[] vertices) {
		this.vertices = vertices;
		dailyClientsProfile = GaussianConsumptionGenerator.generateDailyProfile(vertices.length);
		hourlyClientProfile = new double[vertices.length][24];
		for (int i = 0; i < vertices.length; i++) {
			hourlyClientProfile[i] = GaussianConsumptionGenerator.getHourlyProfile(dailyClientsProfile[i]);
		}
	}

	public List<Transfer> generate(int startBlock) {
		List<Transfer> transfers = new ArrayList<>();
		Random rand = new Random();

		for (int i = 0; i < vertices.length; i++) {
			double energy = hourlyClientProfile[i][startBlock];
			int token = getBolts(energy);
			LNVertex source = vertices[i];

			LNVertex recipient = vertices[rand.nextInt(vertices.length)];
			while (source.equals(recipient)) {
				recipient = new LNVertex(rand.nextInt(vertices.length));
			}
			Transfer transfer = new Transfer(source, recipient, token);
			transfer.setBlockOfDeploymentTime(startBlock);
			transfer.setEnergy(energy);
			transfers.add(transfer);
		}
		return transfers;
	}
	
	public int getBolts(double energy){	
		return (int) ((energy /TransfersSetup.ENERGY_PRICE.value())* TransfersSetup.TOKEN_SCALE.value());
	}
	
	public double[][] getEnergyValues(){
		return hourlyClientProfile;
	}

}
