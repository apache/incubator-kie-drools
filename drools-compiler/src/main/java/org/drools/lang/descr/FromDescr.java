package org.drools.lang.descr;

import java.util.Collections;
import java.util.List;

public class FromDescr extends BaseDescr
    implements
    ConditionalElementDescr {
    private ColumnDescr             column;
    private DeclarativeInvokerDescr dataSource;

    FromDescr() {
        //protected so only factory can create
    }

    public int getLine() {
        return this.column.getLine();
    }

    public void setColumn(final ColumnDescr column) {
        this.column = column;
    }

    public DeclarativeInvokerDescr getDataSource() {
        return this.dataSource;
    }

    public void setDataSource(final DeclarativeInvokerDescr dataSource) {
        this.dataSource = dataSource;
    }

    public ColumnDescr getReturnedColumn() {
        return this.column;
    }

    public void addDescr(final BaseDescr baseDescr) {
        //
    }

    public List getDescrs() {
        return Collections.EMPTY_LIST;
    }

}
