/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.dmn.feel.codegen.feel11;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.BooleanLiteralExpr;
import com.github.javaparser.ast.expr.CastExpr;
import com.github.javaparser.ast.expr.ClassExpr;
import com.github.javaparser.ast.expr.ConditionalExpr;
import com.github.javaparser.ast.expr.EnclosedExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.LambdaExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.NullLiteralExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.expr.UnaryExpr;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.type.UnknownType;
import org.antlr.v4.runtime.ParserRuleContext;
import org.kie.dmn.feel.lang.CompositeType;
import org.kie.dmn.feel.lang.Type;
import org.kie.dmn.feel.lang.ast.InfixOpNode.InfixOperator;
import org.kie.dmn.feel.lang.ast.RangeNode;
import org.kie.dmn.feel.lang.ast.RangeNode.IntervalBoundary;
import org.kie.dmn.feel.lang.ast.UnaryTestNode.UnaryOperator;
import org.kie.dmn.feel.lang.impl.JavaBackedType;
import org.kie.dmn.feel.lang.impl.MapBackedType;
import org.kie.dmn.feel.lang.types.BuiltInType;
import org.kie.dmn.feel.parser.feel11.FEEL_1_1BaseVisitor;
import org.kie.dmn.feel.parser.feel11.FEEL_1_1Parser;
import org.kie.dmn.feel.parser.feel11.FEEL_1_1Parser.ContextEntryContext;
import org.kie.dmn.feel.parser.feel11.FEEL_1_1Parser.IterationContextsContext;
import org.kie.dmn.feel.parser.feel11.FEEL_1_1Parser.KeyNameContext;
import org.kie.dmn.feel.parser.feel11.FEEL_1_1Parser.NameRefContext;
import org.kie.dmn.feel.parser.feel11.ParserHelper;
import org.kie.dmn.feel.runtime.Range;
import org.kie.dmn.feel.runtime.UnaryTest;
import org.kie.dmn.feel.runtime.impl.RangeImpl;
import org.kie.dmn.feel.util.EvalHelper;

import static org.kie.dmn.feel.codegen.feel11.DirectCompilerResult.mergeFDs;

public class DirectCompilerVisitor extends FEEL_1_1BaseVisitor<DirectCompilerResult> {

    private static final Expression DASH_UNARY_TEST = JavaParser.parseExpression(org.kie.dmn.feel.lang.ast.DashNode.DashUnaryTest.class.getCanonicalName() + ".INSTANCE");
    private static final Expression DECIMAL_128 = JavaParser.parseExpression("java.math.MathContext.DECIMAL128");
    private static final Expression EMPTY_LIST = JavaParser.parseExpression("java.util.Collections.emptyList()");
    private static final Expression EMPTY_MAP = JavaParser.parseExpression("java.util.Collections.emptyMap()");
    // TODO as this is now compiled it might not be needed for this compilation strategy, just need the layer 0 of input Types, but to be checked.
    private ScopeHelper scopeHelper;
    private boolean replaceEqualForUnaryTest = false;

    private static class ScopeHelper {

        Deque<Map<String, Type>> stack;

        public ScopeHelper() {
            this.stack = new ArrayDeque<>();
            this.stack.push(new HashMap<>());
        }

        public void addTypes(Map<String, Type> inputTypes) {
            stack.peek().putAll(inputTypes);
        }

        public void addType(String name, Type type) {
            stack.peek().put(name,
                             type);
        }

        public void pushScope() {
            stack.push(new HashMap<>());
        }

        public void popScope() {
            stack.pop();
        }

        public Optional<Type> resolveType(String name) {
            return stack.stream()
                        .map(scope -> Optional.ofNullable(scope.get(name)))
                        .flatMap(o -> o.isPresent() ? Stream.of(o.get()) : Stream.empty())
                        .findFirst();
        }
    }

    public DirectCompilerVisitor(Map<String, Type> inputTypes) {
        this.scopeHelper = new ScopeHelper();
        this.scopeHelper.addTypes(inputTypes);
    }

    /**
     * DMN defines a special case where, unless the expressions are unary tests
     * or ranges, they need to be converted into an equality test unary expression.
     * This way, we have to compile and check the low level AST nodes to properly
     * deal with this case
     * 
     * @param replaceEqualForUnaryTest use `true` to obtain the behavior described.
     */
    public DirectCompilerVisitor(Map<String, Type> inputTypes, boolean replaceEqualForUnaryTest) {
        this(inputTypes);
        this.replaceEqualForUnaryTest = replaceEqualForUnaryTest;
    }

    @Override
    public DirectCompilerResult visitNumberLiteral(FEEL_1_1Parser.NumberLiteralContext ctx) {
        ObjectCreationExpr result = new ObjectCreationExpr();
        result.setType(JavaParser.parseClassOrInterfaceType(BigDecimal.class.getCanonicalName()));
        String originalText = ParserHelper.getOriginalText(ctx);
        result.addArgument(originalText);
        result.addArgument(DECIMAL_128);
        String constantName = "K_"+originalText;
        VariableDeclarator vd = new VariableDeclarator(JavaParser.parseClassOrInterfaceType(BigDecimal.class.getCanonicalName()), constantName);
        vd.setInitializer(result);
        FieldDeclaration fd = new FieldDeclaration();
        fd.setModifier(Modifier.PUBLIC, true);
        fd.setModifier(Modifier.STATIC, true);
        fd.setModifier(Modifier.FINAL, true);
        fd.addVariable(vd);
        return DirectCompilerResult.of(new NameExpr(constantName), BuiltInType.NUMBER, fd);
    }
    
    @Override
    public DirectCompilerResult visitBooleanLiteral(FEEL_1_1Parser.BooleanLiteralContext ctx) {
        Expression result = null;
        String literalText = ParserHelper.getOriginalText(ctx);
        // FEEL spec grammar rule 36. Boolean literal = "true" | "false" ;
        switch (literalText) {
            case "true":
                result = new BooleanLiteralExpr(true);
                break;
            case "false":
                result = new BooleanLiteralExpr(false);
                break;
            default:
                throw new IllegalArgumentException("Reached for a boolean literal but was: "+literalText);
        }
        return DirectCompilerResult.of(result, BuiltInType.BOOLEAN);
    }
    
    @Override
    public DirectCompilerResult visitSignedUnaryExpressionMinus(FEEL_1_1Parser.SignedUnaryExpressionMinusContext ctx) {
        DirectCompilerResult unaryExpr = visit(ctx.unaryExpression());
        if ( unaryExpr.resultType != BuiltInType.NUMBER ) {
            throw new IllegalArgumentException("signedunary should be only over a FEEL NUMBER (bigdecimal).");
        }
        // therefore, unaryExpr is a bigdecimal and operator is `-`.
        MethodCallExpr result = new MethodCallExpr(unaryExpr.getExpression(), "negate");
        return DirectCompilerResult.of(result, unaryExpr.resultType, unaryExpr.getFieldDeclarations() );
    }

