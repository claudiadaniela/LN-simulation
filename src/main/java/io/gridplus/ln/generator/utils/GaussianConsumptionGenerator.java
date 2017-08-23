package io.gridplus.ln.generator.utils;

import java.util.Random;

import io.gridplus.ln.generator.factory.TransfersSetup;
import io.gridplus.ln.network.utils.CSVWriter;

public class GaussianConsumptionGenerator {

	private static final double[] HOURLY_PROFILE = { 0.03, 0.01, 0.01, 0.01, 0.01, 0.02, 0.03, 0.04, 0.05, 0.06, 0.06,
			0.05, 0.04, 0.03, 0.03, 0.04, 0.05, 0.06, 0.07, 0.07, 0.07, 0.06, 0.06, 0.04 };

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

	public static double[] getHourlyProfile(int valuePerDay) {
		double[] values = new double[24];
		for (int i = 0; i < HOURLY_PROFILE.length; i++) {
			values[i] = HOURLY_PROFILE[i] * valuePerDay;
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
