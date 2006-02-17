package org.drools.lang.descr;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OrDescr extends PatternDescr 
    implements ConditionalElement {
    private List descrs = Collections.EMPTY_LIST;
    
    public OrDescr() {
    }    
    
    public void addDescr(PatternDescr patternDescr) {
        if ( this.descrs == Collections.EMPTY_LIST ) {
            this.descrs = new ArrayList(1);
        }
    }
    
    public List getDescrs() {
        return this.descrs;
    }
}