    @Override
    public DirectCompilerResult visitSignedUnaryExpressionPlus(FEEL_1_1Parser.SignedUnaryExpressionPlusContext ctx) {
        DirectCompilerResult unaryExpr = visit(ctx.unaryExpressionNotPlusMinus());
        return unaryExpr;
    }

    @Override
    public DirectCompilerResult visitNullLiteral(FEEL_1_1Parser.NullLiteralContext ctx) {
        NullLiteralExpr result = new NullLiteralExpr();
        return DirectCompilerResult.of(result, BuiltInType.UNKNOWN);
    }

    @Override
    public DirectCompilerResult visitStringLiteral(FEEL_1_1Parser.StringLiteralContext ctx) {
        StringLiteralExpr expr = new StringLiteralExpr(EvalHelper.unescapeString(ParserHelper.getOriginalText(ctx)));
        return DirectCompilerResult.of(expr, BuiltInType.STRING);
    }
    
    @Override
    public DirectCompilerResult visitPrimaryParens(FEEL_1_1Parser.PrimaryParensContext ctx) {
        DirectCompilerResult expr = visit( ctx.expression() );
        EnclosedExpr result = new EnclosedExpr(expr.getExpression());
        return DirectCompilerResult.of(result, expr.resultType, expr.getFieldDeclarations());
    }

    @Override
    public DirectCompilerResult visitLogicalNegation(FEEL_1_1Parser.LogicalNegationContext ctx) {
        DirectCompilerResult unary = visit(ctx.unaryExpression());

        // FEEL spec Table 39: Semantics of negation
        // TODO this is actually not delegated to the builtin FEEL function not(), but not sure is really a problem for visitLogicalNegation.
        if (unary.resultType == BuiltInType.BOOLEAN) {
            return DirectCompilerResult.of(new UnaryExpr(unary.getExpression(), UnaryExpr.Operator.LOGICAL_COMPLEMENT), BuiltInType.BOOLEAN, unary.getFieldDeclarations());
        } else {
            return DirectCompilerResult.of(new NullLiteralExpr(), BuiltInType.UNKNOWN, unary.getFieldDeclarations());
        }
    }

    @Override
    public DirectCompilerResult visitPowExpression(FEEL_1_1Parser.PowExpressionContext ctx) {
        DirectCompilerResult left = visit(ctx.powerExpression());
        DirectCompilerResult right = visit(ctx.filterPathExpression());

        String opText = ctx.op.getText();
        InfixOperator op = InfixOperator.determineOperator(opText);
        if (op == InfixOperator.POW) {
            return visitPow(left, right);
        } else {
            throw new UnsupportedOperationException("this was a visitPowExpression but unrecognized op: " + opText); // parser problem.
        }
    }

    @Override
    public DirectCompilerResult visitMultExpression(FEEL_1_1Parser.MultExpressionContext ctx) {
        DirectCompilerResult left = visit(ctx.multiplicativeExpression());
        DirectCompilerResult right = visit(ctx.powerExpression());

        String opText = ctx.op.getText();
        InfixOperator op = InfixOperator.determineOperator(opText);
        if (op == InfixOperator.MULT) {
            return visitMult(left, right);
        } else if (op == InfixOperator.DIV) {
            return visitDiv(left, right);
        } else {
            throw new UnsupportedOperationException("this was a visitMultExpression but unrecognized op: " + opText); // parser problem.
        }
    }

    @Override
    public DirectCompilerResult visitAddExpression(FEEL_1_1Parser.AddExpressionContext ctx) {
        DirectCompilerResult left = visit( ctx.additiveExpression() );
        DirectCompilerResult right = visit( ctx.multiplicativeExpression() );
        
        String opText = ctx.op.getText();
        InfixOperator op = InfixOperator.determineOperator(opText);
        if ( op == InfixOperator.ADD ) {
            return visitAdd(left, right);
        } else if ( op == InfixOperator.SUB ) {
            return visitSub(left, right);
        } else {
            throw new UnsupportedOperationException("this was a visitAddExpression but unrecognized op: " + opText); // parser problem.
        }
    }
    
    /**
     * PLEASE NOTICE:
     * operation may perform a check for null-literal values, but might need this utility for runtime purposes.
     */
    private Expression groundToNullIfAnyIsNull(Expression originalOperation, Expression... arguments) {
        // Q: What is heavier, checking a list of arguments each one is not null, or just doing the operation on the arguments and try-catch the NPE, please?
        // A: raising exceptions is a lot heavier
        BinaryExpr nullChecks = Stream.of(arguments)
                                      .map(e -> new BinaryExpr(e, new NullLiteralExpr(), BinaryExpr.Operator.EQUALS))
                                      .reduce( (x, y) -> new BinaryExpr(x, y, BinaryExpr.Operator.OR) )
                                      .get();
        
        return new ConditionalExpr(new EnclosedExpr(nullChecks), new NullLiteralExpr(), originalOperation);
    }
    
    private DirectCompilerResult visitAdd( DirectCompilerResult left, DirectCompilerResult right ) {
        if (left.getExpression() instanceof NullLiteralExpr || right.getExpression() instanceof NullLiteralExpr) {
            // optimization: if either left or right is a null literal, just null
            return DirectCompilerResult.of(new NullLiteralExpr(), BuiltInType.UNKNOWN, DirectCompilerResult.mergeFDs(left, right));
        } else if ( left.resultType == BuiltInType.STRING && right.resultType == BuiltInType.STRING ) {
            if (left.getExpression() instanceof StringLiteralExpr && right.getExpression() instanceof StringLiteralExpr) {
                BinaryExpr plusCall = new BinaryExpr(left.getExpression(), right.getExpression(), BinaryExpr.Operator.PLUS);
                Expression result = groundToNullIfAnyIsNull(plusCall, left.getExpression(), right.getExpression());
                return DirectCompilerResult.of(result, BuiltInType.STRING, DirectCompilerResult.mergeFDs(left, right));
            } else {
                Expression newStringBuilderExpr = JavaParser.parseExpression("new StringBuilder()");
                MethodCallExpr appendL = new MethodCallExpr(newStringBuilderExpr, "append");
                appendL.addArgument(left.getExpression());
                MethodCallExpr appendR = new MethodCallExpr(appendL, "append");
                appendR.addArgument(right.getExpression());
                Expression result = new MethodCallExpr(appendR, "toString");
                return DirectCompilerResult.of(result, BuiltInType.STRING, DirectCompilerResult.mergeFDs(left, right));
            }
        } else if ( left.resultType == BuiltInType.NUMBER && right.resultType == BuiltInType.NUMBER ) {
            MethodCallExpr addCall = new MethodCallExpr(left.getExpression(), "add");
            addCall.addArgument(right.getExpression());
            addCall.addArgument(DECIMAL_128);
            Expression result = groundToNullIfAnyIsNull(addCall, left.getExpression(), right.getExpression());
            return DirectCompilerResult.of(result, BuiltInType.NUMBER, DirectCompilerResult.mergeFDs(left, right));
        } else {
            // TODO temporary support strategy; to avoid the below, will require to match all the possible conbination in InfixOpNode#add
            MethodCallExpr addCall = new MethodCallExpr(null, "add");
            addCall.addArgument(left.getExpression());
            addCall.addArgument(right.getExpression());
            Expression result = groundToNullIfAnyIsNull(addCall, left.getExpression(), right.getExpression());
            return DirectCompilerResult.of(result, BuiltInType.UNKNOWN, DirectCompilerResult.mergeFDs(left, right));
        }
    }
    
