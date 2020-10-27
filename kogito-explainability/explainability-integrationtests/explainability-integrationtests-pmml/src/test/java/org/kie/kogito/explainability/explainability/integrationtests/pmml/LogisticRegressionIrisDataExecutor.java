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
package org.kie.kogito.explainability.explainability.integrationtests.pmml;

import java.util.HashMap;
import java.util.Map;

import org.kie.api.pmml.PMML4Result;
import org.kie.pmml.api.runtime.PMMLRuntime;

public class LogisticRegressionIrisDataExecutor extends AbstractPMMLExecutor {

    public static final String MODEL_NAME = "LogisticRegressionIrisData";

    private final double sepalLength;
    private final double sepalWidth;
    private final double petalLength;
    private final double petalWidth;

    public LogisticRegressionIrisDataExecutor(double sepalLength, double sepalWidth, double petalLength,
                                              double petalWidth) {
        this.sepalLength = sepalLength;
        this.sepalWidth = sepalWidth;
        this.petalLength = petalLength;
        this.petalWidth = petalWidth;
    }

    public PMML4Result execute(final PMMLRuntime pmmlRuntime) {
        final Map<String, Object> inputData = new HashMap<>();
        inputData.put("Sepal.Length", sepalLength);
        inputData.put("Sepal.Width", sepalWidth);
        inputData.put("Petal.Length", petalLength);
        inputData.put("Petal.Width", petalWidth);
        return evaluate(pmmlRuntime, inputData, MODEL_NAME);
    }

}
