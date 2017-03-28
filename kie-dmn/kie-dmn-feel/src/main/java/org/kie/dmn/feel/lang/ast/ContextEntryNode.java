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

public class ContextEntryNode
        extends BaseNode {

    private BaseNode name;
    private BaseNode value;

    public ContextEntryNode(ParserRuleContext ctx) {
        super( ctx );
    }

    public ContextEntryNode(ParserRuleContext ctx, BaseNode name, BaseNode value) {
        super( ctx );
        this.name = name;
        this.value = value;
    }

    public BaseNode getName() {
        return name;
    }

    public void setName(BaseNode name) {
        this.name = name;
    }

    public BaseNode getValue() {
        return value;
    }

    public void setValue(BaseNode value) {
        this.value = value;
    }

    public String evaluateName( EvaluationContext ctx ) {
        return (String) name.evaluate( ctx );
    }

    @Override
    public Object evaluate(EvaluationContext ctx) {
        return value.evaluate( ctx );
    }
}