    private DirectCompilerResult visitSub( DirectCompilerResult left, DirectCompilerResult right ) {
        if (left.getExpression() instanceof NullLiteralExpr || right.getExpression() instanceof NullLiteralExpr) {
            // optimization: if either left or right is a null literal, just null
            return DirectCompilerResult.of(new NullLiteralExpr(), BuiltInType.UNKNOWN, DirectCompilerResult.mergeFDs(left, right));
        } else if ( left.resultType == BuiltInType.STRING && right.resultType == BuiltInType.STRING ) {
            // DMN spec Table 45
            // Subtraction is undefined.
            // TODO incosistent when FEEL is in evaluation mode (in contrast to this compilation mode),
            // for now is more important to check the actual java code produced
            BinaryExpr postFixMinus = new BinaryExpr(left.getExpression(), new StringLiteralExpr("-"), BinaryExpr.Operator.PLUS);
            BinaryExpr plusCall = new BinaryExpr(postFixMinus, right.getExpression(), BinaryExpr.Operator.PLUS);
            Expression result = groundToNullIfAnyIsNull(plusCall, left.getExpression(), right.getExpression());
            return DirectCompilerResult.of(result, BuiltInType.STRING, DirectCompilerResult.mergeFDs(left, right));
        } else if ( left.resultType == BuiltInType.NUMBER && right.resultType == BuiltInType.NUMBER ) {
            MethodCallExpr subtractCall = new MethodCallExpr(left.getExpression(), "subtract");
            subtractCall.addArgument(right.getExpression());
            subtractCall.addArgument(DECIMAL_128);
            Expression result = groundToNullIfAnyIsNull(subtractCall, left.getExpression(), right.getExpression());
            return DirectCompilerResult.of(result, BuiltInType.NUMBER, DirectCompilerResult.mergeFDs(left, right));
        } else {
            // TODO temporary support strategy; to avoid the below, will require to match all the possible conbination in InfixOpNode#sub
            MethodCallExpr addCall = new MethodCallExpr(null, "sub");
            addCall.addArgument(left.getExpression());
            addCall.addArgument(right.getExpression());
            Expression result = groundToNullIfAnyIsNull(addCall, left.getExpression(), right.getExpression());
            return DirectCompilerResult.of(result, BuiltInType.UNKNOWN, DirectCompilerResult.mergeFDs(left, right));
        }
    }

    private DirectCompilerResult visitMult(DirectCompilerResult left, DirectCompilerResult right) {
        if (left.getExpression() instanceof NullLiteralExpr || right.getExpression() instanceof NullLiteralExpr) {
            // optimization: if either left or right is a null literal, just null
            return DirectCompilerResult.of(new NullLiteralExpr(), BuiltInType.UNKNOWN, DirectCompilerResult.mergeFDs(left, right));
        } else if (left.resultType == BuiltInType.NUMBER && right.resultType == BuiltInType.NUMBER) {
            MethodCallExpr addCall = new MethodCallExpr(left.getExpression(), "multiply");
            addCall.addArgument(right.getExpression());
            addCall.addArgument(DECIMAL_128);
            Expression result = groundToNullIfAnyIsNull(addCall, left.getExpression(), right.getExpression());
            return DirectCompilerResult.of(result, BuiltInType.NUMBER, DirectCompilerResult.mergeFDs(left, right));
        } else {
            // TODO temporary support strategy:
            MethodCallExpr addCall = new MethodCallExpr(null, "mult");
            addCall.addArgument(left.getExpression());
            addCall.addArgument(right.getExpression());
            Expression result = groundToNullIfAnyIsNull(addCall, left.getExpression(), right.getExpression());
            return DirectCompilerResult.of(result, BuiltInType.UNKNOWN, DirectCompilerResult.mergeFDs(left, right));
        }
    }

    private DirectCompilerResult visitDiv(DirectCompilerResult left, DirectCompilerResult right) {
        if (left.getExpression() instanceof NullLiteralExpr || right.getExpression() instanceof NullLiteralExpr) {
            // optimization: if either left or right is a null literal, just null
            return DirectCompilerResult.of(new NullLiteralExpr(), BuiltInType.UNKNOWN, DirectCompilerResult.mergeFDs(left, right));
        } else if (left.resultType == BuiltInType.NUMBER && right.resultType == BuiltInType.NUMBER) {
            MethodCallExpr subtractCall = new MethodCallExpr(left.getExpression(), "divide");
            subtractCall.addArgument(right.getExpression());
            subtractCall.addArgument(DECIMAL_128);
            Expression result = groundToNullIfAnyIsNull(subtractCall, left.getExpression(), right.getExpression());
            return DirectCompilerResult.of(result, BuiltInType.NUMBER, DirectCompilerResult.mergeFDs(left, right));
        } else {
            // TODO temporary support strategy:
            MethodCallExpr addCall = new MethodCallExpr(null, "div");
            addCall.addArgument(left.getExpression());
            addCall.addArgument(right.getExpression());
            Expression result = groundToNullIfAnyIsNull(addCall, left.getExpression(), right.getExpression());
            return DirectCompilerResult.of(result, BuiltInType.UNKNOWN, DirectCompilerResult.mergeFDs(left, right));
        }
    }

    private DirectCompilerResult visitPow(DirectCompilerResult left, DirectCompilerResult right) {
        if (left.getExpression() instanceof NullLiteralExpr || right.getExpression() instanceof NullLiteralExpr) {
            // optimization: if either left or right is a null literal, just null
            return DirectCompilerResult.of(new NullLiteralExpr(), BuiltInType.UNKNOWN, DirectCompilerResult.mergeFDs(left, right));
        } else if (left.resultType == BuiltInType.NUMBER && right.resultType == BuiltInType.NUMBER) {
            MethodCallExpr subtractCall = new MethodCallExpr(left.getExpression(), "pow");
            subtractCall.addArgument(new MethodCallExpr(right.getExpression(), "intValue"));
            subtractCall.addArgument(DECIMAL_128);
            Expression result = groundToNullIfAnyIsNull(subtractCall, left.getExpression(), right.getExpression());
            return DirectCompilerResult.of(result, BuiltInType.NUMBER, DirectCompilerResult.mergeFDs(left, right));
        } else {
            throw new UnsupportedOperationException("this was a visitPow but either left or right is not a number"); // parser problem.
        }
    }

