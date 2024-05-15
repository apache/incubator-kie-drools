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
package org.drools.mvelcompiler;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.ArrayAccessExpr;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.BooleanLiteralExpr;
import com.github.javaparser.ast.expr.CastExpr;
import com.github.javaparser.ast.expr.CharLiteralExpr;
import com.github.javaparser.ast.expr.DoubleLiteralExpr;
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
import com.github.javaparser.utils.Pair;
import org.drools.mvel.parser.ast.expr.BigDecimalLiteralExpr;
import org.drools.mvel.parser.ast.expr.BigIntegerLiteralExpr;
import org.drools.mvel.parser.ast.expr.DrlNameExpr;
import org.drools.mvel.parser.ast.expr.ListCreationLiteralExpression;
import org.drools.mvel.parser.ast.expr.MapCreationLiteralExpression;
import org.drools.mvel.parser.ast.visitor.DrlGenericVisitor;
import org.drools.mvelcompiler.ast.BigDecimalArithmeticExprT;
import org.drools.mvelcompiler.ast.BigDecimalConvertedExprT;
import org.drools.mvelcompiler.ast.BigDecimalRelationalExprT;
import org.drools.mvelcompiler.ast.BigIntegerConvertedExprT;
import org.drools.mvelcompiler.ast.BinaryExprT;
import org.drools.mvelcompiler.ast.BooleanLiteralExpressionT;
import org.drools.mvelcompiler.ast.CastExprT;
import org.drools.mvelcompiler.ast.CharacterLiteralExpressionT;
import org.drools.mvelcompiler.ast.DoubleLiteralExpressionT;
import org.drools.mvelcompiler.ast.FieldAccessTExpr;
import org.drools.mvelcompiler.ast.FieldToAccessorTExpr;
import org.drools.mvelcompiler.ast.IntegerLiteralExpressionT;
import org.drools.mvelcompiler.ast.ListAccessExprT;
import org.drools.mvelcompiler.ast.ListExprT;
import org.drools.mvelcompiler.ast.LongLiteralExpressionT;
import org.drools.mvelcompiler.ast.MapExprT;
import org.drools.mvelcompiler.ast.MapGetExprT;
import org.drools.mvelcompiler.ast.ObjectCreationExpressionT;
import org.drools.mvelcompiler.ast.SimpleNameTExpr;
import org.drools.mvelcompiler.ast.StringLiteralExpressionT;
import org.drools.mvelcompiler.ast.TypedExpression;
import org.drools.mvelcompiler.ast.UnalteredTypedExpression;
import org.drools.mvelcompiler.context.Declaration;
import org.drools.mvelcompiler.context.MvelCompilerContext;
import org.drools.mvelcompiler.util.MethodResolutionUtils;
import org.drools.mvelcompiler.util.TypeUtils;
import org.drools.mvelcompiler.util.VisitorContext;
import org.drools.util.ClassUtils;
import org.drools.util.MethodUtils.NullType;

import static java.util.Optional.ofNullable;
import static org.drools.mvelcompiler.ast.BigDecimalArithmeticExprT.toBigDecimalMethod;
import static org.drools.mvelcompiler.util.OptionalUtils.map2;
import static org.drools.util.ClassUtils.classFromType;
import static org.drools.util.ClassUtils.getAccessor;

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
public class RHSPhase implements DrlGenericVisitor<TypedExpression, VisitorContext> {

    private static final Set<BinaryExpr.Operator> arithmeticOperators = Set.of(
            BinaryExpr.Operator.PLUS,
            BinaryExpr.Operator.MINUS,
            BinaryExpr.Operator.MULTIPLY,
            BinaryExpr.Operator.DIVIDE,
            BinaryExpr.Operator.REMAINDER
    );

    private static final Set<BinaryExpr.Operator> relationalOperators = Set.of(
            BinaryExpr.Operator.EQUALS,
            BinaryExpr.Operator.NOT_EQUALS,
            BinaryExpr.Operator.LESS,
            BinaryExpr.Operator.GREATER,
            BinaryExpr.Operator.LESS_EQUALS,
            BinaryExpr.Operator.GREATER_EQUALS
    );

