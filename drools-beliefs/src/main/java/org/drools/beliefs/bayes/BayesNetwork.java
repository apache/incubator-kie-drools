package org.drools.beliefs.bayes;

import org.drools.beliefs.graph.impl.GraphImpl;
import org.drools.beliefs.graph.impl.ListGraphStore;

public class BayesNetwork extends GraphImpl<BayesVariable> {

    private String name;
    private String packageName;

    public BayesNetwork(String name) {
        super(new ListGraphStore<BayesVariable>());
        this.name = name;
    }

    public BayesNetwork(String name, String packageName) {
        super(new ListGraphStore<BayesVariable>());
        this.name = name;
        this.packageName = packageName;
    }

    public BayesNetwork() {
        super(new ListGraphStore<BayesVariable>());
    }

    public String getName() {
        return name;
    }

    public String getPackageName() {
        return packageName;
    }
}
