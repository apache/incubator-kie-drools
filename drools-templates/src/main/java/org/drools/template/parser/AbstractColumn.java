package org.drools.template.parser;

import org.drools.util.StringUtils;

/**
 * Base column of in a decision table
 */
public abstract class AbstractColumn implements Column {
    private String name;

    public AbstractColumn(String n) {
        this.name = n;
    }

    public String getName() {
        return name;
    }

    public String getCondition(String condition, int index) {
        StringBuilder conditionString = new StringBuilder(getCellType());
        conditionString.append("(row == r, column == $param");
        if (index != -1) {
            conditionString.append(", index == ").append(index);
        }
        if (!StringUtils.isEmpty(condition)) {
            conditionString.append(", value ").append(condition);
        }
        conditionString.append(")");
        return conditionString.toString();
    }


}
