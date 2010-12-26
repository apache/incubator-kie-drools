/**
 * Copyright 2010 JBoss Inc
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

package org.drools.verifier.misc;

import java.util.List;

import org.drools.verifier.misc.DrlRuleParser;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class DrlRuleDataTest {
    @Test
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

		DrlRuleParser s = DrlRuleParser.findRulesDataFromDrl(drl).get(0);

		assertEquals(1, s.getHeader().size());
		assertEquals(2, s.getLhs().size());
		assertEquals(3, s.getRhs().size());
		assertEquals("", s.getDescription());

	}

    @Test
    public void testHandleDrlNoLineBreaks() {
		String drl = "rule \"CreditScoreApproval\" \n";
		drl += "	dialect \"mvel\" \n";
		drl += "	when    then";
		drl += "		applicant.setApproved(true) \n";
		drl += "		applicant.setName( \"Toni\" ) \n";
		drl += "		applicant.setAge( 10 ) \n";
		drl += "end";
		DrlRuleParser s = DrlRuleParser.findRulesDataFromDrl(drl).get(0);

		assertNotNull(s);

		assertEquals(1, s.getHeader().size());
		assertEquals(0, s.getLhs().size());
		assertEquals(3, s.getRhs().size());
		assertEquals("", s.getDescription());

	}

    @Test
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

		List<DrlRuleParser> list = DrlRuleParser.findRulesDataFromDrl(drl);

		assertEquals(3, list.size());

		DrlRuleParser rd = list.get(0);

		assertNotNull(rd);

		assertEquals(1, rd.getHeader().size());
		assertEquals(2, rd.getLhs().size());
		assertEquals(3, rd.getRhs().size());
		assertEquals(1, rd.getMetadata().size());
		assertNotNull(rd.getDescription());
		assertNotSame("", rd.getDescription());

		DrlRuleParser rd2 = list.get(1);

		assertNotNull(rd2);

		assertEquals(1, rd2.getHeader().size());
		assertEquals(2, rd2.getLhs().size());
		assertEquals(3, rd2.getRhs().size());
		assertEquals(3, rd2.getMetadata().size());
		assertNotNull(rd2.getDescription());

		String description = "Really important information about this rule\n";
		description += "Another line because one was not enough\n\n";

		assertEquals(description, rd2.getDescription());
		assertNotSame("", rd2.getDescription());

		DrlRuleParser rd3 = list.get(2);

		assertNotNull(rd3);

		assertEquals(1, rd3.getHeader().size());
		assertEquals(2, rd3.getLhs().size());
		assertEquals(3, rd3.getRhs().size());
		assertNotNull(rd3.getDescription());
		assertEquals("", rd3.getDescription());
	}
}
