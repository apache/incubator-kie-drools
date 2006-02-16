package org.drools.lang.descr;

import java.util.ArrayList;
import java.util.List;

public class NotDescr extends PatternDescr
    implements
    ConditionalElement {

    private List list;

    public NotDescr(ColumnDescr column) {
        this.list = new ArrayList();
        this.list.add( column );
    }

    public List getDescrs() {
        return this.list;
    }

}
