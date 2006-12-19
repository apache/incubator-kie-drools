package org.drools.decisiontable.parser;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

public class DefaultTemplateContainerTest extends TestCase {
	public void testParseTemplate() {
		InputStream is = DefaultTemplateContainerTest.class.getResourceAsStream("/templates/test_template_simple.drl");
		DefaultTemplateContainer t = new DefaultTemplateContainer(is);
		assertEquals("package This_is_a_ruleset;\n", t.getHeader());
		Map templates = t.getTemplates();
		assertEquals(1, templates.size());
		RuleTemplate template = (RuleTemplate) templates.get("template1");
		assertNotNull(template);
		List columns = template.getColumns();
		assertEquals(1, columns.size());
		assertEquals("name", columns.get(0));
		String contents = template.getContents();
		assertTrue(contents.startsWith("rule \"How cool is $name$ $row.rowNumber$\""));
		assertTrue(contents.endsWith("then\nend\n"));
	}
}
