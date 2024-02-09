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
package org.kie.pmml.commons.testingutility;

import java.util.List;
import java.util.Map;

import org.kie.pmml.api.enums.MINING_FUNCTION;
import org.kie.pmml.api.enums.PMML_MODEL;
import org.kie.pmml.api.runtime.PMMLRuntimeContext;
import org.kie.pmml.commons.model.KiePMMLExtension;
import org.kie.pmml.commons.model.KiePMMLModel;

/**
 * <b>Fake</b> model used for testing. It is mapped to <code>PMML_MODEL.TEST_MODEL</code>
 */
public class KiePMMLTestingModel extends KiePMMLModel {


    private static final long serialVersionUID = 9009765353822151536L;
    public static PMML_MODEL PMML_MODEL_TYPE = PMML_MODEL.TEST_MODEL;

    public KiePMMLTestingModel(String fileName, String name, List<KiePMMLExtension> extensions) {
        super(fileName,  name, extensions);
    }

    public static Builder builder(String fileName, String name, List<KiePMMLExtension> extensions, MINING_FUNCTION miningFunction) {
        return new Builder(fileName, name, extensions, miningFunction);
    }

    @Override
    public Object evaluate(final Map<String, Object> requestData,
                           final PMMLRuntimeContext context) {
        return context;
    }

    public static class Builder extends KiePMMLModel.Builder<KiePMMLTestingModel> {

        private Builder(String fileName, String name, List<KiePMMLExtension> extensions, MINING_FUNCTION miningFunction) {
            super("TestingModel-", PMML_MODEL_TYPE, miningFunction, () -> new KiePMMLTestingModel(fileName, name, extensions));
        }
    }
}