package org.drools.decisiontable.parser;

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
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.drools.util.StringUtils;

/**
 * @author <a href="mailto:stevearoonie@gmail.com">Steven Williams</a>
 * 
 * A rule template made up of a name, the decision table columns required, 
 * the decision table columns that must be empty and the contents of the
 * rule.
 */
public class RuleTemplate
{
    private String name;
    private String contents;
    private List columns;
    private List notColumns;

    public RuleTemplate(final String n)
    {
        name = n;
        columns = new ArrayList();
        notColumns = new ArrayList();
    }

    public String getName()
    {
        return name;
    }

    public List getColumns()
    {
        return columns;
    }

    public String[] getNotColumns()
    {
        return (String[]) notColumns.toArray(new String[notColumns.size()]);
    }
    
    public String getContents()
    {
        return contents;
    }

    public void addColumn(String column)
    {
        if (column.startsWith("!"))
        {
            this.notColumns.add(column.substring(1));
        }
        else
        {
            this.columns.add(column);
        }
    }

    public void setContents(String contents)
    {
        this.contents = replaceOptionals(contents);
    }

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
	       return "RuleTemplate[name,"+this.name+"notColumns,"+this.notColumns+"contents,"+this.columns+"columns";
	}
	/*
	 * Replace the optional columns in the rule contents with an if statement.
	 * if (column is empty) do not show the line.
	 * This is based on antlr StringTemplate and should be replaced with MVEL.
	 */
	private String replaceOptionals(String contents) {
		try {
			final Pattern pattern = Pattern.compile("\\$(.[^\\$]*)\\$");
			final List columns = new ArrayList(getColumns());
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
						newLine.append("$if(").append(matcher.group(1)).append(
								")$");
						optCols++;
					}
				}
				newLine.append(line);
				newLine.append(StringUtils.repeat("$endif$", optCols));
				newLine.append("\n");
			}
			// System.out.println("newLine: " + newLine);
			return newLine.toString();

		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
    
}
