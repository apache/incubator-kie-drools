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
import java.nio.file.Files;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.kie.api.io.Resource;
import org.kie.api.pmml.PMML4Result;
import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.event.DMNRuntimeEventManager;
import org.kie.dmn.core.ast.DMNFunctionDefinitionEvaluator;
import org.kie.dmn.core.impl.DMNResultImpl;
import org.kie.dmn.core.util.Msg;
import org.kie.dmn.core.util.MsgUtil;
import org.kie.dmn.feel.util.EvalHelper;
import org.kie.dmn.model.api.DMNElement;
import org.kie.efesto.common.api.io.IndexFile;
import org.kie.efesto.common.api.model.FRI;
import org.kie.efesto.compilationmanager.api.exceptions.EfestoCompilationManagerException;
import org.kie.efesto.compilationmanager.api.exceptions.KieCompilerServiceException;
import org.kie.efesto.compilationmanager.api.model.EfestoFileResource;
import org.kie.efesto.compilationmanager.api.service.CompilationManager;
import org.kie.efesto.runtimemanager.api.exceptions.EfestoRuntimeManagerException;
import org.kie.efesto.runtimemanager.api.exceptions.KieRuntimeServiceException;
import org.kie.efesto.runtimemanager.api.model.EfestoOutput;
import org.kie.efesto.runtimemanager.api.service.RuntimeManager;
import org.kie.memorycompiler.KieMemoryCompiler;
import org.kie.pmml.api.runtime.PMMLContext;
import org.kie.pmml.evaluator.core.PMMLContextImpl;
import org.kie.pmml.evaluator.core.model.EfestoInputPMML;
import org.kie.pmml.evaluator.core.utils.PMMLRequestDataBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.efesto.common.api.model.FRI.SLASH;
import static org.kie.pmml.commons.Constants.PMML_STRING;
import static org.kie.pmml.commons.Constants.PMML_SUFFIX;
import static org.kie.pmml.commons.utils.KiePMMLModelUtils.getSanitizedClassName;

public class DMNKiePMMLTrustyInvocationEvaluator extends AbstractDMNKiePMMLInvocationEvaluator {

    private static final RuntimeManager runtimeManager =
            org.kie.efesto.runtimemanager.api.utils.SPIUtils.getRuntimeManager(false).orElseThrow(() -> new EfestoRuntimeManagerException("Failed to find an instance of RuntimeManager: please check classpath and dependencies"));

    private static final CompilationManager compilationManager =
            org.kie.efesto.compilationmanager.api.utils.SPIUtils.getCompilationManager(false).orElseThrow(() -> new EfestoCompilationManagerException("Failed to find an instance of CompilationManager: please check classpath and dependencies"));
    private static final Logger LOG = LoggerFactory.getLogger(DMNKiePMMLTrustyInvocationEvaluator.class);

    public DMNKiePMMLTrustyInvocationEvaluator(String dmnNS, DMNElement node, Resource pmmlResource, String model,
                                               PMMLInfo<?> pmmlInfo) {
        super(dmnNS, node, pmmlResource, model, pmmlInfo);
    }

