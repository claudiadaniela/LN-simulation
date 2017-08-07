package io.gridplus.ln.model;

import java.io.Serializable;

public class LNVertex implements Serializable {

	private static final long serialVersionUID = 1L;
	private int id;
	private double fee;

	public NetworkStatus networkStatus;

	public LNVertex(int id) {
		this.id = id;
	}

	public LNVertex(int id, double fee) {
		this.id = id;
		this.fee = (double) Math.round(fee * 100d) / 100d;
	}

	public int getId() {
		return id;
	}

	public double getFee() {
		return fee;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		LNVertex lnVertex = (LNVertex) o;

		return id == lnVertex.id;
	}

	@Override
	public int hashCode() {
		return id;
	}

	@Override
	public String toString() {
		return "V" + id;
	}

	public static class NetworkStatus implements Serializable {
		private static final long serialVersionUID = 1L;
		private double healthScore;

		public NetworkStatus(double healthScore) {
			this.healthScore = healthScore;
		}

		public double getHealthScore() {
			return healthScore;
		}
	}
}