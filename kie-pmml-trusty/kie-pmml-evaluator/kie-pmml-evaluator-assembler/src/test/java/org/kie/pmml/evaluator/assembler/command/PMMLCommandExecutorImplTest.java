/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.kie.pmml.evaluator.assembler.command;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Consumer;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;
import org.kie.api.pmml.PMMLRequestData;
import org.kie.api.pmml.ParameterInfo;
import org.kie.pmml.api.exceptions.KiePMMLException;

import static org.junit.Assert.*;

public class PMMLCommandExecutorImplTest {

    @Test(expected = KiePMMLException.class)
    public void validateNoSource() {
        PMMLRequestData pmmlRequestData = new PMMLRequestData();
        pmmlRequestData.setModelName("modelName");
        PMMLCommandExecutorImpl cmdExecutor = new PMMLCommandExecutorImpl();
        cmdExecutor.validate(pmmlRequestData);
    }

    @Test(expected = KiePMMLException.class)
    public void validateEmptySource() {
        PMMLRequestData pmmlRequestData = new PMMLRequestData();
        pmmlRequestData.setModelName("modelName");
        pmmlRequestData.setSource("");
        PMMLCommandExecutorImpl cmdExecutor = new PMMLCommandExecutorImpl();
        cmdExecutor.validate(pmmlRequestData);
    }

    @Test(expected = KiePMMLException.class)
    public void validateNoModelName() {
        PMMLRequestData pmmlRequestData = new PMMLRequestData();
        pmmlRequestData.setSource("source");
        PMMLCommandExecutorImpl cmdExecutor = new PMMLCommandExecutorImpl();
        cmdExecutor.validate(pmmlRequestData);
    }

    @Test(expected = KiePMMLException.class)
    public void validateEmptyModelName() {
        PMMLRequestData pmmlRequestData = new PMMLRequestData();
        pmmlRequestData.setSource("source");
        pmmlRequestData.setModelName("");
        PMMLCommandExecutorImpl cmdExecutor = new PMMLCommandExecutorImpl();
        cmdExecutor.validate(pmmlRequestData);
    }

    @Test()
    public void getCleanedRequestData() {
        PMMLRequestData pmmlRequestData = getPMMLRequestData();
        PMMLCommandExecutorImpl cmdExecutor = new PMMLCommandExecutorImpl();
        PMMLRequestData retrieved = cmdExecutor.getCleanedRequestData(pmmlRequestData);
        assertNotNull(retrieved);
        assertEquals(pmmlRequestData.getSource(), retrieved.getSource());
        assertEquals(pmmlRequestData.getCorrelationId(), retrieved.getCorrelationId());
        assertEquals(pmmlRequestData.getModelName(), retrieved.getModelName());
        Map<String, ParameterInfo> requestParams = retrieved.getMappedRequestParams();
        pmmlRequestData.getRequestParams().forEach(parameterInfo -> {
            assertTrue(requestParams.containsKey(parameterInfo.getName()));
            ParameterInfo cleaned = requestParams.get(parameterInfo.getName());
            assertEquals(parameterInfo.getName(), cleaned.getName());
            assertEquals(parameterInfo.getCorrelationId(), cleaned.getCorrelationId());
            assertEquals(parameterInfo.getType(), cleaned.getType());
            assertEquals(parameterInfo.getValue(), cleaned.getValue().toString());
            assertEquals(cleaned.getType(), cleaned.getValue().getClass());
        });


    }

    @SuppressWarnings("rawtype")
    private PMMLRequestData getPMMLRequestData() {
        PMMLRequestData toReturn = new PMMLRequestData();
        String correlationId = "correlationId";
        toReturn.setSource("source");
        toReturn.setCorrelationId(correlationId);
        toReturn.setModelName("modelName");
        List<Class> classList = Arrays.asList(Integer.class,
                                              Double.class,
                                              Boolean.class,
                                              String.class);
        classList.forEach(aClass -> {
            Object value = getRandomValue(aClass.getSimpleName()).toString();
            ParameterInfo<?> toAdd = new ParameterInfo(correlationId,
                                                       RandomStringUtils.random(4, true, false),
                                                       aClass,
                                                       value);
            toReturn.addRequestParam(toAdd);

        });
        return toReturn;
    }


    private Object getRandomValue(String type) {
        switch (type) {
            case "Integer":
                return new Random().nextInt(40);
            case "Double":
                return new Random().nextDouble();
            case "Boolean":
                return new Random().nextBoolean();
            case "String":
                return RandomStringUtils.random(6, true, false);
            default:
                throw new RuntimeException();

        }
    }
}