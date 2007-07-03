package org.drools.lang.descr;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This is used to connect restrictions together for a single field
 * eg:
 * 	age < 40 & > 30 
 *
 */
public class RestrictionConnectiveDescr extends RestrictionDescr {

    private static final long serialVersionUID = 400L;
    public final static int   AND              = 0;
    public final static int   OR               = 1;

    private int               connective;
    private List              restrictions;

    public RestrictionConnectiveDescr(final int connective) {
        super();
        this.connective = connective;
        this.restrictions = Collections.EMPTY_LIST;
    }

    public int getConnective() {
        return this.connective;
    }
    
    public void addRestriction( RestrictionDescr restriction ) {
        if( this.restrictions == Collections.EMPTY_LIST ) {
            this.restrictions = new ArrayList();
        }
        this.restrictions.add( restriction );
    }
    
    public void addOrMerge( RestrictionDescr restriction ) {
        if(( restriction instanceof RestrictionConnectiveDescr ) &&
           ((RestrictionConnectiveDescr)restriction).connective == this.connective ) {
            if( this.restrictions == Collections.EMPTY_LIST ) {
                this.restrictions = new ArrayList();
            }
            this.restrictions.addAll( ((RestrictionConnectiveDescr)restriction).getRestrictions() );
        } else {
            this.addRestriction( restriction );
        }
    }
    
    public List getRestrictions() {
        return this.restrictions;
    }
}
