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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author <a href="mailto:stevearoonie@gmail.com">Steven Williams</a>
 * 
 * Container for a set of templates (residing in one file). This class will
 * parse the template file.
 * 
 */
public class DefaultTemplateContainer implements TemplateContainer {
	private String header;

	private Map<String, Column> columnMap = new HashMap<String, Column>();

	private List<Column> columns = new ArrayList<Column>();

	private Map<String, RuleTemplate> templates = new HashMap<String, RuleTemplate>();

	public DefaultTemplateContainer(final String template) {
		this(DefaultTemplateContainer.class.getResourceAsStream(template));
	}

	public DefaultTemplateContainer(final InputStream templateStream) {
		parseTemplate(templateStream);
		validateTemplate();
	}

	private void validateTemplate() {
		if (columns.size() == 0) {
			throw new DecisionTableParseException("Missing header columns");
		}
		if (templates.size() == 0) {
			throw new DecisionTableParseException("Missing templates");
		}

	}

	private void parseTemplate(final InputStream templateStream) {
		try {
			final ColumnFactory cf = new ColumnFactory();
			final BufferedReader templateReader = new BufferedReader(
					new InputStreamReader(templateStream));
			String line = null;
			StringBuffer header = new StringBuffer();
			boolean inTemplate = false;
			boolean inHeader = false;
			boolean inContents = false;
			RuleTemplate template = null;
			StringBuffer contents = new StringBuffer();
			while ((line = templateReader.readLine()) != null) {
				if (line.trim().length() > 0) {
					if (line.startsWith("template header")) {
						inHeader = true;
					} else if (line.startsWith("template")) {
						inTemplate = true;
						String quotedName = line.substring(8).trim();
						quotedName = quotedName.substring(1, quotedName
								.length() - 1);
						template = new RuleTemplate(quotedName, this);
						addTemplate(template);

					} else if (line.startsWith("package")) {
						if (inHeader == false) {
							throw new DecisionTableParseException(
									"Missing header");
						}
						inHeader = false;
						header.append(line).append("\n");
					} else if (inHeader) {
						addColumn(cf.getColumn(line.trim()));
					} else if (!inTemplate && !inHeader) {
						header.append(line).append("\n");
					} else if (!inContents && line.startsWith("rule")) {
						inContents = true;
						contents.append(line).append("\n");
					} else if (line.equals("end template")) {
						template.setContents(contents.toString());
						contents.setLength(0);
						inTemplate = false;
						inContents = false;
					} else if (inContents) {
						contents.append(line).append("\n");
					} else if (inTemplate) {
						template.addColumn(line.trim());
					}
				}

			}
			if (inTemplate) {
				throw new DecisionTableParseException("Missing end template");
			}
			this.header = header.toString();

		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			if (templateStream != null)
				closeStream(templateStream);
		}
	}

	private void addTemplate(RuleTemplate template) {
		templates.put(template.getName(), template);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.drools.decisiontable.parser.TemplateContainer#getTemplates()
	 */
	public Map<String, RuleTemplate> getTemplates() {
		return templates;
	}

	private void addColumn(Column c) {
		columns.add(c);
		columnMap.put(c.getName(), c);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.drools.decisiontable.parser.TemplateContainer#getColumns()
	 */
	public Column[] getColumns() {
		return (Column[]) columns.toArray(new Column[columns.size()]);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.drools.decisiontable.parser.TemplateContainer#getHeader()
	 */
	public String getHeader() {
		return header;
	}

	private void closeStream(final InputStream stream) {
		try {
			stream.close();
		} catch (final Exception e) {
			System.err.print("WARNING: Wasn't able to "
					+ "correctly close stream for decision table. "
					+ e.getMessage());
		}
	}

	public Column getColumn(final String name) {
		return (Column) columnMap.get(name);
	}
}
