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
package org.kie.pmml;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.IntStream;

import org.kie.api.pmml.PMML4Result;
import org.kie.api.pmml.PMMLRequestData;
import org.kie.efesto.runtimemanager.api.model.BaseEfestoInput;
import org.kie.efesto.runtimemanager.api.model.EfestoRuntimeContext;
import org.kie.efesto.runtimemanager.core.model.EfestoRuntimeContextUtils;
import org.kie.memorycompiler.KieMemoryCompiler;
import org.kie.pmml.api.enums.PMML_STEP;
import org.kie.pmml.api.models.PMMLStep;
import org.kie.pmml.api.runtime.PMMLListener;
import org.kie.pmml.api.runtime.PMMLRuntimeContext;
import org.kie.pmml.commons.model.KiePMMLModel;
import org.kie.pmml.evaluator.core.PMMLRuntimeContextImpl;
import org.kie.pmml.evaluator.core.implementations.PMMLRuntimeStep;
import org.kie.pmml.evaluator.core.model.EfestoInputPMML;
import org.kie.pmml.evaluator.core.model.EfestoOutputPMML;
import org.kie.pmml.evaluator.core.utils.PMMLRequestDataBuilder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.pmml.api.enums.ResultCode.OK;
import static org.kie.pmml.commons.Constants.PMML_FILE_NAME;
import static org.kie.pmml.commons.Constants.PMML_MODEL_NAME;

public class TestingHelper {

    private TestingHelper() {
    }

    public static PMMLRuntimeContext getPMMLContext(String fileName, String modelName, KieMemoryCompiler.MemoryCompilerClassLoader memoryCompilerClassLoader) {
        return getPMMLContext(fileName, modelName, Collections.emptySet(), memoryCompilerClassLoader);
    }

    public static  PMMLRuntimeContext getPMMLContext(String fileName, String modelName, Set<PMMLListener> listeners, KieMemoryCompiler.MemoryCompilerClassLoader memoryCompilerClassLoader) {
        return new PMMLRuntimeContextImpl(getPMMLRequestDataWithInputData(modelName, fileName), fileName, listeners, memoryCompilerClassLoader);
    }

    public static EfestoRuntimeContext getEfestoContext(ClassLoader parenClassLoader) {
        return EfestoRuntimeContextUtils.buildWithParentClassLoader(parenClassLoader);
    }

    public static PMMLRequestData getPMMLRequestData(String modelName, String fileName) {
        final PMMLRequestData toReturn = new PMMLRequestData();
        toReturn.setModelName(modelName);
        toReturn.setCorrelationId("CORRELATION_ID");
        IntStream.range(0, 3).forEach(i -> toReturn.addRequestParam("PARAM_" + i, i));
        toReturn.addRequestParam(PMML_FILE_NAME, fileName);
        return toReturn;
    }

    public static void commonEvaluateEfestoOutputPMML(EfestoOutputPMML toEvaluate, EfestoInputPMML inputPMML) {
        assertThat(toEvaluate).isNotNull();
        assertThat(toEvaluate.getModelLocalUriId()).isEqualTo(inputPMML.getModelLocalUriId());
        commonEvaluatePMML4Result(toEvaluate.getOutputData(), inputPMML.getInputData().getRequestData());
    }

    public static void commonEvaluateEfestoOutputPMML(EfestoOutputPMML toEvaluate, BaseEfestoInput<PMMLRequestData> inputPMML) {
        assertThat(toEvaluate).isNotNull();
        assertThat(toEvaluate.getModelLocalUriId()).isEqualTo(inputPMML.getModelLocalUriId());
        commonEvaluatePMML4Result(toEvaluate.getOutputData(), inputPMML.getInputData());
    }

    public static void commonEvaluatePMML4Result(PMML4Result toEvaluate, PMMLRequestData pmmlRequestData) {
        assertThat(toEvaluate).isNotNull();
        assertThat(toEvaluate.getResultCode()).isEqualTo(OK.getName());
        assertThat(toEvaluate.getCorrelationId()).isEqualTo(pmmlRequestData.getCorrelationId());
    }

    public static void commonValuateStep(final PMMLStep toVerify, final PMML_STEP pmmlStep, final KiePMMLModel kiePMMLModel,
                                         final PMMLRequestData requestData) {
        assertThat(toVerify).isNotNull();
        assertThat(toVerify).isInstanceOf(PMMLRuntimeStep.class);
        assertThat(((PMMLRuntimeStep) toVerify).getPmmlStep()).isEqualTo(pmmlStep);
        Map<String, Object> info = toVerify.getInfo();
        assertThat(kiePMMLModel.getName()).isEqualTo(info.get("MODEL"));
        assertThat(requestData.getCorrelationId()).isEqualTo(info.get("CORRELATION ID"));
        assertThat(requestData.getModelName()).isEqualTo(info.get("REQUEST MODEL"));
        requestData.getRequestParams()
                .forEach(requestParam ->
                                 assertThat(info.get(requestParam.getName())).isEqualTo(requestParam.getValue()));
    }

    public static PMMLRequestData getPMMLRequestDataWithInputData(String modelName, String fileName) {
        Map<String, Object> inputData = getInputData();
        String correlationId = "CORRELATION_ID";
        PMMLRequestDataBuilder pmmlRequestDataBuilder = new PMMLRequestDataBuilder(correlationId, modelName);
        for (Map.Entry<String, Object> entry : inputData.entrySet()) {
            Object pValue = entry.getValue();
            Class class1 = pValue.getClass();
            pmmlRequestDataBuilder.addParameter(entry.getKey(), pValue, class1);
        }
        pmmlRequestDataBuilder.addParameter(PMML_FILE_NAME, fileName, String.class);
        pmmlRequestDataBuilder.addParameter(PMML_MODEL_NAME, modelName, String.class);
        return pmmlRequestDataBuilder.build();
    }

    public static Map<String, Object> getInputData() {
        final Map<String, Object> toReturn = new HashMap<>();
        toReturn.put("fld1", 23.2);
        toReturn.put("fld2", 11.34);
        toReturn.put("fld3", "x");
        toReturn.put("fld4", 34.1);
        return toReturn;
    }

    public static Map<String, Object> getInputData(String modelName, String fileName) {
        final Map<String, Object> toReturn = getInputData();
        toReturn.put(PMML_FILE_NAME, fileName);
        toReturn.put(PMML_MODEL_NAME, modelName);
        return toReturn;
    }

}
