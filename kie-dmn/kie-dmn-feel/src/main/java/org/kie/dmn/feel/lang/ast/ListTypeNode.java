/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
import org.kie.dmn.feel.lang.Type;
import org.kie.dmn.feel.lang.types.GenListType;

public class ListTypeNode extends TypeNode {

    private final TypeNode genTypeNode;

    public ListTypeNode(ParserRuleContext ctx, TypeNode gen) {
        super( ctx );
        this.genTypeNode = gen;
    }

    @Override
    public Type evaluate(EvaluationContext ctx) {
        Type gen = genTypeNode.evaluate(ctx);
        return new GenListType(gen);
    }

    @Override
    public <T> T accept(Visitor<T> v) {
        return v.visit(this);
    }

    public TypeNode getGenTypeNode() {
        return genTypeNode;
    }
}
