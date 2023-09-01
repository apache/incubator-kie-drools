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
package org.kie.pmml.compiler.testingutils;

import java.util.Collections;
import java.util.Map;

import org.kie.pmml.api.enums.PMML_MODEL;
import org.kie.pmml.commons.testingutility.KiePMMLTestingModel;
import org.kie.pmml.compiler.api.dto.CompilationDTO;
import org.kie.pmml.compiler.api.provider.ModelImplementationProvider;

public class TestModelImplementationProvider implements ModelImplementationProvider<TestingModel, KiePMMLTestingModel> {

    @Override
    public PMML_MODEL getPMMLModelType() {
        return PMML_MODEL.TEST_MODEL;
    }

    @Override
    public Class<KiePMMLTestingModel> getKiePMMLModelClass() {
        return KiePMMLTestingModel.class;
    }

    @Override
    public Map<String, String> getSourcesMap(final CompilationDTO<TestingModel> compilationDTO) {
        return Collections.emptyMap();
    }


}
