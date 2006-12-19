package org.drools.decisiontable.parser;

import junit.framework.TestCase;

public class RuleTemplateTest extends TestCase {
	public void testSetContents() {
		RuleTemplate rt = new RuleTemplate("rt1");
		rt.setContents("Test template");
		assertEquals("Test template\n", rt.getContents());
	}
}
