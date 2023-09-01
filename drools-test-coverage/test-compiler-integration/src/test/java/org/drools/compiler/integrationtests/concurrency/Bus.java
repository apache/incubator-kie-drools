package org.drools.compiler.integrationtests.concurrency;

import java.math.BigDecimal;

public class Bus {

    private String name;
    private int capacity;
    private BigDecimal weight;
    private Karaoke karaoke = new Karaoke();

    public Bus(String name, int capacity) {
        this.name = name;
        this.capacity = capacity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPerson() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }

    public Karaoke getKaraoke() {
        return karaoke;
    }
}
