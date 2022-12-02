/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

package org.drools.mvelcompiler;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.ArrayAccessExpr;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.BooleanLiteralExpr;
import com.github.javaparser.ast.expr.CastExpr;
import com.github.javaparser.ast.expr.CharLiteralExpr;
import com.github.javaparser.ast.expr.EnclosedExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import com.github.javaparser.ast.expr.LongLiteralExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NullLiteralExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.PatternExpr;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.expr.TextBlockLiteralExpr;
import com.github.javaparser.ast.expr.UnaryExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.YieldStmt;
import org.drools.util.ClassUtils;
import org.drools.util.MethodUtils.NullType;
import org.drools.mvel.parser.ast.expr.BigDecimalLiteralExpr;
import org.drools.mvel.parser.ast.expr.BigIntegerLiteralExpr;
import org.drools.mvel.parser.ast.expr.DrlNameExpr;
import org.drools.mvel.parser.ast.visitor.DrlGenericVisitor;
import org.drools.mvelcompiler.ast.BigDecimalArithmeticExprT;
import org.drools.mvelcompiler.ast.BigDecimalConvertedExprT;
import org.drools.mvelcompiler.ast.BigIntegerConvertedExprT;
import org.drools.mvelcompiler.ast.BinaryExprT;
import org.drools.mvelcompiler.ast.BooleanLiteralExpressionT;
import org.drools.mvelcompiler.ast.CastExprT;
import org.drools.mvelcompiler.ast.CharacterLiteralExpressionT;
import org.drools.mvelcompiler.ast.FieldAccessTExpr;
import org.drools.mvelcompiler.ast.FieldToAccessorTExpr;
import org.drools.mvelcompiler.ast.IntegerLiteralExpressionT;
import org.drools.mvelcompiler.ast.ListAccessExprT;
import org.drools.mvelcompiler.ast.LongLiteralExpressionT;
import org.drools.mvelcompiler.ast.ObjectCreationExpressionT;
import org.drools.mvelcompiler.ast.SimpleNameTExpr;
import org.drools.mvelcompiler.ast.StringLiteralExpressionT;
import org.drools.mvelcompiler.ast.TypedExpression;
import org.drools.mvelcompiler.ast.UnalteredTypedExpression;
import org.drools.mvelcompiler.context.Declaration;
import org.drools.mvelcompiler.context.MvelCompilerContext;
import org.drools.mvelcompiler.util.TypeUtils;

import static java.util.Arrays.asList;
import static java.util.Optional.ofNullable;
import static org.drools.util.ClassUtils.getAccessor;
import static org.drools.mvelcompiler.ast.BigDecimalArithmeticExprT.toBigDecimalMethod;
import static org.drools.mvelcompiler.util.OptionalUtils.map2;
import static org.drools.mvelcompiler.util.TypeUtils.classFromType;

/**
 * This phase processes the right hand side of a Java Expression and creates a new AST
 * with the transformation rules applied i.e.
 *
 * person.name;
 *
 * becomes
 *
 * person.getName();
 *
 * It also returns the type of the expression, useful in the subsequent phase in which we
 * might need to create new variables accordingly.
 *
 */
public class RHSPhase implements DrlGenericVisitor<TypedExpression, RHSPhase.Context> {

    private final MethodCallExprVisitor methodCallExprVisitor;

    static class Context {
        final Optional<TypedExpression> scope;

        Context(TypedExpression scope) {
            this.scope = ofNullable(scope);
        }

        Optional<Type> getScopeType() {
            return scope.flatMap(TypedExpression::getType);
        }
    }

    private final MvelCompilerContext mvelCompilerContext;

    RHSPhase(MvelCompilerContext mvelCompilerContext) {
        this.mvelCompilerContext = mvelCompilerContext;
        methodCallExprVisitor = new MethodCallExprVisitor(this, this.mvelCompilerContext);
    }

    public TypedExpression invoke(Node statement) {
        Context ctx = new Context(null);

        return statement.accept(this, ctx);
    }

    @Override
    public TypedExpression visit(DrlNameExpr n, Context arg) {
        return n.getName().accept(this, arg);
    }

    @Override
    public TypedExpression visit(SimpleName n, Context arg) {
        if (!arg.scope.isPresent()) { // first node
            return simpleNameAsFirstNode(n);
        } else {
            return simpleNameAsField(n, arg);
        }
    }

    @Override
    public TypedExpression visit(YieldStmt n, Context arg) {
        return null;
    }

    @Override
    public TypedExpression visit(TextBlockLiteralExpr n, Context arg) {
        return null;
    }

    @Override
    public TypedExpression visit(PatternExpr n, Context arg) {
        return null;
    }

    private TypedExpression simpleNameAsFirstNode(SimpleName n) {
        return asDeclaration(n)
                .map(Optional::of)
                .orElseGet(() -> asPropertyAccessorOfRootPattern(n))
                .map(Optional::of)
                .orElseGet(() -> asEnum(n))
                .orElseGet(() -> new UnalteredTypedExpression(n));
    }

