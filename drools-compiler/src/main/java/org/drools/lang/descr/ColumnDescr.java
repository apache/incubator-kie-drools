package org.drools.lang.descr;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ColumnDescr extends PatternDescr {
    private String objectType;
    private String binding;
    private List   descrs = Collections.EMPTY_LIST;

    public ColumnDescr(String objectType,
                       String binding) {
        this.objectType = objectType;
        this.binding = binding;
    }

    public void addDescr(PatternDescr patternDescr) {
        if ( this.descrs == Collections.EMPTY_LIST ) {
            this.descrs = new ArrayList( 1 );
        }
    }

    public String getObjectType() {
        return objectType;
    }

    public String getBinding() {
        return binding;
    }

    public List getDescrs() {
        return this.descrs;
    }
}
