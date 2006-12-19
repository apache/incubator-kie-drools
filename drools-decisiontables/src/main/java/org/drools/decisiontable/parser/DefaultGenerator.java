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
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.antlr.stringtemplate.StringTemplate;

public class DefaultGenerator implements Generator {

	Map ruleTemplates;

	Map templates = new HashMap();

	List rules = new ArrayList();

	public DefaultGenerator(final Map t) {
		ruleTemplates = t;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.drools.decisiontable.parser.Generator#generate(java.lang.String,
	 *      org.drools.decisiontable.parser.Row)
	 */
	public void generate(String templateName, Row row) {
		try {
			StringTemplate t = getTemplate(templateName);
			t.setAttribute("row", row);

			for (Iterator it = row.getCells().iterator(); it.hasNext();) {
				Cell cell = (Cell) it.next();
				cell.addValue(t);
			}
			String drl = t.toString();
			rules.add(drl);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private StringTemplate getTemplate(String templateName) throws IOException {
		String contents = (String) templates.get(templateName);
		if (contents == null) {
			RuleTemplate template = (RuleTemplate) ruleTemplates
					.get(templateName);
			contents = template.getContents();
			templates.put(templateName, contents);
		}
		StringTemplate t = new StringTemplate(contents);
		return t;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.drools.decisiontable.parser.Generator#getDrl()
	 */
	public String getDrl() {
		StringBuffer sb = new StringBuffer();
		for (Iterator it = rules.iterator(); it.hasNext();) {
			String rule = (String) it.next();
			sb.append(rule).append("\n");
		}
		return sb.toString();
	}

}
