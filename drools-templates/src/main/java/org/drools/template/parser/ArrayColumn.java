package org.drools.template.parser;

import org.drools.util.StringUtils;

/**
 * A column in a decision table that represents an array (comma-delimited)
 * of values.
 */
public class ArrayColumn extends AbstractColumn {

    private Column type;

    public ArrayColumn(String n, Column typeColumn) {
        super(n);
        this.type = typeColumn;
    }

    public Cell createCell(Row row) {
        return new ArrayCell(row, this);
    }

    public String getCellType() {
        return type.getCellType();
    }

    public Column getType() {
        return type;
    }

    public String getCondition(String condition, int index) {
        if (index == -1) {
            StringBuilder conditionString = new StringBuilder("ArrayCell(row == r, column == $param");
            if (!StringUtils.isEmpty(condition)) {
                conditionString.append(", value ").append(condition);
            }
            conditionString.append(")");
            return conditionString.toString();
        } else {
            return type.getCondition(condition, index);
        }

    }

}
