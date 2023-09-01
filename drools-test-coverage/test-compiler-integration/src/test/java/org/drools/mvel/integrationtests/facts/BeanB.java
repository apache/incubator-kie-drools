package org.drools.mvel.integrationtests.facts;

public class BeanB {
    int seed;

    public BeanB() {
        this.seed = 1;
    }

    public BeanB(int seed) {
        this.seed = seed;
    }

    public int getSeed() {
        return this.seed;
    }

    public void setSeed(int seed) {
        this.seed = seed;
    }
}
