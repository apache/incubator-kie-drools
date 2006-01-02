package org.drools.rule;

import org.drools.spi.ObjectType;

public class ColumnBinding extends Binding {
    private final Column column;

    public ColumnBinding(String identifier,
                         ObjectType objectType,
                         Column column) {
        super( identifier,
               objectType );
        this.column = column;
    }

    /**
     * @return Returns the column.
     */
    public Column getColumn() {
        return this.column;
    }

}
