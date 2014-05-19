package org.drools.beliefs.bayes.integration;

import org.drools.beliefs.PropertyChangeMask;
import org.drools.beliefs.bayes.BayesInstance;
import org.drools.beliefs.bayes.BayesVariableConstructor;
import org.drools.beliefs.bayes.VarName;

public class Garden {
    private BayesInstance bayesInstance;

    private boolean wetGrass;
    private boolean cloudy;
    private boolean sprinkler;
    private boolean rain;

//    public Garden() {
//
//    }

    @BayesVariableConstructor
    public Garden(BayesInstance bayesInstance,
                  @VarName("WetGrass") boolean wetGrass, @VarName("Cloudy") boolean cloudy,
                  @VarName("Sprinkler")  boolean sprinkler, @VarName("Rain") boolean rain) {
        this.bayesInstance = bayesInstance;
        this.wetGrass = wetGrass;
        this.cloudy = cloudy;
        this.sprinkler = sprinkler;
        this.rain = rain;
    }

    @Override
    public String toString() {
        return "Garden{" +
               "bayesInstance=" + bayesInstance +
               ", wetGrass=" + wetGrass +
               ", cloudy=" + cloudy +
               ", sprinkler=" + sprinkler +
               ", rain=" + rain +
               '}';
    }
}
