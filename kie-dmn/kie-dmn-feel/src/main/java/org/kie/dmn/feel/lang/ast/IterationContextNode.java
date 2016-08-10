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
import org.kie.dmn.feel.lang.EvaluationContext;

public class IterationContextNode
        extends BaseNode {

    private NameDefNode name;
    private BaseNode    expression;

    public IterationContextNode(ParserRuleContext ctx, NameDefNode name, BaseNode expression) {
        super( ctx );
        this.name = name;
        this.expression = expression;
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

    public void setExpression(BaseNode expression) {
        this.expression = expression;
    }

    public String evaluateName(EvaluationContext ctx) {
        return (String) this.name.evaluate( ctx );
    }

    @Override
    public Object evaluate(EvaluationContext ctx) {
        return expression != null ? expression.evaluate( ctx ) : null;
    }
}
