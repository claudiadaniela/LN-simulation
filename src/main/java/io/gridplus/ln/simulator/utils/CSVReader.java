package io.gridplus.ln.simulator.utils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.gridplus.ln.model.LNVertex;
import io.gridplus.ln.model.Transfer;

public class CSVReader {

	public static int[] readConsumptionData(String file) {

		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ",";
		List<Integer> values = new ArrayList<>();
		try {

			br = new BufferedReader(new FileReader(file));
			if ((line = br.readLine()) != null) {
				// header line
			}
			while ((line = br.readLine()) != null) {
				String[] valuesS = line.split(cvsSplitBy);
				values.add(Integer.parseInt(valuesS[0]));
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	
		return getIntValues(values);
	}

	private static int[] getIntValues(List<Integer> values) {
		int[] toReturn = new int[values.size()];
		for (int i = 0; i < values.size(); i++) {
			toReturn[i] = values.get(i);
		}
		return toReturn;
	}

	public static List<Transfer> readTransfers(String file) {
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ",";
		List<Transfer> transfers = new ArrayList<>();
		try {

			br = new BufferedReader(new FileReader(file));
			if ((line = br.readLine()) != null) {
				// header line
			}
			while ((line = br.readLine()) != null) {
				String[] valuesS = line.split(cvsSplitBy);
				int source = Integer.parseInt(valuesS[0]);
				int recipient = Integer.parseInt(valuesS[1]);
				int amount = Integer.parseInt(valuesS[2]);
				int blockStart = Integer.parseInt(valuesS[5]);
				Transfer t = new Transfer(new LNVertex(source), new LNVertex(recipient), amount);
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
