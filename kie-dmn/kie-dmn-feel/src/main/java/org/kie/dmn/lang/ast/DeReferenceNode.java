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

package org.kie.dmn.lang.ast;

import org.antlr.v4.runtime.ParserRuleContext;

import java.util.List;

public class DeReferenceNode
        extends BaseNode {

    private BaseNode expression;
    private NameRefNode name;

    public DeReferenceNode(ParserRuleContext ctx, BaseNode expression, NameRefNode name) {
        super( ctx );
        this.expression = expression;
        this.name = name;
    }

    public BaseNode getExpression() {
        return expression;
    }

    public void setExpression(BaseNode expression) {
        this.expression = expression;
    }

    public NameRefNode getName() {
        return name;
    }

    public void setName(NameRefNode name) {
        this.name = name;
    }
}
