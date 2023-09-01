package org.drools.drl.ast.descr;

/**
 * This represents a constraint in a pattern defined by an arbitrary
 * expression. The constraint can be any valid java/mvel expression.
 */
public class ExprConstraintDescr extends BaseDescr implements ExpressionDescr {

    private static final long serialVersionUID = 520l;

    public static enum Type {
        NAMED, POSITIONAL;
    }
    
    private Type type = Type.NAMED;
    private int position = -1;

    public ExprConstraintDescr() { }
    
    public ExprConstraintDescr(final String expr) {
        setText( expr );
    }
    
    public void setExpression( final String expr ) {
        setText( expr );
    }
    
    public String getExpression( ) {
        return getText();
    }

    /**
     * @return the type
     */
    public Type getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType( Type type ) {
        this.type = type;
    }

    /**
     * @return the position
     */
    public int getPosition() {
        return position;
    }

    /**
     * @param position the position to set
     */
    public void setPosition( int position ) {
        this.position = position;
    }
    
    @Override
    public String toString() {
        return getText();
    }

    @Override
    public BaseDescr negate() {
        setText("!(" + getText() + ")");
        return this;
    }
}
