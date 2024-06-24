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
package org.kie.dmn.feel.lang.ast;

import org.antlr.v4.runtime.ParserRuleContext;
import org.kie.dmn.feel.lang.EvaluationContext;

public class IterationContextNode
        extends BaseNode {

    private NameDefNode name;
    private BaseNode    expression;
    private BaseNode    rangeEndExpr = null;

    public IterationContextNode(ParserRuleContext ctx, NameDefNode name, BaseNode expression) {
        super( ctx );
        this.name = name;
        this.expression = expression;
    }

    public IterationContextNode(ParserRuleContext ctx, NameDefNode name, BaseNode expression, BaseNode rangeEndExpr) {
        super(ctx);
        this.name = name;
        this.expression = expression;
        this.rangeEndExpr = rangeEndExpr;
    }

    public IterationContextNode(NameDefNode name, BaseNode expression, BaseNode rangeEndExpr, String text) {
        this.name = name;
        this.expression = expression;
        this.rangeEndExpr = rangeEndExpr;
        this.setText(text);
    }

    public NameDefNode getName() {
        return name;
    }

    public void setName(NameDefNode name) {
        this.name = name;
    }

    public BaseNode getExpression() {
        return expression;
    }

    public BaseNode getRangeEndExpr() {
        return rangeEndExpr;
    }

    public void setExpression(BaseNode expression) {
        this.expression = expression;
    }

    public String evaluateName(EvaluationContext ctx) {
        return this.name.evaluate(ctx);
    }

    @Override
    public Object evaluate(EvaluationContext ctx) {
        return expression != null ? expression.evaluate( ctx ) : null;
    }

    public Object evaluateRangeEnd(EvaluationContext ctx) {
        return rangeEndExpr != null ? rangeEndExpr.evaluate(ctx) : null;
    }

    @Override
    public ASTNode[] getChildrenNode() {
        if( rangeEndExpr != null ) {
            return new ASTNode[] { name, expression, rangeEndExpr };
        }
        return new ASTNode[] { name, expression };
    }

    @Override
    public <T> T accept(Visitor<T> v) {
        return v.visit(this);
    }

}
