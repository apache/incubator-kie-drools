package org.drools.drl.ast.descr;

public class AtomicExprDescr extends BaseDescr implements ExpressionDescr {
    private static final long serialVersionUID = 510l;

    private String            expression;
    private String            rewrittenExpression;
    private boolean           literal;

    public AtomicExprDescr() { }

    public AtomicExprDescr(final String expression) {
        this( expression, false );
    }

    public AtomicExprDescr(final String expression, final boolean isLiteral ) {
        this.expression = expression;
        this.literal = isLiteral;
    }

    public String getExpression() {
        return this.expression;
    }

    public void setExpression( final String expression ) {
        this.expression = expression;
    }

    @Override
    public String toString() {
        return expression;
    }

    public boolean isLiteral() {
        return literal;
    }

    public void setLiteral( boolean literal ) {
        this.literal = literal;
    }

    public String getRewrittenExpression() {
        return rewrittenExpression != null ? rewrittenExpression : expression;
    }

    public boolean hasRewrittenExpression() {
        return rewrittenExpression != null;
    }

    public void setRewrittenExpression(String rewrittenExpression) {
        this.rewrittenExpression = rewrittenExpression;
    }

    @Override
    public AtomicExprDescr replaceVariable(String oldVar, String newVar) {
        expression = expression.replace( oldVar, newVar );
        return this;
    }

    @Override
    public BaseDescr negate() {
        this.expression = "!(" + expression + ")";
        return this;
    }
}
