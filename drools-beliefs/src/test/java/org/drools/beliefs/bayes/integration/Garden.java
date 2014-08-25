package org.drools.beliefs.bayes.integration;

import org.drools.beliefs.PropertyChangeMask;
import org.drools.beliefs.bayes.BayesFact;
import org.drools.beliefs.bayes.BayesInstance;
import org.drools.beliefs.bayes.BayesVariableConstructor;
import org.drools.beliefs.bayes.VarName;

public class Garden implements BayesFact  {
    private BayesInstance bayesInstance;

    @VarName("WetGrass")
    private boolean wetGrass;

    @VarName("Cloudy")
    private boolean cloudy;

    @VarName("Sprinkler")
    private boolean sprinkler;

    @VarName("Rain")
    private boolean rain;

    public Garden() {

    }

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

    @Override
    public BayesInstance getBayesInstance() {
        return bayesInstance;
    }

    public boolean isWetGrass() {
        return wetGrass;
    }

    public boolean isCloudy() {
        return cloudy;
    }

    public boolean isSprinkler() {
        return sprinkler;
    }

    public boolean isRain() {
        return rain;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }

        Garden garden = (Garden) o;

        if (cloudy != garden.cloudy) { return false; }
        if (rain != garden.rain) { return false; }
        if (sprinkler != garden.sprinkler) { return false; }
        if (wetGrass != garden.wetGrass) { return false; }

        return true;
    }

    @Override
    public int hashCode() {
        int result = (wetGrass ? 1 : 0);
        result = 31 * result + (cloudy ? 1 : 0);
        result = 31 * result + (sprinkler ? 1 : 0);
        result = 31 * result + (rain ? 1 : 0);
        return result;
    }
}
