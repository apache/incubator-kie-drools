package org.drools.rule;

import java.io.Serializable;

import org.drools.spi.DataProvider;

public class From extends ConditionalElement
    implements
    Serializable {
    private Column       column;

    private DataProvider dataProvider;

    public From(final Column column,
                final DataProvider dataProvider) {
        this.column = column;
        this.dataProvider = dataProvider;
    }

    public Column getColumn() {
        return this.column;
    }

    public DataProvider getDataProvider() {
        return this.dataProvider;
    }

    public Object clone() {
        // TODO Auto-generated method stub
        return null;
    }
}
