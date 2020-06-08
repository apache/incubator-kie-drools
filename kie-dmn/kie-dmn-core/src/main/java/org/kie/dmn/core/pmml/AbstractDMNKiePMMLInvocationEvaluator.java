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
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import org.kie.api.io.Resource;
import org.kie.api.pmml.PMML4Field;
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
import org.kie.dmn.feel.util.EvalHelper;
import org.kie.dmn.model.api.DMNElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract <code>DMNKiePMMLInvocationEvaluator</code> to delegate actual <code>PMML4Result</code> retrieval to specific
 * kie-pmml implementation (legacy or new)
 */
public abstract class AbstractDMNKiePMMLInvocationEvaluator extends AbstractPMMLInvocationEvaluator {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractDMNKiePMMLInvocationEvaluator.class);
    private final PMMLInfo<?> pmmlInfo;

    public AbstractDMNKiePMMLInvocationEvaluator(String dmnNS, DMNElement node, Resource pmmlResource, String model, PMMLInfo<?> pmmlInfo) {
        super(dmnNS, node, pmmlResource, model);
        this.pmmlInfo = pmmlInfo;
    }

    @Override
    public EvaluatorResult evaluate(DMNRuntimeEventManager eventManager, DMNResult dmnr) {
        PMML4Result resultHolder = getPMML4Result(dmnr);

        Map<String, Object> resultVariables = resultHolder.getResultVariables();

        Map<String, Object> result = new HashMap<>();
        for (Entry<String, Object> kv : resultVariables.entrySet()) {
            Object r = kv.getValue();
            if (r instanceof PMML4Field) {
                final String resultName = kv.getKey();
                if (resultName != null && !resultName.isEmpty()) {
                    Optional<String> outputFieldNameFromInfo;
                    Optional<DMNType> opt = getCompositeOutput();
                    if (opt.isPresent()) {
                        CompositeTypeImpl type = (CompositeTypeImpl) opt.get();
                        outputFieldNameFromInfo = type.getFields()
                                .keySet()
                                .stream()
                                .filter(k -> k.equalsIgnoreCase(resultName))
                                .findFirst();
                    } else {
                        outputFieldNameFromInfo = pmmlInfo.getModels()
                                .stream()
                                .filter(m -> model.equals(m.getName()))
                                .flatMap(m -> m.getOutputFieldNames().stream())
                                .filter(ofn -> ofn.equalsIgnoreCase(resultName))
                                .findFirst();
                    }
                    if (outputFieldNameFromInfo.isPresent()) {
                        String name = outputFieldNameFromInfo.get();
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
        }

        Object coercedResult = result.size() > 1 ? result : result.values().iterator().next();
        return new EvaluatorResultImpl(coercedResult, ResultType.SUCCESS);
    }

    protected abstract PMML4Result getPMML4Result(DMNResult dmnr);

    private Optional<DMNType> getCompositeOutput() {
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
