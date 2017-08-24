package io.gridplus.ln.simulator.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import io.gridplus.ln.model.LNEdge;
import io.gridplus.ln.model.LNVertex;
import io.gridplus.ln.model.Transfer;

public class CSVWriter {

	public static void writeConsumptionData(String file, int[] values) {
		PrintWriter pw;
		StringBuilder sb = new StringBuilder();
		sb.append("value");
		sb.append('\n');
		try {
			pw = new PrintWriter(new File(file));
			for (int i = 0; i < values.length; i++) {
				sb.append(values[i]);
				sb.append('\n');
			}

			pw.write(sb.toString());
			pw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static void writeInputEnergyData(String file, double[][] values) {
		PrintWriter pw;
		StringBuilder sb = new StringBuilder();
		sb.append("value");
		sb.append('\n');
		try {
			pw = new PrintWriter(new File(file));
			for (int i = 0; i < values.length; i++) {
				for (int j = 0; j < values[i].length; j++) {
					sb.append(values[i][j]);
					sb.append(',');
				}
				sb.append('\n');
			}

			pw.write(sb.toString());
			pw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static void writeNetwrokStateData(String file, Map<String, Map<String, Integer>> networkState) {
		PrintWriter pw;
		StringBuilder sb = new StringBuilder();
		sb.append("source node");
		sb.append(',');
		sb.append("target node");
		sb.append(',');
		sb.append("amount available");
		sb.append('\n');
		try {
			pw = new PrintWriter(new File(file));
			for (Map.Entry<String, Map<String, Integer>> entry : networkState.entrySet()) {
				sb.append(entry.getKey());
				sb.append(',');
				sb.append('-');
				sb.append(',');
				sb.append('-');
				sb.append('\n');
				for (Map.Entry<String, Integer> target : entry.getValue().entrySet()) {
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

	public static void writeHopsRefundsData(String file, Map<LNEdge, Integer> hopsRefundState) {
		PrintWriter pw;
		StringBuilder sb = new StringBuilder();
		sb.append("source node");
		sb.append(',');
		sb.append("target node");
		sb.append(',');
		sb.append("amount refunded");
		sb.append('\n');
		try {
			pw = new PrintWriter(new File(file));
			for (Map.Entry<LNEdge, Integer> entry : hopsRefundState.entrySet()) {
				sb.append(entry.getKey().getSource());
				sb.append(',');
				sb.append(entry.getKey().getTarget());
				sb.append(',');
				sb.append(entry.getValue());
				sb.append('\n');
			}

			pw.write(sb.toString());
			pw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	public static void writeHopsFeesData(String file, Map<LNVertex, ? extends Number> hopsFeesState) {
		PrintWriter pw;
		StringBuilder sb = new StringBuilder();
		sb.append("node");
		sb.append(',');
		sb.append("amount refunded");
		sb.append('\n');
		try {
			pw = new PrintWriter(new File(file));
			for (Map.Entry<LNVertex, ? extends Number> entry : hopsFeesState.entrySet()) {
				sb.append(entry.getKey());
				sb.append(',');
				sb.append(entry.getValue());
				sb.append('\n');
			}

			pw.write(sb.toString());
			pw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	public static void writeTransfers(String file, List<Transfer> transferList) {
		PrintWriter pw;
		StringBuilder sb = new StringBuilder();
		sb.append("source");
		sb.append(',');
		sb.append("recipient");
		sb.append(',');
		sb.append("amount");
		sb.append(',');
		sb.append("lockTime");
		sb.append(',');
		sb.append("htlc");
		sb.append(',');
		sb.append("deployBlock");
		sb.append('\n');
		try {
			pw = new PrintWriter(new File(file));
			for (Transfer t : transferList) {
				sb.append(t.getSource().getId());
				sb.append(',');
				sb.append(t.getRecipient().getId());
				sb.append(',');
				sb.append(t.getAmount());
				sb.append(',');
				sb.append(t.getBlockOfDeploymentTime());
				sb.append('\n');
			}

			pw.write(sb.toString());
			pw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

}
