/*
 * Copyright 2005 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.template.parser;

import org.drools.core.util.StringUtils;
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
        StringBuffer conditionString = new StringBuffer();
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
