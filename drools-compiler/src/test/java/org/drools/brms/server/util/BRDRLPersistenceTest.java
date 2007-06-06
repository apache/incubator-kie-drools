package org.drools.brms.server.util;

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
                          "\t\t>p1 : Person( f1 : age < 42 )\n"+
                          "\t\t>not Cancel( )\n"+
                          "\tthen\n"+
                          "\t\t>p1.setStatus( \"rejected\" );\n"+
                          "\t\t>modify( p1 );\n"+
                          "\t\t>retract( p1 );\n"+
                          "\t\tSend an email to administrator\n"+
                          "end\n";

        final String drl = p.marshal( m );

        assertEquals( expected, drl );

    }
    
    
    public void testFieldBindingWithNoConstraints() {
        //to satisfy JBRULES-850
        RuleModel m = getModelWithNoConstraints();
        String s = BRDRLPersistence.getInstance().marshal( m );
        System.out.println(s);
        assertTrue(s.indexOf( "Person( f1 : age)" ) != -1);
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
    
    private RuleModel getModelWithNoConstraints() {
        final RuleModel m = new RuleModel();
        m.name = "Complex Rule";
        final FactPattern pat = new FactPattern();
        pat.boundName = "p1";
        pat.factType = "Person";
        final SingleFieldConstraint con = new SingleFieldConstraint();
        con.fieldBinding = "f1";
        con.fieldName = "age";
//        con.operator = "<";
//        con.value = "42";
        pat.addConstraint( con );

        m.addLhsItem( pat );
        
        return m;
    }
    
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

    //    public void testLoadEmpty() {
    //        RuleModel m = BRXMLPersistence.getInstance().unmarshal( null );
    //        assertNotNull( m );
    //
    //        m = BRXMLPersistence.getInstance().unmarshal( "" );
    //        assertNotNull( m );
    //    }
    
    public void testCompositeConstraints() {
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
        
        String actual = BRDRLPersistence.getInstance().marshal( m );
        String expected = "rule \"with composite\" " +
            " when " +
                "p1 : Person( ) " + 
                "Goober( goo == \"foo\"  || == \"bar\" || goo2 == \"foo\" || ( goo == \"whee\" && gabba == \"whee\" ), goo3 == \"foo\" )" +
            " then " +
                "assert( new Whee() );" +
        "end";
        assertEqualsIgnoreWhitespace( expected, actual );
        
    }
    
    public void testFieldsDeclaredButNoConstraints() {
        RuleModel m = new RuleModel();
        m.name = "boo";
        
        FactPattern p = new FactPattern();
        p.factType = "Person";
        
        //this isn't an effective constraint, so it should be ignored.
        p.addConstraint( new SingleFieldConstraint("field1") );

        m.addLhsItem( p );
        
        String actual = BRDRLPersistence.getInstance().marshal( m );

        String expected = "rule \"boo\" when Person() then end";
        
        assertEqualsIgnoreWhitespace( expected, actual );
        
        SingleFieldConstraint con = (SingleFieldConstraint) p.constraintList.constraints[0];
        con.fieldBinding = "q";
        
        //now it should appear, as we are binding a var to it
        
        actual = BRDRLPersistence.getInstance().marshal( m );

        expected = "rule \"boo\" when Person(q : field1) then end";
        
        assertEqualsIgnoreWhitespace( expected, actual );
        
        
    }
    
    public void testLiteralStrings() {
        
        RuleModel m = new RuleModel();
        m.name = "test literal strings";
        
        FactPattern p = new FactPattern("Person");
        SingleFieldConstraint con = new SingleFieldConstraint();
        con.fieldName = "field1";
        con.operator = "==";
        con.value = "goo";
        con.constraintValueType = SingleFieldConstraint.TYPE_LITERAL;
        p.addConstraint( con );
        
        
        SingleFieldConstraint con2 = new SingleFieldConstraint();
        con2.fieldName = "field2";
        con2.operator = "==";
        con2.value = "variableHere";
        con2.constraintValueType = SingleFieldConstraint.TYPE_VARIABLE;
        p.addConstraint( con2 );
        
        
        
        m.addLhsItem( p );
        
        String result = BRDRLPersistence.getInstance().marshal( m );
     
        assertEqualsIgnoreWhitespace( "rule \"test literal strings\"" +
                                          " when " +
                                          "     Person(field1 == \"goo\", field2 == variableHere)" +
                                          " then " +
                                          "end", result );

        
        
        
    }
    
    private void assertEqualsIgnoreWhitespace(final String expected,
                                              final String actual) {
        final String cleanExpected = expected.replaceAll( "\\s+",
                                                          "" );
        final String cleanActual = actual.replaceAll( "\\s+",
                                                      "" );

        assertEquals( cleanExpected,
                      cleanActual );
    }
    
    public void testConnective() {
        
        RuleModel m = new RuleModel();
        m.name = "test literal strings";
        
        FactPattern p = new FactPattern("Person");
        SingleFieldConstraint con = new SingleFieldConstraint();
        con.fieldName = "field1";
        con.operator = "==";
        con.value = "goo";
        con.constraintValueType = SingleFieldConstraint.TYPE_VARIABLE;
        p.addConstraint( con );

        ConnectiveConstraint connective = new ConnectiveConstraint();
        connective.constraintValueType = ISingleFieldConstraint.TYPE_LITERAL;
        connective.operator = "|| ==";
        connective.value = "blah";
        
        con.connectives = new ConnectiveConstraint[1];
        con.connectives[0] = connective;
        
        m.addLhsItem( p );
        
        String result = BRDRLPersistence.getInstance().marshal( m );
        
        String expected = "rule \"test literal strings\" " +
            " when " +
                "Person( field1 == goo  || == \"blah\" )" + 
                " then " +
                "end";
        assertEqualsIgnoreWhitespace( expected, result );
        

    }
    
    

}
