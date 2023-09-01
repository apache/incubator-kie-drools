package org.kie.pmml.models.clustering.model;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class KiePMMLCluster {

    private final List<Double> values;
    private final Optional<String> id;
    private final Optional<String> name;

    public KiePMMLCluster(String id, String name, List<Double> values) {
        this.values = Collections.unmodifiableList(values);
        this.id = Optional.ofNullable(id);
        this.name = Optional.ofNullable(name);
    }

    public List<Double> getValues() {
        return values;
    }

    public double[] getValuesArray() {
        return values.stream().mapToDouble(Double::doubleValue).toArray();
    }

    public Optional<String> getId() {
        return id;
    }

    public Optional<String> getName() {
        return name;
    }
}
