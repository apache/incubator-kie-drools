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

import java.lang.reflect.Type;
import java.util.Objects;
import java.util.Optional;

import com.github.javaparser.ast.expr.Expression;
import org.drools.mvel.parser.printer.PrintUtil;

import static org.drools.modelcompiler.util.ClassUtil.toNonPrimitiveType;
import static org.drools.modelcompiler.util.ClassUtil.toRawClass;

public class TypedExpression {

    private Expression expression;
    private Type type;
    private String fieldName;
    private Optional<String> unificationVariable = Optional.empty();
    private Optional<String> unificationName = Optional.empty();
    private Boolean staticExpr;
    private TypedExpression left;
    private TypedExpression right;

    public TypedExpression( Expression expression ) {
        this(expression, null);
    }

    public TypedExpression( Expression expression, Type type ) {
        this(expression, type, null);
    }

    public TypedExpression( Expression expression, Type type, String fieldName ) {
        this.expression = expression;
        this.type = type;
        this.fieldName = fieldName;
    }

    public TypedExpression( String unificationVariable, Type type, String name) {
        this.unificationVariable = Optional.of(unificationVariable);
        this.type = type;
        this.unificationName = Optional.of(name);
    }

    public boolean isThisExpression() {
        return DrlxParseUtil.isThisExpression( expression );
    }

    public String getFieldName() {
        return fieldName;
    }

    public Expression getExpression() {
        return expression;
    }

    public TypedExpression setType( Type type ) {
        this.type = type;
        return this;
    }

    public Type getType() {
        return type;
    }

    public Class<?> getRawClass() {
        return toRawClass( type );
    }

    public Optional<Class<?>> getBoxedType() {
        if(type instanceof Class<?>) {
            return Optional.of(toNonPrimitiveType((Class<?>) type));
        } else {
            return Optional.empty();
        }
    }

    public boolean isPrimitive() {
        return type != null && toRawClass(type).isPrimitive();
    }

    public boolean isArray() {
        return type != null && toRawClass(type).isArray();
    }

    public Optional<String> getUnificationVariable() {
        return unificationVariable;
    }

    public Optional<String> getUnificationName() {
        return unificationName;
    }

    public TypedExpression setStatic(Boolean aStatic) {
        staticExpr = aStatic;
        return this;
    }

    public Boolean isStatic() {
        return staticExpr;
    }

    public TypedExpression setLeft(TypedExpression left) {
        this.left = left;
        return this;
    }

    public TypedExpression getLeft() {
        return left;
    }

    public TypedExpression setRight( TypedExpression right ) {
        this.right = right;
        return this;
    }

    public TypedExpression getRight() {
        return right;
    }

    public boolean isNumberLiteral() {
        return expression != null &&
                (expression.isCharLiteralExpr()
                        || expression.isIntegerLiteralExpr()
                        || expression.isLongLiteralExpr()
                        || expression.isDoubleLiteralExpr());
    }

    public TypedExpression cloneWithNewExpression( Expression newExpression) {
        final TypedExpression cloned = new TypedExpression(newExpression, type, fieldName);
        cloned.unificationName = unificationName;
        cloned.unificationVariable = unificationVariable;
        cloned.staticExpr = staticExpr;
        cloned.left = left;
        return cloned;

    }

    @Override
    public String toString() {
        return "TypedExpression{" +
                "expression=" + expression +
                ", jpType=" + (expression == null ? "" : expression.getClass().getSimpleName()) +
                ", type=" + type +
                ", fieldName='" + fieldName + '\'' +
                ", unificationVariable=" + unificationVariable +
                ", unificationName=" + unificationName +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TypedExpression that = (TypedExpression) o;
        return Objects.equals(PrintUtil.printConstraint(expression), PrintUtil.printConstraint(that.expression)) &&
                Objects.equals(type, that.type) &&
                Objects.equals(fieldName, that.fieldName) &&
                Objects.equals(unificationVariable, that.unificationVariable) &&
                Objects.equals(unificationName, that.unificationName);
    }

    @Override
    public int hashCode() {

        return Objects.hash(expression, type, fieldName, unificationVariable, unificationName);
    }
}