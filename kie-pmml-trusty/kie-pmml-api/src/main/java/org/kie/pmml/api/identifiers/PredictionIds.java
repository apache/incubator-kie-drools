package org.kie.pmml.api.identifiers;

import org.kie.efesto.common.api.identifiers.ComponentRoot;

public class PredictionIds implements ComponentRoot {
    public LocalPredictionId get(String fileName, String name) {
        return new LocalPredictionId(fileName, name);
    }
}
