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

import java.time.Duration;
import java.time.chrono.ChronoPeriod;

import org.antlr.v4.runtime.ParserRuleContext;
import org.kie.dmn.feel.lang.EvaluationContext;
import org.kie.dmn.feel.lang.SimpleType;
import org.kie.dmn.feel.lang.Type;
import org.kie.dmn.feel.lang.types.BuiltInType;

public class InstanceOfNode
        extends BaseNode {

    private BaseNode expression;
    private TypeNode type;

    public InstanceOfNode(ParserRuleContext ctx, BaseNode expression, TypeNode type) {
        super( ctx );
        this.expression = expression;
        this.type = type;
    }

    public InstanceOfNode(BaseNode expression, TypeNode type, String text) {
        this.expression = expression;
        this.type = type;
        this.setText(text);
    }

    public BaseNode getExpression() {
        return expression;
    }

    public void setExpression(BaseNode expression) {
        this.expression = expression;
    }

    public TypeNode getType() {
        return type;
    }

    public void setType(TypeNode type) {
        this.type = type;
    }

    @Override
    public Object evaluate(EvaluationContext ctx) {
        Object value = expression.evaluate( ctx );
        Type t = type.evaluate( ctx );
        if (t != BuiltInType.DURATION) {
            return t.isInstanceOf(value);
        } else {
            switch (type.getText()) {
                case SimpleType.YEARS_AND_MONTHS_DURATION:
                    return value instanceof ChronoPeriod;
                case SimpleType.DAYS_AND_TIME_DURATION:
                    return value instanceof Duration;
                default:
                    return t.isInstanceOf(value);
            }
        }
    }

    @Override
    public Type getResultType() {
        return BuiltInType.BOOLEAN;
    }

    @Override
    public ASTNode[] getChildrenNode() {
        return new ASTNode[] { expression };
    }

    @Override
    public <T> T accept(Visitor<T> v) {
        return v.visit(this);
    }

}
