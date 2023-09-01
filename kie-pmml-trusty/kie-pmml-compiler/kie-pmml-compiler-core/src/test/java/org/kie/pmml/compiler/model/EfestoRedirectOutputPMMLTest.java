package org.kie.pmml.compiler.model;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.kie.efesto.common.api.identifiers.LocalUri;
import org.kie.efesto.common.api.identifiers.ModelLocalUriId;

import static org.assertj.core.api.Assertions.assertThat;

class EfestoRedirectOutputPMMLTest {

    private static ModelLocalUriId modelLocalUriId;

    @BeforeAll
    public static void setup() {
        String path = "/example/some-id/instances/some-instance-id";
        LocalUri parsed = LocalUri.parse(path);
        modelLocalUriId = new ModelLocalUriId(parsed);
    }

    @Test
    void constructor() {
        String modelFile = "modelFile";
        EfestoRedirectOutputPMML retrieved = new EfestoRedirectOutputPMML(modelLocalUriId, modelFile);
        assertThat(retrieved.getModelLocalUriId()).isEqualTo(modelLocalUriId);
        assertThat(retrieved.getFullClassNames()).isNull();
    }

    @Test
    void getTargetEngine() {
        String modelFile = "modelFile";
        EfestoRedirectOutputPMML retrieved = new EfestoRedirectOutputPMML(modelLocalUriId, modelFile);
        assertThat(retrieved.getTargetEngine()).isEqualTo("drl");
    }

    @Test
    void getContent() {
        String modelFile = "modelFile";
        EfestoRedirectOutputPMML retrieved = new EfestoRedirectOutputPMML(modelLocalUriId, modelFile);
        assertThat(retrieved.getContent()).isEqualTo(modelFile);
    }
}