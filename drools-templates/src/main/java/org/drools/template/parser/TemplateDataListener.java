package org.drools.template.parser;

import java.io.InputStream;
import java.util.Map;
import java.util.Objects;

import org.drools.template.model.DRLOutput;

/**
 * SheetListener for creating rules from a template
 */
public class TemplateDataListener
        implements
        DataListener {

    private int startRow = -1;

    private boolean tableFinished = false;

    private Row currentRow;

    private Column[] columns;

    private TemplateContainer templateContainer;

    private int startCol;

    private Generator generator;

    private boolean checkEmptyRows = true;

    // private WorkingMemoryFileLogger logger;

    public TemplateDataListener(final TemplateContainer tc) {
        this(1,
                1,
                tc,
                true);
    }

    public TemplateDataListener(final TemplateContainer tc,
            final boolean checkEmptyRows) {
        this(1,
                1,
                tc,
                checkEmptyRows);
    }

    public TemplateDataListener(final int startRow,
            final int startCol,
            final String template) {
        this(startRow,
                startCol,
                new DefaultTemplateContainer(template));
    }

    public TemplateDataListener(final int startRow,
            final int startCol,
            final InputStream templateStream) {
        this(startRow,
                startCol,
                new DefaultTemplateContainer(templateStream));
    }

    public TemplateDataListener(final int startRow,
            final int startCol,
            final TemplateContainer tc) {
        this(startRow,
                startCol,
                tc,
                true);
    }

    public TemplateDataListener(final int startRow,
            final int startCol,
            final TemplateContainer tc,
            final boolean checkEmptyRows) {
        this(startRow,
                startCol,
                tc,
                new DefaultGenerator(tc.getTemplates()),
                checkEmptyRows);
    }

    public TemplateDataListener(final int startRow,
            final int startCol,
            final TemplateContainer tc,
            final Generator generator) {
        this(startRow,
                startCol,
                tc,
                generator,
                true);
    }

    public TemplateDataListener(final int startRow,
            final int startCol,
            final TemplateContainer tc,
            final Generator generator,
            final boolean checkEmptyRows) {
        this.startRow = startRow - 1;
        this.startCol = startCol - 1;
        this.columns = tc.getColumns();
        this.templateContainer = tc;
        this.generator = generator;
        this.checkEmptyRows = checkEmptyRows;
    }

    public void finishSheet() {
        if (currentRow != null) {
            generateDRLForTemplates(currentRow);
        }
    }

    public void newCell(int row,
            int column,
            String value,
            int mergedColStart) {
        if (currentRow != null && column >= startCol && value != null && value.trim().length() > 0) {
            int columnIndex = column - startCol;
            if (columnIndex < columns.length) {
                Cell cell = currentRow.getCell(columnIndex);
                cell.setValue(value);
            }
        }
    }

    public void newRow(int rowNumber,
            int columnCount) {
        if (!tableFinished && rowNumber >= startRow) {
            if (currentRow != null && (checkEmptyRows && currentRow.isEmpty())) {
                currentRow = null;
                tableFinished = true;
            } else {
                if (currentRow != null) {
                    generateDRLForTemplates(currentRow);
                }
                currentRow = new Row(rowNumber,
                        columns);
            }
        }
    }

    public void startSheet(String name) {

    }

    public String renderDRL() {
        DRLOutput out = new DRLOutput();
        out.writeLine(templateContainer.getHeader());

        out.writeLine(generator.getDrl());
        // System.err.println(out.getDRL());
        return out.getDRL();
    }

    private void generateDRLForTemplates(Row row) {
        Map<String, RuleTemplate> ruleTemplates = templateContainer.getTemplates();
        if (Objects.nonNull(ruleTemplates)) {
            ruleTemplates.forEach((name, rc) -> {
                generator.generate(name, row);
            });
        }
    }

}
