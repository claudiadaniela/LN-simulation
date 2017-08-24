package io.gridplus.ln.generator.utils;

import java.util.Random;

import io.gridplus.ln.generator.factory.TransfersSetup;
import io.gridplus.ln.simulator.utils.CSVWriter;

public class GaussianConsumptionGenerator {

	public static int[] generateDailyProfile(int size) {
		Random r = new Random();
		int[] values = new int[size];
		for (int i = 0; i < size; i++) {
			double val = r.nextGaussian() * TransfersSetup.HOUSEHOLD_ENERGH_STD.value()
					+ TransfersSetup.HOUSEHOULD_ENERGY_MEAN.value();
			if (val < 0)
				continue;
			int value = (int) Math.round(val);
			values[i] = value;
		}
		return values;
	}


	public static int[] histogram(int[] values) {
		int[] histogram = new int[(int) TransfersSetup.HOUSEHOLD_MAX_VALUE.value()];
		for (int i = 0; i < values.length; i++) {
			histogram[values[i]]++;
		}
		return histogram;
	}

	public static void main(String[] args) {
		int[] values = generateDailyProfile(30000);
		CSVWriter.writeConsumptionData("household-consumption-daily.csv", values);
		int[] hitogram = histogram(values);
		CSVWriter.writeConsumptionData("histogram.csv", hitogram);

	}
}