    @Override
    public DirectCompilerResult visitRelExpressionBetween(FEEL_1_1Parser.RelExpressionBetweenContext ctx) {
        DirectCompilerResult value = visit(ctx.val);
        DirectCompilerResult start = visit(ctx.start);
        DirectCompilerResult end = visit(ctx.end);
        MethodCallExpr betweenCall = new MethodCallExpr(null, "between");
        betweenCall.addArgument(value.getExpression());
        betweenCall.addArgument(start.getExpression());
        betweenCall.addArgument(end.getExpression());
        Expression result = groundToNullIfAnyIsNull(betweenCall, value.getExpression(), start.getExpression(), end.getExpression());
        return DirectCompilerResult.of(result, BuiltInType.BOOLEAN).withFD(value).withFD(start).withFD(end);
    }

    /**
     * NOTE: technically this rule of the grammar does not have an equivalent Java expression (or a valid FEEL expression) per-se.
     * Using here as assuming if this grammar rule trigger, it is intended as a List, either to be returned, or re-used internally in this visitor.
     */
    @Override
    public DirectCompilerResult visitExpressionList(FEEL_1_1Parser.ExpressionListContext ctx) {
        List<DirectCompilerResult> exprs = new ArrayList<>();
        for (int i = 0; i < ctx.getChildCount(); i++) {
            if (ctx.getChild(i) instanceof FEEL_1_1Parser.ExpressionContext) {
                FEEL_1_1Parser.ExpressionContext childCtx = (FEEL_1_1Parser.ExpressionContext) ctx.getChild(i);
                DirectCompilerResult child = visit(childCtx);

                if (!replaceEqualForUnaryTest) {
                    // we are NOT compiling unary test, so we continue as-is
                    exprs.add(child);
                } else {
                    if (child.resultType == BuiltInType.UNARY_TEST) {
                        // is already a unary test, so we can add it as-is
                        exprs.add(child);
                    } else if (child.resultType == BuiltInType.RANGE) {
                        // being a range, need the `in` operator.
                        DirectCompilerResult replaced = createUnaryTestExpression(childCtx, child, UnaryOperator.IN);
                        exprs.add(replaced);
                    } else {
                        // implied a unarytest for the `=` equal operator.
                        DirectCompilerResult replaced = createUnaryTestExpression(childCtx, child, UnaryOperator.EQ);
                        exprs.add(replaced);
                    }
                }
            }
        }
        MethodCallExpr list = new MethodCallExpr(new NameExpr(Arrays.class.getCanonicalName()), "asList");
        exprs.stream().map(DirectCompilerResult::getExpression).forEach(list::addArgument);
        return DirectCompilerResult.of(list, BuiltInType.LIST, DirectCompilerResult.mergeFDs(exprs.toArray(new DirectCompilerResult[]{})));
    }

//    @Override
//    public DirectCompilerResult visitRelExpressionValueList(FEEL_1_1Parser.RelExpressionValueListContext ctx) {
//        throw new UnsupportedOperationException("TODO"); // TODO
//    }

    @Override
    public DirectCompilerResult visitInterval(FEEL_1_1Parser.IntervalContext ctx) {
        DirectCompilerResult start = visit(ctx.start);
        DirectCompilerResult end = visit(ctx.end);
        RangeNode.IntervalBoundary low = ctx.low.getText().equals("[") ? RangeNode.IntervalBoundary.CLOSED : RangeNode.IntervalBoundary.OPEN;
        RangeNode.IntervalBoundary up = ctx.up.getText().equals("]") ? RangeNode.IntervalBoundary.CLOSED : RangeNode.IntervalBoundary.OPEN;

        Expression BOUNDARY_CLOSED = JavaParser.parseExpression(org.kie.dmn.feel.runtime.Range.RangeBoundary.class.getCanonicalName() + ".CLOSED");
        Expression BOUNDARY_OPEN = JavaParser.parseExpression(org.kie.dmn.feel.runtime.Range.RangeBoundary.class.getCanonicalName() + ".OPEN");
        
        String originalText = ParserHelper.getOriginalText(ctx);

        ObjectCreationExpr initializer = new ObjectCreationExpr();
        initializer.setType(JavaParser.parseClassOrInterfaceType(RangeImpl.class.getCanonicalName()));
        initializer.addArgument(low == IntervalBoundary.CLOSED ? BOUNDARY_CLOSED : BOUNDARY_OPEN);
        initializer.addArgument(start.getExpression());
        initializer.addArgument(end.getExpression());
        initializer.addArgument(up == IntervalBoundary.CLOSED ? BOUNDARY_CLOSED : BOUNDARY_OPEN);
        String constantName = "RANGE_" + CodegenStringUtil.escapeIdentifier(originalText);
        VariableDeclarator vd = new VariableDeclarator(JavaParser.parseClassOrInterfaceType(Range.class.getCanonicalName()), constantName);
        vd.setInitializer(initializer);
        FieldDeclaration fd = new FieldDeclaration();
        fd.setModifier(Modifier.PUBLIC, true);
        fd.setModifier(Modifier.STATIC, true);
        fd.setModifier(Modifier.FINAL, true);
        fd.addVariable(vd);

        fd.setJavadocComment(" FEEL range: " + originalText + " ");

        DirectCompilerResult directCompilerResult = DirectCompilerResult.of(new NameExpr(constantName), BuiltInType.RANGE, DirectCompilerResult.mergeFDs(start, end));
        directCompilerResult.addFieldDesclaration(fd);
        return directCompilerResult;
    }

    @Override
    public DirectCompilerResult visitPositiveUnaryTestIneq(FEEL_1_1Parser.PositiveUnaryTestIneqContext ctx) {
        DirectCompilerResult endpoint = visit(ctx.endpoint());
        String opText = ctx.op.getText();
        UnaryOperator op = UnaryOperator.determineOperator(opText);

        return createUnaryTestExpression(ctx, endpoint, op);
    }

