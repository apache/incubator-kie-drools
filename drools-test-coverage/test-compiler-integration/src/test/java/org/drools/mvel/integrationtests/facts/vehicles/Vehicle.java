package org.drools.mvel.integrationtests.facts.vehicles;

import java.util.Objects;

public abstract class Vehicle<TEngine extends Engine> {

    private final String maker;
    private final String model;

    private int score;

    public Vehicle(String maker, String model) {
        this.maker = Objects.requireNonNull(maker);
        this.model = Objects.requireNonNull(model);
    }

    public String getMaker() {
        return maker;
    }

    public String getModel() {
        return model;
    }

    public abstract TEngine getEngine();

    public TEngine getMotor() {
        return getEngine();
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
