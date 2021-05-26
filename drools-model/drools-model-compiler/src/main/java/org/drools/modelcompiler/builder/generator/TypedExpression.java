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
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import com.github.javaparser.ast.expr.Expression;
import org.drools.mvel.parser.printer.PrintUtil;

import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.toClassOrInterfaceType;
import static org.drools.modelcompiler.util.ClassUtil.toNonPrimitiveType;
import static org.drools.modelcompiler.util.ClassUtil.toRawClass;

public class TypedExpression {

    private Class<?> originalPatternType;
    private final Expression expression;
    private Type type;
    private final String fieldName;

    protected Boolean staticExpr;
    protected TypedExpression left;
    protected TypedExpression right;

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

    public com.github.javaparser.ast.type.Type getJPType() {
        return toClassOrInterfaceType(toNonPrimitiveType((Class<?>) type));
    }

    public boolean isPrimitive() {
        return type != null && toRawClass(type).isPrimitive();
    }

    public boolean isArray() {
        return type != null && toRawClass(type).isArray();
    }

    public boolean isList() {
        return type != null && toRawClass(type).isAssignableFrom( List.class );
    }

    public boolean isMap() {
        return type != null && toRawClass(type).isAssignableFrom( Map.class );
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
        return isNumberLiteral(expression);
    }

    public static boolean isNumberLiteral(Expression expression) {
        return expression != null &&
                (expression.isCharLiteralExpr()
                        || expression.isIntegerLiteralExpr()
                        || expression.isLongLiteralExpr()
                        || expression.isDoubleLiteralExpr()
                        || expression.isEnclosedExpr() && isNumberLiteral(expression.asEnclosedExpr().getInner()));
    }

    public TypedExpression cloneWithNewExpression( Expression newExpression) {
        final TypedExpression cloned = new TypedExpression(newExpression, type, fieldName);
        cloned.staticExpr = staticExpr;
        cloned.left = left;
        return cloned;

    }

    public Optional<Class<?>> getOriginalPatternType() {
        return Optional.ofNullable(originalPatternType);
    }

    public void setOriginalPatternType(Class<?> originalPatternType) {
        this.originalPatternType = originalPatternType;
    }

    @Override
    public String toString() {
        return "TypedExpression{" +
                "expression=" + expression +
                ", jpType=" + (expression == null ? "" : expression.getClass().getSimpleName()) +
                ", type=" + type +
                ", fieldName='" + fieldName + '\'' +
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
                Objects.equals(fieldName, that.fieldName);
    }

    @Override
    public int hashCode() {

        return Objects.hash(expression, type, fieldName);
    }

    public Expression uncastExpression() {
        return DrlxParseUtil.uncastExpr(expression);
    }

    public boolean isBigDecimal() {
        return type != null && toRawClass(type).isAssignableFrom( BigDecimal.class );
    }
}