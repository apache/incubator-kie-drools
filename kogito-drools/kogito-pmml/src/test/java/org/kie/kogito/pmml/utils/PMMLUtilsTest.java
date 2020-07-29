package org.kie.kogito.pmml.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;
import org.kie.api.pmml.PMMLRequestData;
import org.kie.api.pmml.ParameterInfo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PMMLUtilsTest {

    @Test
    void getPMMLRequestData() {
        final String modelName = "MODEL_NAME";
        final Map<String, Object> parameters = getParameters();
        final PMMLRequestData retrieved = PMMLUtils.getPMMLRequestData(modelName, parameters);
        assertNotNull(retrieved);
        assertEquals(modelName,  retrieved.getModelName());
        final Map<String, ParameterInfo> parameterInfos = retrieved.getMappedRequestParams();
        assertEquals(parameters.size(), parameterInfos.size());
        parameters.forEach((key, value) -> {
            assertTrue(parameterInfos.containsKey(key));
            ParameterInfo parameterInfo = parameterInfos.get(key);
            assertEquals(value, parameterInfo.getValue());
            assertEquals(value.getClass(), parameterInfo.getType());
        });
    }

    private Map<String, Object> getParameters() {
        final Map<String, Object> toReturn = new HashMap<>();
        IntStream.range(0, 3).forEach(i -> {
            toReturn.put("KEY_" + i, "VALUE_" + i);
        });
        return toReturn;
    }
}