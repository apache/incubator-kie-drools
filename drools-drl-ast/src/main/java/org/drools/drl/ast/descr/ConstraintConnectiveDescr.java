package org.drools.drl.ast.descr;

import java.util.ArrayList;
import java.util.List;

import org.kie.api.io.Resource;

/**
 * A descriptor to represent logical connectives in constraints, like
 * &&, || and ^. 
 */
public class ConstraintConnectiveDescr extends AnnotatedBaseDescr {
    private static final long serialVersionUID = 520l;
    
    private ConnectiveType     connective       = ConnectiveType.AND;
    private List<BaseDescr>    descrs           = new ArrayList<>();

    private boolean negated;

    public ConstraintConnectiveDescr() { }
    
    public ConstraintConnectiveDescr( ConnectiveType connective ) {
        this.connective = connective;
    }
    
    public static ConstraintConnectiveDescr newAnd() {
        return new ConstraintConnectiveDescr( ConnectiveType.AND );
    }

    public static ConstraintConnectiveDescr newOr() {
        return new ConstraintConnectiveDescr( ConnectiveType.OR );
    }

    public static ConstraintConnectiveDescr newXor() {
        return new ConstraintConnectiveDescr( ConnectiveType.XOR );
    }

    public static ConstraintConnectiveDescr newIncAnd() {
        return new ConstraintConnectiveDescr( ConnectiveType.INC_AND );
    }

    public static ConstraintConnectiveDescr newIncOr() {
        return new ConstraintConnectiveDescr( ConnectiveType.INC_OR );
    }

    public void addDescr(final BaseDescr baseDescr) {
        this.descrs.add( baseDescr );
    }

    public List<BaseDescr> getDescrs() {
        return this.descrs;
    }
    
    public ConnectiveType getConnective() {
        return connective;
    }

    public void setConnective( ConnectiveType connective ) {
        this.connective = connective;
    }

    public void addOrMerge(final BaseDescr baseDescr) {
        if ( baseDescr instanceof ConstraintConnectiveDescr ) {
            ConstraintConnectiveDescr con = (ConstraintConnectiveDescr)baseDescr;
            if( con.getConnective().equals( this.connective ) ) {
                for( BaseDescr descr : con.getDescrs() ) {
                    addDescr( descr );
                    for ( String annKey : con.getAnnotationNames() ) {
                        addAnnotation( con.getAnnotation( annKey ) );
                    }
                }
            } else {
                addDescr( con );
            }
        } else {
            addDescr( baseDescr );
        }
    }

    @Override
    public void setResource(Resource resource) {
        super.setResource(resource);
        for( BaseDescr descr : descrs )  {
            descr.setResource(resource);
        }
    }
    ;
    @Override
    public String toString() {
        return "["+this.connective+" "+descrs+" ]";
    }

    @Override
    public void copyLocation(BaseDescr d) {
        super.copyLocation(d);
        if (descrs.size() == 1 && descrs.get(0) instanceof BindingDescr) {
            descrs.get(0).copyLocation(d);
        }
    }

    @Override
    public boolean isNegated() {
        return negated;
    }

    public void setNegated( boolean negated ) {
        this.negated = negated;
    }

    @Override
    public BaseDescr negate() {
        if (connective == ConnectiveType.OR) {
            connective = ConnectiveType.AND;
        } else if (connective == ConnectiveType.AND) {
            connective = ConnectiveType.OR;
        } else {
            throw new UnsupportedOperationException();
        }

        for (BaseDescr descr : descrs) {
            descr.negate();
        }
        return this;
    }
}