    /**
     * Create a DirectCompilerResult for an equivalent expression representing a Unary test.
     * That means the resulting expression is the name of the unary test,
     * which is referring to a FieldDeclaration, for a class field member using said name, of type UnaryTest and as value a lambda expression of a unarytest
     * 
     * @param ctx mainly used to retrieve original text information (used to build the FieldDeclaration javadoc of the original FEEL text representation)
     * @param endpoint the right of the unary test
     * @param op the operator of the unarytest
     */
    private DirectCompilerResult createUnaryTestExpression(ParserRuleContext ctx, DirectCompilerResult endpoint, UnaryOperator op) {
        String originalText = ParserHelper.getOriginalText(ctx);

        LambdaExpr initializer = new LambdaExpr();
        initializer.setEnclosingParameters(true);
        initializer.addParameter(new Parameter(new UnknownType(), "feelExprCtx"));
        initializer.addParameter(new Parameter(new UnknownType(), "left"));
        Statement lambdaBody = null;
        switch (op) {
            case EQ:
            {
                MethodCallExpr expression = new MethodCallExpr(null, "eq");
                expression.addArgument(new NameExpr("left"));
                expression.addArgument(endpoint.getExpression());
                lambdaBody = new ExpressionStmt(expression);
            }
                break;
            case GT:
            {
                MethodCallExpr expression = new MethodCallExpr(null, "gt");
                expression.addArgument(new NameExpr("left"));
                expression.addArgument(endpoint.getExpression());
                lambdaBody = new ExpressionStmt(expression);
            }
                break;
            case GTE:
            {
                MethodCallExpr expression = new MethodCallExpr(null, "gte");
                expression.addArgument(new NameExpr("left"));
                expression.addArgument(endpoint.getExpression());
                lambdaBody = new ExpressionStmt(expression);
            }
                break;
            case IN:
            {
                MethodCallExpr expression = new MethodCallExpr(endpoint.getExpression(), "includes");
                expression.addArgument(new NameExpr("left"));
                lambdaBody = new ExpressionStmt(expression);
            }
                break;
            case LT:
            {
                MethodCallExpr expression = new MethodCallExpr(null, "lt");
                expression.addArgument(new NameExpr("left"));
                expression.addArgument(endpoint.getExpression());
                lambdaBody = new ExpressionStmt(expression);
            }
                break;
            case LTE:
            {
                MethodCallExpr expression = new MethodCallExpr(null, "lte");
                expression.addArgument(new NameExpr("left"));
                expression.addArgument(endpoint.getExpression());
                lambdaBody = new ExpressionStmt(expression);
            }
                break;
            case NE:
            {
                MethodCallExpr expression = new MethodCallExpr(null, "ne");
                expression.addArgument(new NameExpr("left"));
                expression.addArgument(endpoint.getExpression());
                lambdaBody = new ExpressionStmt(expression);
            }
                break;
            case NOT: {
                MethodCallExpr expression = new MethodCallExpr(null, "not");
                expression.addArgument(new NameExpr("left"));
                expression.addArgument(endpoint.getExpression());
                lambdaBody = new ExpressionStmt(expression);
            }
                break;
            default:
                throw new UnsupportedOperationException("Unable to determine operator of unary test");
        }
        initializer.setBody(lambdaBody);
        String constantName = "UT_" + CodegenStringUtil.escapeIdentifier(originalText);
        VariableDeclarator vd = new VariableDeclarator(JavaParser.parseClassOrInterfaceType(UnaryTest.class.getCanonicalName()), constantName);
        vd.setInitializer(initializer);
        FieldDeclaration fd = new FieldDeclaration();
        fd.setModifier(Modifier.PUBLIC, true);
        fd.setModifier(Modifier.STATIC, true);
        fd.setModifier(Modifier.FINAL, true);
        fd.addVariable(vd);

        fd.setJavadocComment(" FEEL unary test: " + originalText + " ");

        DirectCompilerResult directCompilerResult = DirectCompilerResult.of(new NameExpr(constantName), BuiltInType.UNARY_TEST, endpoint.getFieldDeclarations());
        directCompilerResult.addFieldDesclaration(fd);
        return directCompilerResult;
    }

//    @Override
//    public DirectCompilerResult visitSimpleUnaryTests(FEEL_1_1Parser.SimpleUnaryTestsContext ctx) {
//        throw new UnsupportedOperationException("TODO"); // TODO
//    }
//
//    @Override
//    public DirectCompilerResult visitRelExpressionTestList(FEEL_1_1Parser.RelExpressionTestListContext ctx) {
//        throw new UnsupportedOperationException("TODO"); // TODO
//    }
//    
//    @Override
//    public DirectCompilerResult visitRelExpressionValue(RelExpressionValueContext ctx) {
//        throw new UnsupportedOperationException("TODO"); // TODO
//    }

    @Override
    public DirectCompilerResult visitPositiveUnaryTestNull(FEEL_1_1Parser.PositiveUnaryTestNullContext ctx) {
        throw new UnsupportedOperationException("TODO"); // TODO
    }

    @Override
    public DirectCompilerResult visitPositiveUnaryTestDash(FEEL_1_1Parser.PositiveUnaryTestDashContext ctx) {
        return DirectCompilerResult.of(DASH_UNARY_TEST, BuiltInType.UNARY_TEST);
    }

    @Override
    public DirectCompilerResult visitCompExpression(FEEL_1_1Parser.CompExpressionContext ctx) {
        DirectCompilerResult left = visit(ctx.left);
        DirectCompilerResult right = visit(ctx.right);

        String opText = ctx.op.getText();
        InfixOperator op = InfixOperator.determineOperator(opText);
        String methodName;
        switch (op) {
            case LTE:
                methodName = "lte";
                break;
            case LT:
                methodName = "lt";
                break;
            case GTE:
                methodName = "gte";
                break;
            case GT:
                methodName = "gt";
                break;
            case EQ:
                methodName = "eq";
                break;
            case NE:
                methodName = "ne";
                break;
            default:
                throw new UnsupportedOperationException("this was a visitCompExpression but unrecognized op: " + opText); // parser problem.
        }
        MethodCallExpr result = new MethodCallExpr(null, methodName);
        result.addArgument(left.getExpression());
        result.addArgument(right.getExpression());
        return DirectCompilerResult.of(result, BuiltInType.BOOLEAN).withFD(left).withFD(right);
    }

    @Override
    public DirectCompilerResult visitCondOr(FEEL_1_1Parser.CondOrContext ctx) {
        DirectCompilerResult left = visit( ctx.left );
        DirectCompilerResult right = visit( ctx.right );
        MethodCallExpr result = new MethodCallExpr(null, "or");
        result.addArgument(left.getExpression());
        result.addArgument(right.getExpression());
        return DirectCompilerResult.of(result, BuiltInType.BOOLEAN).withFD(left).withFD(right);
    }

    @Override
    public DirectCompilerResult visitCondAnd(FEEL_1_1Parser.CondAndContext ctx) {
        DirectCompilerResult left = visit( ctx.left );
        DirectCompilerResult right = visit( ctx.right );
        MethodCallExpr result = new MethodCallExpr(null, "and");
        result.addArgument(left.getExpression());
        result.addArgument(right.getExpression());
        return DirectCompilerResult.of(result, BuiltInType.BOOLEAN).withFD(left).withFD(right);
    }

