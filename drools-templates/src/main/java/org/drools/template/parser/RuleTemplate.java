/*
 * Copyright 2005 JBoss Inc
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
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.drools.core.util.StringUtils;

/**
 * @author <a href="mailto:stevearoonie@gmail.com">Steven Williams</a>
 * 
 * A rule template made up of a name, the decision table columns required, the
 * decision table columns that must be empty and the contents of the rule.
 */
public class RuleTemplate {
    private String name;

    private String contents;

    private List<TemplateColumn> columns;

    private TemplateContainer templateContainer;

    public RuleTemplate(final String n, final TemplateContainer tc) {
        name = n;
        columns = new ArrayList<TemplateColumn>();
        templateContainer = tc;
    }

    public String getName() {
        return name;
    }

    public List<TemplateColumn> getColumns() {
        return columns;
    }

    public String getContents() {
        return contents;
    }

    public void addColumn(String columnString) {
        TemplateColumn column = new DefaultTemplateColumn(templateContainer, columnString);
        this.columns.add(column);
    }

    public void setContents(String contents) {
        this.contents = replaceOptionals(contents);
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "RuleTemplate[name," + this.name + "contents," + this.columns
                + "columns";
    }

    /*
     * Replace the optional columns in the rule contents with an if statement.
     * if (column is empty) do not show the line.
     */
    private String replaceOptionals(String contents) {
        try {
            final Pattern pattern = Pattern.compile("@\\{(.[^}]*)\\}");
            final Collection<String> columns = getColumnNames();
            columns.add("row.rowNumber");
            final BufferedReader reader = new BufferedReader(new StringReader(
                    contents));
            String line = null;
            final StringBuffer newLine = new StringBuffer();
            while ((line = reader.readLine()) != null) {
                final Matcher matcher = pattern.matcher(line);
                int optCols = 0;
                while (matcher.find()) {
                    final String c = matcher.group(1);
                    if (!columns.contains(c)) {
                        newLine.append("@if{").append(matcher.group(1)).append(
                                " != null}");
                        optCols++;
                    }
                }
                newLine.append(line);
                newLine.append(StringUtils.repeat("@end{}", optCols));
                newLine.append("\n");
            }
//			System.out.println("newLine: " + newLine);
            return newLine.toString();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Collection<String> getColumnNames() {
        Collection<String> columnNames = new ArrayList<String>();
        for ( TemplateColumn column : getColumns() ) {
            columnNames.add(column.getName());
        }
        return columnNames;
    }

}
