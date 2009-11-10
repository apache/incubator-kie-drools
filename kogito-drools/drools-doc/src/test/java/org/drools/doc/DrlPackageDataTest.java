package org.drools.doc;

import java.util.List;

import junit.framework.TestCase;

public class DrlPackageDataTest extends TestCase {
	public void testHandleDrl() {

		String rule1 = "";
		rule1 += "package org.drools.test\n";
		rule1 += "global java.util.List list\n";
		rule1 += "rule rule1\n";
		rule1 += "when\n";
		rule1 += "then\n";
		rule1 += "list.add( drools.getRule().getName() );\n";
		rule1 += "end\n";
		rule1 += "rule rule2\n";
		rule1 += "when\n";
		rule1 += "then\n";
		rule1 += "list.add( drools.getRule().getName() );\n";
		rule1 += "end\n";

		DrlPackageData s = DrlPackageData.findPackageDataFromDrl(rule1);

		assertEquals("org.drools.test", s.packageName);
		assertEquals(2, s.rules.size());
		assertEquals("", s.description);

	}

	public void testHandleDrlWithComments() {

		String rule1 = "";
		rule1 += "# important information\n";
		rule1 += "# about this package\n";
		rule1 += "# it contains some rules\n";
		rule1 += "package org.drools.test\n";
		rule1 += "global java.util.List list\n";
		rule1 += "rule rule1\n";
		rule1 += "	when\n";
		rule1 += "	then\n";
		rule1 += "		list.add( drools.getRule().getName() );\n";
		rule1 += "end\n";
		rule1 += "rule rule2\n";
		rule1 += "	when\n";
		rule1 += "	then\n";
		rule1 += "		list.add( drools.getRule().getName() );\n";
		rule1 += "end\n";

		DrlPackageData data = DrlPackageData.findPackageDataFromDrl(rule1);

		assertEquals("org.drools.test", data.packageName);
		assertEquals(2, data.rules.size());
		assertEquals(1, data.globals.size());
		assertEquals("java.util.List list", data.globals.get(0));
		assertEquals(
				"important information\nabout this package\nit contains some rules\n",
				data.description);

		DrlRuleData rd1 = data.rules.get(0);
		assertEquals("rule1", rd1.ruleName);
		assertEquals("", rd1.description);

		DrlRuleData rd2 = data.rules.get(1);
		assertEquals("rule2", rd2.ruleName);
		assertEquals("", rd2.description);
	}

	public void testfindGlobals() {

		String header = "global LoanApplication gg";

		List<String> globals = DrlPackageData.findGlobals(header);

		assertEquals(1, globals.size());
		assertEquals("LoanApplication gg", globals.get(0));
	}

}
