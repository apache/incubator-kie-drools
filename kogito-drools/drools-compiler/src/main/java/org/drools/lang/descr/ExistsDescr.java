package org.drools.lang.descr;

import java.util.ArrayList;
import java.util.List;

public class ExistsDescr extends PatternDescr
    implements
    ConditionalElement {

    private List list;

    public ExistsDescr(ColumnDescr column) {
        this.list = new ArrayList();
        this.list.add( column );
    }

    public List getDescrs() {
        return this.list;
    }

}
