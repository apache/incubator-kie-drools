package org.drools.template.parser;

/**
 * A cell in a decision table containing a long value
 */
public class BooleanCell extends AbstractCell<Boolean> {

    BooleanCell(Row r, Column c) {
        super(r, c);
    }

    public void setValue(String v) {
        value = Boolean.valueOf(v);
    }
}
