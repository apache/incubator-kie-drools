package org.drools.lang.descr;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ColumnDescr extends PatternDescr {
    private String objectType;
    private String identifier;
    private List   descrs = Collections.EMPTY_LIST;

    public ColumnDescr(String objectType) {
        this(objectType, null);
    }    
    
    public ColumnDescr(String objectType,
                       String identifier) {
        this.objectType = objectType;
        this.identifier = identifier;
    }

    public void addDescr(PatternDescr patternDescr) {
        if ( this.descrs == Collections.EMPTY_LIST ) {
            this.descrs = new ArrayList( 1 );
        }
        this.descrs.add(  patternDescr );
    }

    public String getObjectType() {
        return objectType;
    }

    public String getIdentifier() {
        return identifier;
    }

    public List getDescrs() {
        return this.descrs;
    }
}
