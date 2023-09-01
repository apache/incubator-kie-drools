package org.drools.template.parser;

/**
 * Callback interface for scanning an spreadsheet.
 */
public interface DataListener {

    int NON_MERGED = -1;

    /**
     * Start a new sheet
     *
     * @param name the sheet name
     */
    void startSheet(String name);

    /**
     * Come to the end of the sheet.
     */
    void finishSheet();

    /**
     * Enter a new row.
     *
     * @param rowNumber
     * @param columns
     */
    void newRow(int rowNumber,
            int columns);

    /**
     * Enter a new cell.
     * Do NOT call this event for trailling cells at the end of the line.
     * It will just confuse the parser. If all the trailing cells are empty, just
     * stop raising events.
     *
     * @param row            the row number
     * @param column         the column alpha character label
     * @param value          the string value of the cell
     * @param mergedColStart the "source" column if it is merged. -1 otherwise.
     */
    void newCell(int row,
            int column,
            String value,
            int mergedColStart);

}
