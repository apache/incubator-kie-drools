package org.kie.pmml.api.identifiers;

import org.junit.jupiter.api.Test;
import org.kie.efesto.common.api.identifiers.LocalId;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.efesto.common.api.identifiers.LocalUri.SLASH;

class LocalPredictionIdTest {

    private static final String fileName = "fileName";
    private static final String name = "name";

    @Test
    void prefix() {
        String retrieved = new LocalPredictionId(fileName, name).asLocalUri().toUri().getPath();
        String expected = SLASH + LocalPredictionId.PREFIX + SLASH;
        assertThat(retrieved).startsWith(expected);
    }

    @Test
    void getFileName() {
        LocalPredictionId retrieved = new LocalPredictionId(fileName, name);
        assertThat(retrieved.getFileName()).isEqualTo(fileName);
    }

    @Test
    void name() {
        LocalPredictionId retrieved = new LocalPredictionId(fileName, name);
        assertThat(retrieved.name()).isEqualTo(name);
    }

    @Test
    void toLocalId() {
        LocalPredictionId localPredictionId = new LocalPredictionId(fileName, name);
        LocalId retrieved = localPredictionId.toLocalId();
        assertThat(retrieved).isEqualTo(localPredictionId);
    }
}