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
package org.kie.dmn.core.ast;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.DMNType;
import org.kie.dmn.api.core.ast.DecisionServiceNode;
import org.kie.dmn.api.core.event.DMNRuntimeEventManager;
import org.kie.dmn.core.api.DMNExpressionEvaluator;
import org.kie.dmn.core.api.EvaluatorResult;
import org.kie.dmn.core.api.EvaluatorResult.ResultType;
import org.kie.dmn.core.ast.DMNFunctionDefinitionEvaluator.FormalParameter;
import org.kie.dmn.core.impl.DMNResultImpl;
import org.kie.dmn.core.impl.DMNRuntimeImpl;
import org.kie.dmn.core.util.Msg;
import org.kie.dmn.core.util.MsgUtil;
import org.kie.dmn.feel.lang.EvaluationContext;
import org.kie.dmn.feel.runtime.functions.BaseFEELFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DMNDecisionServiceFunctionDefinitionEvaluator implements DMNExpressionEvaluator {

    private static final Logger LOG = LoggerFactory.getLogger(DMNDecisionServiceFunctionDefinitionEvaluator.class);
    private DecisionServiceNode dsNode;
    private List<DSFormalParameter> parameters;
    private boolean coerceSingletonResult;

    public DMNDecisionServiceFunctionDefinitionEvaluator(DecisionServiceNode dsNode, List<DSFormalParameter> parameters, boolean coerceSingletonResult) {
        this.dsNode = dsNode;
        this.parameters = parameters;
        this.coerceSingletonResult = coerceSingletonResult;
    }

    @Override
    public EvaluatorResult evaluate(DMNRuntimeEventManager eventManager, DMNResult dmnr) {
        DMNResultImpl result = (DMNResultImpl) dmnr;
        DMNDSFunction function = new DMNDSFunction(dsNode.getName(), parameters, new DMNDecisionServiceEvaluator(dsNode, false, coerceSingletonResult), eventManager, result);
        return new EvaluatorResultImpl(function, ResultType.SUCCESS);
    }

    public static class DSFormalParameter extends FormalParameter {

        private final String importName;
        private final String elementName;

        public DSFormalParameter(String importName, String elementName, DMNType type) {
            super(importName != null ? importName + "." + elementName : elementName, type);
            this.importName = importName;
            this.elementName = elementName;
        }

        public String getImportName() {
            return importName;
        }

        public String getElementName() {
            return elementName;
        }
    }

    public static class DMNDSFunction extends BaseFEELFunction {

        private final List<DSFormalParameter> parameters;
        private final DMNExpressionEvaluator evaluator;
        private final DMNRuntimeEventManager eventManager;
        private final DMNResultImpl resultContext;
        private final boolean typeCheck;

        public DMNDSFunction(String name, List<DSFormalParameter> parameters, DMNExpressionEvaluator evaluator, DMNRuntimeEventManager eventManager, DMNResultImpl result) {
            super(name);
            this.parameters = parameters;
            this.evaluator = evaluator;
            this.eventManager = eventManager;
            this.resultContext = result;
            typeCheck = ((DMNRuntimeImpl) eventManager.getRuntime()).performRuntimeTypeCheck(result.getModel());
        }

        public Object invoke(EvaluationContext ctx, Object[] params) {
            DMNContext previousContext = resultContext.getContext();

            DMNContext dmnContext = eventManager.getRuntime().newContext();
            previousContext.getMetadata().asMap().forEach(dmnContext.getMetadata()::set);
            try {
                if (params.length != parameters.size()) {
                    MsgUtil.reportMessage(LOG,
                                          DMNMessage.Severity.ERROR,
                                          null,
                                          resultContext,
                                          null,
                                          null,
                                          Msg.PARAMETER_COUNT_MISMATCH_DS,
                                          getName());
                    return null;
                }
                for (int i = 0; i < params.length; i++) {
                    DSFormalParameter formalParameter = parameters.get(i);
                    if (formalParameter.getImportName() == null) {
                        dmnContext.set(formalParameter.name, performTypeCheckIfNeeded(params[i], i));
                    } else {
                        Map<String, Object> importNameCtx;
                        if (dmnContext.isDefined(formalParameter.getImportName())) {
                            importNameCtx = (Map) dmnContext.get(formalParameter.getImportName());
                        } else {
                            importNameCtx = new HashMap<>();
                            dmnContext.set(formalParameter.getImportName(), importNameCtx);
                        }
                        importNameCtx.put(formalParameter.getElementName(), performTypeCheckIfNeeded(params[i], i));
                    }
                }
                resultContext.setContext(dmnContext);
                EvaluatorResult result = evaluator.evaluate(eventManager, resultContext);
                if (result.getResultType() == ResultType.SUCCESS) {
                    return result.getResult();
                }
                return null;
            } catch (Exception e) {
                MsgUtil.reportMessage(LOG,
                                      DMNMessage.Severity.ERROR,
                                      null,
                                      resultContext,
                                      e,
                                      null,
                                      Msg.ERR_INVOKING_FUNCTION_ON_NODE,
                                      getName(),
                                      getName());
                return null;
            } finally {
                resultContext.setContext(previousContext);
            }
        }

        private Object performTypeCheckIfNeeded(Object param, int paramIndex) {
            DSFormalParameter dsFormalParameter = parameters.get(paramIndex);
            Object result = DMNRuntimeImpl.coerceUsingType(param,
                                                           dsFormalParameter.type,
                                                           typeCheck,
                                                           (rx, tx) -> MsgUtil.reportMessage(LOG,
                                                                                             DMNMessage.Severity.WARN,
                                                                                             null,
                                                                                             resultContext,
                                                                                             null,
                                                                                             null,
                                                                                             Msg.PARAMETER_TYPE_MISMATCH_DS,
                                                                                             dsFormalParameter.name,
                                                                                             tx,
                                                                                             MsgUtil.clipString(rx.toString(), 50)));
            return result;
        }

        @Override
        protected boolean isCustomFunction() {
            return true;
        }

        public List<List<Param>> getParameters() {
            return Collections.singletonList(parameters.stream().map(FormalParameter::asFEELParam).collect(Collectors.toList()));
        }

        public List<List<DMNType>> getParameterTypes() {
            return Collections.singletonList(parameters.stream().map(p -> p.type).collect(Collectors.toList()));
        }

        public String toString() {
            return "function " + getName() + "( " + parameters.stream().map(p -> p.name).collect(Collectors.joining(", ")) + " )";
        }
    }
}
