package org.kie.dmn.feel.codegen.feel11;

import java.util.ArrayList;
import java.util.Map;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.drools.javaparser.ast.NodeList;
import org.drools.javaparser.ast.expr.Expression;
import org.drools.javaparser.ast.expr.MethodCallExpr;
import org.drools.javaparser.ast.expr.NameExpr;
import org.drools.javaparser.ast.expr.StringLiteralExpr;
import org.kie.dmn.feel.lang.Type;
import org.kie.dmn.feel.lang.ast.UnaryTestNode;
import org.kie.dmn.feel.lang.types.BuiltInType;
import org.kie.dmn.feel.parser.feel11.FEEL_1_1Parser;

public class UnaryCompilerVisitor extends DirectCompilerVisitor {

    public UnaryCompilerVisitor(Map<String, Type> inputTypes) {
        super(inputTypes);
    }


    /**
     * DMN defines a special case where, unless the expressions are unary tests
     * or ranges, they need to be converted into an equality test unary expression.
     * This way, we have to compile and check the low level AST nodes to properly
     * deal with this case
     */
    public DirectCompilerResult compileUnaryTests(FEEL_1_1Parser.ExpressionListContext ctx) {
        ArrayList<DirectCompilerResult> exprs = new ArrayList<>();
        for (FEEL_1_1Parser.ExpressionContext expressionContext : ctx.expression()) {
            DirectCompilerResult child = visit(expressionContext);
            if (child.resultType == BuiltInType.UNARY_TEST) {
                exprs.add(child);
            } else if (child.resultType == BuiltInType.RANGE) {
                // being a range, need the `in` operator.
                DirectCompilerResult replaced = createUnaryTestExpression(expressionContext, child, UnaryTestNode.UnaryOperator.IN);
                exprs.add(replaced);
            } else if (isExtendedUnaryTest(expressionContext)) {
                if (isNegateUnaryTest(child.getExpression())) {
                    MethodCallExpr expression = (MethodCallExpr) child.getExpression();
                    Expression expr = expression.getArgument(0);

                    MethodCallExpr invokeCall = new MethodCallExpr(new NameExpr(CompiledFEELSupport.class.getSimpleName()), "invoke");
                    invokeCall.addArgument(new NameExpr("feelExprCtx"));
                    invokeCall.addArgument(new MethodCallExpr(
                            new NameExpr("feelExprCtx"),
                            "getValue",
                            new NodeList<>(new StringLiteralExpr("not"))
                    ));
                    invokeCall.addArgument(expr);

                    DirectCompilerResult r = DirectCompilerResult.of(invokeCall, BuiltInType.BOOLEAN, child.getFieldDeclarations());

                    DirectCompilerResult replaced = createUnaryTestExpression(expressionContext, r, UnaryTestNode.UnaryOperator.TEST);
                    exprs.add(replaced);
                } else {
                    DirectCompilerResult replaced = createUnaryTestExpression(expressionContext, child, UnaryTestNode.UnaryOperator.TEST);
                    exprs.add(replaced);
                }
            } else if (isNegateUnaryTest(child.getExpression())) {
                DirectCompilerResult replaced = createUnaryTestExpression(expressionContext, child, UnaryTestNode.UnaryOperator.TEST);
                exprs.add(replaced);
            } else {
                // implied a unarytest for the `=` equal operator.
                DirectCompilerResult replaced = createUnaryTestExpression(expressionContext, child, UnaryTestNode.UnaryOperator.EQ);
                exprs.add(replaced);
            }

        }
        MethodCallExpr list = new MethodCallExpr(null, "list");
        exprs.stream().map(DirectCompilerResult::getExpression).forEach(list::addArgument);
        return DirectCompilerResult.of(list, BuiltInType.LIST, DirectCompilerResult.mergeFDs(exprs.toArray(new DirectCompilerResult[]{})));
    }

    @Override
    protected DirectCompilerResult buildNotCall(ParserRuleContext ctx, DirectCompilerResult name, ParseTree params) {
        if (params.getChildCount() == 1) {
            DirectCompilerResult parameter = visit(params.getChild(0));
            // this is an ambiguous call: defer choice to runtime
            MethodCallExpr expr = new MethodCallExpr(
                    null,
                    "negateUnaryTest",
                    new NodeList<>(
                            parameter.getExpression()));
            return DirectCompilerResult.of(expr, BuiltInType.UNKNOWN, parameter.getFieldDeclarations());
        } else {
            DirectCompilerResult parameters = visit(params);
            // if childcount != 1 assume not expression
            return createUnaryTestExpression(ctx, parameters, UnaryTestNode.UnaryOperator.NOT);
        }
    }

    private boolean isNegateUnaryTest(Expression expr) {
        return expr instanceof MethodCallExpr
                && ((MethodCallExpr) expr).getName().asString().equals("negateUnaryTest");
    }

    /**
     * Returns true if the given subtree contains the special variable "?"
     *
     * similar to FEELImpl#isExtendedUnaryTest
     * this is sort of a hack: we should carry over this information
     * in a structured way
     *
     *
     */
    private boolean isExtendedUnaryTest(ParseTree o) {
        if( "?".equals(o.getText()) ) {
            return true;
        } else {
            for( int i = 0 ; i < o.getChildCount() ; i++ ) {
                if( isExtendedUnaryTest( o.getChild(i) ) ) {
                    return true;
                }
            }
        }
        return false;
    }

}
