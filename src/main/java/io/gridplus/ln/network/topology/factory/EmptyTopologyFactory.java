package io.gridplus.ln.network.topology.factory;

import io.gridplus.ln.network.topology.NetworkTopology;

public class EmptyTopologyFactory extends NetworkTopologyAbstractFactory {
    @Override
    public NetworkTopology createTopology(int noHops, int noNodes) {
        return new NetworkTopology();
    }

    @Override
    public NetworkTopology createTopology(String file) {
        return new NetworkTopology();
    }
}
