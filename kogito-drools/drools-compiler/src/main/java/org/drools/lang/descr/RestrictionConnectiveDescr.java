package org.drools.lang.descr;

/**
 * This is used to connect restrictions together for a single field
 * eg:
 * 	age < 40 & > 30 
 *
 */
public class RestrictionConnectiveDescr extends RestrictionDescr {

    private static final long serialVersionUID = 320;
    public final static int   AND              = 0;
    public final static int   OR               = 1;

    private int               connective;

    public RestrictionConnectiveDescr(final int connective) {
        super();
        this.connective = connective;
    }

    public int getConnective() {
        return this.connective;
    }

}
