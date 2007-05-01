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
import java.io.Reader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.StatefulSession;
import org.drools.compiler.PackageBuilder;
import org.drools.decisiontable.model.Condition;
import org.drools.decisiontable.model.Consequence;
import org.drools.decisiontable.model.DRLOutput;
import org.drools.decisiontable.model.Global;
import org.drools.decisiontable.model.Import;
import org.drools.decisiontable.model.Rule;
import org.drools.decisiontable.model.SnippetBuilder;
import org.drools.rule.Package;

/**
 * 
 * @author <a href="mailto:stevearoonie@gmail.com">Steven Williams</a>
 * 
 * Create a rule base for the set of rule templates in the 
 * TemplateContainer. These rules are used internally by the
 * engine to generate the actual decision table rules based on
 * which columns have been filled in.
 * 
 * Basically, if a rule template requires columns A and B then 
 * the template rule base will generate a rule with columns A and B
 * as the LHS and a RHS which triggers the rule to be generated.
 * ie.
 * rule "template1"
 *   when
 *     r : Row()
 *     Cell(row == r, column == "column1")
 *     Cell(row == r, column == "column2")
 *   then
 *     generator.generate( "template1", r);
 *   end
 * 
 */
public class DefaultTemplateRuleBase implements TemplateRuleBase {
	private RuleBase ruleBase;

	public DefaultTemplateRuleBase(final TemplateContainer tc) {
		ruleBase = readRule(getDTRules(tc.getTemplates()));
	}
	
	/* (non-Javadoc)
	 * @see org.drools.decisiontable.parser.TemplateRuleBase#newWorkingMemory()
	 */
	public StatefulSession newStatefulSession() {
		return ruleBase.newStatefulSession();
	}
	/**
	 * 
	 * @param templates
	 * @return
	 */
	private String getDTRules(Map templates) {
		org.drools.decisiontable.model.Package p = new org.drools.decisiontable.model.Package(
				"org.drools.decisiontable.parser");
		addImports(p);
		addGlobals(p);
		int i = 1;
		for (Iterator it = templates.values().iterator(); it.hasNext();) {
			RuleTemplate template = (RuleTemplate) it.next();

			createTemplateRule(p, i++, template);
		}
		DRLOutput out = new DRLOutput();
		p.renderDRL(out);
		return out.getDRL();

	}

	private void createTemplateRule(org.drools.decisiontable.model.Package p, int index, RuleTemplate template) {
		Rule rule = new Rule(template.getName(), null, index);
		Condition condition = new Condition();
		condition.setSnippet("r : Row()");
		rule.addCondition(condition);
		createColumnConditions(template, rule);
		createNotColumnConditions(template, rule);
		rule.addConsequence(createConsequence(template));
		p.addRule(rule);
	}

	private void createNotColumnConditions(RuleTemplate template, Rule rule) {
		String[] templateNotColumns = template.getNotColumns();
		for (int j = 0; j < templateNotColumns.length; j++) {
			rule.addCondition(createNotCondition(templateNotColumns[j]));
		}
	}

	private void createColumnConditions(RuleTemplate template, Rule rule) {
		List templateColumns = template.getColumns();
		for (Iterator it1 = templateColumns.iterator(); it1.hasNext();) {
			String column = (String) it1.next();
			rule.addCondition(createCondition(column));
		}
	}


	private void addGlobals(org.drools.decisiontable.model.Package p) {
		Global global = new Global();
		global.setClassName(DefaultGenerator.class.getName());
		global.setIdentifier("generator");
		p.addVariable(global);
	}

	private void addImports(org.drools.decisiontable.model.Package p) {
		Import drlImport1 = new Import();
		drlImport1.setClassName(Map.class.getName());
		Import drlImport2 = new Import();
		drlImport2.setClassName(HashMap.class.getName());
		p.addImport(drlImport1);
		p.addImport(drlImport2);
	}

	private Consequence createConsequence(RuleTemplate template) {
		StringBuffer action = new StringBuffer();
		action.append("generator.generate( \"");
		action.append(template.getName()).append("\", r);");
		final Consequence consequence = new Consequence();
		consequence.setSnippet(action.toString());
		return consequence;
	}

	private Condition createCondition(final String value) {
		SnippetBuilder snip = new SnippetBuilder(
				"Cell(row == r, column == \"$param\")");
		String result = snip.build(value);
		Condition condition = new Condition();
		condition.setSnippet(result);
		return condition;
	}

	private Condition createNotCondition(final String value) {
		SnippetBuilder snip = new SnippetBuilder(
				"not Cell(row == r, column == \"$param\")");
		String result = snip.build(value);
		Condition condition = new Condition();
		condition.setSnippet(result);
		return condition;
	}
	private RuleBase readRule(String drl) {
		try {
			System.out.println(drl);
			// read in the source
			Reader source = new StringReader(drl);
			PackageBuilder builder = new PackageBuilder();
			builder.addPackageFromDrl(source);
			Package pkg = builder.getPackage();

			// add the package to a rulebase (deploy the rule package).
			RuleBase ruleBase = RuleBaseFactory.newRuleBase();
			ruleBase.addPackage(pkg);
			return ruleBase;

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
