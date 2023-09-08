/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.pmml.models.regression.model.enums;

import java.util.Arrays;

import org.kie.pmml.api.exceptions.KieEnumException;

/**
 * @see <a href=http://dmg.org/pmml/v4-4/Regression.html#xsdElement_RegressionModel>RegressionModel</a>
 */
public enum MODEL_TYPE {

    LINEAR_REGRESSION("linearRegression"),
    STEPWISE_POLYNOMIAL_REGRESSION("stepwisePolynomialRegression"),
    LOGISTIC_REGRESSION("logisticRegression");

    private String name;

    MODEL_TYPE(String name) {
        this.name = name;
    }

    public static MODEL_TYPE byName(String name) {
        return Arrays.stream(MODEL_TYPE.values()).filter(value -> name.equals(value.name)).findFirst().orElseThrow(() -> new KieEnumException("Failed to find MODEL_TYPE with name: " + name));
    }

    public String getName() {
        return name;
    }
}
