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

import java.util.Map;

import org.kie.api.pmml.PMMLRequestData;
import org.kie.pmml.evaluator.core.utils.PMMLRequestDataBuilder;

/**
 * Class providing common methods
 */
public class PMMLUtils {

    private static final String CORRELATION_ID = "CORRELATION_ID";

    private PMMLUtils() {
        // Avoid instantiation
    }

    public static PMMLRequestData getPMMLRequestData(String modelName, Map<String, Object> parameters) {
        PMMLRequestDataBuilder pmmlRequestDataBuilder = new PMMLRequestDataBuilder(CORRELATION_ID, modelName);
        for (Map.Entry<String, Object> entry : parameters.entrySet()) {
            Object pValue = entry.getValue();
            Class class1 = pValue.getClass();
            pmmlRequestDataBuilder.addParameter(entry.getKey(), pValue, class1);
        }
        return pmmlRequestDataBuilder.build();
    }

    public static PMMLRequestData getPMMLRequestData(String modelName) {
        PMMLRequestDataBuilder pmmlRequestDataBuilder = new PMMLRequestDataBuilder(CORRELATION_ID, modelName);
        return pmmlRequestDataBuilder.build();
    }

}
