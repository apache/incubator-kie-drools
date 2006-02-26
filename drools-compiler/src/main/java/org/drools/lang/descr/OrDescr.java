package org.drools.lang.descr;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OrDescr extends PatternDescr 
    implements ConditionalElementDescr {
    private List descrs = Collections.EMPTY_LIST;
    
    public OrDescr() {
    }    
    
    public void addDescr(PatternDescr patternDescr) {
        if ( this.descrs == Collections.EMPTY_LIST ) {
            this.descrs = new ArrayList(1);
        }
        this.descrs.add( patternDescr );
    }
    
    public List getDescrs() {
        return this.descrs;
    }
}
