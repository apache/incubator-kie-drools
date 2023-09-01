package org.drools.template.parser;

/**
 * A column of type Long in a decision table
 */
public class LongColumn extends AbstractColumn {

    public LongColumn(String n) {
        super(n);
    }

    public Cell createCell(Row row) {
        return new LongCell(row, this);
    }

    public String getCellType() {
        return "LongCell";
    }

}
