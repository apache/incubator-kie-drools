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
package org.kie.pmml.evaluator.core.model;

import org.junit.jupiter.api.Test;
import org.kie.pmml.api.identifiers.LocalComponentIdPmml;
import org.kie.pmml.api.runtime.PMMLRuntimeContext;
import org.kie.pmml.commons.testingutility.PMMLRuntimeContextTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.pmml.commons.CommonTestingUtility.getModelLocalUriIdFromPmmlIdFactory;

class EfestoInputPMMLTest {

    private final String fileNameNoSuffix = "fileNameNoSuffix";
    private final String modelName = "modelName";

    @Test
    void constructor() {
        LocalComponentIdPmml modelLocalUriId = getModelLocalUriIdFromPmmlIdFactory(fileNameNoSuffix, modelName);
        PMMLRuntimeContext inputData = new
                PMMLRuntimeContextTest();
        EfestoInputPMML retrieved = new EfestoInputPMML(modelLocalUriId, inputData);
        assertThat(retrieved.getModelLocalUriId()).isEqualTo(modelLocalUriId);
        assertThat(retrieved.getInputData()).isEqualTo(inputData);
    }
}