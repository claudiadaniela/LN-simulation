package io.gridplus.ln.network.factory;

import io.gridplus.ln.model.NetworkTopology;


public abstract class NetworkTopologyAbstractFactory {
    public enum Type {RANDOM, GAUSSIAN, FILE}

    public static NetworkTopologyAbstractFactory getInstance(Type type) {

        if (Type.FILE.equals(type)) {
            return new GraphMLTopologyFactory();
        }

        if (Type.RANDOM.equals(type)) {
            return new RandomNetworkTopologyFactory();
        }
        return new EmptyTopologyFactory();
    }

    public abstract NetworkTopology createTopology(int noHops, int noNodes, int initTokenHop);




}
