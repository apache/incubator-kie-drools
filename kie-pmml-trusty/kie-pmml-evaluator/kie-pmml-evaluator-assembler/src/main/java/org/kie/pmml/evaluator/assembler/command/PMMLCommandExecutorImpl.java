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

import org.kie.api.KieBase;
import org.kie.api.pmml.PMML4Result;
import org.kie.api.pmml.PMMLRequestData;
import org.kie.api.pmml.ParameterInfo;
import org.kie.api.runtime.Context;
import org.kie.internal.command.RegistryContext;
import org.kie.internal.pmml.PMMLCommandExecutor;
import org.kie.pmml.api.PMMLRuntimeFactory;
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.api.runtime.PMMLContext;
import org.kie.pmml.api.runtime.PMMLRuntime;
import org.kie.pmml.api.utils.ConverterTypeUtil;
import org.kie.pmml.evaluator.assembler.factories.PMMLRuntimeFactoryImpl;
import org.kie.pmml.evaluator.core.PMMLContextImpl;

public class PMMLCommandExecutorImpl implements PMMLCommandExecutor {

    private static final PMMLRuntimeFactory PMML_RUNTIME_FACTORY = new PMMLRuntimeFactoryImpl();

    /**
     * Evaluate the given <code>PMMLRequestData<code>
     * @param pmmlRequestData : it must contain the pmml file name (in the <i>source</i> property)
     * @param context and the model name
     * @return
     */
    @Override
    public PMML4Result execute(final PMMLRequestData pmmlRequestData, final Context context) {
        final PMMLRequestData cleanedRequestData = getCleanedRequestData(pmmlRequestData);
        validate(cleanedRequestData);
        final String pmmlFileName = cleanedRequestData.getSource();
        final String pmmlModelName = cleanedRequestData.getModelName();
        final PMMLRuntime pmmlRuntime = getPMMLRuntime(pmmlFileName, pmmlModelName, ((RegistryContext) context).lookup(KieBase.class));
        return evaluate(cleanedRequestData, pmmlRuntime);
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

    /**
     * Return a <b>new</b> <code>PMMLRequestData</code> with the values of the original <code>PMMLRequestData</code> restored to their actual type.
     *
     * Needed because <code>JSONMarshallerPMMLParamInfo</code> convert all of them to <code>String</code>
     *
     * @see <a href="https://github.com/kiegroup/droolsjbpm-integration/blob/master/kie-server-parent/kie-server-api/src/main/java/org/kie/server/api/marshalling/json/JSONMarshallerPMMLParamInfo.java#L67">JSONMarshallerPMMLParamInfo.PMMLParamSerializer.serialize(ParameterInfo, JsonGenerator, SerializerProvider)</a>
     * @param source
     * @return
     */
    @SuppressWarnings("rawtype")
    protected PMMLRequestData getCleanedRequestData(PMMLRequestData source) {
        final PMMLRequestData toReturn = new PMMLRequestData();
        toReturn.setSource(source.getSource());
        toReturn.setCorrelationId(source.getCorrelationId());
        toReturn.setModelName(source.getModelName());
        source.getRequestParams().forEach(parameterInfo -> {
            Object value = ConverterTypeUtil.convert(parameterInfo.getType(), parameterInfo.getValue());
            ParameterInfo<?> toAdd = new ParameterInfo(parameterInfo.getCorrelationId(), parameterInfo.getName(),
                                                       parameterInfo.getType(), value);
            toReturn.addRequestParam(toAdd);
        });
        return toReturn;
    }

    private PMML4Result evaluate(final PMMLRequestData pmmlRequestData, final PMMLRuntime pmmlRuntime) {
        String modelName = pmmlRequestData.getModelName();
        final PMMLContext pmmlContext = new PMMLContextImpl(pmmlRequestData);
        return pmmlRuntime.evaluate(modelName, pmmlContext);
    }

    private PMMLRuntime getPMMLRuntime(String pmmlFileName, String pmmlModelName, KieBase kieBase) {
        return PMML_RUNTIME_FACTORY.getPMMLRuntimeFromFileNameModelNameAndKieBase(pmmlFileName, pmmlModelName, kieBase);
    }
}
