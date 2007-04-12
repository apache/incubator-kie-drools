package org.drools.rule;

import java.io.Serializable;
import java.util.Map;

import org.drools.spi.DataProvider;

public class From extends ConditionalElement
    implements
    Serializable {

    private static final long serialVersionUID = -2640290731776949513L;

    private Column            column;

    private DataProvider      dataProvider;

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

    public Map getInnerDeclarations() {
        return this.column.getInnerDeclarations();
    }

    public Map getOuterDeclarations() {
        return this.column.getOuterDeclarations();
    }

    /**
     * @inheritDoc
     */
    public Declaration resolveDeclaration(final String identifier) {
        return (Declaration) this.column.getInnerDeclarations().get( identifier );
    }

}
