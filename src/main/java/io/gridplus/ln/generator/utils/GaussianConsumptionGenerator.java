package io.gridplus.ln.generator.utils;


import io.gridplus.ln.network.utils.CSVWriter;

import java.util.Random;

/**
 * 2015 source: http://www.lowcarbonlivingcrc.com.au/sites/all/files/publications_file_attachments/statistical_analysis_of_driving_factors_of_residential_energy_demand_-_final.pdf
 * Mean Electricity demand 18.84 kWh / day = 0.785 kWh/hour  = 785 Wh
 * Standard deviation 4.82 kWh / day = 0.200 kWh/hour = 200 Wh
 *
 * June 2017 source; https://www.bls.gov/regions/mid-atlantic/news-release/averageenergyprices_washingtondc.htm
 * Energy price : 0.142 /kWh => 1 Dollar = 1 Bolt = 7042 Wh
 */
public class GaussianConsumptionGenerator {

    private static final int STD_DEVIATION = 200;
    public static final int MEAN = 785;

    public static int[] generate(int size) {
        Random r = new Random();
        int[] values = new int[size];
        for (int i = 0; i < size; i++) {
            double val = r.nextGaussian() * STD_DEVIATION + MEAN;
            if (val < 0) continue;
            int value = (int) Math.round(val);
            values[i] = value;
        }

        return values;
    }

    public static int[] histogram(int[] values) {
        int[] histogram = new int[MEAN + STD_DEVIATION * 6];
        for (int i = 0; i < values.length; i++) {
            histogram[values[i]]++;
        }
        return histogram;
    }

    public static void main(String[] args) {
        int[] values = generate(30000);
        CSVWriter.writeConsumptionData("household-consumption-hour-" + 0 + ".csv", values);
        int[] hitogram = histogram(values);
        CSVWriter.writeConsumptionData("histogram-" + 0 + ".csv", hitogram);
    }
}
