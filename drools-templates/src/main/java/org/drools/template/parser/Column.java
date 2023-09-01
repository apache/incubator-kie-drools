package org.drools.template.parser;

/**
 * A column in a decision table
 */
public interface Column {
    String getName();

    Cell createCell(Row row);

    String getCellType();

    String getCondition(String condition, int index);

}
