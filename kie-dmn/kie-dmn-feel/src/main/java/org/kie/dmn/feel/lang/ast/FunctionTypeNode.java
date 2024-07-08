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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.antlr.v4.runtime.ParserRuleContext;
import org.kie.dmn.feel.lang.EvaluationContext;
import org.kie.dmn.feel.lang.Type;
import org.kie.dmn.feel.lang.types.GenFnType;

public class FunctionTypeNode extends TypeNode {

    private final List<TypeNode> argTypes;
    private final TypeNode retType;

    public FunctionTypeNode(ParserRuleContext ctx, List<TypeNode> argTypes, TypeNode gen) {
        super( ctx );
        this.argTypes = new ArrayList<>(argTypes);
        this.retType = gen;
    }

    public FunctionTypeNode(List<TypeNode> argTypes, TypeNode retType, String text) {
        this.argTypes = new ArrayList<>(argTypes);
        this.retType = retType;
        this.setText(text);
    }

    @Override
    public Type evaluate(EvaluationContext ctx) {
        List<Type> args = argTypes.stream().map(t -> t.evaluate(ctx)).collect(Collectors.toList());
        Type ret = retType.evaluate(ctx);
        return new GenFnType(args, ret);
    }

    @Override
    public <T> T accept(Visitor<T> v) {
        return v.visit(this);
    }

    public List<TypeNode> getArgTypes() {
        return argTypes;
    }

    public TypeNode getRetType() {
        return retType;
    }

}
