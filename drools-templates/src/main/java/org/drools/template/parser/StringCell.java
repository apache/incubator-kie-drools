package org.drools.template.parser;

import org.drools.util.StringUtils;

/**
 * A cell in a decision table containing a String value
 */
public class StringCell extends AbstractCell<String> {

    StringCell(Row r, Column c) {
        super(r, c);
    }

    public void setValue(String v) {
        value = v;
    }

    public boolean isEmpty() {
        return StringUtils.isEmpty(value);
    }
}
