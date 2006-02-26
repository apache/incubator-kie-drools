package org.drools.lang.descr;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ExistsDescr extends PatternDescr
    implements
    ConditionalElementDescr {

    private List descrs = new  ArrayList(1);

    public ExistsDescr() {
    }
    
    public ExistsDescr(ColumnDescr column) {
        addDescr( column );
    }

    public void addDescr(PatternDescr patternDescr) {
        this.descrs.add( patternDescr );
    }
    
    public List getDescrs() {
        return this.descrs;
    }

}
