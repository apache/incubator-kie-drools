package org.drools.lang.descr;

import java.util.Collections;
import java.util.List;

public class FromDescr extends BaseDescr
    implements
    ConditionalElementDescr {
    private PatternDescr             pattern;
    private DeclarativeInvokerDescr dataSource;

    FromDescr() {
        //protected so only factory can create
    }

    public int getLine() {
        return this.pattern.getLine();
    }

    public void setPattern(final PatternDescr pattern) {
        this.pattern = pattern;
    }

    public DeclarativeInvokerDescr getDataSource() {
        return this.dataSource;
    }

    public void setDataSource(final DeclarativeInvokerDescr dataSource) {
        this.dataSource = dataSource;
    }

    public PatternDescr getReturnedPattern() {
        return this.pattern;
    }

    public void addDescr(final BaseDescr baseDescr) {
        //
    }

    public List getDescrs() {
        return Collections.EMPTY_LIST;
    }

}
