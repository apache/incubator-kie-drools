package org.kie.kogito.pmml;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.IntStream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.kie.api.KieBase;
import org.kie.api.pmml.PMML4Result;
import org.kie.api.pmml.PMMLRequestData;
import org.kie.api.pmml.ParameterInfo;
import org.kie.pmml.commons.model.KiePMMLModel;
import org.kie.pmml.evaluator.api.executor.PMMLContext;
import org.kie.pmml.evaluator.api.executor.PMMLRuntime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PmmlPredictionModelTest {

    private static final PMML4Result  PMML_4_RESULT = new PMML4Result();
    private final static String MODEL_NAME = "MODEL_NAME";
    private final static KiePMMLModel KIE_PMML_MODEL = getKiePMMLModelInternal(MODEL_NAME);
    private final static PMMLRuntime PMML_RUNTIME = getPMMLRuntime();

    private static PmmlPredictionModel pmmlPredictionModel;

    @BeforeAll
    public static void setup() {
        pmmlPredictionModel = new PmmlPredictionModel(PMML_RUNTIME, MODEL_NAME);
        assertNotNull(pmmlPredictionModel);
    }


    @Test
    void newContext() {
        final Map<String, Object> parameters = getParameters();
        PMMLContext retrieved = pmmlPredictionModel.newContext(parameters);
        assertNotNull(retrieved);
        PMMLRequestData pmmlRequestData = retrieved.getRequestData();
        assertNotNull(retrieved);
        assertEquals(MODEL_NAME,  pmmlRequestData.getModelName());
        final Map<String, ParameterInfo> parameterInfos = pmmlRequestData.getMappedRequestParams();
        assertEquals(parameters.size(), parameterInfos.size());
        parameters.forEach((key, value) -> {
            assertTrue(parameterInfos.containsKey(key));
            ParameterInfo parameterInfo = parameterInfos.get(key);
            assertEquals(value, parameterInfo.getValue());
            assertEquals(value.getClass(), parameterInfo.getType());
        });
    }

    @Test
    void evaluateAll() {
        final Map<String, Object> parameters = getParameters();
        PMMLContext context = pmmlPredictionModel.newContext(parameters);
        assertEquals(PMML_4_RESULT, pmmlPredictionModel.evaluateAll(context));
    }

    @Test
    void getKiePMMLModel() {
        assertEquals(KIE_PMML_MODEL, pmmlPredictionModel.getKiePMMLModel());
    }

    private Map<String, Object> getParameters() {
        final Map<String, Object> toReturn = new HashMap<>();
        IntStream.range(0, 3).forEach(i -> {
            toReturn.put("KEY_" + i, "VALUE_" + i);
        });
        return toReturn;
    }

    private static PMMLRuntime getPMMLRuntime() {
        return new PMMLRuntime() {

            private final List<KiePMMLModel> models = Collections.singletonList(KIE_PMML_MODEL);

            @Override
            public List<KiePMMLModel> getModels() {
                return models;
            }

            @Override
            public Optional<KiePMMLModel> getModel(String s) {
                return models.stream().filter(model -> model.getName().equals(s)).findFirst();
            }

            @Override
            public PMML4Result evaluate(String s, PMMLContext pmmlContext) {
                return PMML_4_RESULT;
            }

            @Override
            public KieBase getKnowledgeBase() {
                return null;
            }
        };
    }

    private static KiePMMLModel getKiePMMLModelInternal(String modelName) {
        return new KiePMMLModel(modelName, Collections.emptyList()) {

            @Override
            public Object evaluate(Object o, Map<String, Object> map) {
                return null;
            }
        };
    }
}