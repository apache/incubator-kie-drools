package org.kie.pmml.api.identifiers;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PredictionIdsTest {

    private static final String fileName = "fileName";
    private static final String name = "name";

    @Test
    void get() {
        LocalPredictionId retrieved = new PredictionIds().get(fileName, name);
        LocalPredictionId expected = new LocalPredictionId(fileName, name);
        assertThat(retrieved).isEqualTo(expected);
    }
}