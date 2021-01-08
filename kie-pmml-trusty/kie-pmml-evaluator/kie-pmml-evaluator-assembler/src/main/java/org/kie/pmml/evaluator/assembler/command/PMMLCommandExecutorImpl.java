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

import org.kie.api.pmml.PMML4Result;
import org.kie.api.pmml.PMMLRequestData;
import org.kie.internal.pmml.PMMLCommandExecutor;
import org.kie.pmml.api.PMMLRuntimeFactory;
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.api.runtime.PMMLContext;
import org.kie.pmml.api.runtime.PMMLRuntime;
import org.kie.pmml.evaluator.assembler.factories.PMMLRuntimeFactoryImpl;
import org.kie.pmml.evaluator.core.PMMLContextImpl;

public class PMMLCommandExecutorImpl implements PMMLCommandExecutor {

    private static final PMMLRuntimeFactory PMML_RUNTIME_FACTORY = new PMMLRuntimeFactoryImpl();

    /**
     * Evaluate the given <code>PMMLRequestData<code>
     * @param pmmlRequestData : it must contain the pmml file name (in the <i>source</i> property)
     * and the model name
     * @return
     */
    @Override
    public PMML4Result execute(final PMMLRequestData pmmlRequestData) {
        validate(pmmlRequestData);
        final String pmmlFileName = pmmlRequestData.getSource();
        final PMMLRuntime pmmlRuntime = getPMMLRuntime(pmmlFileName);
        return evaluate(pmmlRequestData, pmmlRuntime);
    }

    protected void validate(final PMMLRequestData pmmlRequestData) {
        String toValidate = pmmlRequestData.getSource();
        if (toValidate == null || toValidate.isEmpty()) {
            throw new KiePMMLException("Missing required field 'source' with the PMML file name");
        }
        toValidate = pmmlRequestData.getModelName();
        if (toValidate == null || toValidate.isEmpty()) {
            throw new KiePMMLException("Missing required field 'modelName'");
        }
    }

    private PMML4Result evaluate(final PMMLRequestData pmmlRequestData, final PMMLRuntime pmmlRuntime) {
        String modelName = pmmlRequestData.getModelName();
        final PMMLContext pmmlContext = new PMMLContextImpl(pmmlRequestData);
        return pmmlRuntime.evaluate(modelName, pmmlContext);
    }

    private PMMLRuntime getPMMLRuntime(String pmmlFileName) {
        return PMML_RUNTIME_FACTORY.getPMMLRuntimeFromClasspath(pmmlFileName);
    }
}
