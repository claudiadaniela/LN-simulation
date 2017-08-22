package io.gridplus.ln.network.factory;

import io.gridplus.ln.model.NetworkTopology;

public class EmptyTopologyFactory extends  NetworkTopologyAbstractFactory {
    @Override
    public NetworkTopology createTopology(int noHops, int noNodes) {
        return new NetworkTopology();
    }

    @Override
    public NetworkTopology createTopology(String file) {
        return new NetworkTopology();
    }
}
