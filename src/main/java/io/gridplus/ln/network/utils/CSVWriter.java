package io.gridplus.ln.network.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Map;

public class CSVWriter {

	public static void writeConsumptionData(String file, int[] values, int[] histogram) {
		PrintWriter pw;
		StringBuilder sb = new StringBuilder();
		sb.append("index");
		sb.append(',');
		sb.append("value");
		sb.append(',');
		sb.append("histogram");
		sb.append('\n');
		try {
			pw = new PrintWriter(new File(file+ ".csv"));
			for (int i= 0; i < values.length; i++ ) {
				sb.append(i);
				sb.append(',');
				sb.append(values[i]);
				sb.append(',');
				sb.append(histogram[i]);
				sb.append('\n');
			}

			pw.write(sb.toString());
			pw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static void writeNetwrokStateData(String file,  Map<String, Map<String, Integer>> networkState) {
		PrintWriter pw;
		StringBuilder sb = new StringBuilder();
		sb.append("source node");
		sb.append(',');
		sb.append("target node");
		sb.append(',');
		sb.append("amount available");
		sb.append('\n');
		try {
			pw = new PrintWriter(new File(file+ ".csv"));
			for (Map.Entry<String, Map<String, Integer>> entry: networkState.entrySet() ) {
				sb.append(entry.getKey());
				sb.append(',');
				sb.append('-');
				sb.append(',');
				sb.append('-');
				sb.append('\n');
				for( Map.Entry<String, Integer> target: entry.getValue().entrySet()){
					sb.append("-");
					sb.append(',');
					sb.append(target.getKey());
					sb.append(',');
					sb.append(target.getValue());
					sb.append('\n');
				}
			}

			pw.write(sb.toString());
			pw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

}
