package org.drools.template.parser;

/**
 * A column of type Long in a decision table
 */
public class DoubleColumn extends AbstractColumn {

    public DoubleColumn(String n) {
        super(n);
    }

    public Cell createCell(Row row) {
        return new DoubleCell(row, this);
    }

    public String getCellType() {
        return "DoubleCell";
    }

}
