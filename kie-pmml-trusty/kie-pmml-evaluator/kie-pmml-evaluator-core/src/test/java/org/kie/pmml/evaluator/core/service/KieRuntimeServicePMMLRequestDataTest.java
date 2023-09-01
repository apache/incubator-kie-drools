package org.kie.pmml.evaluator.core.service;

import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.kie.api.pmml.PMMLRequestData;
import org.kie.efesto.common.api.identifiers.ModelLocalUriId;
import org.kie.efesto.runtimemanager.api.model.BaseEfestoInput;
import org.kie.efesto.runtimemanager.api.model.EfestoInput;
import org.kie.efesto.runtimemanager.api.model.EfestoRuntimeContext;
import org.kie.memorycompiler.KieMemoryCompiler;
import org.kie.pmml.evaluator.core.model.EfestoOutputPMML;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.pmml.TestingHelper.getEfestoContext;
import static org.kie.pmml.TestingHelper.getPMMLContext;
import static org.kie.pmml.TestingHelper.getPMMLRequestData;
import static org.kie.pmml.TestingHelper.getPMMLRequestDataWithInputData;
import static org.kie.pmml.commons.CommonTestingUtility.getModelLocalUriIdFromPmmlIdFactory;

class KieRuntimeServicePMMLRequestDataTest {
    private static final String MODEL_NAME = "TestMod";
    private static final String FILE_NAME = "FileName";
    private static KieRuntimeServicePMMLRequestData kieRuntimeServicePMMLRequestData;
    private static KieMemoryCompiler.MemoryCompilerClassLoader memoryCompilerClassLoader;

    private ModelLocalUriId modelLocalUriId;

    @BeforeAll
    public static void setup() {
        kieRuntimeServicePMMLRequestData = new KieRuntimeServicePMMLRequestData();
        memoryCompilerClassLoader =
                new KieMemoryCompiler.MemoryCompilerClassLoader(Thread.currentThread().getContextClassLoader());
    }
    @Test
    void canManageEfestoInput() {
        modelLocalUriId = getModelLocalUriIdFromPmmlIdFactory(FILE_NAME, MODEL_NAME);
        EfestoRuntimeContext runtimeContext = getEfestoContext(memoryCompilerClassLoader);
        PMMLRequestData pmmlRequestData = new PMMLRequestData();
        EfestoInput<PMMLRequestData> inputPMML = new BaseEfestoInput<>(modelLocalUriId, pmmlRequestData);
        assertThat(kieRuntimeServicePMMLRequestData.canManageInput(inputPMML, runtimeContext)).isTrue();
    }

    @Test
    void evaluateCorrectInput() {
        modelLocalUriId = getModelLocalUriIdFromPmmlIdFactory(FILE_NAME, MODEL_NAME);
        PMMLRequestData pmmlRequestData = getPMMLRequestDataWithInputData(MODEL_NAME, FILE_NAME);
        EfestoInput<PMMLRequestData> efestoInput = new BaseEfestoInput<>(modelLocalUriId, pmmlRequestData);
        Optional<EfestoOutputPMML> retrieved = kieRuntimeServicePMMLRequestData.evaluateInput(efestoInput,
                                                                                              getEfestoContext(memoryCompilerClassLoader));
        assertThat(retrieved).isNotNull().isPresent();
    }

    @Test
    void evaluateWrongIdentifier() {
        modelLocalUriId = getModelLocalUriIdFromPmmlIdFactory(FILE_NAME, "wrongmodel");
        PMMLRequestData pmmlRequestData = getPMMLRequestData(MODEL_NAME, FILE_NAME);
        EfestoInput<PMMLRequestData> efestoInput = new BaseEfestoInput<>(modelLocalUriId, pmmlRequestData);
        Optional<EfestoOutputPMML> retrieved = kieRuntimeServicePMMLRequestData.evaluateInput(efestoInput,
                                                                                              getEfestoContext(memoryCompilerClassLoader));
        assertThat(retrieved).isNotNull().isNotPresent();
    }

    @Test
    void evaluatePMMLRuntimeContext() {
        modelLocalUriId = getModelLocalUriIdFromPmmlIdFactory(FILE_NAME, MODEL_NAME);
        EfestoRuntimeContext runtimeContext =
                getPMMLContext(FILE_NAME, MODEL_NAME, memoryCompilerClassLoader);
        PMMLRequestData pmmlRequestData = getPMMLRequestData(MODEL_NAME, FILE_NAME);
        EfestoInput<PMMLRequestData> efestoInput = new BaseEfestoInput<>(modelLocalUriId, pmmlRequestData);

        Optional<EfestoOutputPMML> retrieved = kieRuntimeServicePMMLRequestData.evaluateInput(efestoInput, runtimeContext);
        assertThat(retrieved).isNotNull().isPresent();
    }
}