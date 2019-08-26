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

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.UUID;

import org.kie.api.io.Resource;
import org.kie.api.pmml.PMML4Field;
import org.kie.api.pmml.PMML4Result;
import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.event.DMNRuntimeEventManager;
import org.kie.dmn.core.api.EvaluatorResult;
import org.kie.dmn.core.api.EvaluatorResult.ResultType;
import org.kie.dmn.core.ast.DMNFunctionDefinitionEvaluator.FormalParameter;
import org.kie.dmn.core.ast.EvaluatorResultImpl;
import org.kie.dmn.core.impl.DMNResultImpl;
import org.kie.dmn.core.util.Msg;
import org.kie.dmn.core.util.MsgUtil;
import org.kie.dmn.feel.util.EvalHelper;
import org.kie.dmn.model.api.DMNElement;
import org.kie.pmml.pmml_4_2.PMML4ExecutionHelper;
import org.kie.pmml.pmml_4_2.PMML4ExecutionHelper.PMML4ExecutionHelperFactory;
import org.kie.pmml.pmml_4_2.PMMLRequestDataBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DMNKiePMMLInvocationEvaluator extends AbstractPMMLInvocationEvaluator {

    private static final Logger LOG = LoggerFactory.getLogger(DMNKiePMMLInvocationEvaluator.class);
    private final PMML4ExecutionHelper helper;
    private final PMMLInfo<?> pmmlInfo;

    public DMNKiePMMLInvocationEvaluator(String dmnNS, DMNElement node, Resource pmmlResource, String model, PMMLInfo<?> pmmlInfo) {
        super(dmnNS, node, pmmlResource, model);
        this.pmmlInfo = pmmlInfo;
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
    public EvaluatorResult evaluate(DMNRuntimeEventManager eventManager, DMNResult dmnr) {
        PMMLRequestDataBuilder request = new PMMLRequestDataBuilder(UUID.randomUUID().toString(),
                                                                    model);

        for (FormalParameter p : parameters) {
            Object pValue = getValueForPMMLInput(dmnr, p.name);
            Class class1 = pValue.getClass();
            request.addParameter(p.name, pValue, class1);
        }
        PMML4Result resultHolder = helper.submitRequest(request.build());

        Map<String, Object> resultVariables = resultHolder.getResultVariables();
        Map<String, Object> result = new HashMap<>();
        for (Entry<String, Object> kv : resultVariables.entrySet()) {
            Object r = kv.getValue();
            if (r instanceof PMML4Field) {
                PMML4Field pmml4Field = (PMML4Field) r;
                final String resultName = kv.getKey();
                if (resultName != null && !resultName.isEmpty()) {
                    String name = resultName;
                    Optional<String> outputFieldNameFromInfo = pmmlInfo.getModels()
                                                                       .stream()
                                                                       .filter(m -> model.equals(m.getName()))
                                                                       .flatMap(m -> m.getOutputFieldNames().stream())
                                                                       .filter(ofn -> ofn.equalsIgnoreCase(resultName))
                                                                       .findFirst();
                    if (outputFieldNameFromInfo.isPresent()) {
                        name = outputFieldNameFromInfo.get();
                    }
                    try {
                        Method method = r.getClass().getMethod("getValue");
                        Object value = method.invoke(r);
                        result.put(name, EvalHelper.coerceNumber(value));
                    } catch (Throwable e) {
                        MsgUtil.reportMessage(LOG,
                                              DMNMessage.Severity.WARN,
                                              node,
                                              ((DMNResultImpl) result),
                                              e,
                                              null,
                                              Msg.INVALID_NAME,
                                              name,
                                              e.getMessage());
                        result.put(name, null);
                    }
                }
            }
        }

        return new EvaluatorResultImpl(result, ResultType.SUCCESS);
    }

}
