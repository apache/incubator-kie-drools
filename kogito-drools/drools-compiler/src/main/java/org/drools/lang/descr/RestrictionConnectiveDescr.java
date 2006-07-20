package org.drools.lang.descr;

public class RestrictionConnectiveDescr extends RestrictionDescr {    
    /**
     * 
     */
    private static final long serialVersionUID = 320;
    private final static int AND = 0;
    private final static int OR = 1;
    
    private int connective;

    public RestrictionConnectiveDescr(int connective) {
        super();
        this.connective = connective;
    }

    public int getConnective() {
        return this.connective;
    }
            
}
