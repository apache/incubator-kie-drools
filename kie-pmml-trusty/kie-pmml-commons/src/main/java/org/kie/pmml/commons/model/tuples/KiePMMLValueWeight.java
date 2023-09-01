package org.kie.pmml.commons.model.tuples;

import java.util.Objects;

/**
 * Class to represent a <b>Object/Weight (double)</b> tuple
 */
public class KiePMMLValueWeight {

    private final double value;

    private final double weight;

    public KiePMMLValueWeight(double value, double weight) {
        this.value = value;
        this.weight = weight;
    }

    public double getValue() {
        return value;
    }

    public double getWeight() {
        return weight;
    }

    public double weightedValue() {
        return value * weight;
    }

    @Override
    public String toString() {
        return "KiePMMLValueWeight{" +
                "value=" + value +
                ", weight=" + weight +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        KiePMMLValueWeight that = (KiePMMLValueWeight) o;
        return Double.compare(that.weight, weight) == 0 &&
                Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, weight);
    }
}