    @Override
    public DirectCompilerResult visitList(FEEL_1_1Parser.ListContext ctx) {
        if (ctx.expressionList() == null) {
            // empty list -> children are [ ]
            return DirectCompilerResult.of(EMPTY_LIST, BuiltInType.LIST);
        } else {
            // returns actual list
            return visit(ctx.expressionList());
        }
    }

    @Override
    public DirectCompilerResult visitNameDefinition(FEEL_1_1Parser.NameDefinitionContext ctx) {
        // this is used by the For loop for the variable name of the iteration contexts.
        StringLiteralExpr expr = new StringLiteralExpr(EvalHelper.normalizeVariableName(ParserHelper.getOriginalText(ctx)));
        return DirectCompilerResult.of(expr, BuiltInType.STRING);
    }
//
//    @Override
//    public DirectCompilerResult visitContextEntry(FEEL_1_1Parser.ContextEntryContext ctx) {
//        throw new UnsupportedOperationException("TODO"); // TODO I don't think this will be needed for this visitor.
//    }

    @Override
    public DirectCompilerResult visitKeyString(FEEL_1_1Parser.KeyStringContext ctx) {
        // Need to repeat the same impl as visitStringLiteral because is an ANTLR terminal node, so cannot delegate.
        StringLiteralExpr expr = new StringLiteralExpr(EvalHelper.unescapeString(ParserHelper.getOriginalText(ctx)));
        return DirectCompilerResult.of(expr, BuiltInType.STRING);
    }

    @Override
    public DirectCompilerResult visitKeyName(KeyNameContext ctx) {
        StringLiteralExpr expr = new StringLiteralExpr(EvalHelper.normalizeVariableName(ParserHelper.getOriginalText(ctx)));
        return DirectCompilerResult.of(expr, BuiltInType.STRING);
    }

    @Override
    public DirectCompilerResult visitContextEntries(FEEL_1_1Parser.ContextEntriesContext ctx) {
        MethodCallExpr openContextCall = new MethodCallExpr(new NameExpr(CompiledFEELSupport.class.getSimpleName()), "openContext");
        openContextCall.addArgument(new NameExpr("feelExprCtx"));

        scopeHelper.pushScope();
        MapBackedType returnType = new MapBackedType();
        Expression chainedCallScope = openContextCall;

        List<DirectCompilerResult> collectedEntryValues = new ArrayList<>();
        for (ContextEntryContext ceCtx : ctx.contextEntry()) {
            DirectCompilerResult key = visit(ceCtx.key());
            if (key.resultType != BuiltInType.STRING) {
                throw new IllegalArgumentException("a Context Entry Key must be a valid FEEL String type");
            }
            String keyText = ((StringLiteralExpr) key.getExpression()).getValue();
            DirectCompilerResult entryValueResult = visit(ceCtx.expression());
            collectedEntryValues.add(entryValueResult);
            MethodCallExpr setEntryContextCall = new MethodCallExpr(chainedCallScope, "setEntry");
            setEntryContextCall.addArgument(new StringLiteralExpr(keyText));
            setEntryContextCall.addArgument(entryValueResult.getExpression());
            chainedCallScope = setEntryContextCall;

            scopeHelper.addType(keyText, entryValueResult.resultType);
            returnType.addField(keyText, entryValueResult.resultType);
        }

        MethodCallExpr closeContextCall = new MethodCallExpr(chainedCallScope, "closeContext");
        scopeHelper.popScope();
        return DirectCompilerResult.of(closeContextCall, returnType, DirectCompilerResult.mergeFDs(collectedEntryValues.toArray(new DirectCompilerResult[]{})));
    }

    @Override
    public DirectCompilerResult visitContext(FEEL_1_1Parser.ContextContext ctx) {
        if (ctx.contextEntries() == null) {
            return DirectCompilerResult.of(EMPTY_MAP, BuiltInType.CONTEXT);
        } else {
            return visit(ctx.contextEntries());
        }
    }

//    @Override
//    public DirectCompilerResult visitFormalParameters(FEEL_1_1Parser.FormalParametersContext ctx) {
//        throw new UnsupportedOperationException("TODO"); // TODO
//    }
//
//    @Override
//    public DirectCompilerResult visitFunctionDefinition(FEEL_1_1Parser.FunctionDefinitionContext ctx) {
//        throw new UnsupportedOperationException("TODO"); // TODO
//    }
//
//    @Override
//    public DirectCompilerResult visitIterationContext(FEEL_1_1Parser.IterationContextContext ctx) {
    //        throw new UnsupportedOperationException("TODO"); // TODO won't be needed
//    }
//
//    @Override
//    public DirectCompilerResult visitIterationContexts(FEEL_1_1Parser.IterationContextsContext ctx) {
    //        throw new UnsupportedOperationException("TODO"); // TODO won't be needed
//    }

    @Override
    public DirectCompilerResult visitForExpression(FEEL_1_1Parser.ForExpressionContext ctx) {
        Set<FieldDeclaration> fds = new HashSet<>();
        MethodCallExpr forCall = new MethodCallExpr(new NameExpr(CompiledFEELSupport.class.getSimpleName()), "ffor");
        forCall.addArgument(new NameExpr("feelExprCtx"));
        Expression curForCallTail = forCall;
        IterationContextsContext iCtxs = ctx.iterationContexts();
        for (FEEL_1_1Parser.IterationContextContext ic : iCtxs.iterationContext()) {
            DirectCompilerResult name = visit(ic.nameDefinition());
            DirectCompilerResult expr = visit(ic.expression().get(0));
            fds.addAll(name.getFieldDeclarations());
            fds.addAll(expr.getFieldDeclarations());
            if (ic.expression().size() == 1) {
                MethodCallExpr filterWithCall = new MethodCallExpr(curForCallTail, "with");
                Expression nameParam = anonFunctionEvaluationContext2Object(name.getExpression());
                Expression exprParam = anonFunctionEvaluationContext2Object(expr.getExpression());
                filterWithCall.addArgument(nameParam);
                filterWithCall.addArgument(exprParam);
                curForCallTail = filterWithCall;
            } else {
                DirectCompilerResult rangeEndExpr = visit(ic.expression().get(1));
                fds.addAll(rangeEndExpr.getFieldDeclarations());
                MethodCallExpr filterWithCall = new MethodCallExpr(curForCallTail, "with");
                Expression nameParam = anonFunctionEvaluationContext2Object(name.getExpression());
                Expression exprParam = anonFunctionEvaluationContext2Object(expr.getExpression());
                Expression rangeEndExprParam = anonFunctionEvaluationContext2Object(rangeEndExpr.getExpression());
                filterWithCall.addArgument(nameParam);
                filterWithCall.addArgument(exprParam);
                filterWithCall.addArgument(rangeEndExprParam);
                curForCallTail = filterWithCall;
            }
        }
        DirectCompilerResult expr = visit(ctx.expression());
        fds.addAll(expr.getFieldDeclarations());
        MethodCallExpr returnCall = new MethodCallExpr(curForCallTail, "rreturn");
        Expression returnParam = anonFunctionEvaluationContext2Object(expr.getExpression());
        returnCall.addArgument(returnParam);
        return DirectCompilerResult.of(returnCall, expr.resultType, fds);
    }

