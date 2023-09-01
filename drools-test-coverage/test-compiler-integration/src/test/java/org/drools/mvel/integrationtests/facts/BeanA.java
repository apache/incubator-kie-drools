package org.drools.mvel.integrationtests.facts;

public class BeanA {
    int seed;

    public BeanA() {
        this.seed = 1;
    }

    public BeanA(int seed) {
        this.seed = seed;
    }

    public int getSeed() {
        return this.seed;
    }

    public void setSeed(int seed) {
        this.seed = seed;
    }
}
