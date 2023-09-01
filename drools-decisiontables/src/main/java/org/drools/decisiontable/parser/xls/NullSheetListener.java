package org.drools.decisiontable.parser.xls;

import org.drools.template.parser.DataListener;

/**
 *
 * Null listner.
 */
public class NullSheetListener
    implements
    DataListener {

    public void startSheet(final String name) {
    }

    public void finishSheet() {
    }

    public void newRow(final int rowNumber,
                       final int columns) {
    }

    public void newCell(final int row,
                        final int column,
                        final String value,
                        final int mergedColstart) {
    }

}
