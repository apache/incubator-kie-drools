package org.drools.lang.descr;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * This is used to connect restrictions together for a single field
 * eg:
 * 	age < 40 & > 30 
 *
 */
public class RestrictionConnectiveDescr extends RestrictionDescr {

    private static final long                     serialVersionUID = 400L;

    public final static RestrictionConnectiveType AND              = RestrictionConnectiveType.AND;
    public final static RestrictionConnectiveType OR               = RestrictionConnectiveType.OR;

    private RestrictionConnectiveType             connective;
    private List<RestrictionDescr>                restrictions;

    public RestrictionConnectiveDescr(final RestrictionConnectiveType connective) {
        super();
        this.connective = connective;
        this.restrictions = Collections.emptyList();
    }

    public RestrictionConnectiveType getConnective() {
        return this.connective;
    }

    public void addRestriction(RestrictionDescr restriction) {
        if ( this.restrictions == Collections.EMPTY_LIST ) {
            this.restrictions = new ArrayList<RestrictionDescr>();
        }
        this.restrictions.add( restriction );
    }

    public void addOrMerge(RestrictionDescr restriction) {
        if ( (restriction instanceof RestrictionConnectiveDescr) && ((RestrictionConnectiveDescr) restriction).connective == this.connective ) {
            if ( this.restrictions == Collections.EMPTY_LIST ) {
                this.restrictions = new ArrayList<RestrictionDescr>();
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

    /**
     * The connective types that can be used for a restriction
     * 
     * @author etirelli
     */
    public static enum RestrictionConnectiveType {
        AND {
            public String toString() {
                return "&&";
            }
        },
        OR {
            public String toString() {
                return "||";
            }
        };
    }
}
