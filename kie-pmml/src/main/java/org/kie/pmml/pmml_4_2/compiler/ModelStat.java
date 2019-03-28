package org.kie.pmml.pmml_4_2.compiler;


public class ModelStat {

    private boolean neuralNetwork;

    public ModelStat(boolean neuralNetwork) {
        super();
        this.neuralNetwork = neuralNetwork;
    }

    public ModelStat() {
        super();
    }

    public boolean isNeuralNetwork() {
        return neuralNetwork;
    }

    public void setNeuralNetwork(boolean neuralNetwork) {
        this.neuralNetwork = neuralNetwork;
    }

}
