package org.drools.drl.ast.descr;


import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;

public class AccessorDescr extends DeclarativeInvokerDescr {

    private static final long serialVersionUID = 510l;

    private String            variableName;
    private List              invokers;

    public AccessorDescr() {
        this( null );
    }

    public AccessorDescr(final String rootVariableName) {
        super();
        this.variableName = rootVariableName;
        this.invokers = new ArrayList();
    }

    public DeclarativeInvokerDescr[] getInvokersAsArray() {
        return (DeclarativeInvokerDescr[]) this.invokers.toArray( new DeclarativeInvokerDescr[0] );
    }

    public List getInvokers() {
        return this.invokers;
    }

    public void addInvoker(final DeclarativeInvokerDescr accessor) {
        this.invokers.add( accessor );
    }

    public void addFirstInvoker(final DeclarativeInvokerDescr accessor) {
        this.invokers.add( 0, accessor );
    }

    public String getVariableName() {
        return this.variableName;
    }

    public void setVariableName(final String methodName) {
        this.variableName = methodName;
    }

    public String toString() {
        final StringBuilder buf = new StringBuilder();
        buf.append( (this.variableName != null) ? this.variableName : "" );
        for ( final Iterator it = this.invokers.iterator(); it.hasNext(); ) {
            if ( buf.length() > 0 ) {
                buf.append( "." );
            }
            buf.append( it.next().toString() );
        }
        return buf.toString();
    }

}