    @Override
    public DirectCompilerResult visitQualifiedName(FEEL_1_1Parser.QualifiedNameContext ctx) {
        List<NameRefContext> parts = ctx.nameRef();
        DirectCompilerResult nameRef0 = visit(parts.get(0)); // previously qualifiedName visitor "ingest"-ed directly by calling directly "visitNameRef"
        Type typeCursor = nameRef0.resultType;
        Expression exprCursor = nameRef0.getExpression();
        for (NameRefContext acc : parts.subList(1, parts.size())) {
            String accText = ParserHelper.getOriginalText(acc);
            if (typeCursor instanceof CompositeType) {
                CompositeType compositeType = (CompositeType) typeCursor;

                // setting next typeCursor
                typeCursor = compositeType.getFields().get(accText);

                // setting next exprCursor
                if (compositeType instanceof MapBackedType) {
                    CastExpr castExpr = new CastExpr(JavaParser.parseType(Map.class.getCanonicalName()), exprCursor);
                    EnclosedExpr enclosedExpr = new EnclosedExpr(castExpr);
                    MethodCallExpr getExpr = new MethodCallExpr(enclosedExpr, "get");
                    getExpr.addArgument(new StringLiteralExpr(accText));
                    exprCursor = getExpr;
                } else if (compositeType instanceof JavaBackedType) {
                    JavaBackedType javaBackedType = (JavaBackedType) compositeType;
                    Method accessor = EvalHelper.getGenericAccessor(javaBackedType.getWrapped(), accText);
                    CastExpr castExpr = new CastExpr(JavaParser.parseType(javaBackedType.getWrapped().getCanonicalName()), exprCursor);
                    EnclosedExpr enclosedExpr = new EnclosedExpr(castExpr);
                    exprCursor = new MethodCallExpr(enclosedExpr, accessor.getName());
                } else {
                    throw new UnsupportedOperationException("A Composite type is either MapBacked or JavaBAcked");
                }
            } else {
                //  degraded mode, or accessing fields of DATE etc.
                DirectCompilerResult telescope = telescopePathAccessor(DirectCompilerResult.of(exprCursor, typeCursor), Arrays.asList(accText));
                exprCursor = telescope.getExpression();
                typeCursor = telescope.resultType;
            }
        }
        return DirectCompilerResult.of(exprCursor, typeCursor);
    }

    @Override
    public DirectCompilerResult visitIfExpression(FEEL_1_1Parser.IfExpressionContext ctx) {
        DirectCompilerResult c = visit( ctx.c );
        DirectCompilerResult t = visit( ctx.t );
        DirectCompilerResult e = visit( ctx.e );
        
//        String snippet = "(e1 instanceof Boolean) ? ((boolean) e1 ? e2 : e3 ) : "+CompiledFEELUtils.class.getCanonicalName()+".conditionWasNotBoolean(feelExprCtx)";
//        
//        Expression parsed = JavaParser.parseExpression(snippet);
//        for ( NameExpr ne : parsed.getChildNodesByType(NameExpr.class) ) {
//            switch (ne.getNameAsString()) {
//                case "e1":
//                    ne.replace(ne, c.expression);
//                    break;
//                case "e2":
//                    ne.replace(ne, t.expression);
//                    break;
//                case "e3":
//                    ne.replace(ne, e.expression);
//                    break;
//            }
//        }
//        return DirectCompilerResult.of(parsed, BuiltInType.UNKNOWN);
        
        Expression errorExpression = JavaParser.parseExpression(CompiledFEELSupport.class.getSimpleName() + ".conditionWasNotBoolean(feelExprCtx)");
        MethodCallExpr castC = new MethodCallExpr(new ClassExpr(JavaParser.parseType(Boolean.class.getSimpleName())), "cast");
        castC.addArgument(new EnclosedExpr(c.getExpression()));
        Expression safeInternal = new ConditionalExpr(castC, new EnclosedExpr(t.getExpression()), new EnclosedExpr(e.getExpression()));
        safeInternal = new EnclosedExpr(safeInternal);
        MethodCallExpr instanceOfBoolean = new MethodCallExpr(new ClassExpr(JavaParser.parseType(Boolean.class.getSimpleName())), "isInstance");
        instanceOfBoolean.addArgument(new EnclosedExpr(c.getExpression()));
        ConditionalExpr result = new ConditionalExpr(instanceOfBoolean, safeInternal, errorExpression);
        return DirectCompilerResult.of(result, BuiltInType.UNKNOWN, DirectCompilerResult.mergeFDs(c, t, e));
    }

//    @Override
//    public DirectCompilerResult visitQuantExprSome(FEEL_1_1Parser.QuantExprSomeContext ctx) {
//        throw new UnsupportedOperationException("TODO"); // TODO
//    }
//
//    @Override
//    public DirectCompilerResult visitQuantExprEvery(FEEL_1_1Parser.QuantExprEveryContext ctx) {
//        throw new UnsupportedOperationException("TODO"); // TODO
//    }

    @Override
    public DirectCompilerResult visitNameRef(FEEL_1_1Parser.NameRefContext ctx) {
        String nameRefText = ParserHelper.getOriginalText(ctx);
        Type type = scopeHelper.resolveType(nameRefText).orElse(BuiltInType.UNKNOWN);
        NameExpr scope = new NameExpr("feelExprCtx");
        MethodCallExpr getFromScope = new MethodCallExpr(scope, "getValue");
        getFromScope.addArgument(new StringLiteralExpr(nameRefText));
        return DirectCompilerResult.of(getFromScope, type);
    }

//    @Override
//    public DirectCompilerResult visitPositionalParameters(FEEL_1_1Parser.PositionalParametersContext ctx) {
//        throw new UnsupportedOperationException("TODO"); // TODO
//    }
//
//    @Override
//    public DirectCompilerResult visitNamedParameter(FEEL_1_1Parser.NamedParameterContext ctx) {
//        throw new UnsupportedOperationException("TODO"); // TODO
//    }
//
//    @Override
//    public DirectCompilerResult visitNamedParameters(FEEL_1_1Parser.NamedParametersContext ctx) {
//        throw new UnsupportedOperationException("TODO"); // TODO
//    }
//
//    @Override
//    public DirectCompilerResult visitParametersEmpty(FEEL_1_1Parser.ParametersEmptyContext ctx) {
//        throw new UnsupportedOperationException("TODO"); // TODO
//    }
//
//    @Override
//    public DirectCompilerResult visitParametersNamed(FEEL_1_1Parser.ParametersNamedContext ctx) {
//        throw new UnsupportedOperationException("TODO"); // TODO
//    }
//
//    @Override
//    public DirectCompilerResult visitParametersPositional(FEEL_1_1Parser.ParametersPositionalContext ctx) {
//        throw new UnsupportedOperationException("TODO"); // TODO
//    }
//
//    @Override
//    public DirectCompilerResult visitPrimaryName(FEEL_1_1Parser.PrimaryNameContext ctx) {
//        throw new UnsupportedOperationException("TODO"); // TODO
//    }
//
//    private String getFunctionName(DirectCompilerResult name) {
//        throw new UnsupportedOperationException("TODO"); // TODO
//    }
//
//    private DirectCompilerResult buildFunctionCall(ParserRuleContext ctx, DirectCompilerResult name, ListNode params) {
//        throw new UnsupportedOperationException("TODO"); // TODO
//    }
//
//    private DirectCompilerResult buildNotCall(ParserRuleContext ctx, DirectCompilerResult name, ListNode params) {
//        throw new UnsupportedOperationException("TODO"); // TODO
//    }