    private TypedExpression simpleNameAsField(SimpleName n, Context arg) {
        return asPropertyAccessor(n, arg)
                .map(Optional::of)
                .orElseGet(() -> asFieldAccessTExpr(n, arg))
                .orElseGet(() -> new UnalteredTypedExpression(n));
    }

    private Optional<TypedExpression> asFieldAccessTExpr(SimpleName n, Context arg) {
        Optional<TypedExpression> lastTypedExpression = arg.scope;
        Optional<Type> scopeType = arg.getScopeType();

        Optional<Field> fieldType = scopeType.flatMap(te -> {
            Class parentClass = TypeUtils.classFromType(te);
            Field field = ClassUtils.getField(parentClass, n.asString());
            return ofNullable(field);
        });

        return map2(lastTypedExpression, fieldType, FieldAccessTExpr::new);
    }

    private Optional<TypedExpression> asDeclaration(SimpleName n) {
        Optional<Declaration> typeFromDeclarations = mvelCompilerContext.findDeclarations(n.asString());
        return typeFromDeclarations.map(d -> {
            Class<?> clazz = d.getClazz();
            return new SimpleNameTExpr(n.asString(), clazz);
        });
    }

    private Optional<TypedExpression> asEnum(SimpleName n) {
        Optional<Class<?>> enumType = mvelCompilerContext.findEnum(n.asString());
        return enumType.map(clazz -> new SimpleNameTExpr(n.asString(), clazz));
    }

    private Optional<TypedExpression> asPropertyAccessor(SimpleName n, Context arg) {
        Optional<TypedExpression> lastTypedExpression = arg.scope;

        Optional<Type> scopeType = lastTypedExpression.filter(ListAccessExprT.class::isInstance)
                                                      .map(ListAccessExprT.class::cast)
                                                      .map(expr -> expr.getElementType())
                                                      .orElse(arg.getScopeType());

        Optional<Method> optAccessor = scopeType.flatMap(t -> ofNullable(getAccessor(classFromType(t), n.asString())));

        return map2(lastTypedExpression, optAccessor, FieldToAccessorTExpr::new);
    }

    private Optional<TypedExpression> asPropertyAccessorOfRootPattern(SimpleName n) {
        Optional<Class<?>> scopeType = mvelCompilerContext.getRootPattern();
        Optional<Method> optAccessor = scopeType.flatMap(t -> ofNullable(getAccessor(classFromType(t), n.asString())));

        return map2(mvelCompilerContext.createRootTypePrefix(), optAccessor, FieldToAccessorTExpr::new);
    }

    @Override
    public TypedExpression visit(FieldAccessExpr n, Context arg) {
        TypedExpression scope = n.getScope().accept(this, arg);
        return n.getName().accept(this, new Context(scope));
    }

    @Override
    public TypedExpression visit(MethodCallExpr n, Context arg) {
        return n.accept(methodCallExprVisitor, arg);
    }

    @Override
    public TypedExpression visit(BinaryExpr n, Context arg) {
        TypedExpression left = n.getLeft().accept(this, arg);
        TypedExpression right = n.getRight().accept(this, arg);
        return withPossiblyBigDecimalConversion(left, right, n.getOperator());
    }

    private TypedExpression withPossiblyBigDecimalConversion(TypedExpression left, TypedExpression right, BinaryExpr.Operator operator) {
        Optional<Type> optTypeLeft = left.getType();
        Optional<Type> optTypeRight = right.getType();

        if (!optTypeLeft.isPresent() || !optTypeRight.isPresent()) { // coerce only when types are known
            return new BinaryExprT(left, right, operator);
        }

        Type typeLeft = optTypeLeft.get();
        Type typeRight = optTypeRight.get();

        boolean binaryOperatorNeedBigDecimalConversion = asList(BinaryExpr.Operator.PLUS,
                                                                BinaryExpr.Operator.DIVIDE,
                                                                BinaryExpr.Operator.MINUS,
                                                                BinaryExpr.Operator.MULTIPLY,
                                                                BinaryExpr.Operator.REMAINDER,
                                                                BinaryExpr.Operator.EQUALS,
                                                                BinaryExpr.Operator.NOT_EQUALS
        ).contains(operator);

        boolean isStringConcatenation = operator == BinaryExpr.Operator.PLUS &&
                (typeLeft == String.class || typeRight == String.class);

        if (binaryOperatorNeedBigDecimalConversion && !isStringConcatenation) {

            boolean shouldNegate = operator == BinaryExpr.Operator.NOT_EQUALS;

            if (typeLeft == BigDecimal.class && typeRight == BigDecimal.class) { // do not convert
                return new BigDecimalArithmeticExprT(toBigDecimalMethod(operator),
                                                     left, right, shouldNegate);
            } else if (typeLeft != BigDecimal.class && typeRight == BigDecimal.class) { // convert left
                return new BigDecimalArithmeticExprT(toBigDecimalMethod(operator),
                                                     new BigDecimalConvertedExprT(left), right, shouldNegate);
            } else if (typeLeft == BigDecimal.class && typeRight != BigDecimal.class) { // convert right
                return new BigDecimalArithmeticExprT(toBigDecimalMethod(operator),
                                                     left, new BigDecimalConvertedExprT(right), shouldNegate);
            }
        }

        return new BinaryExprT(left, right, operator);
    }

