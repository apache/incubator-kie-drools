package org.drools.decisiontable.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.drools.template.model.SnippetBuilder;
import org.drools.template.parser.DecisionTableParseException;

/**
 * Builds up a consequence entry.
 */
public class RhsBuilder implements SourceBuilder {

    private int headerRow;
    private int headerCol;
    private ActionType.Code actionTypeCode;
    private Map<Integer, String> templates;
    private String variable;
    private List<String> values;
    private boolean hasValues;

    /**
     * @param boundVariable Pass in a bound variable if there is one.
     * Any cells below then will be called as methods on it.
     * Leaving it blank will make it work in "classic" mode.
     */
    public RhsBuilder(ActionType.Code code, int row, int column, String boundVariable) {
        this.actionTypeCode = code;
        this.headerRow = row;
        this.headerCol = column;
        this.variable = boundVariable == null ? "" : boundVariable.trim();
        this.templates = new HashMap<>();
        this.values = new ArrayList<>();
    }

    public ActionType.Code getActionTypeCode() {
        return this.actionTypeCode;
    }

    public void addTemplate(int row, int column, String content) {
        Integer key = Integer.valueOf(column);
        content = content.trim();
        if (isBoundVar()) {
            content = variable + "." + content + ";";
        }
        this.templates.put(key, content);
    }

    private boolean isBoundVar() {
        return !("".equals(variable));
    }

    public void addCellValue(int row, int column, String value) {
        hasValues = true;
        String template = this.templates.get(Integer.valueOf(column));
        if (template == null) {
            throw new DecisionTableParseException("No code snippet for " +
                                                          this.actionTypeCode + ", above cell " +
                                                          RuleSheetParserUtil.rc2name(this.headerRow + 2, this.headerCol));
        }
        SnippetBuilder snip = new SnippetBuilder(template);
        this.values.add(snip.build(value));
    }

    public void clearValues() {
        this.hasValues = false;
        this.values.clear();
    }

    public String getResult() {
        StringBuilder buf = new StringBuilder();
        for ( Iterator<String> iter = this.values.iterator(); iter.hasNext(); ) {
            buf.append( iter.next() );
            if (iter.hasNext()) {
                buf.append( '\n' );
            }
        }
        return buf.toString();
    }

    public boolean hasValues() {
        return hasValues;
    }

    public int getColumn() {
        return headerCol;
    }
}
