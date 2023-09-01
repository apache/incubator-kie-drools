package org.kie.pmml.commons.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.commons.testingutility.PMMLRuntimeContextTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;

public class KiePMMLFactoryModelTest {

    private KiePMMLFactoryModel kiePMMLFactoryModel;

    @BeforeEach
    public void setup() {
        kiePMMLFactoryModel = new KiePMMLFactoryModel("", "", "", new HashMap<>());
    }

    @Test
    void getSourcesMap() {
        assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(() -> {
            Map<String, String> retrieved = kiePMMLFactoryModel.getSourcesMap();
            retrieved.put("KEY", "VALUE");
        });
    }

    @Test
    void addSourceMap() {
        Map<String, String> retrieved = kiePMMLFactoryModel.getSourcesMap();
        assertThat(retrieved).isEmpty();
        kiePMMLFactoryModel.addSourceMap("KEY", "VALUE");
        retrieved = kiePMMLFactoryModel.getSourcesMap();
        assertThat(retrieved).containsKey("KEY");
        assertThat(retrieved.get("KEY")).isEqualTo("VALUE");
    }

    @Test
    void evaluate() {
        assertThatExceptionOfType(KiePMMLException.class).isThrownBy(() -> {
            kiePMMLFactoryModel.evaluate(Collections.emptyMap(), new PMMLRuntimeContextTest());
        });
    }
}