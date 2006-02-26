package org.drools.lang.descr;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NotDescr extends PatternDescr
    implements
    ConditionalElementDescr {

    private List descrs = new  ArrayList(1);

    public NotDescr() {
    }
    
    public NotDescr(ColumnDescr column) {
        addDescr( column );
    }

    public void addDescr(PatternDescr patternDescr) {
        this.descrs.add( patternDescr );
    }
    
    public List getDescrs() {
        return this.descrs;
    }

}
