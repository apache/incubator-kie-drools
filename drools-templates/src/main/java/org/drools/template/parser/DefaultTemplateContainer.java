package org.drools.template.parser;

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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

	public DefaultTemplateContainer(final InputStream templateReader) {
		parseTemplate(templateReader);
		validateTemplate();
	}

	private void validateTemplate() {
		if (columns.size() == 0) {
			throw new DecisionTableParseException("No template parameters");
		}
		if (templates.size() == 0) {
			throw new DecisionTableParseException("No templates");
		}
	}
	
	Pattern parPattern = Pattern.compile( "@\\{([^}]+)\\}" );

	/**
	 * Check whether all parameters have been defined.
	 * @param line a line in a rule
	 * qparam lno the line number
	 */
	private void checkLine( String line, int lno ){
	    Matcher parMatcher = parPattern.matcher( line );
	    while( parMatcher.find() ){
	        String par = parMatcher.group( 1 ).trim();
	        if( ! columnMap.containsKey( par ) &&
	            ! "row.rowNumber".equals( par ) ){
	            throw new DecisionTableParseException( "Undeclared parameter '" + par + "' in line " + lno );
	        }
	    }
	}
	
	private void parseTemplate(final InputStream givenStream) {
		try {
		    InputStreamReader isr =  new InputStreamReader( givenStream );
	        LineNumberReader templateReader = new LineNumberReader( isr );
			final ColumnFactory cf = new ColumnFactory();
			String line = null;
			StringBuilder header = new StringBuilder();
			boolean inTemplate = false;
			boolean inHeader = false;
			boolean inContents = false;
			RuleTemplate template = null;
			StringBuilder contents = new StringBuilder();
            String lastTemplateName = "?";
            int    lastTemplateLine = 0;
			while ((line = templateReader.readLine()) != null) {
			    String trimmed = line.trim();
				if( trimmed.length() > 0 ){
				    int lineNo = templateReader.getLineNumber();
				    
					if( trimmed.matches( "template\\s+header" ) ){
						inHeader = true;
					
					} else if (trimmed.startsWith("template")) {
					    if( inTemplate ){
					        throw new DecisionTableParseException(
                            "Nested template, within '" + lastTemplateName + "'. at line " + lineNo );
					    }
						inTemplate = true;
						String quotedName = line.substring(8).trim();						
                        if( quotedName.startsWith( "\"" ) && quotedName.endsWith( "\"" ) ||
                            quotedName.startsWith( "'" ) && quotedName.endsWith( "'" ) ){
 						    quotedName = quotedName.substring(1, quotedName.length() - 1);
                        }
                        if( quotedName.length() == 0 ){
                            throw new DecisionTableParseException(
                                    "Template name missing at line " + lineNo );
                        }
                        if( templates.containsKey( quotedName ) ){
                            throw new DecisionTableParseException(
                                    "Duplicate template name '" + quotedName + "' at line " + lineNo );
                        }
                        lastTemplateName = quotedName;
                        lastTemplateLine = lineNo;
						template = new RuleTemplate( quotedName, this);
						addTemplate(template);

					} else if ( trimmed.startsWith("package") ) {
						if ( ! inHeader ) {
							throw new DecisionTableParseException(
									"Missing header at line " + lineNo );
						}
						inHeader = false;
						header.append(line).append("\n");
						
					} else if ( inHeader ) {
						if( ! addColumn(cf.getColumn(trimmed)) ){
						    throw new DecisionTableParseException( "Duplicate parameter '" +
						            trimmed + "' at line " + lineNo);
						}
						
					} else if (!inTemplate && !inHeader) {
						header.append(line).append("\n");
						
					} else if (!inContents && trimmed.startsWith("rule")) {
					    if( !inTemplate ){
	                        header.append(line).append("\n");
					    } else {
     						inContents = true;
                            checkLine( line, lineNo );
	    					contents.append(line).append("\n");	    					
					    }
					    
					} else if (trimmed.matches("end\\s+template")) {
						template.setContents(contents.toString());
						contents.setLength(0);
						inTemplate = false;
						inContents = false;
						
					} else if (inContents) {
					    checkLine( line, lineNo );
						contents.append(line).append("\n");
						
					} else if (inTemplate) {
						template.addColumn( trimmed );
					}
				}
			}
			if (inTemplate) {
				throw new DecisionTableParseException( "Unterminated template '" +
				        lastTemplateName + "', started in line " + lastTemplateLine );
			}
			this.header = header.toString();

		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			if (givenStream != null)
				closeStream(givenStream);
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

	private boolean addColumn(Column c) {
		columns.add(c);
		return null == columnMap.put(c.getName(), c);
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
			System.err.print("Warning: Unable to "
					+ "close stream for decision table template. "
					+ e.getMessage());
		}
	}

	public Column getColumn(final String name) {
		return (Column) columnMap.get(name);
	}
}
