/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.dmn.core.pmml;

import java.util.UUID;

import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.io.Resource;
import org.kie.api.pmml.PMML4Result;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieRuntimeFactory;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.core.ast.DMNFunctionDefinitionEvaluator.FormalParameter;
import org.kie.dmn.model.api.DMNElement;
import org.kie.pmml.evaluator.api.executor.PMMLContext;
import org.kie.pmml.evaluator.api.executor.PMMLRuntime;
import org.kie.pmml.evaluator.core.PMMLContextImpl;
import org.kie.pmml.evaluator.core.utils.PMMLRequestDataBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DMNKiePMMLNewInvocationEvaluator extends AbstractDMNKiePMMLInvocationEvaluator {

    private static final Logger LOG = LoggerFactory.getLogger(DMNKiePMMLNewInvocationEvaluator.class);
    private final PMMLRuntime pmmlRuntime;

    public DMNKiePMMLNewInvocationEvaluator(String dmnNS, DMNElement node, Resource pmmlResource, String model, PMMLInfo<?> pmmlInfo) {
        super(dmnNS, node, pmmlResource, model, pmmlInfo);
        final KieServices kieServices = KieServices.get();
        KieContainer kieContainer = kieServices.newKieClasspathContainer();
        KieBase kieBase = kieContainer.getKieBase(model);
        final KieRuntimeFactory kieRuntimeFactory = KieRuntimeFactory.of(kieBase);
        pmmlRuntime = kieRuntimeFactory.get(PMMLRuntime.class);
    }

    @Override
    protected PMML4Result getPMML4Result(DMNResult dmnr) {
        LOG.debug("getPMML4Result");
        PMMLContext pmmlContext = getPMMLPMMLContext(UUID.randomUUID().toString(), model, dmnr);
        return pmmlRuntime.evaluate(model, pmmlContext);
    }

    private PMMLContext getPMMLPMMLContext(String correlationId, String modelName, DMNResult dmnr) {
        PMMLRequestDataBuilder pmmlRequestDataBuilder = new PMMLRequestDataBuilder(correlationId, modelName);
        for (FormalParameter p : parameters) {
            Object pValue = getValueForPMMLInput(dmnr, p.name);
            Class class1 = pValue.getClass();
            pmmlRequestDataBuilder.addParameter(p.name, pValue, class1);
        }
        return new PMMLContextImpl(pmmlRequestDataBuilder.build());
    }
}
