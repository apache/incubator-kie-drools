package org.drools.doc;

import java.util.List;

import junit.framework.TestCase;

public class DrlRuleDataTest extends TestCase {
    public void testHandleDrl() {
        String drl = 
            "rule \"Something\" \n " + 
            "dialect \"Java\" \n " + 
            "when \n " + 
            "Person() \n " + 
            "Cheesery() \n " + 
            "then \n " + 
            "insert( new Person()) \n " + 
            "insert( new Car()) \n " + 
            "insert( new Cheese()) \n " + 
            "end ";
        
        DrlRuleData s = DrlRuleData.findRulesDataFromDrl( drl ).get( 0 );

        assertEquals( 1,
                      s.header.size() );
        assertEquals( 2,
                      s.lhs.size() );
        assertEquals( 3,
                      s.rhs.size() );
        assertEquals( "",
                      s.description );
        
    }
    
    public void testHandleDrlNoLineBreaks() {
        String drl = 
            "rule \"CreditScoreApproval\" \n" + 
               "dialect \"mvel\" \n" +
               "when    then" +
                   "applicant.setApproved(true) \n" +
                   "applicant.setName( \"Toni\" ) \n" +
                   "applicant.setAge( 10 ) \n" +
            "end";
        DrlRuleData s = DrlRuleData.findRulesDataFromDrl( drl ).get( 0 );

        assertNotNull( s );

        assertEquals( 1,
                      s.header.size() );
        assertEquals( 0,
                      s.lhs.size() );
        assertEquals( 3,
                      s.rhs.size() );
        assertEquals( "",
                      s.description );
        
    }

    public void testHandleDrlWithComment() {
        String drl = 
            "# Really important information about this rule \n" + 
            "# Another line because one was not enough \n" + 
            "#  \n" + 
            "# @author: trikkola \n" + 
            "rule \"First\" \n" + 
            "dialect \"mvel\" \n" +
            "when \n " + 
            "Person() \n " + 
            "Cheesery() \n " + 
            "then \n " + 
            "applicant.setApproved(true) \n" +
            "applicant.setName( \"Toni\" ) \n" +
            "applicant.setAge( 10 ) \n" +
            "end \n" +
            "\n" +
            "# Really important information about this rule \n" + 
            "# Another line because one was not enough \n" + 
            "#  \n" + 
            "# @author: trikkola \n" + 
            "# @created: 29.12.2001 \n" + 
            "# @edited: 5.5.2005 \n" + 
            "rule \"Second\" \n" + 
            "dialect \"mvel\" \n" +
            "when \n " + 
            "Person() \n " + 
            "Cheesery() \n " + 
            "then \n " + 
            "applicant.setApproved(true) \n" +
            "applicant.setName( \"Toni\" ) \n" +
            "applicant.setAge( 10 ) \n" +
            "end" +
            "\n" +
            "rule \"Third\" \n" + 
            "dialect \"mvel\" \n" +
            "when \n " + 
            "Person() \n " + 
            "Cheesery() \n " + 
            "then \n " + 
            "applicant.setApproved(true) \n" +
            "applicant.setName( \"Toni\" ) \n" +
            "applicant.setAge( 10 ) \n" +
            "end";
        
        
        List<DrlRuleData> list = DrlRuleData.findRulesDataFromDrl( drl );

        assertEquals( 3,
                      list.size() );

        DrlRuleData rd = list.get( 0 );

        assertNotNull( rd );

        assertEquals( 1,
                      rd.header.size() );
        assertEquals( 2,
                      rd.lhs.size() );
        assertEquals( 3,
                      rd.rhs.size() );
        assertEquals( 1,
                      rd.metadata.size() );
        assertNotNull( rd.description );
        assertNotSame( "",
                       rd.description );
        
        DrlRuleData rd2 = list.get( 1 );

        assertNotNull( rd2 );

        assertEquals( 1,
                      rd2.header.size() );
        assertEquals( 2,
                      rd2.lhs.size() );
        assertEquals( 3,
                      rd2.rhs.size() );
        assertEquals( 3,
                      rd2.metadata.size() );
        assertNotNull( rd2.description );
        assertNotSame( "",
                       rd2.description );

        DrlRuleData rd3 = list.get( 2 );

        assertNotNull( rd3 );

        assertEquals( 1,
                      rd3.header.size() );
        assertEquals( 2,
                      rd3.lhs.size() );
        assertEquals( 3,
                      rd3.rhs.size() );
        assertNotNull( rd3.description );
        assertEquals( "",
                       rd3.description );
    }
}
