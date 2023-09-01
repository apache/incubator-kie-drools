package org.drools.drl.ast.descr;

import java.util.List;

/**
 * A descriptor to represent a relational expression
 */
public class RelationalExprDescr extends BaseDescr {
    private static final long serialVersionUID = 520l;

    private BaseDescr         left;
    private BaseDescr         right;
    private OperatorDescr     operator;

    private String            expression;

    public RelationalExprDescr() { }

    public RelationalExprDescr(String operator,
                               boolean negated,
                               List<String> parameters,
                               BaseDescr left,
                               BaseDescr right) {
        this.left = left;
        this.right = right;
        this.operator = new OperatorDescr( operator,
                                           negated,
                                           parameters );
    }

    public BaseDescr getLeft() {
        return left;
    }

    public void setLeft( BaseDescr left ) {
        this.left = left;
    }

    public BaseDescr getRight() {
        return right;
    }

    public void setRight( BaseDescr right ) {
        this.right = right;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression( String expression ) {
        this.expression = expression;
    }

    public String getOperator() {
        return operator != null ? operator.getOperator() : null;
    }

    public void setOperator( String operator ) {
        createOrGetOperator().setOperator( operator );
    }

    public boolean isNegated() {
        return operator != null ? operator.isNegated() : false;
    }

    public void setNegated( boolean negated ) {
        createOrGetOperator().setNegated( negated );
    }

    @Override
    public BaseDescr negate() {
        setNegated( !isNegated() );
        return this;
    }

    public List<String> getParameters() {
        return operator != null ? operator.getParameters() : null;
    }

    public String getParametersText() {
        if ( operator != null && operator.getParameters() != null ) {
            StringBuilder builder = new StringBuilder();
            boolean first = true;
            for ( String param : operator.getParameters() ) {
                if ( first ) {
                    first = false;
                } else {
                    builder.append( "," );
                }
                builder.append( param );
            }
            return builder.toString();
        }
        return null;
    }

    public void setParameters( List<String> parameters ) {
        createOrGetOperator().setParameters( parameters );
    }

    private OperatorDescr createOrGetOperator() {
        if( this.operator == null ) {
            this.operator = new OperatorDescr();
            this.operator.setResource(getResource());
        }
        return this.operator;
    }
    
    public OperatorDescr getOperatorDescr() {
        return this.operator;
    }
    
    public void setOperatorDescr( OperatorDescr operator ) {
        this.operator = operator;
    }

    @Override
    public RelationalExprDescr replaceVariable(String oldVar, String newVar) {
        expression = expression.replace( oldVar, newVar );
        left = left.replaceVariable( oldVar, newVar );
        right = right.replaceVariable( oldVar, newVar );
        return this;
    }

    @Override
    public String toString() {
        return this.left + (isNegated() ? " not " : " ") + getOperator() + (getParameters() != null ? getParameters().toString() + " " : " ") + this.right;
    }
}
