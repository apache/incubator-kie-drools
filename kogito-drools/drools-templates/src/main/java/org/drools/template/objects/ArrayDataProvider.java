package org.drools.template.objects;

import org.drools.template.DataProvider;

public class ArrayDataProvider implements DataProvider {

    private String[][] rows;
    private int currRow;
    private int rowsCount;

    public ArrayDataProvider(String[][] rows) {
        if (rows == null) {
            this.rows = new String[0][0];
        } else {
            this.rows = rows;
        }
        this.currRow = 0;
        this.rowsCount = this.rows.length;
    }

    public boolean hasNext() {
        return currRow < rowsCount;
    }

    public String[] next() {
        return rows[currRow++];
    }

}
