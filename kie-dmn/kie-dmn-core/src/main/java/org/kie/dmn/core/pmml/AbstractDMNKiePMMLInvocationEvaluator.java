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
import java.util.Map;
import java.util.Optional;

import org.kie.api.io.Resource;
import org.kie.api.pmml.PMML4Result;
import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.DMNType;
import org.kie.dmn.api.core.event.DMNRuntimeEventManager;
import org.kie.dmn.core.api.EvaluatorResult;
import org.kie.dmn.core.api.EvaluatorResult.ResultType;
import org.kie.dmn.core.ast.EvaluatorResultImpl;
import org.kie.dmn.core.impl.CompositeTypeImpl;
import org.kie.dmn.core.impl.DMNResultImpl;
import org.kie.dmn.core.util.Msg;
import org.kie.dmn.core.util.MsgUtil;
import org.kie.dmn.model.api.DMNElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract <code>DMNKiePMMLInvocationEvaluator</code> to delegate actual <code>PMML4Result</code> retrieval to specific
 * kie-pmml implementation (legacy or new)
 */
public abstract class AbstractDMNKiePMMLInvocationEvaluator extends AbstractPMMLInvocationEvaluator {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractDMNKiePMMLInvocationEvaluator.class);
    protected final PMMLInfo<?> pmmlInfo;

    public AbstractDMNKiePMMLInvocationEvaluator(String dmnNS, DMNElement node, Resource pmmlResource, String model, PMMLInfo<?> pmmlInfo) {
        super(dmnNS, node, pmmlResource, model);
        this.pmmlInfo = pmmlInfo;
    }

    @Override
    public EvaluatorResult evaluate(DMNRuntimeEventManager eventManager, DMNResult dmnr) {
        PMML4Result resultHolder = getPMML4Result(eventManager, dmnr);

        Map<String, Object> resultVariables = resultHolder.getResultVariables();
        Map<String, Object> result = getOutputFieldValues(resultHolder, resultVariables, dmnr);
        if (result.isEmpty()) {
            result = getPredictedValues(resultHolder, dmnr);
        }
        if (result.isEmpty()) {
            MsgUtil.reportMessage(LOG,
                                  DMNMessage.Severity.ERROR,
                                  node,
                                  ((DMNResultImpl) dmnr),
                                  null,
                                  null,
                                  Msg.UNABLE_TO_RETRIEVE_PMML_RESULT,
                                  model);
            return new EvaluatorResultImpl(null, ResultType.FAILURE);
        } else {
            Object coercedResult = result.size() > 1 ? result : result.values().iterator().next();
            return new EvaluatorResultImpl(coercedResult, ResultType.SUCCESS);
        }
    }

    /**
     * Returns the <code>PMML4Result</code>
     * @param eventManager
     * @param dmnr
     * @return
     */
    protected abstract PMML4Result getPMML4Result(DMNRuntimeEventManager eventManager, DMNResult dmnr);

    /**
     * Returns a <code>Map&lt;String, Object&gt;</code> of values identified by <b>Output</b> definition
     * @param pmml4Result
     * @param resultVariables
     * @param dmnr
     * @return
     */
    protected abstract Map<String, Object> getOutputFieldValues(PMML4Result pmml4Result, Map<String, Object> resultVariables, DMNResult dmnr);

    /**
     * Returns a <code>Map&lt;String, Object&gt;</code> of predicted values identified by <b>MiningSchema/Targets</b> definitions
     * @param pmml4Result
     * @param dmnr
     * @return
     */
    protected abstract Map<String, Object> getPredictedValues(PMML4Result pmml4Result, DMNResult dmnr);

    protected Optional<String> getOutputFieldNameFromInfo(String resultName) {
        Optional<String> toReturn;
        Optional<DMNType> opt = getCompositeOutput();
        if (opt.isPresent()) {
            CompositeTypeImpl type = (CompositeTypeImpl) opt.get();
            toReturn = type.getFields()
                    .keySet()
                    .stream()
                    .filter(k -> k.equalsIgnoreCase(resultName))
                    .findFirst();
        } else {
            toReturn = pmmlInfo.getModels()
                    .stream()
                    .filter(m -> model.equals(m.getName()))
                    .flatMap(m -> m.getOutputFieldNames().stream())
                    .filter(ofn -> ofn.equalsIgnoreCase(resultName))
                    .findFirst();
        }
        return toReturn;
    }

    protected Optional<DMNType> getCompositeOutput() {
        Collection<? extends PMMLModelInfo> models = pmmlInfo.getModels();
        return models.stream()
                .filter(m -> model.equals(m.getName()))
                .filter(m -> m instanceof DMNPMMLModelInfo)
                .flatMap(m -> ((DMNPMMLModelInfo) m).getOutputFields().entrySet().stream())
                .filter(e -> e.getKey().equals(model))
                .filter(e -> e.getValue() instanceof CompositeTypeImpl)
                .map(Map.Entry::getValue)
                .findFirst();
    }
}
