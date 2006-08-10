package org.drools.rule;

import java.io.Serializable;

import org.drools.spi.DataProvider;

public class From implements Serializable{
    private Column column;   
    
    private DataProvider dataProvider;
    
    public From(final Column column,
                final DataProvider dataProvider) {
        this.column = column;
        this.dataProvider = dataProvider;
    }

    public Column getColumn() {
        return column;
    }

    public DataProvider getDataProvider() {
        return dataProvider;
    }    
}
