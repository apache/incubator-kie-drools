package org.drools.brms.server.util;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;

import junit.framework.TestCase;

import org.drools.brms.client.modeldriven.SuggestionCompletionEngine;
import org.drools.brms.client.modeldriven.brxml.ActionAssertFact;
import org.drools.brms.client.modeldriven.brxml.ActionFieldValue;
import org.drools.brms.client.modeldriven.brxml.ActionModifyField;
import org.drools.brms.client.modeldriven.brxml.ActionRetractFact;
import org.drools.brms.client.modeldriven.brxml.CompositeFactPattern;
import org.drools.brms.client.modeldriven.brxml.CompositeFieldConstraint;
import org.drools.brms.client.modeldriven.brxml.ConnectiveConstraint;
import org.drools.brms.client.modeldriven.brxml.ISingleFieldConstraint;
import org.drools.brms.client.modeldriven.brxml.SingleFieldConstraint;
import org.drools.brms.client.modeldriven.brxml.DSLSentence;
import org.drools.brms.client.modeldriven.brxml.FactPattern;
import org.drools.brms.client.modeldriven.brxml.RuleAttribute;
import org.drools.brms.client.modeldriven.brxml.RuleModel;
import org.drools.lang.DRLParser;

public class BRXMLPersitenceTest extends TestCase {

    public void testGenerateEmptyXML() {
        final BRLPersistence p = BRXMLPersistence.getInstance();
        final String xml = p.marshal( new RuleModel() );
        assertNotNull( xml );
        assertFalse( xml.equals( "" ) );

        assertTrue( xml.startsWith( "<rule>" ) );
        assertTrue( xml.endsWith( "</rule>" ) );
    }

    public void testBasics() {
        final BRLPersistence p = BRXMLPersistence.getInstance();
        final RuleModel m = new RuleModel();
        m.addLhsItem( new FactPattern( "Person" ) );
        m.addLhsItem( new FactPattern( "Accident" ) );
        m.addAttribute( new RuleAttribute( "no-loop",
                                           "true" ) );

        m.addRhsItem( new ActionAssertFact( "Report" ) );
        m.name = "my rule";
        final String xml = p.marshal( m );
        //System.out.println(xml);
        assertTrue( xml.indexOf( "Person" ) > -1 );
        assertTrue( xml.indexOf( "Accident" ) > -1 );
        assertTrue( xml.indexOf( "no-loop" ) > -1 );
        assertTrue( xml.indexOf( "org.drools" ) == -1 );

    }

    public void testMoreComplexRendering() {
        final BRLPersistence p = BRXMLPersistence.getInstance();
        final RuleModel m = getComplexModel();

        final String xml = p.marshal( m );
        //System.out.println( xml );

        assertTrue( xml.indexOf( "org.drools" ) == -1 );

    }

    public void testRoundTrip() {
        final RuleModel m = getComplexModel();

        final String xml = BRXMLPersistence.getInstance().marshal( m );

        final RuleModel m2 = BRXMLPersistence.getInstance().unmarshal( xml );
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

        final String newXML = BRXMLPersistence.getInstance().marshal( m2 );
        assertEquals( xml,
                      newXML );

    }
    
