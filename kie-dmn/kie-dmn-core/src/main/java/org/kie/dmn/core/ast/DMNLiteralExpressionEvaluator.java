/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.dmn.core.ast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.event.DMNRuntimeEventManager;
import org.kie.dmn.api.feel.runtime.events.FEELEvent;
import org.kie.dmn.api.feel.runtime.events.FEELEvent.Severity;
import org.kie.dmn.api.feel.runtime.events.FEELEventListener;
import org.kie.dmn.core.api.DMNExpressionEvaluator;
import org.kie.dmn.core.api.EvaluatorResult;
import org.kie.dmn.core.api.EvaluatorResult.ResultType;
import org.kie.dmn.core.compiler.DMNProfile;
import org.kie.dmn.core.impl.DMNResultImpl;
import org.kie.dmn.core.impl.DMNRuntimeImpl;
import org.kie.dmn.core.util.Msg;
import org.kie.dmn.core.util.MsgUtil;
import org.kie.dmn.feel.FEEL;
import org.kie.dmn.feel.codegen.feel11.ProcessedExpression;
import org.kie.dmn.feel.lang.CompiledExpression;
import org.kie.dmn.feel.lang.impl.CompiledExpressionImpl;
import org.kie.dmn.feel.lang.impl.EvaluationContextImpl;
import org.kie.dmn.feel.lang.impl.FEELImpl;
import org.kie.dmn.model.api.LiteralExpression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An evaluator for DMN Literal Expressions
 */
public class DMNLiteralExpressionEvaluator
        implements DMNExpressionEvaluator {

    private static final Logger LOG = LoggerFactory.getLogger(DMNLiteralExpressionEvaluator.class);

    private LiteralExpression expressionNode;
    private CompiledExpression expression;
    private boolean isFunctionDef;

    public DMNLiteralExpressionEvaluator(CompiledExpression expression, LiteralExpression expressionNode) {
        this.expressionNode = expressionNode;
        this.expression = expression;
        if (expression instanceof CompiledExpressionImpl) {
            this.isFunctionDef = ((CompiledExpressionImpl) expression).isFunctionDef();
        } else if (expression instanceof ProcessedExpression) {
            this.isFunctionDef = ((ProcessedExpression) expression).getInterpreted().isFunctionDef();
        } else {
            throw new IllegalArgumentException(
                    "Cannot create DMNLiteralExpressionEvaluator: unsupported type " + expression.getClass());
        }
    }

    public boolean isFunctionDefinition() {
        return isFunctionDef;
    }

    public CompiledExpression getExpression() {
        return this.expression;
    }

    @Override
    public EvaluatorResult evaluate(DMNRuntimeEventManager dmrem, DMNResult dmnr) {
        DMNResultImpl result = (DMNResultImpl) dmnr;
        // in case an exception is thrown, the parent node will report it
        List<DMNProfile> profiles = ((DMNRuntimeImpl) dmrem.getRuntime()).getProfiles();
        @SuppressWarnings({"unchecked", "rawtypes"})
        FEELImpl feelInstance = (FEELImpl) FEEL.newInstance(dmrem.getRuntime().getRootClassLoader(), (List) profiles);
        LiteralInvocationListener liListener = new LiteralInvocationListener();
        @SuppressWarnings({"unchecked", "rawtypes"})
        EvaluationContextImpl ectx = feelInstance.newEvaluationContext(Arrays.asList(liListener), result.getContext().getAll());
        ectx.setDMNRuntime(dmrem.getRuntime());
        Object val = feelInstance.evaluate(expression, ectx);
        ResultType resultType = ResultType.SUCCESS;
        for (FEELEvent captured : liListener.events) {
            MsgUtil.reportMessage(LOG,
                                  dmnSeverityFromFEELSeverity(captured.getSeverity()),
                                  expressionNode,
                                  result,
                                  null,
                                  captured,
                                  Msg.FEEL_EVENT_EVAL_LITERAL_EXPRESSION,
                                  captured.getSeverity().toString(),
                                  MsgUtil.clipString(expressionNode.getText(), 50),
                                  captured.getMessage());
            if (captured.getSeverity() == Severity.ERROR) { // as FEEL events are being cycled, compute it here.
                resultType = ResultType.FAILURE;
            }
        }
        return new EvaluatorResultImpl(val, resultType);
    }

    private static DMNMessage.Severity dmnSeverityFromFEELSeverity(FEELEvent.Severity severity) {
        switch (severity) {
            case ERROR:
                return DMNMessage.Severity.ERROR;
            case TRACE:
                return DMNMessage.Severity.TRACE;
            case WARN:
                return DMNMessage.Severity.WARN;
            case INFO:
            default:
                return DMNMessage.Severity.INFO;
        }
    }

    static class LiteralInvocationListener implements FEELEventListener {

        public final List<FEELEvent> events = new ArrayList<>();

        @Override
        public void onEvent(FEELEvent event) {
            events.add(event);
        }
    }
}
