/*
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
package org.kie.dmn.core.pmml;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.event.DMNRuntimeEventManager;
import org.kie.dmn.core.ast.DMNFunctionDefinitionEvaluator;
import org.kie.dmn.core.impl.DMNResultImpl;
import org.kie.dmn.core.util.Msg;
import org.kie.dmn.core.util.MsgUtil;
import org.kie.dmn.feel.util.NumberEvalHelper;
import org.kie.dmn.model.api.DMNElement;
import org.kie.efesto.common.api.identifiers.LocalUri;
import org.kie.efesto.common.api.identifiers.ModelLocalUriId;
import org.kie.efesto.common.api.model.EfestoCompilationContext;
import org.kie.efesto.common.api.model.GeneratedResources;
import org.kie.efesto.common.core.storage.ContextStorage;
import org.kie.efesto.runtimemanager.api.exceptions.EfestoRuntimeManagerException;
import org.kie.efesto.runtimemanager.api.exceptions.KieRuntimeServiceException;
import org.kie.efesto.runtimemanager.api.model.BaseEfestoInput;
import org.kie.efesto.runtimemanager.api.model.EfestoInput;
import org.kie.efesto.runtimemanager.api.model.EfestoLocalRuntimeContext;
import org.kie.efesto.runtimemanager.api.model.EfestoOutput;
import org.kie.efesto.runtimemanager.api.service.RuntimeManager;
import org.kie.efesto.runtimemanager.core.model.EfestoRuntimeContextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings({"unchecked", "rawtypes"})
public class DMNKiePMMLTrustyInvocationEvaluator extends AbstractDMNKiePMMLInvocationEvaluator {

    private static final RuntimeManager runtimeManager =
            org.kie.efesto.runtimemanager.api.utils.SPIUtils.getRuntimeManager(false).orElseThrow(() -> new EfestoRuntimeManagerException("Failed to find an instance of RuntimeManager: please check classpath and dependencies"));

    private static final Logger LOG = LoggerFactory.getLogger(DMNKiePMMLTrustyInvocationEvaluator.class);

    private static final String CHECK_CLASSPATH = "check classpath and dependencies!";

    static final String PMML_FILE_NAME = "_pmml_file_name_";
    static final String PMML_MODEL_NAME = "_model_name_";
    static final String RESULT_OBJECT_NAME = "_result_object_name_";
    static final String RESULT_CODE = "_result_code_";

    public DMNKiePMMLTrustyInvocationEvaluator(String dmnNS, DMNElement node, ModelLocalUriId pmmlModelLocalUriID, String model,
                                               PMMLInfo<?> pmmlInfo) {
        super(dmnNS, node, pmmlModelLocalUriID, model, pmmlInfo);
    }

    @Override
    protected Map<String, Object> getPMMLResult(DMNRuntimeEventManager eventManager, DMNResult dmnr) {
        return evaluate(model, pmmlModelLocalUriID, dmnr, eventManager.getRuntime().getRootClassLoader());
    }

    @Override
    protected Map<String, Object> getOutputFieldValues(Map<String, Object> resultVariables,
                                                       DMNResult dmnResult) {
        Map<String, Object> toReturn = new HashMap<>();
        for (Map.Entry<String, Object> kv : resultVariables.entrySet()) {
            String resultName = kv.getKey();
            if (resultName == null || resultName.isEmpty()) {
                continue;
            }
            Object r = kv.getValue();
            populateWithObject(toReturn, kv.getKey(), r, dmnResult);
        }
        return toReturn;
    }

    @Override
    protected Map<String, Object> getPredictedValues(Map<String, Object> resultVariables, DMNResult dmnr) {
        Map<String, Object> toReturn = new HashMap<>();
        String resultName = (String) resultVariables.get(RESULT_OBJECT_NAME);
        Object value = resultVariables.get(resultName);
        toReturn.put(resultName, NumberEvalHelper.coerceNumber(value));
        return toReturn;
    }

    private void populateWithObject(Map<String, Object> toPopulate, String resultName, Object r, DMNResult dmnResult) {
        Optional<String> outputFieldNameFromInfo = getOutputFieldNameFromInfo(resultName);
        if (outputFieldNameFromInfo.isPresent()) {
            String name = outputFieldNameFromInfo.get();
            try {
                toPopulate.put(name, NumberEvalHelper.coerceNumber(r));
            } catch (Exception e) {
                MsgUtil.reportMessage(LOG,
                                      DMNMessage.Severity.WARN,
                                      node,
                                      ((DMNResultImpl) dmnResult),
                                      e,
                                      null,
                                      Msg.INVALID_NAME,
                                      name,
                                      e.getMessage());
                toPopulate.put(name, null);
            }
        }
    }

    protected Map<String, Object> evaluate(String modelName, ModelLocalUriId pmmlModelLocalUriID, DMNResult dmnr,
                                   ClassLoader parentClassloader) {
        EfestoCompilationContext efestoCompilationContext = ContextStorage.getEfestoCompilationContext(pmmlModelLocalUriID);

        EfestoLocalRuntimeContext runtimeContext = getEfestoRuntimeContext(parentClassloader, efestoCompilationContext.getGeneratedResourcesMap());
        String pmmlFileName = ((LocalUri.LocalUriPathComponent)pmmlModelLocalUriID.asLocalUri().parent()).getComponent() + ".pmml";

        Map<String, Object> pmmlRequestData = getPMMLRequestData(UUID.randomUUID().toString(), modelName, pmmlFileName,
                                                             dmnr);
        EfestoInput<Map<String, Object>> inputPMML = new BaseEfestoInput<>(pmmlModelLocalUriID, pmmlRequestData);
        Collection<EfestoOutput> retrieved = evaluateInput(inputPMML, runtimeContext);
        if (retrieved.isEmpty()) {
            String errorMessage = String.format("Failed to get result for %s: please %s",
                                                inputPMML.getModelLocalUriId(),
                                                CHECK_CLASSPATH);
            LOG.error(errorMessage);
            throw new KieRuntimeServiceException(errorMessage);
        }
        return (Map) retrieved.iterator().next().getOutputData();
    }

    private Collection<EfestoOutput> evaluateInput(EfestoInput<Map<String, Object>> inputPMML,
                                                   EfestoLocalRuntimeContext runtimeContext) {
        try {
            return runtimeManager.evaluateInput(runtimeContext, inputPMML);
        } catch (Exception t) {
            String errorMessage = String.format("Evaluation error for %s using %s due to %s: please %s",
                                                inputPMML.getModelLocalUriId(),
                                                inputPMML,
                                                t.getMessage(),
                                                CHECK_CLASSPATH);
            LOG.error(errorMessage);
            throw new KieRuntimeServiceException(errorMessage, t);
        }
    }

    protected Map<String, Object> getPMMLRequestData(String correlationId, String modelName, String fileName,
                                               DMNResult dmnr) {
        Map<String, Object> toReturn = new HashMap<>();
        for (DMNFunctionDefinitionEvaluator.FormalParameter p : parameters) {
            Object pValue = getValueForPMMLInput(dmnr, p.name);
            toReturn.put(p.name, pValue);
        }
        toReturn.put(PMML_FILE_NAME, fileName);
        toReturn.put(PMML_MODEL_NAME, modelName);
        return toReturn;
    }

    private EfestoLocalRuntimeContext getEfestoRuntimeContext(final ClassLoader parentClassloader, final Map<String, GeneratedResources> generatedResourcesMap) {
        return EfestoRuntimeContextUtils.buildWithParentClassLoader(parentClassloader, generatedResourcesMap);
    }

}