    @Override
    public DirectCompilerResult visitType(FEEL_1_1Parser.TypeContext ctx) {
        String typeAsText = ParserHelper.getOriginalText(ctx);
        Expression biT = JavaParser.parseExpression("org.kie.dmn.feel.lang.types.BuiltInType");
        MethodCallExpr determineCall = new MethodCallExpr(biT, "determineTypeFromName");
        determineCall.addArgument(new StringLiteralExpr(typeAsText));
        return DirectCompilerResult.of(determineCall, BuiltInType.UNKNOWN);
    }

    @Override
    public DirectCompilerResult visitRelExpressionInstanceOf(FEEL_1_1Parser.RelExpressionInstanceOfContext ctx) {
        DirectCompilerResult expr = visit(ctx.val);
        DirectCompilerResult type = visit(ctx.type());
        MethodCallExpr isInstanceOfCall = new MethodCallExpr(type.getExpression(), "isInstanceOf");
        isInstanceOfCall.addArgument(expr.getExpression());
        return DirectCompilerResult.of(isInstanceOfCall, BuiltInType.BOOLEAN, mergeFDs(expr, type));
    }

    @Override
    public DirectCompilerResult visitFilterPathExpression(FEEL_1_1Parser.FilterPathExpressionContext ctx) {
        if (ctx.filter != null) {
            DirectCompilerResult expr = visit(ctx.filterPathExpression());
            DirectCompilerResult filter = visit(ctx.expression());
            MethodCallExpr filterCall = new MethodCallExpr(new NameExpr(CompiledFEELSupport.class.getSimpleName()), "filter");
            filterCall.addArgument(new NameExpr("feelExprCtx"));
            filterCall.addArgument(expr.getExpression());
            MethodCallExpr filterWithCall = new MethodCallExpr(filterCall, "with");
            if (filter.resultType != BuiltInType.BOOLEAN) {
                // Then is the case Table 54: Semantics of lists, ROW: e1 is a list and e2 is an integer (0 scale number)
                filterWithCall.addArgument(filter.getExpression());
            } else {
                // Then is the case Table 54: Semantics of lists, ROW: e1 is a list and type(FEEL(e2 , s')) is boolean
                Expression anonFunctionClass = anonFunctionEvaluationContext2Object(filter.getExpression());
                filterWithCall.addArgument(anonFunctionClass);
            }
            return DirectCompilerResult.of(filterWithCall, BuiltInType.UNKNOWN).withFD(expr).withFD(filter);
        } else if (ctx.qualifiedName() != null) {
            DirectCompilerResult expr = visit(ctx.filterPathExpression());
            List<String> names = ctx.qualifiedName().nameRef().stream().map(nameRefContext -> ParserHelper.getOriginalText(nameRefContext)).collect(Collectors.toList());
            return telescopePathAccessor(expr, names);
        } else {
            return visit(ctx.unaryExpression());
        }
    }

    private Expression anonFunctionEvaluationContext2Object(Expression expression) {
        Expression anonFunctionClass = JavaParser.parseExpression("new java.util.function.Function<EvaluationContext, Object>() {\n" +
                                                                  "    @Override\n" +
                                                                  "    public Object apply(EvaluationContext feelExprCtx) {\n" +
                                                                  "        return null;\n" +
                                                                  "    }\n" +
                                                                  "}");
        List<ReturnStmt> lookupReturnList = anonFunctionClass.getChildNodesByType(ReturnStmt.class);
        if (lookupReturnList.size() != 1) {
            throw new RuntimeException("Something unexpected changed in the template.");
        }
        ReturnStmt returnStmt = lookupReturnList.get(0);
        returnStmt.setExpression(expression);
        return anonFunctionClass;
    }

    @Override
    public DirectCompilerResult visitExpressionTextual(FEEL_1_1Parser.ExpressionTextualContext ctx) {
        DirectCompilerResult expr = visit( ctx.expr );
        return expr;
    }

    @Override
    public DirectCompilerResult visitUenpmPrimary(FEEL_1_1Parser.UenpmPrimaryContext ctx) {
        DirectCompilerResult expr = visit(ctx.primary());
        if (ctx.qualifiedName() != null) {
            List<String> names = ctx.qualifiedName().nameRef().stream().map(nameRefContext -> ParserHelper.getOriginalText(nameRefContext)).collect(Collectors.toList());
            return telescopePathAccessor(expr, names);
        }
        return expr;
    }

    private DirectCompilerResult telescopePathAccessor(DirectCompilerResult scopeExpr, List<String> names) {
        MethodCallExpr pathCall = new MethodCallExpr(new NameExpr(CompiledFEELSupport.class.getSimpleName()), "path");
        pathCall.addArgument(new NameExpr("feelExprCtx"));
        pathCall.addArgument(scopeExpr.getExpression());
        MethodCallExpr filterPathCall = new MethodCallExpr(pathCall, "with");
        for (String n : names) {
            filterPathCall.addArgument(new StringLiteralExpr(n));
        }
        // TODO here I could still try to infer the result type.
        return DirectCompilerResult.of(filterPathCall, BuiltInType.UNKNOWN).withFD(scopeExpr);
    }

    @Override
    public DirectCompilerResult visitCompilation_unit(FEEL_1_1Parser.Compilation_unitContext ctx) {
        return visit( ctx.expression() );
    }

    @Override
    public DirectCompilerResult visitNegatedUnaryTests(FEEL_1_1Parser.NegatedUnaryTestsContext ctx) {
        FEEL_1_1Parser.SimpleUnaryTestsContext child = (FEEL_1_1Parser.SimpleUnaryTestsContext) ctx.getChild(2);
        return createUnaryTestExpression(ctx, child.accept(this), UnaryOperator.NOT);
    }
}
