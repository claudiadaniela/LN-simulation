package io.gridplus.ln.generator.factory;

/**
 * 2015 source: http://www.lowcarbonlivingcrc.com.au/sites/all/files/
 * publications_file_attachments/
 * statistical_analysis_of_driving_factors_of_residential_energy_demand_-_final.
 * pdf Mean Electricity demand 18.84 kWh / day = 0.785 kWh/hour = 785 Wh
 * Standard deviation 4.82 kWh / day = 0.200 kWh/hour = 200 Wh
 * <p>
 * June 2017 source; https://www.bls.gov/regions/mid-atlantic/news-release/
 * averageenergyprices_washingtondc.htm Energy price : 0.142 $ /kWh => 1 Dollar =
 * 1 Bolt = 7042 Wh
 */
public enum TransfersSetup {
    HOUSEHOLD_ENERGY_MEAN(18840),
    HOUSEHOLD_ENERGY_STD(4820),
    HOUSEHOLD_MAX_TOKEN_VALUE(((18840 + 6 * 4820) / 7042) * 10000),
    TOKEN_SCALE(10000),
    ENERGY_PRICE(7042);

    private double value;

    TransfersSetup(double value) {
        this.value = value;
    }

    public double value() {
        return value;
    }
}
