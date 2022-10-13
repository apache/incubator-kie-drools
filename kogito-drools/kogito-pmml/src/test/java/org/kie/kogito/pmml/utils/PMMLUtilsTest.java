/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.kogito.pmml.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;
import org.kie.api.pmml.PMMLRequestData;
import org.kie.api.pmml.ParameterInfo;

import static org.assertj.core.api.Assertions.assertThat;

class PMMLUtilsTest {

    @Test
    void getPMMLRequestData() {
        final String modelName = "MODEL_NAME";
        final Map<String, Object> parameters = getParameters();
        final PMMLRequestData retrieved = PMMLUtils.getPMMLRequestData(modelName, parameters);
        assertThat(retrieved).isNotNull();
        assertThat(retrieved.getModelName()).isEqualTo(modelName);
        final Map<String, ParameterInfo> parameterInfos = retrieved.getMappedRequestParams();
        assertThat(parameterInfos).hasSameSizeAs(parameters);

        assertThat(parameters).allSatisfy((key, value) -> {
            assertThat(parameterInfos).containsKey(key);
            ParameterInfo parameterInfo = parameterInfos.get(key);
            assertThat(parameterInfo.getValue()).isEqualTo(value);
            assertThat(parameterInfo.getType()).isEqualTo(value.getClass());
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