    private final MethodCallExprVisitor methodCallExprVisitor;

    private final MvelCompilerContext mvelCompilerContext;

    RHSPhase(MvelCompilerContext mvelCompilerContext) {
        this.mvelCompilerContext = mvelCompilerContext;
        methodCallExprVisitor = new MethodCallExprVisitor(this, this.mvelCompilerContext);
    }

    public TypedExpression invoke(Node statement) {
        VisitorContext ctx = new VisitorContext(null);

        return statement.accept(this, ctx);
    }

    @Override
    public TypedExpression visit(DrlNameExpr n, VisitorContext arg) {
        return n.getName().accept(this, arg);
    }

    @Override
    public TypedExpression visit(SimpleName n, VisitorContext arg) {
        if (arg.getScope().isEmpty()) { // first node
            return simpleNameAsFirstNode(n);
        } else {
            return simpleNameAsField(n, arg);
        }
    }

    @Override
    public TypedExpression visit(YieldStmt n, VisitorContext arg) {
        return null;
    }

    @Override
    public TypedExpression visit(TextBlockLiteralExpr n, VisitorContext arg) {
        return new UnalteredTypedExpression(n, String.class);
    }

    @Override
    public TypedExpression visit(PatternExpr n, VisitorContext arg) {
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

    private TypedExpression simpleNameAsField(SimpleName n, VisitorContext arg) {
        return asPropertyAccessor(n, arg)
                .map(Optional::of)
                .orElseGet(() -> asFieldAccessTExpr(n, arg))
                .orElseGet(() -> new UnalteredTypedExpression(n));
    }

    private Optional<TypedExpression> asFieldAccessTExpr(SimpleName n, VisitorContext arg) {
        Optional<TypedExpression> lastTypedExpression = arg.getScope();
        Optional<Type> scopeType = arg.getScopeType();

        Optional<Field> fieldType = scopeType.flatMap(te -> {
            Class parentClass = classFromType(te);
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

    private Optional<TypedExpression> asPropertyAccessor(SimpleName n, VisitorContext arg) {
        Optional<TypedExpression> lastTypedExpression = arg.getScope();

        Optional<Type> propertyType = lastTypedExpression.filter(ListAccessExprT.class::isInstance)
                                                         .map(ListAccessExprT.class::cast)
                                                         .map(expr -> expr.getElementType())
                                                         .orElse(arg.getScopeType());

        Optional<Type> scopeType = lastTypedExpression.flatMap(TypedExpression::getScopeType);

        Optional<Method> optAccessor = propertyType.flatMap(t -> ofNullable(getAccessor(classFromType(t, scopeType.orElse(null)), n.asString())));

        return map2(lastTypedExpression, optAccessor, FieldToAccessorTExpr::new);
    }

    private Optional<TypedExpression> asPropertyAccessorOfRootPattern(SimpleName n) {
        Optional<Class<?>> scopeType = mvelCompilerContext.getRootPattern();
        Optional<Method> optAccessor = scopeType.flatMap(t -> ofNullable(getAccessor(classFromType(t), n.asString())));

        return map2(mvelCompilerContext.createRootTypePrefix(), optAccessor, FieldToAccessorTExpr::new);
    }

    @Override
    public TypedExpression visit(FieldAccessExpr n, VisitorContext arg) {
        TypedExpression scope = n.getScope().accept(this, arg);
        if (scope.getType().map(TypeUtils::isMapAccessField).orElse(false)) {
            String key = n.getName().toString();

            // "size" is an edge case and could mean both the size of the map and the value of the key "size".
            // To keep backward compatibility it is necessary to assume that it is the size of the map,
            // but this implies that at the moment it is not possible to read the value of the "size" key
            // using the field access notation
            if (!"size".equals(key)) {
                return new MapGetExprT(scope, key);
            }
        }
        return n.getName().accept(this, new VisitorContext(scope));
    }

    @Override
    public TypedExpression visit(MethodCallExpr n, VisitorContext arg) {
        return n.accept(methodCallExprVisitor, arg);
    }

    @Override
    public TypedExpression visit(BinaryExpr n, VisitorContext arg) {
        TypedExpression left = n.getLeft().accept(this, arg);
        TypedExpression right = n.getRight().accept(this, arg);
        return withPossiblyBigDecimalConversion(left, right, n.getOperator());
    }

    private TypedExpression withPossiblyBigDecimalConversion(TypedExpression left, TypedExpression right, BinaryExpr.Operator operator) {
        Optional<Type> optTypeLeft = left.getType();
        Optional<Type> optTypeRight = right.getType();

        if (optTypeLeft.isEmpty() || optTypeRight.isEmpty()) { // coerce only when types are known
            return new BinaryExprT(left, right, operator);
        }

        Type typeLeft = optTypeLeft.get();
        Type typeRight = optTypeRight.get();

        boolean isStringConcatenation = operator == BinaryExpr.Operator.PLUS &&
                (typeLeft == String.class || typeRight == String.class);

        if (arithmeticOperators.contains(operator) && !isStringConcatenation) {
            return convertToBigDecimalArithmeticExprTIfNeeded(left, right, operator, typeLeft, typeRight);
        } else if (relationalOperators.contains(operator)) {
            return convertToBigDecimalRelationalExprTIfNeeded(left, right, operator, typeLeft, typeRight);
        }

        return new BinaryExprT(left, right, operator);
    }

    private TypedExpression convertToBigDecimalArithmeticExprTIfNeeded(TypedExpression left, TypedExpression right, BinaryExpr.Operator operator, Type typeLeft, Type typeRight) {
        if (typeLeft == BigDecimal.class && typeRight == BigDecimal.class) { // do not convert
            return new BigDecimalArithmeticExprT(toBigDecimalMethod(operator), left, right);
        } else if (typeLeft != BigDecimal.class && typeRight == BigDecimal.class) { // convert left
            return new BigDecimalArithmeticExprT(toBigDecimalMethod(operator), new BigDecimalConvertedExprT(left), right);
        } else if (typeLeft == BigDecimal.class && typeRight != BigDecimal.class) { // convert right
            return new BigDecimalArithmeticExprT(toBigDecimalMethod(operator), left, new BigDecimalConvertedExprT(right));
        } else {
            return new BinaryExprT(left, right, operator);
        }
    }

    private TypedExpression convertToBigDecimalRelationalExprTIfNeeded(TypedExpression left, TypedExpression right, BinaryExpr.Operator operator, Type typeLeft, Type typeRight) {
        if (typeLeft == BigDecimal.class && typeRight == BigDecimal.class) { // do not convert
            return new BigDecimalRelationalExprT(operator, left, right);
        } else if (typeLeft != BigDecimal.class && typeRight == BigDecimal.class) { // convert left
            return new BigDecimalRelationalExprT(operator, new BigDecimalConvertedExprT(left), right);
        } else if (typeLeft == BigDecimal.class && typeRight != BigDecimal.class) { // convert right
            return new BigDecimalRelationalExprT(operator, left, new BigDecimalConvertedExprT(right));
        } else {
            return new BinaryExprT(left, right, operator);
        }
    }

    @Override
    public TypedExpression visit(ExpressionStmt n, VisitorContext arg) {
        return n.getExpression().accept(this, arg);
    }

    @Override
    public TypedExpression visit(VariableDeclarationExpr n, VisitorContext arg) {
        return n.getVariables().iterator().next().accept(this, arg);
    }

    @Override
    public TypedExpression visit(VariableDeclarator n, VisitorContext arg) {
        Optional<TypedExpression> initExpression = n.getInitializer().map(i -> i.accept(this, arg));
        return initExpression.orElse(null);
    }

    @Override
    public TypedExpression visit(AssignExpr n, VisitorContext arg) {
        return n.getValue().accept(this, arg);
    }

    @Override
    public TypedExpression visit(StringLiteralExpr n, VisitorContext arg) {
        return new StringLiteralExpressionT(n);
    }

    @Override
    public TypedExpression visit(IntegerLiteralExpr n, VisitorContext arg) {
        return new IntegerLiteralExpressionT(n);
    }

    @Override
    public TypedExpression visit(DoubleLiteralExpr n, VisitorContext arg) {
        return new DoubleLiteralExpressionT(n);
    }

    @Override
    public TypedExpression visit(CharLiteralExpr n, VisitorContext arg) {
        return new CharacterLiteralExpressionT(n);
    }

    @Override
    public TypedExpression visit(LongLiteralExpr n, VisitorContext arg) {
        return new LongLiteralExpressionT(n);
    }

    @Override
    public TypedExpression visit(BooleanLiteralExpr n, VisitorContext arg) {
        return new BooleanLiteralExpressionT(n);
    }

    @Override
    public TypedExpression defaultMethod(Node n, VisitorContext context) {
        return new UnalteredTypedExpression(n);
    }

    @Override
    public TypedExpression visit(ObjectCreationExpr n, VisitorContext arg) {
        final Class<?> type = resolveType(n.getType());
        final Pair<List<TypedExpression>, List<Integer>> typedArgumentsResult =
                MethodResolutionUtils.getTypedArgumentsWithEmptyCollectionArgumentDetection(n.getArguments(), this, arg);
        if (!typedArgumentsResult.b.isEmpty()) {
            return new ObjectCreationExpressionT(
                    MethodResolutionUtils.coerceCorrectConstructorArguments(type, typedArgumentsResult.a, typedArgumentsResult.b),
                    type);
        } else {
            return new ObjectCreationExpressionT(typedArgumentsResult.a, type);
        }
    }

    @Override
    public TypedExpression visit(NullLiteralExpr n, VisitorContext arg) {
        return new UnalteredTypedExpression(n, NullType.class);
    }

    @Override
    public TypedExpression visit(ArrayAccessExpr n, VisitorContext arg) {
        TypedExpression name = n.getName().accept(this, arg);

        Optional<Type> type = name.getType();
        if(type.filter(ClassUtils::isCollection).isPresent()) {
            return new ListAccessExprT(name, n.getIndex(), type.get());
        }
        return new UnalteredTypedExpression(n, type.orElse(null));
    }

    @Override
    public TypedExpression visit(EnclosedExpr n, VisitorContext arg) {
        return n.getInner().accept(this, arg);
    }

    @Override
    public TypedExpression visit(CastExpr n, VisitorContext arg) {
        TypedExpression innerExpr = n.getExpression().accept(this, arg);
        return new CastExprT(innerExpr, resolveType(n.getType()));
    }

    @Override
    public TypedExpression visit(BigDecimalLiteralExpr n, VisitorContext arg) {
        return new BigDecimalConvertedExprT(new StringLiteralExpressionT(new StringLiteralExpr(n.getValue())));
    }

    @Override
    public TypedExpression visit(BigIntegerLiteralExpr n, VisitorContext arg) {
        return new BigIntegerConvertedExprT(new StringLiteralExpressionT(new StringLiteralExpr(n.getValue())));
    }

    @Override
    public TypedExpression visit(UnaryExpr n, VisitorContext arg) {
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

    @Override
    public TypedExpression visit(ListCreationLiteralExpression n, VisitorContext arg) {
        return new ListExprT(n);
    }

    @Override
    public TypedExpression visit(MapCreationLiteralExpression n, VisitorContext arg) {
        return new MapExprT(n);
    }

    private Class<?> resolveType(com.github.javaparser.ast.type.Type type) {
        return mvelCompilerContext.resolveType(type.asString());
    }
}

