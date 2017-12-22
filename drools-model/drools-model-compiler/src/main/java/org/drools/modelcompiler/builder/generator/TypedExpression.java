/*
 * Copyright 2005 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.modelcompiler.builder.generator;

import java.util.Optional;

import org.drools.javaparser.ast.expr.Expression;
import org.drools.javaparser.ast.expr.MethodCallExpr;

public class TypedExpression {

    private Expression expression;
    private Class<?> type;
    private String fieldName;
    private Expression prefixExpression;
    private Optional<String> unificationVariable = Optional.empty();
    private Optional<String> unificationName = Optional.empty();

    public TypedExpression() { }

    public TypedExpression( Expression expression ) {
        this(expression, null);
    }

    public TypedExpression( Expression expression, Class<?> type ) {
        this(expression, type, null);
    }

    public TypedExpression( Expression expression, Class<?> type, String fieldName ) {
        this.expression = expression;
        this.type = type;
        this.fieldName = fieldName;
    }

    public TypedExpression( String unificationVariable, Class<?> type, String name) {
        this.unificationVariable = Optional.of(unificationVariable);
        this.type = type;
        this.unificationName = Optional.of(name);
    }

    public String getFieldName() {
        return fieldName;
    }

    public Expression getExpression() {
        return expression;
    }

    public TypedExpression setExpression( Expression expression ) {
        this.expression = expression;
        return this;
    }

    public TypedExpression setType( Class<?> type ) {
        this.type = type;
        return this;
    }

    public Expression getPrefixExpression() {
        return prefixExpression;
    }

    public TypedExpression setPrefixExpression( Expression prefixExpression ) {
        this.prefixExpression = prefixExpression;
        return this;
    }

    public String getExpressionAsString() {
        return expression.toString();
    }

    public Class<?> getType() {
        return type;
    }

    public boolean isPrimitive() {
        return type != null && type.isPrimitive();
    }

    public Optional<String> getUnificationVariable() {
        return unificationVariable;
    }

    public Optional<String> getUnificationName() {
        return unificationName;
    }
}