    @Override
    protected PMML4Result getPMML4Result(DMNRuntimeEventManager eventManager, DMNResult dmnr) {
        String pmmlFilePath = documentResource.getSourcePath();
        String pmmlFileName = pmmlFilePath.contains("/") ? pmmlFilePath.substring(pmmlFilePath.lastIndexOf('/') + 1)
                : pmmlFilePath;
        final KieMemoryCompiler.MemoryCompilerClassLoader memoryCompilerClassLoader =
                new KieMemoryCompiler.MemoryCompilerClassLoader(eventManager.getRuntime().getRootClassLoader());
        PMMLContext pmmlContext = getPMMLPMMLContext(UUID.randomUUID().toString(), pmmlFileName, model, dmnr,
                                                     memoryCompilerClassLoader);
        return evaluate(model, pmmlContext);
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

    protected PMML4Result evaluate(String modelName, PMMLContext context) {
        String basePath = context.getFileNameNoSuffix() + SLASH + getSanitizedClassName(modelName);
        FRI fri = new FRI(basePath, PMML_STRING);
        EfestoInputPMML darInputPMML = new EfestoInputPMML(fri, context);
        Collection<EfestoOutput> retrieved = evaluateInput(darInputPMML);
        if (retrieved.isEmpty()) {
            LOG.warn("Failed to get a result for {}@{}: trying to invoke compilation....", context.getFileName(),
                     context.getRequestData().getModelName());
            compileFile(context);
        }
        retrieved = evaluateInput(darInputPMML);
        if (retrieved.isEmpty()) {
            String errorMessage = String.format("Failed to get result for %s@%s: please" +
                                                        " check classpath and dependencies!",
                                                context.getFileName(),
                                                context.getRequestData().getModelName());
            LOG.error(errorMessage);
            throw new KieRuntimeServiceException(errorMessage);
        }
        return (PMML4Result) retrieved.iterator().next().getOutputData();
    }

    protected Collection<EfestoOutput> evaluateInput(EfestoInputPMML darInputPMML) {
        PMMLContext context = darInputPMML.getInputData();
        try {
            return runtimeManager.evaluateInput((KieMemoryCompiler.MemoryCompilerClassLoader) context.getMemoryClassLoader(), darInputPMML);
        } catch (Throwable t) {
            String errorMessage = String.format("Evaluation error for %s@%s using %s due to %s: please" +
                                                        " check classpath and dependencies!",
                                                context.getFileName(),
                                                context.getRequestData().getModelName(),
                                                darInputPMML,
                                                t.getMessage());
            LOG.error(errorMessage);
            throw new KieRuntimeServiceException(errorMessage, t);
        }
    }

    protected void compileFile(PMMLContext context) {
        Collection<IndexFile> retrievedIndexFiles;
        try {
            EfestoFileResource toProcess = new EfestoFileResource(getPMMLFile(context.getFileName()));
            retrievedIndexFiles = compilationManager.processResource((KieMemoryCompiler.MemoryCompilerClassLoader) context.getMemoryClassLoader(), toProcess);
        } catch (Throwable t) {
            String errorMessage = String.format("Compilation error for %s@%s due to %s: please" +
                                                        " check classpath and dependencies!",
                                                context.getFileName(),
                                                context.getRequestData().getModelName(),
                                                t.getMessage());
            LOG.error(errorMessage);
            throw new KieCompilerServiceException(errorMessage, t);
        }
        if (retrievedIndexFiles == null || retrievedIndexFiles.isEmpty() || retrievedIndexFiles.stream().noneMatch(indexFile -> indexFile.getModel().equals(PMML_STRING))) {
            String errorMessage = String.format("Failed to create index files for %s@%s: please" +
                                                        " check classpath and dependencies!",
                                                context.getFileName(),
                                                context.getRequestData().getModelName());
            LOG.error(errorMessage);
            throw new KieCompilerServiceException(errorMessage);
        }
    }

    private PMMLContext getPMMLPMMLContext(String correlationId, String fileName, String modelName, DMNResult dmnr,
                                           final KieMemoryCompiler.MemoryCompilerClassLoader memoryCompilerClassLoader) {
        PMMLRequestDataBuilder pmmlRequestDataBuilder = new PMMLRequestDataBuilder(correlationId, modelName);
        for (DMNFunctionDefinitionEvaluator.FormalParameter p : parameters) {
            Object pValue = getValueForPMMLInput(dmnr, p.name);
            Class class1 = pValue.getClass();
            pmmlRequestDataBuilder.addParameter(p.name, pValue, class1);
        }
        return new PMMLContextImpl(pmmlRequestDataBuilder.build(), fileName, memoryCompilerClassLoader);
    }

    private File getPMMLFile(String fileName) {
        try (InputStream inputStream = documentResource.getInputStream()) {
            return getPMMLFile(fileName, inputStream);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Load a <code>File</code> with the given <b>name</b> from the given
     * <code>InputStream</code>
     * @param fileName <b>name</b> of file to load
     * @param inputStream
     * @return
     */
    private File getPMMLFile(String fileName, InputStream inputStream) {
        FileOutputStream outputStream = null;
        String fileNameToUse = fileName.endsWith(PMML_SUFFIX) ? fileName.replace(PMML_SUFFIX, "") : fileName;
        try {
            File tmpFile = File.createTempFile(fileNameToUse, null);
            File toReturn = new File(tmpFile.getParentFile().getAbsolutePath() + File.separator + fileName);
            Files.move(tmpFile.toPath(), toReturn.toPath());

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
