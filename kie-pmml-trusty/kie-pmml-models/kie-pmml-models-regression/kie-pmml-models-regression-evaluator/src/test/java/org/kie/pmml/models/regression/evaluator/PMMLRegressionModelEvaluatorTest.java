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
package org.kie.pmml.models.regression.evaluator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.pmml.api.enums.PMML_MODEL;

import static org.assertj.core.api.Assertions.assertThat;

public class PMMLRegressionModelEvaluatorTest {

    private PMMLRegressionModelEvaluator executor;

    @BeforeEach
    public void setUp() {
        executor = new PMMLRegressionModelEvaluator();
    }

    @Test
    void getPMMLModelType() {
        assertThat(executor.getPMMLModelType()).isEqualTo(PMML_MODEL.REGRESSION_MODEL);
    }
}
