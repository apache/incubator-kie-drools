package org.kie.dmn.feel.lang.ast;

import org.antlr.v4.runtime.ParserRuleContext;
import org.kie.dmn.api.feel.runtime.events.FEELEvent.Severity;
import org.kie.dmn.feel.lang.EvaluationContext;
import org.kie.dmn.feel.lang.Type;
import org.kie.dmn.feel.util.EvalHelper;
import org.kie.dmn.feel.util.Msg;

import java.math.BigDecimal;

public class SignedUnaryNode
        extends BaseNode {

    public static enum Sign {
        POSITIVE, NEGATIVE;

        public static Sign determineSign(String str) {
            if ( "-".equals( str ) ) {
                return NEGATIVE;
            } else if ( "+".equals( str ) ) {
                return POSITIVE;
            }
            throw new IllegalArgumentException( "Unknown sign: '" + str + "'. Expecting either '+' or '-'." );
        }
    }

    private Sign     sign;
    private BaseNode expression;

    public SignedUnaryNode(ParserRuleContext ctx, BaseNode expr) {
        super( ctx );
        sign = Sign.determineSign( ctx.start.getText() );
        expression = expr;
    }

    public Sign getSign() {
        return sign;
    }

    public BaseNode getExpression() {
        return expression;
    }

    @Override
    public Object evaluate(EvaluationContext ctx) {
        if (expression == null) return null;
        Object expressionResult = expression.evaluate( ctx );
        if (expressionResult instanceof String) {
            ctx.notifyEvt( astEvent(Severity.ERROR, Msg.createMessage(Msg.CANNOT_BE_SIGNED)));
            return null;
        }
        BigDecimal result = EvalHelper.getBigDecimalOrNull( expressionResult );
        if ( result == null ) {
            ctx.notifyEvt( astEvent(Severity.WARN, Msg.createMessage(Msg.NEGATING_A_NULL)));
            return null;
        } else if ( Sign.NEGATIVE == sign ) {
            return BigDecimal.valueOf( -1 ).multiply( result );
        } else {
            return result;
        }
    }

    @Override
    public Type getResultType() {
        return expression.getResultType();
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
