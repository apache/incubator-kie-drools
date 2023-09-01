package org.drools.template.parser;

/**
 * A cell in a decision table containing a long value
 */
public class DoubleCell extends AbstractCell<Double> {

    public DoubleCell(Row row, Column column) {
        super(row, column);
    }

    public void setValue(String v) {
        value = Double.valueOf(v);
    }
}
