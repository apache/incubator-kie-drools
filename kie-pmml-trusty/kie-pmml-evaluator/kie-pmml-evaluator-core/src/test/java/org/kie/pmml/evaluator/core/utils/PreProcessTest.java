/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.pmml.evaluator.core.utils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.kie.api.pmml.PMMLRequestData;
import org.kie.api.pmml.ParameterInfo;
import org.kie.pmml.api.enums.MINING_FUNCTION;
import org.kie.pmml.api.runtime.PMMLContext;
import org.kie.pmml.evaluator.core.PMMLContextImpl;
import org.kie.pmml.evaluator.core.service.PMMLRuntimeInternalImplTest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PreProcessTest {

    @Test
    public void addMissingValuesReplacements() {
        Map<String, Object> missingValueReplacementMap = new HashMap<>();
        missingValueReplacementMap.put("fieldA", "one");
        missingValueReplacementMap.put("fieldB", 2);
        PMMLRuntimeInternalImplTest.KiePMMLTestingModel model = PMMLRuntimeInternalImplTest.KiePMMLTestingModel.builder("TESTINGMODEL", Collections.emptyList(), MINING_FUNCTION.REGRESSION)
                .withMissingValueReplacementMap(missingValueReplacementMap)
                .build();
        PMMLRequestData pmmlRequestData = new PMMLRequestData("123", "modelName");
        pmmlRequestData.addRequestParam("age", 123);
        pmmlRequestData.addRequestParam("work", "work");
        PMMLContext pmmlContext = new PMMLContextImpl(pmmlRequestData);
        missingValueReplacementMap.keySet().forEach(key -> {
            assertFalse(pmmlContext.getRequestData().getMappedRequestParams().containsKey(key));
            assertFalse(pmmlContext.getMissingValueReplacedMap().containsKey(key));
        });
        PreProcess.addMissingValuesReplacements(model, pmmlContext);
        missingValueReplacementMap.forEach((key, value) -> {
            assertTrue(pmmlContext.getRequestData().getMappedRequestParams().containsKey(key));
            final ParameterInfo<?> parameterInfo = pmmlContext.getRequestData().getMappedRequestParams().get(key);
            assertEquals(key, parameterInfo.getName());
            assertEquals(value.getClass(), parameterInfo.getType());
            assertEquals(value, parameterInfo.getValue());
            assertTrue(pmmlContext.getMissingValueReplacedMap().containsKey(key));
            assertEquals(value, pmmlContext.getMissingValueReplacedMap().get(key));
        });
    }

}