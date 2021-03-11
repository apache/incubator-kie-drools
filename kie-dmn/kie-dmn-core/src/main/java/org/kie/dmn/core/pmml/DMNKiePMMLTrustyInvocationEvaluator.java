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

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.kie.api.io.Resource;
import org.kie.api.pmml.PMML4Result;
import org.kie.api.runtime.KieRuntimeFactory;
import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.event.DMNRuntimeEventManager;
import org.kie.dmn.core.ast.DMNFunctionDefinitionEvaluator.FormalParameter;
import org.kie.dmn.core.impl.DMNResultImpl;
import org.kie.dmn.core.impl.DMNRuntimeImpl;
import org.kie.dmn.core.impl.DMNRuntimeKB;
import org.kie.dmn.core.impl.DMNRuntimeKBWrappingIKB;
import org.kie.dmn.core.util.Msg;
import org.kie.dmn.core.util.MsgUtil;
import org.kie.dmn.feel.util.EvalHelper;
import org.kie.dmn.model.api.DMNElement;
import org.kie.pmml.api.PMMLRuntimeFactory;
import org.kie.pmml.api.runtime.PMMLContext;
import org.kie.pmml.api.runtime.PMMLRuntime;
import org.kie.pmml.evaluator.assembler.factories.PMMLRuntimeFactoryImpl;
import org.kie.pmml.evaluator.core.PMMLContextImpl;
import org.kie.pmml.evaluator.core.utils.PMMLRequestDataBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DMNKiePMMLTrustyInvocationEvaluator extends AbstractDMNKiePMMLInvocationEvaluator {

    private static final Logger LOG = LoggerFactory.getLogger(DMNKiePMMLTrustyInvocationEvaluator.class);

    private static final PMMLRuntimeFactory PMML_RUNTIME_FACTORY = new PMMLRuntimeFactoryImpl();

    public DMNKiePMMLTrustyInvocationEvaluator(String dmnNS, DMNElement node, Resource pmmlResource, String model,
                                               PMMLInfo<?> pmmlInfo) {
        super(dmnNS, node, pmmlResource, model, pmmlInfo);
    }

    @Override
    protected PMML4Result getPMML4Result(DMNRuntimeEventManager eventManager, DMNResult dmnr) {
        PMMLContext pmmlContext = getPMMLPMMLContext(UUID.randomUUID().toString(), model, dmnr);
        PMMLRuntime pmmlRuntime = getPMMLRuntime(eventManager);
        return pmmlRuntime.evaluate(model, pmmlContext);
    }

    @Override
    protected Map<String, Object> getOutputFieldValues(PMML4Result pmml4Result, Map<String, Object> resultVariables,
                                                       DMNResult dmnr) {
        Map<String, Object> toReturn = new HashMap<>();
        for (Map.Entry<String, Object> kv : resultVariables.entrySet()) {
            String resultName = kv.getKey();
            if (resultName == null || resultName.isEmpty()) {
                continue;
            }
            Object r = kv.getValue();
            populateWithObject(toReturn, kv.getKey(), r, dmnr);
        }
        return toReturn;
    }

    @Override
    protected Map<String, Object> getPredictedValues(PMML4Result pmml4Result, DMNResult dmnr) {
        Map<String, Object> toReturn = new HashMap<>();
        String resultName = pmml4Result.getResultObjectName();
        Object value = pmml4Result.getResultVariables().get(resultName);
        toReturn.put(resultName, EvalHelper.coerceNumber(value));
        return toReturn;
    }

    private void populateWithObject(Map<String, Object> toPopulate, String resultName, Object r, DMNResult dmnr) {
        Optional<String> outputFieldNameFromInfo = getOutputFieldNameFromInfo(resultName);
        if (outputFieldNameFromInfo.isPresent()) {
            String name = outputFieldNameFromInfo.get();
            try {
                toPopulate.put(name, EvalHelper.coerceNumber(r));
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
    }

    /**
     * @param eventManager
     * @return
     */
    private PMMLRuntime getPMMLRuntime(DMNRuntimeEventManager eventManager) {
        DMNRuntimeImpl dmnRuntime = (DMNRuntimeImpl) eventManager.getRuntime();
        final DMNRuntimeKB runtimeKB = dmnRuntime.getRuntimeKB();
        if (runtimeKB instanceof DMNRuntimeKBWrappingIKB) { // We are in drools
            return getPMMLRuntimeFromDMNRuntimeKBWrappingIKB((DMNRuntimeKBWrappingIKB)runtimeKB);
        } else { // we are in kogito
            KieRuntimeFactory kieFactory = dmnRuntime.getKieRuntimeFactory(model);
            return kieFactory.get(PMMLRuntime.class);
        }
    }

    private PMMLRuntime getPMMLRuntimeFromDMNRuntimeKBWrappingIKB(DMNRuntimeKBWrappingIKB runtimeKB) {
        String pmmlFilePath = documentResource.getSourcePath();
        String pmmlFileName = pmmlFilePath.contains("/") ? pmmlFilePath.substring(pmmlFilePath.lastIndexOf('/')+1) : pmmlFilePath;
        PMMLRuntime toReturn = PMML_RUNTIME_FACTORY.getPMMLRuntimeFromFileNameModelNameAndKieBase(pmmlFileName,
                                                                                                  model,
                                                                                                  runtimeKB.getInternalKnowledgeBase());
        if (!toReturn.getPMMLModel(model).isPresent()) {
            File pmmlFile = getPMMLFile();
            toReturn = PMML_RUNTIME_FACTORY.getPMMLRuntimeFromFile(pmmlFile);
        }
        return toReturn;
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

    private File getPMMLFile() {
        try (InputStream inputStream = documentResource.getInputStream()) {
            return getPMMLFile(model, inputStream);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Load a <code>File</code> with the given <b>fullFileName</b> from the given
     * <code>InputStream</code>
     * @param fileName <b>full path</b> of file to load
     * @param inputStream
     * @return
     */
    private File getPMMLFile(String fileName, InputStream inputStream) {
        FileOutputStream outputStream = null;
        try {
            File toReturn = File.createTempFile(fileName, null);
            outputStream = new FileOutputStream(toReturn);
            byte[] byteArray = new byte[1024];
            int i;
            while ((i = inputStream.read(byteArray)) > 0) {
                outputStream.write(byteArray, 0, i);
            }
            return toReturn;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (Exception e) {
                LOG.warn("Failed to close outputStream", e);
            }
        }
    }
}
