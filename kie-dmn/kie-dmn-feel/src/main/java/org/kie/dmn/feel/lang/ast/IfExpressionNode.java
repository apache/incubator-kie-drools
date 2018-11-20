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

package org.kie.dmn.feel.lang.ast;

import org.antlr.v4.runtime.ParserRuleContext;
import org.kie.dmn.api.feel.runtime.events.FEELEvent.Severity;
import org.kie.dmn.feel.lang.EvaluationContext;
import org.kie.dmn.feel.lang.Type;
import org.kie.dmn.feel.lang.types.BuiltInType;
import org.kie.dmn.feel.util.Msg;

public class IfExpressionNode
        extends BaseNode {

    private BaseNode condition;
    private BaseNode thenExpression;
    private BaseNode elseExpression;

    public IfExpressionNode(ParserRuleContext ctx, BaseNode condition, BaseNode thenExpression, BaseNode elseExpression) {
        super( ctx );
        this.condition = condition;
        this.thenExpression = thenExpression;
        this.elseExpression = elseExpression;
    }

    public BaseNode getCondition() {
        return condition;
    }

    public void setCondition(BaseNode condition) {
        this.condition = condition;
    }

    public BaseNode getThenExpression() {
        return thenExpression;
    }

    public void setThenExpression(BaseNode thenExpression) {
        this.thenExpression = thenExpression;
    }

    public BaseNode getElseExpression() {
        return elseExpression;
    }

    public void setElseExpression(BaseNode elseExpression) {
        this.elseExpression = elseExpression;
    }

    @Override
    public Object evaluate(EvaluationContext ctx) {
        // spec says: if FEEL(e1) is true then FEEL(e2) else FEEL(e3)
        Object cond = this.condition.evaluate( ctx );
        if ( cond != null && cond instanceof Boolean && cond == Boolean.TRUE ) {
            return this.thenExpression.evaluate( ctx );
        } else {
            return this.elseExpression.evaluate( ctx );
        }
    }

    @Override
    public Type getResultType() {
        if ( thenExpression.getResultType().equals(elseExpression.getResultType()) ) {
            return thenExpression.getResultType();
        } else {
            return BuiltInType.UNKNOWN;
        }
    }

    @Override
    public ASTNode[] getChildrenNode() {
        return new ASTNode[] { condition, thenExpression, elseExpression };
    }

    @Override
    public <T> T accept(Visitor<T> v) {
        return v.visit(this);
    }
}
