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
package org.kie.dmn.core.pmml;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.kie.api.io.Resource;
import org.kie.api.pmml.PMML4Result;
import org.kie.api.pmml.PMMLRequestData;
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
import org.kie.efesto.common.api.model.GeneratedResources;
import org.kie.efesto.compilationmanager.api.exceptions.EfestoCompilationManagerException;
import org.kie.efesto.compilationmanager.api.exceptions.KieCompilerServiceException;
import org.kie.efesto.compilationmanager.api.model.EfestoCompilationContext;
import org.kie.efesto.compilationmanager.api.model.EfestoInputStreamResource;
import org.kie.efesto.compilationmanager.api.service.CompilationManager;
import org.kie.efesto.compilationmanager.core.model.EfestoCompilationContextUtils;
import org.kie.efesto.runtimemanager.api.exceptions.EfestoRuntimeManagerException;
import org.kie.efesto.runtimemanager.api.exceptions.KieRuntimeServiceException;
import org.kie.efesto.runtimemanager.api.model.BaseEfestoInput;
import org.kie.efesto.runtimemanager.api.model.EfestoInput;
import org.kie.efesto.runtimemanager.api.model.EfestoOutput;
import org.kie.efesto.runtimemanager.api.model.EfestoRuntimeContext;
import org.kie.efesto.runtimemanager.api.service.RuntimeManager;
import org.kie.efesto.runtimemanager.core.model.EfestoRuntimeContextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.efesto.common.api.identifiers.LocalUri.SLASH;
import static org.kie.efesto.common.utils.PackageClassNameUtils.getSanitizedClassName;
import static org.kie.efesto.runtimemanager.api.utils.GeneratedResourceUtils.isPresentExecutableOrRedirect;


public class DMNKiePMMLTrustyInvocationEvaluator extends AbstractDMNKiePMMLInvocationEvaluator {

    private static final RuntimeManager runtimeManager =
            org.kie.efesto.runtimemanager.api.utils.SPIUtils.getRuntimeManager(false).orElseThrow(() -> new EfestoRuntimeManagerException("Failed to find an instance of RuntimeManager: please check classpath and dependencies"));

    private static final CompilationManager compilationManager =
            org.kie.efesto.compilationmanager.api.utils.SPIUtils.getCompilationManager(false).orElseThrow(() -> new EfestoCompilationManagerException("Failed to find an instance of CompilationManager: please check classpath and dependencies"));
    private static final Logger LOG = LoggerFactory.getLogger(DMNKiePMMLTrustyInvocationEvaluator.class);

    private static final String CHECK_CLASSPATH = "check classpath and dependencies!";

    public DMNKiePMMLTrustyInvocationEvaluator(String dmnNS, DMNElement node, Resource pmmlResource, String model,
                                               PMMLInfo<?> pmmlInfo) {
        super(dmnNS, node, pmmlResource, model, pmmlInfo);
    }

    @Override
    protected PMML4Result getPMML4Result(DMNRuntimeEventManager eventManager, DMNResult dmnr) {
        String pmmlFilePath = documentResource.getSourcePath();
        String pmmlFileName = pmmlFilePath.contains("/") ? pmmlFilePath.substring(pmmlFilePath.lastIndexOf('/') + 1)
                : pmmlFilePath;
        return evaluate(model, pmmlFileName, dmnr, eventManager.getRuntime().getRootClassLoader());
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
        toReturn.put(resultName, NumberEvalHelper.coerceNumber(value));
        return toReturn;
    }

