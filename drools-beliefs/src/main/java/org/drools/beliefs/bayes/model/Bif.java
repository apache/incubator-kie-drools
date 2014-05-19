package org.drools.beliefs.bayes.model;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("BIF")
public class Bif {
    @XStreamAlias("NETWORK")
    private Network network;

    public Network getNetwork() {
        return network;
    }

    public void setNetwork(Network network) {
        this.network = network;
    }
}
