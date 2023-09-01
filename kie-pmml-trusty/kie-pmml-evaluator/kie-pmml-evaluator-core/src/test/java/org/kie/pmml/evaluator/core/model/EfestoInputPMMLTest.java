package org.kie.pmml.evaluator.core.model;

import org.junit.jupiter.api.Test;
import org.kie.pmml.api.identifiers.LocalComponentIdPmml;
import org.kie.pmml.api.runtime.PMMLRuntimeContext;
import org.kie.pmml.commons.testingutility.PMMLRuntimeContextTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.pmml.commons.CommonTestingUtility.getModelLocalUriIdFromPmmlIdFactory;

class EfestoInputPMMLTest {

    private final String fileNameNoSuffix = "fileNameNoSuffix";
    private final String modelName = "modelName";

    @Test
    void constructor() {
        LocalComponentIdPmml modelLocalUriId = getModelLocalUriIdFromPmmlIdFactory(fileNameNoSuffix, modelName);
        PMMLRuntimeContext inputData = new
                PMMLRuntimeContextTest();
        EfestoInputPMML retrieved = new EfestoInputPMML(modelLocalUriId, inputData);
        assertThat(retrieved.getModelLocalUriId()).isEqualTo(modelLocalUriId);
        assertThat(retrieved.getInputData()).isEqualTo(inputData);
    }
}