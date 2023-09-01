package org.drools.drl.ast.descr;

public class MVELExprDescr extends DeclarativeInvokerDescr {

    private static final long serialVersionUID = 510l;

    public MVELExprDescr() {
        this( null );
    }

    public MVELExprDescr(final String expr) {
        super( );
        setText( expr );
    }
    
    public String getExpression() {
        return getText();
    }

    public String toString() {
        return getText();
    }
}
