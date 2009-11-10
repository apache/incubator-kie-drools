package org.drools.doc;

import java.util.List;

import junit.framework.TestCase;

public class DrlRuleDataTest extends TestCase {
	public void testHandleDrl() {
		String drl = "rule \"Something\" \n ";
		drl += "dialect \"Java\" \n ";
		drl += "	when \n ";
		drl += "		Person() \n ";
		drl += "		Cheesery() \n ";
		drl += "	then \n ";
		drl += "		insert( new Person()) \n ";
		drl += "		insert( new Car()) \n ";
		drl += "		insert( new Cheese()) \n ";
		drl += "end ";

		DrlRuleData s = DrlRuleData.findRulesDataFromDrl(drl).get(0);

		assertEquals(1, s.header.size());
		assertEquals(2, s.lhs.size());
		assertEquals(3, s.rhs.size());
		assertEquals("", s.description);

	}

	public void testHandleDrlNoLineBreaks() {
		String drl = "rule \"CreditScoreApproval\" \n";
		drl += "	dialect \"mvel\" \n";
		drl += "	when    then";
		drl += "		applicant.setApproved(true) \n";
		drl += "		applicant.setName( \"Toni\" ) \n";
		drl += "		applicant.setAge( 10 ) \n";
		drl += "end";
		DrlRuleData s = DrlRuleData.findRulesDataFromDrl(drl).get(0);

		assertNotNull(s);

		assertEquals(1, s.header.size());
		assertEquals(0, s.lhs.size());
		assertEquals(3, s.rhs.size());
		assertEquals("", s.description);

	}

	public void testHandleDrlWithComment() {
		String drl = "# Really important information about this rule \n";
		drl += "# Another line because one was not enough \n";
		drl += "#  \n";
		drl += "# @author: trikkola \n";
		drl += "rule \"First\" \n";
		drl += "	dialect \"mvel\" \n";
		drl += "	when \n ";
		drl += "		Person() \n ";
		drl += "		Cheesery() \n ";
		drl += "	then \n ";
		drl += "		applicant.setApproved(true) \n";
		drl += "		applicant.setName( \"Toni\" ) \n";
		drl += "		applicant.setAge( 10 ) \n";
		drl += "end \n";
		drl += "\n";
		drl += "# Really important information about this rule \n";
		drl += "# Another line because one was not enough \n";
		drl += "#  \n";
		drl += "# @author: trikkola \n";
		drl += "# @created: 29.12.2001 \n";
		drl += "# @edited: 5.5.2005 \n";
		drl += "rule \"Second\" \n";
		drl += "	dialect \"mvel\" \n";
		drl += "	when \n ";
		drl += "		Person() \n ";
		drl += "		Cheesery() \n ";
		drl += "	then \n ";
		drl += "		applicant.setApproved(true) \n";
		drl += "		applicant.setName( \"Toni\" ) \n";
		drl += "		applicant.setAge( 10 ) \n";
		drl += "end";
		drl += "\n";
		drl += "rule \"Third\" \n";
		drl += "	dialect \"mvel\" \n";
		drl += "	when \n ";
		drl += "		Person() \n ";
		drl += "		Cheesery() \n ";
		drl += "	then \n ";
		drl += "		applicant.setApproved(true) \n";
		drl += "		applicant.setName( \"Toni\" ) \n";
		drl += "		applicant.setAge( 10 ) \n";
		drl += "end";

		List<DrlRuleData> list = DrlRuleData.findRulesDataFromDrl(drl);

		assertEquals(3, list.size());

		DrlRuleData rd = list.get(0);

		assertNotNull(rd);

		assertEquals(1, rd.header.size());
		assertEquals(2, rd.lhs.size());
		assertEquals(3, rd.rhs.size());
		assertEquals(1, rd.metadata.size());
		assertNotNull(rd.description);
		assertNotSame("", rd.description);

		DrlRuleData rd2 = list.get(1);

		assertNotNull(rd2);

		assertEquals(1, rd2.header.size());
		assertEquals(2, rd2.lhs.size());
		assertEquals(3, rd2.rhs.size());
		assertEquals(3, rd2.metadata.size());
		assertNotNull(rd2.description);

		String description = "Really important information about this rule\n";
		description += "Another line because one was not enough\n\n";

		assertEquals(description, rd2.description);
		assertNotSame("", rd2.description);

		DrlRuleData rd3 = list.get(2);

		assertNotNull(rd3);

		assertEquals(1, rd3.header.size());
		assertEquals(2, rd3.lhs.size());
		assertEquals(3, rd3.rhs.size());
		assertNotNull(rd3.description);
		assertEquals("", rd3.description);
	}
}
