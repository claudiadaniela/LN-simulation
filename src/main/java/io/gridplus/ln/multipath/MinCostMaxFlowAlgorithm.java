package io.gridplus.ln.multipath;

import java.util.Arrays;

/**
 * SOURCE :
 * https://github.com/jaehyunp/stanfordacm/blob/master/code/MinCostMaxFlow.java
 */
public class MinCostMaxFlowAlgorithm {
    private boolean found[];
    private int noNodes, capacity[][], flow[][], dad[];
    private double dist[], pi[];
    private double cost[][];

    static final int INF = Integer.MAX_VALUE / 2 - 1;

    private boolean search(int source, int sink) {
        Arrays.fill(found, false);
        Arrays.fill(dist, INF);
        dist[source] = 0;

        while (source != noNodes) {
            int best = noNodes;
            found[source] = true;
            for (int k = 0; k < noNodes; k++) {
                if (found[k])
                    continue;
                if (flow[k][source] != 0) {
                    double val = dist[source] + pi[source] - pi[k] - cost[k][source];
                    if (dist[k] > val) {
                        dist[k] = val;
                        dad[k] = source;
                    }
                }
                if (flow[source][k] < capacity[source][k]) {
                    double val = dist[source] + pi[source] - pi[k] + cost[source][k];
                    if (dist[k] > val) {
                        dist[k] = val;
                        dad[k] = source;
                    }
                }

                if (dist[k] < dist[best])
                    best = k;
            }
            source = best;
        }
        for (int k = 0; k < noNodes; k++)
            pi[k] = Math.min(pi[k] + dist[k], INF);
        return found[sink];
    }

    public double[] getMaxFlow(int cap[][], double cost[][], int source, int sink) {
        this.capacity = cap;
        this.cost = cost;

        noNodes = cap.length;
        found = new boolean[noNodes];
        flow = new int[noNodes][noNodes];
        dist = new double[noNodes + 1];
        dad = new int[noNodes];
        pi = new double[noNodes];

        int totflow = 0;
        double totcost = 0;
        while (search(source, sink)) {
            int amt = INF;
            for (int x = sink; x != source; x = dad[x])
                amt = Math.min(amt, flow[x][dad[x]] != 0 ? flow[x][dad[x]] : cap[dad[x]][x] - flow[dad[x]][x]);
            for (int x = sink; x != source; x = dad[x]) {
                if (flow[x][dad[x]] != 0) {
                    flow[x][dad[x]] -= amt;
                    totcost -= amt * cost[x][dad[x]];
                } else {
                    flow[dad[x]][x] += amt;
                    totcost += amt * cost[dad[x]][x];
                }
            }
            totflow += amt;
        }

        return new double[]{totflow, totcost};
    }
}