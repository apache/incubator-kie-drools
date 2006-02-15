package org.drools.lang.descr;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AndDescr extends PatternDescr {
    private List descrs = Collections.EMPTY_LIST;
    
    public AndDescr() {
    }
    
    public void addConfiguration(PatternDescr patternDescr) {
        if ( this.descrs == Collections.EMPTY_LIST ) {
            this.descrs = new ArrayList(1);
        }
    }
    
    public List getDescrs() {
        return this.descrs;
    }
}
