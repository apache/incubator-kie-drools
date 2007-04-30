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

import org.mvel.MVELTemplateRegistry;
import org.mvel.TemplateInterpreter;
import org.mvel.TemplateRegistry;
/**
 * @author <a href="mailto:stevearoonie@gmail.com">Steven Williams</a>
 * 
 * Generate the rules for a decision table row from a rule template.
 */
public class DefaultGenerator implements Generator {

	private Map ruleTemplates;

	private TemplateRegistry registry = new MVELTemplateRegistry();

	private List rules = new ArrayList();

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
			String content = getTemplate(templateName);
            Map vars = new HashMap();
			vars.put("row", row);

			for (Iterator it = row.getCells().iterator(); it.hasNext();) {
				Cell cell = (Cell) it.next();
				cell.addValue(vars);
			}
			String drl = (String) TemplateInterpreter.parse( content, null, vars, this.registry ); 
			rules.add(drl);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private String getTemplate(String templateName) throws IOException {
		String contents = (String) registry.getTemplate( templateName );
		if (contents == null) {
			RuleTemplate template = (RuleTemplate) ruleTemplates
					.get(templateName);
			contents = template.getContents();
            registry.registerTemplate( templateName, contents);
		}
		return contents;
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
