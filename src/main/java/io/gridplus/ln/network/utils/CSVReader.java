package io.gridplus.ln.network.utils;

import io.gridplus.ln.model.LNVertex;
import io.gridplus.ln.model.Transfer;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CSVReader {

    public static List<Integer> readConsumptionData(String file) {

        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ",";
        List<Integer> values = new ArrayList<>();
        try {

            br = new BufferedReader(new FileReader(file));
            if ((line = br.readLine()) != null) {
                //header line
            }
            while ((line = br.readLine()) != null) {
                String[] valuesS = line.split(cvsSplitBy);
                System.out.println(" [value= " + valuesS[0]);
                values.add(Integer.parseInt(valuesS[0]));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return values;
    }


    public static List<Transfer> readTransfers(String file) {
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ",";
        List<Transfer> transfers = new ArrayList<>();
        try {

            br = new BufferedReader(new FileReader(file));
            if ((line = br.readLine()) != null) {
                //header line
            }
            while ((line = br.readLine()) != null) {
                String[] valuesS = line.split(cvsSplitBy);
                int source = Integer.parseInt(valuesS[0]);
                int recipient = Integer.parseInt(valuesS[1]);
                int amount = Integer.parseInt(valuesS[2]);
                int lockTime = Integer.parseInt(valuesS[3]);
                int htlc = Integer.parseInt(valuesS[4]);
                int blockStart = Integer.parseInt(valuesS[5]);
                Transfer t = new Transfer(new LNVertex(source), new LNVertex(recipient), amount, lockTime, htlc);
                t.setBlockOfDeploymentTime(blockStart);
                transfers.add(t);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return transfers;
    }

}
