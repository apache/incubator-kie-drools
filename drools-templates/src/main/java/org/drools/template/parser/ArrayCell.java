/**
 * Cell containing an array of values
 */
package org.drools.template.parser;

import java.util.Map;

import org.drools.util.StringUtils;

public class ArrayCell extends AbstractCell<String> {

    private String[] values;

    public ArrayCell(final Row r, final ArrayColumn c) {
        super(r, c);
    }

    public void addValue(Map<String, Object> vars) {
        for (int i = 0; i < values.length; i++) {
            vars.put(column.getName() + i, values[i]);
        }
    }

    public void setIndex(int i) {
        throw new RuntimeException("You cannot call setQueueIndex on an ArrayCell");
    }

    public int getIndex() {
        return -1;
    }

    public void setValue(String v) {
        value = v;
        values = StringUtils.splitPreserveAllTokens(value, ",");
    }

    public boolean isEmpty() {
        return StringUtils.isEmpty(value);
    }

}
