package org.drools.beliefs.bayes;

import java.util.Arrays;

public class BayesVariable<T> {
    private String           name;
    private int              id;
    private double[][]       probabilityTable;
    private T[]              outcomes;
    private int              familyIndex;
    private String[]         given;

    public BayesVariable(String name, int id, T[] outcomes, double[][] probabilities) {
        this( name, id, outcomes, probabilities, new String[0]);
    }

    public BayesVariable(String name, int id, T[] outcomes, double[][] probabilities, String[] given) {
        this.name = name;
        this.id = id;
        this.probabilityTable = probabilities;
        this.outcomes = outcomes;
        this.given = given;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public double[][] getProbabilityTable() {
        return probabilityTable;
    }

    public T[] getOutcomes() {
        return outcomes;
    }

    public String[] getGiven() {
        return given;
    }

    public int getFamily() {
        return familyIndex;
    }

    public void setFamily(int familyIndex) {
        this.familyIndex = familyIndex;
    }

    public BayesVariableState createState() {
        return new BayesVariableState(this, new double[outcomes.length]);
    }

    @Override
    public String toString() {
        return "BayesVariable{" +
               "name='" + name + '\'' +
               ", id=" + id +
               //", outcomes=" + Arrays.toString(outcomes) +
               ", probabilityTable=" + Arrays.toString(probabilityTable) +
               '}';
    }
}
