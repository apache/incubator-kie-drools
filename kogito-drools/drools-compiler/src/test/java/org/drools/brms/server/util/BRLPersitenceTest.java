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

public class BRLPersitenceTest extends TestCase {

    public void testGenerateEmptyXML() {
        final BRLPersistence p = BRLPersistence.getInstance();
        final String xml = p.toXML( new RuleModel() );
        assertNotNull( xml );
        assertFalse( xml.equals( "" ) );

        assertTrue( xml.startsWith( "<rule>" ) );
        assertTrue( xml.endsWith( "</rule>" ) );
    }

    public void testBasics() {
        final BRLPersistence p = BRLPersistence.getInstance();
        final RuleModel m = new RuleModel();
        m.addLhsItem( new FactPattern( "Person" ) );
        m.addLhsItem( new FactPattern( "Accident" ) );
        m.addAttribute( new RuleAttribute( "no-loop",
                                           "true" ) );

        m.addRhsItem( new ActionAssertFact( "Report" ) );
        m.name = "my rule";
        final String xml = p.toXML( m );
        assertTrue( xml.indexOf( "Person" ) > -1 );
        assertTrue( xml.indexOf( "Accident" ) > -1 );
        assertTrue( xml.indexOf( "no-loop" ) > -1 );
        assertTrue( xml.indexOf( "org.drools" ) == -1 );

    }

    public void testMoreComplexRendering() {
        final BRLPersistence p = BRLPersistence.getInstance();
        final RuleModel m = getComplexModel();

        final String xml = p.toXML( m );
        System.out.println( xml );

        assertTrue( xml.indexOf( "org.drools" ) == -1 );

    }

    public void testRoundTrip() {
        final RuleModel m = getComplexModel();

        final String xml = BRLPersistence.getInstance().toXML( m );

        final RuleModel m2 = BRLPersistence.getInstance().toModel( xml );
        assertNotNull( m2 );
        assertEquals( m.name,
                      m2.name );
        assertEquals( m.lhs.length,
                      m2.lhs.length );
        assertEquals( m.rhs.length,
                      m2.rhs.length );
        assertEquals( 1,
                      m.attributes.length );

        final RuleAttribute at = m.attributes[0];
        assertEquals( "no-loop",
                      at.attributeName );
        assertEquals( "true",
                      at.value );

        final String newXML = BRLPersistence.getInstance().toXML( m2 );
        assertEquals( xml,
                      newXML );

    }

    private RuleModel getComplexModel() {
        final RuleModel m = new RuleModel();

        m.addAttribute( new RuleAttribute( "no-loop",
                                           "true" ) );

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

    public void testLoadEmpty() {
        RuleModel m = BRLPersistence.getInstance().toModel( null );
        assertNotNull( m );

        m = BRLPersistence.getInstance().toModel( "" );
        assertNotNull( m );
    }

}
