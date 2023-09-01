package org.drools.template.parser;

/**
 * A column of type String in a decision table
 */
public class StringColumn extends AbstractColumn {

    public StringColumn(String n) {
        super(n);
    }
    //
    //    public void addValue(Map vars, Object value) {
    //        vars.put(getName(), value);
    //    }

    public Cell createCell(Row row) {
        return new StringCell(row, this);
    }

    public String getCellType() {
        return "StringCell";
    }

}
