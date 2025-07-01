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
package org.kie.dmn.feel.lang.ast;

import org.antlr.v4.runtime.ParserRuleContext;
import org.kie.dmn.api.feel.runtime.events.FEELEvent;
import org.kie.dmn.feel.lang.EvaluationContext;
import org.kie.dmn.feel.util.Msg;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Map;

public class DescendantExpressionNode extends BaseNode {

    private BaseNode expression;
    private BaseNode name;

    public DescendantExpressionNode(ParserRuleContext ctx, BaseNode expression, BaseNode name) {
        super( ctx );
        this.expression = expression;
        this.name = name;
    }

    public DescendantExpressionNode(BaseNode expression, BaseNode name, String text) {
        this.expression = expression;
        this.name = name;
        this.setText(text);
    }

    public BaseNode getExpression() {
        return expression;
    }

    public void setExpression(BaseNode expression) {
        this.expression = expression;
    }

    public BaseNode getName() {
        return name;
    }

    public void setName(BaseNode name) {
        this.name = name;
    }

    @Override
    public Object evaluate(EvaluationContext ctx) {
        if (expression == null || expression.getText() == null || expression.getText().isEmpty()) {
            ctx.notifyEvt(astEvent(FEELEvent.Severity.ERROR, Msg.createMessage(Msg.IS_NULL, "expression")));
            return null;
        }
        if (!(expression instanceof ContextNode)) {
            ctx.notifyEvt(astEvent(FEELEvent.Severity.ERROR, Msg.createMessage(Msg.ERROR_EVALUATING_DESCENDANT_EXPRESSION_NOT_CONTEXT, expression.getText())));
            return null;
        }
        if (name == null || name.getText() == null || name.getText().isEmpty()) {
            ctx.notifyEvt(astEvent(FEELEvent.Severity.ERROR, Msg.createMessage(Msg.IS_NULL, "name")));
            return null;
        }

        try {
            Object evaluatedExpression = this.expression.evaluate(ctx);
            List<Object> results = new ArrayList<>();
            Deque<Object> currentContextNestingLevel = new ArrayDeque<>();
            currentContextNestingLevel.push(evaluatedExpression);

            while (!currentContextNestingLevel.isEmpty()) {
                Object current = currentContextNestingLevel.pop();

                if (current instanceof Map<?, ?> map) {
                    for (Map.Entry<?, ?> entry : map.entrySet()) {
                        Object key = entry.getKey();
                        Object value = entry.getValue();

                        if (name.getText().equals(key)) {
                            results.add(value);
                        }

                        if (value instanceof Map<?, ?>) {
                            currentContextNestingLevel.push(value);
                        }
                    }
                }
            }

            return results;
        } catch ( Exception e ) {
            ctx.notifyEvt( astEvent(FEELEvent.Severity.ERROR, Msg.createMessage(Msg.ERROR_EVALUATING_DESCENDANT_EXPRESSION, expression.getText(), name.getText()), e) );
            return null;
        }
    }

    @Override
    public ASTNode[] getChildrenNode() {
        return new ASTNode[] { expression, name };
    }

    @Override
    public <T> T accept(Visitor<T> v) {
        return v.visit(this);
    }

}