    @Override
    public TypedExpression visit(ExpressionStmt n, Context arg) {
        return n.getExpression().accept(this, arg);
    }

    @Override
    public TypedExpression visit(VariableDeclarationExpr n, Context arg) {
        return n.getVariables().iterator().next().accept(this, arg);
    }

    @Override
    public TypedExpression visit(VariableDeclarator n, Context arg) {
        Optional<TypedExpression> initExpression = n.getInitializer().map(i -> i.accept(this, arg));
        return initExpression.orElse(null);
    }

    @Override
    public TypedExpression visit(AssignExpr n, Context arg) {
        return n.getValue().accept(this, arg);
    }

    @Override
    public TypedExpression visit(StringLiteralExpr n, Context arg) {
        return new StringLiteralExpressionT(n);
    }

    @Override
    public TypedExpression visit(IntegerLiteralExpr n, Context arg) {
        return new IntegerLiteralExpressionT(n);
    }

    @Override
    public TypedExpression visit(CharLiteralExpr n, Context arg) {
        return new CharacterLiteralExpressionT(n);
    }

    @Override
    public TypedExpression visit(LongLiteralExpr n, Context arg) {
        return new LongLiteralExpressionT(n);
    }

    @Override
    public TypedExpression visit(BooleanLiteralExpr n, Context arg) {
        return new BooleanLiteralExpressionT(n);
    }

    @Override
    public TypedExpression defaultMethod(Node n, Context context) {
        return new UnalteredTypedExpression(n);
    }

    @Override
    public TypedExpression visit(ObjectCreationExpr n, Context arg) {
        List<TypedExpression> constructorArguments = new ArrayList<>();
        for(Expression e : n.getArguments()) {
            TypedExpression compiledArgument = e.accept(this, arg);
            constructorArguments.add(compiledArgument);
        }
        return new ObjectCreationExpressionT(constructorArguments, resolveType(n.getType()));
    }

    @Override
    public TypedExpression visit(NullLiteralExpr n, Context arg) {
        return new UnalteredTypedExpression(n, NullType.class);
    }

    @Override
    public TypedExpression visit(ArrayAccessExpr n, Context arg) {
        TypedExpression name = n.getName().accept(this, arg);

        Optional<Type> type = name.getType();
        if(type.filter(TypeUtils::isCollection).isPresent()) {
            return new ListAccessExprT(name, n.getIndex(), type.get());
        }
        return new UnalteredTypedExpression(n, type.orElse(null));
    }

    @Override
    public TypedExpression visit(EnclosedExpr n, Context arg) {
        return n.getInner().accept(this, arg);
    }

    @Override
    public TypedExpression visit(CastExpr n, Context arg) {
        TypedExpression innerExpr = n.getExpression().accept(this, arg);
        return new CastExprT(innerExpr, resolveType(n.getType()));
    }

    @Override
    public TypedExpression visit(BigDecimalLiteralExpr n, Context arg) {
        return new BigDecimalConvertedExprT(new StringLiteralExpressionT(new StringLiteralExpr(n.getValue())));
    }

    @Override
    public TypedExpression visit(BigIntegerLiteralExpr n, Context arg) {
        return new BigIntegerConvertedExprT(new StringLiteralExpressionT(new StringLiteralExpr(n.getValue())));
    }

    @Override
    public TypedExpression visit(UnaryExpr n, Context arg) {
        Expression innerExpr = n.getExpression();
        UnaryExpr.Operator operator = n.getOperator();
        if (innerExpr instanceof BigDecimalLiteralExpr && operator == UnaryExpr.Operator.MINUS) {
            return new BigDecimalConvertedExprT(new StringLiteralExpressionT(new StringLiteralExpr(operator.asString() + ((BigDecimalLiteralExpr) innerExpr).getValue())));
        } else if (innerExpr instanceof BigIntegerLiteralExpr && operator == UnaryExpr.Operator.MINUS) {
            return new BigIntegerConvertedExprT(new StringLiteralExpressionT(new StringLiteralExpr(operator.asString() + ((BigIntegerLiteralExpr) innerExpr).getValue())));
        } else {
            return defaultMethod(n, arg);
        }
    }

    private Class<?> resolveType(com.github.javaparser.ast.type.Type type) {
        return mvelCompilerContext.resolveType(type.asString());
    }
}

