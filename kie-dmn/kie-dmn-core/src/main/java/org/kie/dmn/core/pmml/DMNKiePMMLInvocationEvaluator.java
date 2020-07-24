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
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;

import org.kie.api.io.Resource;
import org.kie.api.pmml.PMML4Field;
import org.kie.api.pmml.PMML4OutputField;
import org.kie.api.pmml.PMML4Result;
import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.event.DMNRuntimeEventManager;
import org.kie.dmn.core.ast.DMNFunctionDefinitionEvaluator.FormalParameter;
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
    protected PMML4Result getPMML4Result(DMNRuntimeEventManager eventManager, DMNResult dmnr) {
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

    @Override
    protected Map<String, Object> getOutputFieldValues(PMML4Result pmml4Result, Map<String, Object> resultVariables, DMNResult dmnr) {
        LOG.debug("getOutputFieldValues");
        Map<String, Object> toReturn = new HashMap<>();
        for (Map.Entry<String, Object> kv : resultVariables.entrySet()) {
            String resultName = kv.getKey();
            if (resultName == null || resultName.isEmpty()) {
                continue;
            }
            Object r = kv.getValue();
            if (r instanceof PMML4Field) {
                populateWithPMML4Field(toReturn, kv.getKey(), (PMML4Field) r, dmnr);
            }
        }
        return toReturn;
    }

    @Override
    protected Map<String, Object> getPredictedValues(PMML4Result pmml4Result, DMNResult dmnr) {
        LOG.debug("getPredictedValues");
        final DMNPMMLModelInfo modelInfo = getDMNPMMLInfo();
        if (modelInfo == null) {
            return Collections.emptyMap();
        }
        return getTargetObjects(modelInfo.getTargetFieldNames(), pmml4Result, dmnr);
    }

    private Map<String, Object> getTargetObjects(Collection<String> targetFieldNames, PMML4Result pmml4Result, DMNResult dmnr) {
        Map<String, Object> toReturn = new HashMap<>();
        pmml4Result.getResultVariables().forEach((key, value) -> {
            if (containsIgnoredCaseString(targetFieldNames, key) && value instanceof PMML4OutputField) {
                PMML4OutputField pmml4OutputField = (PMML4OutputField) value;
                populateWithReflection(toReturn, key, pmml4OutputField, dmnr);
            }
        });
        return toReturn;
    }

    private void populateWithPMML4Field(Map<String, Object> toPopulate, String resultName, PMML4Field pmml4Field, DMNResult dmnr) {
        Optional<String> outputFieldNameFromInfo = getOutputFieldNameFromInfo(resultName);
        if (outputFieldNameFromInfo.isPresent()) {
            String name = outputFieldNameFromInfo.get();
            populateWithReflection(toPopulate, name, pmml4Field, dmnr);
        }
    }

    private void populateWithReflection(Map<String, Object> toPopulate, String name, PMML4Field pmml4Field, DMNResult dmnr) {
        try {
            Method method = pmml4Field.getClass().getMethod("getValue");
            Object value = method.invoke(pmml4Field);
            toPopulate.put(name, EvalHelper.coerceNumber(value));
        } catch (Throwable e) {
            MsgUtil.reportMessage(LOG,
                                  DMNMessage.Severity.WARN,
                                  node,
                                  ((DMNResultImpl) dmnr),
                                  e,
                                  null,
                                  Msg.INVALID_NAME,
                                  name,
                                  e.getMessage());
            toPopulate.put(name, null);
        }
    }

    /**
     * Returns <code>true</code> if <b>containingCollection</b> contains <b>searchedString</b> OR <b>searchedString.toLowerCase()</b>
     * @param containingCollection
     * @param searchedString
     * @return
     */
    private boolean containsIgnoredCaseString(Collection<String> containingCollection, String searchedString) {
        return containingCollection.contains(searchedString) || containingCollection.contains(searchedString.toLowerCase());
    }

    private DMNPMMLModelInfo getDMNPMMLInfo() {
        return pmmlInfo.getModels()
                .stream()
                .filter((Predicate<Object>) o -> (o instanceof DMNPMMLModelInfo) && ((DMNPMMLModelInfo) o).name.equals(model))
                .map(o -> (DMNPMMLModelInfo) o)
                .findFirst()
                .orElse(null);
    }
}
