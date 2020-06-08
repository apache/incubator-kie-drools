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

import org.kie.api.io.Resource;
import org.kie.api.pmml.PMML4Result;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.core.ast.DMNFunctionDefinitionEvaluator.FormalParameter;
import org.kie.dmn.model.api.DMNElement;
import org.kie.pmml.pmml_4_2.PMML4ExecutionHelper;
import org.kie.pmml.pmml_4_2.PMML4ExecutionHelper.PMML4ExecutionHelperFactory;
import org.kie.pmml.pmml_4_2.PMMLRequestDataBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DMNKiePMMLInvocationEvaluator extends AbstractDMNKiePMMLInvocationEvaluator {

    private static final Logger LOG = LoggerFactory.getLogger(DMNKiePMMLInvocationEvaluator.class);
    private final PMML4ExecutionHelper helper;

    public DMNKiePMMLInvocationEvaluator(String dmnNS, DMNElement node, Resource pmmlResource, String model, PMMLInfo<?> pmmlInfo) {
        super(dmnNS, node, pmmlResource, model, pmmlInfo);
        helper = PMML4ExecutionHelperFactory.getExecutionHelper(model,
                                                                pmmlResource,
                                                                null,
                                                                pmmlInfo.getModels().stream().anyMatch(m -> "MiningModel".equals(m.className) &&
                                                                        ((model != null && model.equals(m.name)) ||
                                                                                (model == null && m.name == null))));
        helper.addPossiblePackageName(pmmlInfo.getHeader().getHeaderExtensions().get("modelPackage"));
        helper.initModel();
    }

    @Override
    protected PMML4Result getPMML4Result(DMNResult dmnr) {
        LOG.debug("getPMML4Result");
        PMMLRequestDataBuilder request = new PMMLRequestDataBuilder(UUID.randomUUID().toString(),
                                                                    model);

        for (FormalParameter p : parameters) {
            Object pValue = getValueForPMMLInput(dmnr, p.name);
            Class class1 = pValue.getClass();
            request.addParameter(p.name, pValue, class1);
        }
        return helper.submitRequest(request.build());
    }
}