    private void populateWithObject(Map<String, Object> toPopulate, String resultName, Object r, DMNResult dmnr) {
        Optional<String> outputFieldNameFromInfo = getOutputFieldNameFromInfo(resultName);
        if (outputFieldNameFromInfo.isPresent()) {
            String name = outputFieldNameFromInfo.get();
            try {
                toPopulate.put(name, NumberEvalHelper.coerceNumber(r));
            } catch (Exception e) {
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

    protected PMML4Result evaluate(String modelName, String pmmlFileName, DMNResult dmnr,
                                   ClassLoader parentClassloader) {
        EfestoRuntimeContext runtimeContext = getEfestoRuntimeContext(parentClassloader);
        ModelLocalUriId modelLocalUriId = getModelLocalUriId(pmmlFileName, modelName);

        Collection<EfestoOutput> retrieved;
        if (!(isPresentExecutableOrRedirect(modelLocalUriId, runtimeContext))) {
            LOG.warn("GeneratedResources for {}@{} are not present: trying to invoke compilation....",
                     pmmlFileName,
                     modelName);
            Map<String, GeneratedResources> generatedResourcesMap = compileFile(pmmlFileName,
                                                                                parentClassloader);
            runtimeContext.getGeneratedResourcesMap().putAll(generatedResourcesMap);
        }
        PMMLRequestData pmmlRequestData = getPMMLRequestData(UUID.randomUUID().toString(), modelName, pmmlFileName,
                                                             dmnr);
        EfestoInput<PMMLRequestData> inputPMML = new BaseEfestoInput<>(modelLocalUriId, pmmlRequestData);
        retrieved = evaluateInput(inputPMML, runtimeContext);
        if (retrieved.isEmpty()) {
            String errorMessage = String.format("Failed to get result for %s@%s: please %s",
                                                inputPMML.getModelLocalUriId(),
                                                inputPMML.getInputData().getModelName(),
                                                CHECK_CLASSPATH);
            LOG.error(errorMessage);
            throw new KieRuntimeServiceException(errorMessage);
        }
        return (PMML4Result) retrieved.iterator().next().getOutputData();
    }

    private Collection<EfestoOutput> evaluateInput(EfestoInput<PMMLRequestData> inputPMML,
                                                     EfestoRuntimeContext runtimeContext) {
        try {
            return runtimeManager.evaluateInput(runtimeContext, inputPMML);
        } catch (Exception t) {
            String errorMessage = String.format("Evaluation error for %s@%s using %s due to %s: please %s",
                                                inputPMML.getModelLocalUriId(),
                                                inputPMML.getInputData().getModelName(),
                                                inputPMML,
                                                t.getMessage(),
                                                CHECK_CLASSPATH);
            LOG.error(errorMessage);
            throw new KieRuntimeServiceException(errorMessage, t);
        }
    }

    protected Map<String, GeneratedResources> compileFile(String fileName,
                                                          ClassLoader parentClassLoader) {
        try {
            EfestoCompilationContext compilationContext =
                    EfestoCompilationContextUtils.buildWithParentClassLoader(parentClassLoader);
            EfestoInputStreamResource toProcess = new EfestoInputStreamResource(documentResource.getInputStream(),
                                                                                fileName);
            compilationManager.processResource(compilationContext, toProcess);
            return compilationContext.getGeneratedResourcesMap();
        } catch (Exception t) {
            String errorMessage = String.format("Compilation error for %s due to %s: please %s",
                                                fileName,
                                                t.getMessage(),
                                                CHECK_CLASSPATH);
            LOG.error(errorMessage);
            throw new KieCompilerServiceException(errorMessage, t);
        }
    }

    protected PMMLRequestData getPMMLRequestData(String correlationId, String modelName, String fileName,
                                               DMNResult dmnr) {
        PMMLRequestData toReturn = new PMMLRequestData(correlationId, modelName);
        for (DMNFunctionDefinitionEvaluator.FormalParameter p : parameters) {
            Object pValue = getValueForPMMLInput(dmnr, p.name);
            toReturn.addRequestParam(p.name, pValue);
        }
        toReturn.addRequestParam("_pmml_file_name_", fileName);
        return toReturn;
    }

    private EfestoRuntimeContext getEfestoRuntimeContext(final ClassLoader parentClassloader) {
        return EfestoRuntimeContextUtils.buildWithParentClassLoader(parentClassloader);
    }

    private ModelLocalUriId getModelLocalUriId(String fileName, String modelName) {
        String path = "/pmml/" + getFileNameNoSuffix(fileName) + SLASH + getSanitizedClassName(modelName);
        LocalUri parsed = LocalUri.parse(path);
        return new ModelLocalUriId(parsed);
    }

    private String getFileNameNoSuffix(String fileName) {
        return fileName.contains(".") ? fileName.substring(0, fileName.lastIndexOf('.')) : fileName;
    }
}
