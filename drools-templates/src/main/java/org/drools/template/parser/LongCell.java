package org.drools.template.parser;

/**
 * A cell in a decision table containing a long value
 */
public class LongCell extends AbstractCell<Long> {

    public LongCell(Row row, Column column) {
        super(row, column);
    }

    public void setValue(String v) {
        value = Long.valueOf(v);
    }
}
