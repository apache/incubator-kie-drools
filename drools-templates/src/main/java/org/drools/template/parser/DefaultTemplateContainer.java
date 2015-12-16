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

import org.drools.core.util.IoUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Container for a set of templates (residing in one file). This class will
 * parse the template file.
 */
public class DefaultTemplateContainer implements TemplateContainer {
    private String header;

    private Map<String, Column> columnMap = new HashMap<String, Column>();

    private List<Column> columns = new ArrayList<Column>();

    private Map<String, RuleTemplate> templates = new HashMap<String, RuleTemplate>();

    private boolean replaceOptionals;

    public DefaultTemplateContainer(final String template) {
        this(DefaultTemplateContainer.class.getResourceAsStream(template), true);
    }

    public DefaultTemplateContainer(final InputStream templateStream) {
        this(templateStream, true);
    }

    public DefaultTemplateContainer(final String template, boolean replaceOptionals) {
        this(DefaultTemplateContainer.class.getResourceAsStream(template), replaceOptionals);
    }

    public DefaultTemplateContainer(final InputStream templateStream, boolean replaceOptionals) {
        this.replaceOptionals = replaceOptionals;
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
                    new InputStreamReader(templateStream, IoUtils.UTF8_CHARSET));
            String line;

            boolean inTemplate = false;
            boolean inHeader = false;
            boolean inContents = false;
            boolean inMultiLineComment = false;

            RuleTemplate template = null;
            StringBuilder header = new StringBuilder();
            StringBuilder contents = new StringBuilder();

            while ((line = templateReader.readLine()) != null) {

                if (inMultiLineComment) {
                    int commentEnd = line.indexOf( "*/" );
                    if (commentEnd >= 0) {
                        line = line.substring( commentEnd+2 );
                        inMultiLineComment = false;
                    } else {
                        line = "";
                    }
                } else {
                    int commentStart = line.indexOf( "/*" );
                    if (commentStart >= 0) {
                        int commentEnd = line.indexOf( "*/" );
                        if (commentEnd > commentStart) {
                            line = line.substring( 0, commentStart ) + line.substring( commentEnd+2 );
                        } else {
                            line = line.substring( 0, commentStart );
                            inMultiLineComment = true;
                        }
                    }
                }

                String trimmed = line.trim();
                if (trimmed.length() > 0) {
                    if (trimmed.startsWith("template header")) {
                        inHeader = true;

                    } else if (trimmed.startsWith("template")) {
                        inTemplate = true;
                        inHeader = false;
                        String quotedName = trimmed.substring(8).trim();
                        quotedName = quotedName.substring(1, quotedName.length() - 1);
                        template = new RuleTemplate(quotedName, this, replaceOptionals );
                        addTemplate(template);

                    } else if (trimmed.startsWith("package")) {
                        if ( !inHeader ) {
                            throw new DecisionTableParseException(
                                    "Missing header");
                        }
                        inHeader = false;
                        header.append(line).append("\n");

                    } else if (trimmed.startsWith("import")) {
                        inHeader = false;
                        header.append(line).append("\n");

                    } else if (inHeader) {
                        addColumn(cf.getColumn(trimmed));

                    } else if (!inTemplate) {
                        header.append(line).append("\n");

                    } else if (!inContents && trimmed.startsWith("rule")) {
                        inContents = true;
                        contents.append(line).append("\n");

                    } else if (trimmed.equals("end template")) {
                        template.setContents(contents.toString());
                        contents.setLength(0);
                        inTemplate = false;
                        inContents = false;

                    } else if (inContents) {
                        contents.append(removeSingleLineComment(line)).append( "\n");

                    } else {
                        template.addColumn(trimmed);
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
            if (templateStream != null) { closeStream(templateStream); }
        }
    }

    private String removeSingleLineComment(String line) {
        int commentStart = line.indexOf( "//" );
        return commentStart < 0 ? line : line.substring( 0, commentStart );
    }

    private void addTemplate(RuleTemplate template) {
        templates.put(template.getName(), template);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.kie.decisiontable.parser.TemplateContainer#getTemplates()
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
     * @see org.kie.decisiontable.parser.TemplateContainer#getColumns()
     */
    public Column[] getColumns() {
        return columns.toArray(new Column[columns.size()]);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.kie.decisiontable.parser.TemplateContainer#getHeader()
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
        return columnMap.get(name);
    }
}
