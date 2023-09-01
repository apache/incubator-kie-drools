package org.drools.template.parser;

/**
 * A column of type Long in a decision table
 */
public class BooleanColumn extends AbstractColumn {

    public BooleanColumn(String n) {
        super(n);
    }
    //
    //    public void addValue(Map vars, Object value) {
    //        vars.put(getName(), value);
    //    }

    public Cell createCell(Row row) {
        return new BooleanCell(row, this);
    }

    public String getCellType() {
        return "BooleanCell";
    }

}
