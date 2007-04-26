package org.drools.brms.server.util;

import junit.framework.TestCase;

import org.drools.brms.client.modeldriven.brxml.ActionAssertFact;
import org.drools.brms.client.modeldriven.brxml.ActionFieldValue;
import org.drools.brms.client.modeldriven.brxml.ActionModifyField;
import org.drools.brms.client.modeldriven.brxml.ActionRetractFact;
import org.drools.brms.client.modeldriven.brxml.CompositeFactPattern;
import org.drools.brms.client.modeldriven.brxml.Constraint;
import org.drools.brms.client.modeldriven.brxml.DSLSentence;
import org.drools.brms.client.modeldriven.brxml.FactPattern;
import org.drools.brms.client.modeldriven.brxml.RuleAttribute;
import org.drools.brms.client.modeldriven.brxml.RuleModel;

public class BRDRLPersistenceTest extends TestCase {

    private BRLPersistence p;

    protected void setUp() throws Exception {
        super.setUp();
        p = BRDRLPersistence.getInstance();
    }

    public void testGenerateEmptyDRL() {
        String expected = "rule \"null\"\n\twhen\n\tthen\nend\n";

        final String drl = p.marshal( new RuleModel() );

        assertNotNull( drl );
        assertEquals( expected,
                      drl );
    }

    public void testBasics() {
        String expected = "rule \"my rule\"\n\tno-loop true\n\twhen\n\t\tPerson( )\n" + 
                          "\t\tAccident( )\n\tthen\n\t\tassert( new Report() );\nend\n";
        final RuleModel m = new RuleModel();
        m.addLhsItem( new FactPattern( "Person" ) );
        m.addLhsItem( new FactPattern( "Accident" ) );
        m.addAttribute( new RuleAttribute( "no-loop",
                                           "true" ) );

        m.addRhsItem( new ActionAssertFact( "Report" ) );
        m.name = "my rule";

        final String drl = p.marshal( m );
        assertEquals( expected,
                      drl );
    }

    public void testMoreComplexRendering() {
        final RuleModel m = getComplexModel();
        String expected = "rule \"Complex Rule\"\n" +
                          "\tno-loop true\n" +
                          "\tsalience -10\n" +
                          "\tagenda-group \"aGroup\"\n" +
                          "\twhen\n"+
                          "\t\tp1 : Person( f1 : age < 42 )\n"+
                          "\t\tnot Cancel( )\n"+
                          "\tthen\n"+
                          "\t\tp1.setStatus( \"rejected\" );\n"+
                          "\t\tmodify( p1 );\n"+
                          "\t\tretract( p1 );\n"+
                          "\t\tSend an email to {administrator}\n"+
                          "end\n";

        final String drl = p.marshal( m );
        //System.out.println( drl );

        assertEquals( expected, drl );

    }

    //
    //    public void testRoundTrip() {
    //        final RuleModel m = getComplexModel();
    //
    //        final String xml = BRXMLPersistence.getInstance().marshal( m );
    //
    //        final RuleModel m2 = BRXMLPersistence.getInstance().unmarshal( xml );
    //        assertNotNull( m2 );
    //        assertEquals( m.name,
    //                      m2.name );
    //        assertEquals( m.lhs.length,
    //                      m2.lhs.length );
    //        assertEquals( m.rhs.length,
    //                      m2.rhs.length );
    //        assertEquals( 1,
    //                      m.attributes.length );
    //
    //        final RuleAttribute at = m.attributes[0];
    //        assertEquals( "no-loop",
    //                      at.attributeName );
    //        assertEquals( "true",
    //                      at.value );
    //
    //        final String newXML = BRXMLPersistence.getInstance().marshal( m2 );
    //        assertEquals( xml,
    //                      newXML );
    //
    //    }
    //
    private RuleModel getComplexModel() {
        final RuleModel m = new RuleModel();
        m.name = "Complex Rule";

        m.addAttribute( new RuleAttribute( "no-loop",
                                           "true" ) );
        m.addAttribute( new RuleAttribute( "salience",
                                           "-10" ) );
        m.addAttribute( new RuleAttribute( "agenda-group",
                                           "aGroup" ) );

        final FactPattern pat = new FactPattern();
        pat.boundName = "p1";
        pat.factType = "Person";
        final Constraint con = new Constraint();
        con.fieldBinding = "f1";
        con.fieldName = "age";
        con.operator = "<";
        con.value = "42";
        pat.addConstraint( con );

        m.addLhsItem( pat );

        final CompositeFactPattern comp = new CompositeFactPattern( "not" );
        comp.addFactPattern( new FactPattern( "Cancel" ) );
        m.addLhsItem( comp );

        final ActionModifyField set = new ActionModifyField();
        set.variable = "p1";
        set.addFieldValue( new ActionFieldValue( "status",
                                                 "rejected" ) );
        m.addRhsItem( set );

        final ActionRetractFact ret = new ActionRetractFact( "p1" );
        m.addRhsItem( ret );

        final DSLSentence sen = new DSLSentence();
        sen.sentence = "Send an email to {administrator}";

        m.addRhsItem( sen );
        return m;
    }

    //    public void testLoadEmpty() {
    //        RuleModel m = BRXMLPersistence.getInstance().unmarshal( null );
    //        assertNotNull( m );
    //
    //        m = BRXMLPersistence.getInstance().unmarshal( "" );
    //        assertNotNull( m );
    //    }

}
