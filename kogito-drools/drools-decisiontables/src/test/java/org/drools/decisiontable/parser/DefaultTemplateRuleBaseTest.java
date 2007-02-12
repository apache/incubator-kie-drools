package org.drools.decisiontable.parser;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.drools.rule.GroupElement;
import org.drools.rule.LiteralConstraint;
import org.drools.rule.Package;
import org.drools.rule.Rule;

public class DefaultTemplateRuleBaseTest extends TestCase {

	public void testSimpleTemplate() throws Exception
	{
		TemplateContainer tc = new TemplateContainer() {

			public Column[] getColumns() {
				return new Column[] {
						new Column("column1"),
						new Column("column2")
				};
			}

			public String getHeader() {
				return null;
			}

			public Map getTemplates() {
				Map templates = new HashMap();
				RuleTemplate ruleTemplate = new RuleTemplate("template1");
				ruleTemplate.addColumn("column1");
				ruleTemplate.addColumn("column2");
				templates.put("template1", ruleTemplate);
				return templates;
			}
			
		};
		DefaultTemplateRuleBase ruleBase = new DefaultTemplateRuleBase(tc);
		Package[] packages = ruleBase.newWorkingMemory().getRuleBase().getPackages();
		assertEquals(1, packages.length);
		Map globals = packages[0].getGlobals();
		assertEquals(DefaultGenerator.class, globals.get("generator"));
		Rule[] rules = packages[0].getRules();
		assertEquals(1, rules.length);
		assertEquals("template1", rules[0].getName());
		GroupElement lhs = rules[0].getLhs();
		//when
		//  r : Row()
		//  Cell(row == r, column == "column1")
		//  Cell(row == r, column == "column2")
		assertEquals(3, lhs.getChildren().size());
		org.drools.rule.Column column = (org.drools.rule.Column) lhs.getChildren().get(1);
		LiteralConstraint constraint = (LiteralConstraint) column.getConstraints().get(1);
		assertEquals("column1", constraint.getField().getValue());
		column = (org.drools.rule.Column) lhs.getChildren().get(2);
		constraint = (LiteralConstraint) column.getConstraints().get(1);
		assertEquals("column2", constraint.getField().getValue());
	}
}
