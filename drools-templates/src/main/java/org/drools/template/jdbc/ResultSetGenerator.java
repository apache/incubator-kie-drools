package org.drools.template.jdbc;

import org.drools.template.parser.TemplateContainer;
import org.drools.template.parser.DefaultTemplateContainer;
import org.drools.template.parser.TemplateDataListener;
import org.drools.template.parser.DataListener;

import java.io.InputStream;
import java.util.List;
import java.util.ArrayList;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

/**
 * <p> A Drools template compiler which takes a ResultSet and compiles it into
 * a template using DefaultTemplateContainer.</p>
 *
 * To use simply you need a JDBC ResultSet - with the field names mapping to the field names used in the template !
 *
 * @author <a href="mailto:javatestcase@yahoo.com">Bill Tarr</a>
 */
public class ResultSetGenerator {

/**
 * Generates DRL from a data provider for the spreadsheet data and templates.
 *
 * @param rs       the resultset for the table data
 * @param template the string containing the template resource name
 * @return the generated DRL text as a String
 */
public String compile(final ResultSet rs,
                      final String template) {
    final InputStream templateStream = this.getClass().getResourceAsStream(template);
    return compile(rs,
            templateStream);
}

/**
 * Generates DRL from a data provider for the spreadsheet data and templates.
 *
 * @param rs             the resultset for the table data
 * @param templateStream the InputStream for reading the templates
 * @return the generated DRL text as a String
 */
public String compile(final ResultSet rs,
                      final InputStream templateStream) {
    TemplateContainer tc = new DefaultTemplateContainer(templateStream);
    closeStream(templateStream);
    return compile(rs,
            new TemplateDataListener(tc));
}

/**
 * Generates DRL from a data provider for the spreadsheet data and templates.
 *
 * @param rs       the resultset for the table data
 * @param listener a template data listener
 * @return the generated DRL text as a String
 */
public String compile(final ResultSet rs,
                      final TemplateDataListener listener) {
    List<DataListener> listeners = new ArrayList<DataListener>();
    listeners.add(listener);
    processData(rs,
            listeners);
    return listener.renderDRL();
}

/**
 * Iterate through the resultset.
 * @param rs       the resultset for the table data
 * @param listeners list of template data listener
 */
private void processData(final ResultSet rs,
                         List<DataListener> listeners) {

    try {
        ResultSetMetaData rsmd = rs.getMetaData();
        int colCount = rsmd.getColumnCount();

        int i = 0;

        while (rs.next()) {
            newRow(listeners, i, colCount);
            for (int cellNum = 1; cellNum < colCount + 1; cellNum++) {
                String cell;

                int sqlType = rsmd.getColumnType(cellNum);
                switch (sqlType) {
                    case java.sql.Types.DATE:
                        cell = rs.getDate(cellNum).toString();
                        break;
                    case java.sql.Types.INTEGER:
                    case java.sql.Types.DOUBLE:
                        cell = String.valueOf(rs.getInt(cellNum));
                        break;
                    default:
                        cell = rs.getString(cellNum);
                }

                newCell(listeners,
                        i,
                        cellNum -1,
                        cell,
                        DataListener.NON_MERGED);
            }
            i++;
        }

    } catch (SQLException e) {
         //TODO: you need to throw or handle
    }

    finishData(listeners);
}

private void finishData(List<DataListener> listeners) {
    for (DataListener listener : listeners) {
        listener.finishSheet();
    }
}

private void newRow(List<DataListener> listeners,
                    int row,
                    int cols) {
    for (DataListener listener : listeners) {
        listener.newRow(row,
                cols);
    }
}

public void newCell(List<DataListener> listeners,
                    int row,
                    int column,
                    String value,
                    int mergedColStart) {
    for (DataListener listener : listeners) {
        listener.newCell(row,
                column,
                value,
                mergedColStart);
    }
}

protected void closeStream(final InputStream stream) {
    try {
        stream.close();
    } catch (final Exception e) {
        System.err.print("WARNING: Wasn't able to correctly close stream for rule template. " + e.getMessage());
    }
}
}
