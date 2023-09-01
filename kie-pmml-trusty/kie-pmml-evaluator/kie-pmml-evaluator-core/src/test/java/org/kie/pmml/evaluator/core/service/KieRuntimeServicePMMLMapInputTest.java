package org.kie.pmml.evaluator.core.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.kie.efesto.common.api.identifiers.ModelLocalUriId;
import org.kie.efesto.runtimemanager.api.model.BaseEfestoInput;
import org.kie.efesto.runtimemanager.api.model.EfestoInput;
import org.kie.efesto.runtimemanager.api.model.EfestoRuntimeContext;
import org.kie.memorycompiler.KieMemoryCompiler;
import org.kie.pmml.evaluator.core.model.EfestoOutputPMML;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.pmml.TestingHelper.getEfestoContext;
import static org.kie.pmml.TestingHelper.getInputData;
import static org.kie.pmml.TestingHelper.getPMMLContext;
import static org.kie.pmml.commons.CommonTestingUtility.getModelLocalUriIdFromPmmlIdFactory;

class KieRuntimeServicePMMLMapInputTest {

    private static final String MODEL_NAME = "TestMod";
    private static final String FILE_NAME = "FileName";
    private static KieRuntimeServicePMMLMapInput kieRuntimeServicePMMLMapInput;
    private static KieMemoryCompiler.MemoryCompilerClassLoader memoryCompilerClassLoader;

    private ModelLocalUriId modelLocalUriId;

    private EfestoInput<Map<String, Object>> inputPMML;
    private EfestoRuntimeContext efestoRuntimeContext;

    @BeforeAll
    public static void setup() {
        kieRuntimeServicePMMLMapInput = new KieRuntimeServicePMMLMapInput();
        memoryCompilerClassLoader =
                new KieMemoryCompiler.MemoryCompilerClassLoader(Thread.currentThread().getContextClassLoader());
    }

    @Test
    void canManageManageableInput() {
        modelLocalUriId = getModelLocalUriIdFromPmmlIdFactory(FILE_NAME, MODEL_NAME);
        Map<String, Object> inputData = new HashMap<>();
        inputPMML = new BaseEfestoInput<>(modelLocalUriId, inputData);
        assertThat(kieRuntimeServicePMMLMapInput.canManageInput(inputPMML,
                                                                getEfestoContext(memoryCompilerClassLoader))).isTrue();
    }

    @Test
    void evaluateCorrectInput() {
        modelLocalUriId = getModelLocalUriIdFromPmmlIdFactory(FILE_NAME, MODEL_NAME);
        inputPMML = new BaseEfestoInput<>(modelLocalUriId,
                                          getInputData(MODEL_NAME, FILE_NAME));
        efestoRuntimeContext = getEfestoContext(memoryCompilerClassLoader);
        Optional<EfestoOutputPMML> retrieved = kieRuntimeServicePMMLMapInput.evaluateInput(inputPMML,
                                                                                           efestoRuntimeContext);
        assertThat(retrieved).isNotNull().isPresent();
    }

    @Test
    void evaluateWrongIdentifier() {
        modelLocalUriId = getModelLocalUriIdFromPmmlIdFactory(FILE_NAME, "wrongmodel");
        inputPMML = new BaseEfestoInput<>(modelLocalUriId,
                                          getInputData(MODEL_NAME, FILE_NAME));
        efestoRuntimeContext = getEfestoContext(memoryCompilerClassLoader);
        Optional<EfestoOutputPMML> retrieved = kieRuntimeServicePMMLMapInput.evaluateInput(inputPMML,
                                                                                           efestoRuntimeContext);
        assertThat(retrieved).isNotNull().isNotPresent();
    }

    @Test
    void evaluatePMMLRuntimeContext() {
        modelLocalUriId = getModelLocalUriIdFromPmmlIdFactory(FILE_NAME, MODEL_NAME);
        inputPMML = new BaseEfestoInput<>(modelLocalUriId, getInputData(MODEL_NAME, FILE_NAME));
        efestoRuntimeContext = getPMMLContext(FILE_NAME, MODEL_NAME, memoryCompilerClassLoader);
        Optional<EfestoOutputPMML> retrieved = kieRuntimeServicePMMLMapInput.evaluateInput(inputPMML,
                                                                                           efestoRuntimeContext);
        assertThat(retrieved).isNotNull().isPresent();
    }
}