    public void testCompositeConstraintsRoundTrip() throws Exception {
        RuleModel m = new RuleModel();
        m.name = "with composite";
    
        FactPattern p1 = new FactPattern("Person");
        p1.boundName = "p1";
        m.addLhsItem( p1 );
        
        FactPattern p = new FactPattern("Goober");
        m.addLhsItem( p );
        CompositeFieldConstraint comp = new CompositeFieldConstraint();
        comp.compositeJunctionType = CompositeFieldConstraint.COMPOSITE_TYPE_OR;
        p.addConstraint( comp );
        
        final SingleFieldConstraint X = new SingleFieldConstraint();
        X.fieldName = "goo";
        X.constraintValueType = SingleFieldConstraint.TYPE_LITERAL;
        X.value = "foo";
        X.operator = "==";
        X.connectives = new ConnectiveConstraint[1];
        X.connectives[0] = new ConnectiveConstraint();
        X.connectives[0].constraintValueType = ConnectiveConstraint.TYPE_LITERAL;
        X.connectives[0].operator = "|| ==";
        X.connectives[0].value = "bar";
        comp.addConstraint( X );
        
        final SingleFieldConstraint Y = new SingleFieldConstraint();
        Y.fieldName = "goo2";
        Y.constraintValueType = SingleFieldConstraint.TYPE_LITERAL;
        Y.value = "foo";
        Y.operator = "==";
        comp.addConstraint( Y );

        CompositeFieldConstraint comp2 = new CompositeFieldConstraint();
        comp2.compositeJunctionType = CompositeFieldConstraint.COMPOSITE_TYPE_AND;
        final SingleFieldConstraint Q1 = new SingleFieldConstraint();
        Q1.fieldName = "goo";
        Q1.operator = "==";
        Q1.value = "whee";
        Q1.constraintValueType = ISingleFieldConstraint.TYPE_LITERAL;
        
        comp2.addConstraint( Q1 );
        
        final SingleFieldConstraint Q2 = new SingleFieldConstraint();
        Q2.fieldName = "gabba";
        Q2.operator = "==";
        Q2.value = "whee";
        Q2.constraintValueType = ISingleFieldConstraint.TYPE_LITERAL;
        
        comp2.addConstraint( Q2 );
        
        //now nest it
        comp.addConstraint( comp2 );
        
        
        
        final SingleFieldConstraint Z = new SingleFieldConstraint();
        Z.fieldName = "goo3";
        Z.constraintValueType = SingleFieldConstraint.TYPE_LITERAL;
        Z.value = "foo";
        Z.operator = "==";
        
        p.addConstraint( Z );
        
        ActionAssertFact ass = new ActionAssertFact("Whee");
        m.addRhsItem( ass );

        
        String xml = BRXMLPersistence.getInstance().marshal( m );
        //System.err.println(xml);
        
        RuleModel m2 = BRXMLPersistence.getInstance().unmarshal( xml );
        assertNotNull(m2);
        assertEquals("with composite", m2.name);
        
        assertEquals(m2.lhs.length, m.lhs.length);
        assertEquals(m2.rhs.length, m.rhs.length);
        
        
        
        
    }

    /**
     * This will verify that we can load an old BRXML change. If this fails,
     * then backwards compatability is broken.
     */
    public void testBackwardsCompat() throws Exception {
        RuleModel m2 = BRXMLPersistence.getInstance().unmarshal( loadResource( "existing_brxml.xml" ) );
        
        assertNotNull(m2);
        assertEquals(3, m2.rhs.length);
    }
    
    private String loadResource(final String name) throws Exception {

        //        System.err.println( getClass().getResource( name ) );
        final InputStream in = getClass().getResourceAsStream( name );

    
        final Reader reader = new InputStreamReader( in );

        final StringBuffer text = new StringBuffer();

        final char[] buf = new char[1024];
        int len = 0;

        while ( (len = reader.read( buf )) >= 0 ) {
            text.append( buf,
                         0,
                         len );
        }

        return text.toString();
    }    

    private RuleModel getComplexModel() {
        final RuleModel m = new RuleModel();

        m.addAttribute( new RuleAttribute( "no-loop",
                                           "true" ) );

        final FactPattern pat = new FactPattern();
        pat.boundName = "p1";
        pat.factType = "Person";
        final SingleFieldConstraint con = new SingleFieldConstraint();
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
                                                 "rejected",
                                                 SuggestionCompletionEngine.TYPE_STRING ) );
        m.addRhsItem( set );

        final ActionRetractFact ret = new ActionRetractFact( "p1" );
        m.addRhsItem( ret );

        final DSLSentence sen = new DSLSentence();
        sen.sentence = "Send an email to {administrator}";

        m.addRhsItem( sen );
        return m;
    }

    public void testLoadEmpty() {
        RuleModel m = BRXMLPersistence.getInstance().unmarshal( null );
        assertNotNull( m );

        m = BRXMLPersistence.getInstance().unmarshal( "" );
        assertNotNull( m );
    }

}
