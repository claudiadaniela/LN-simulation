package io.gridplus.ln.network.topology.factory;

import io.gridplus.ln.network.topology.NetworkTopology;


public abstract class NetworkTopologyAbstractFactory {

    public enum Type {RANDOM, FILE}

    public static NetworkTopologyAbstractFactory getInstance(Type type) {
        if (Type.FILE.equals(type)) {
            return new GraphMLTopologyFactory();
        }

        if (Type.RANDOM.equals(type)) {
            return new RandomNetworkTopologyFactory();
        }
        return new EmptyTopologyFactory();
    }

    public abstract NetworkTopology createTopology(int noHops, int noNodes);

    public abstract NetworkTopology createTopology(String file);
}
