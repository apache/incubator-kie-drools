package org.drools.rule;

import org.drools.spi.ColumnExtractor;
import org.drools.spi.ObjectType;

public class ColumnBinding extends Binding {
    private final Column column;

    public ColumnBinding(String identifier,
                         ObjectType objectType,
                         Column column) {
        super( identifier,
               objectType,
               new ColumnExtractor(objectType));
        this.column = column;
    }

    /**
     * @return Returns the column.
     */
    public Column getColumn() {
        return this.column;
    }

}
