package org.drools.template.parser;

import org.drools.util.StringUtils;
import org.drools.template.model.Condition;
import org.drools.template.model.Rule;
import org.drools.template.model.SnippetBuilder;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The default column condition for a rule template to be generated. If the
 * conditon starts with "!" then the template will only be generated if the
 * column condition does not exist. If there is no condition string then the
 * template will be generated only if the column contains a value. The condition
 * can be any valid rule condition.
 */
class DefaultTemplateColumn implements TemplateColumn {
    private static final Pattern COLUMN_PATTERN = Pattern
            .compile("^(!?)([a-zA-Z0-9_]*)(\\[([0-9]+)\\])?\\s*(.*)");

    private boolean notCondition;

    private String columnName;

    private String condition;

    private TemplateContainer templateContainer;

    private int index = -1;

    DefaultTemplateColumn(TemplateContainer tc, String columnString) {
        templateContainer = tc;
        Matcher matcher = COLUMN_PATTERN.matcher(columnString);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("column " + columnString
                                               + " is not valid");
        }
        notCondition = !StringUtils.isEmpty(matcher.group(1));
        columnName = matcher.group(2);
        String indexString = matcher.group(4);
        condition = matcher.group(5);
        if (!StringUtils.isEmpty(indexString)) {
            index = Integer.parseInt(indexString);
        }
    }

    private void createCellCondition(final Rule rule) {
        StringBuilder conditionString = new StringBuilder();
        Column column = templateContainer.getColumn(columnName);
        column.getCondition(condition, index);

        if (notCondition) {
            conditionString.append("not ");
        }
        conditionString.append("exists ");
        conditionString.append(column.getCondition(condition, index));
        SnippetBuilder snip = new SnippetBuilder(conditionString.toString());
        Condition condition = new Condition();
        condition.setSnippet(snip.build(columnName));
        rule.addCondition(condition);
    }

    private void createColumnCondition(final Rule rule, final String value) {
        SnippetBuilder colSnip = new SnippetBuilder(
                "$param : Column(name == \"$param\")");
        Condition colCondition = new Condition();
        colCondition.setSnippet(colSnip.build(value));
        rule.addCondition(colCondition);
    }

    public void addCondition(Rule rule) {
        createColumnCondition(rule, columnName);
        createCellCondition(rule);
    }

    public String getName() {
        return columnName;
    }

    public boolean isNotCondition() {
        return notCondition;
    }

    public String getCondition() {
        return condition;
    }
}
