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
import org.kie.dmn.feel.lang.SimpleType;
import org.kie.dmn.feel.lang.Type;
import org.kie.dmn.feel.lang.types.BuiltInType;
import org.kie.dmn.feel.runtime.FEELFunction.Param;
import org.kie.dmn.feel.runtime.functions.BaseFEELFunction;

public class FormalParameterNode extends BaseNode {

    private final NameDefNode name;
    private final TypeNode type;

    public FormalParameterNode(ParserRuleContext ctx, NameDefNode name, TypeNode type) {
        super( ctx );
        this.name = name;
        if (type != null) {
            this.type = type;
        } else {
            TypeNode synthetic = new CTypeNode(BuiltInType.UNKNOWN);
            synthetic.copyLocationAttributesFrom(name);
            synthetic.setText(SimpleType.ANY);
            this.type = synthetic;
        }
    }

    public FormalParameterNode(NameDefNode name, TypeNode type, String text) {
        this.name = name;
        this.type = type;
        this.setText(text);
    }

    @Override
    public BaseFEELFunction.Param evaluate(EvaluationContext ctx) {
        return new Param(name.evaluate(ctx), type.evaluate(ctx));
    }

    @Override
    public Type getResultType() {
        return BuiltInType.UNKNOWN;
    }

    @Override
    public <T> T accept(Visitor<T> v) {
        return v.visit(this);
    }

    public NameDefNode getName() {
        return name;
    }

    public TypeNode getType() {
        return type;
    }

}
