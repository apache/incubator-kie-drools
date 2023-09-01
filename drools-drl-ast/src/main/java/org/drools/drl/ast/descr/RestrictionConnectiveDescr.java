package org.drools.drl.ast.descr;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * This is used to connect restrictions together for a single field
 * eg:
 * age < 40 & > 30
 */
public class RestrictionConnectiveDescr extends RestrictionDescr {

    private static final long                     serialVersionUID = 510l;

    public final static ConnectiveDescr.RestrictionConnectiveType AND              = ConnectiveDescr.RestrictionConnectiveType.AND;
    public final static ConnectiveDescr.RestrictionConnectiveType OR               = ConnectiveDescr.RestrictionConnectiveType.OR;

    private ConnectiveDescr.RestrictionConnectiveType connective;
    private List<RestrictionDescr>                restrictions;

    public RestrictionConnectiveDescr() { }

    public RestrictionConnectiveDescr(final ConnectiveDescr.RestrictionConnectiveType connective) {
        super();
        this.connective = connective;
        this.restrictions = Collections.emptyList();
    }

    @Override
    public void readExternal( ObjectInput in ) throws IOException,
                                                      ClassNotFoundException {
        super.readExternal( in );
        this.connective = ( ConnectiveDescr.RestrictionConnectiveType ) in.readObject();
        this.restrictions = (List<RestrictionDescr>) in.readObject();
    }

    @Override
    public void writeExternal( ObjectOutput out ) throws IOException {
        super.writeExternal( out );
        out.writeObject( connective );
        out.writeObject( restrictions );
    }

    public ConnectiveDescr.RestrictionConnectiveType getConnective() {
        return this.connective;
    }

    public void addRestriction(RestrictionDescr restriction) {
        if ( this.restrictions == Collections.EMPTY_LIST ) {
            this.restrictions = new ArrayList<>();
        }
        this.restrictions.add( restriction );
    }

    public void addOrMerge(RestrictionDescr restriction) {
        if ( (restriction instanceof RestrictionConnectiveDescr) && ((RestrictionConnectiveDescr) restriction).connective == this.connective ) {
            if ( this.restrictions == Collections.EMPTY_LIST ) {
                this.restrictions = new ArrayList<>();
            }
            this.restrictions.addAll( ((RestrictionConnectiveDescr) restriction).getRestrictions() );
        } else {
            this.addRestriction( restriction );
        }
    }

    public List<RestrictionDescr> getRestrictions() {
        return this.restrictions;
    }

    public String toString() {
        final StringBuilder buf = new StringBuilder();
        buf.append( "( " );
        for ( Iterator it = this.restrictions.iterator(); it.hasNext(); ) {
            buf.append( it.next().toString() );
            if ( it.hasNext() ) {
                buf.append( this.connective.toString() );
            }
        }
        buf.append( "  )" );
        return buf.toString();
    }
}
