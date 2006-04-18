package org.drools.decisiontable.model;
/*
 * Copyright 2005 JBoss Inc
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








import junit.framework.TestCase;

/**
 * @author <a href="mailto:michael.neale@gmail.com"> Michael Neale</a>
 * 
 * Tests how the rule parse tree renders itself to a rule XML fragment.
 */
public class RuleRenderTest extends TestCase
{

    public void testRuleRender()
    {
        Rule rule = new Rule( "myrule",
                              new Integer(42), 1 );
        rule.setComment( "rule comments" );

        Condition cond = new Condition( );
        cond.setComment( "cond comment" );
        cond.setSnippet( "cond snippet" );
        rule.addCondition( cond );

        Consequence cons = new Consequence( );
        cons.setComment( "cons comment" );
        cons.setSnippet( "cons snippet;" );
        rule.addConsequence( cons );
        rule.addConsequence( cons );

        DRLOutput out= new DRLOutput();
        rule.renderDRL(out);
        String xml = out.getDRL();
        assertNotNull( xml );

        assertTrue( xml.indexOf( "cond snippet" ) != -1 );
        assertTrue( xml.indexOf( "cons snippet" ) != -1 );
        assertTrue( xml.indexOf( "salience 42" ) != -1 );
        assertTrue( xml.indexOf( "salience 42" ) < xml.indexOf("when") );
        assertTrue( xml.indexOf( "cond snippet" ) < xml.indexOf("then") );
        assertTrue( xml.indexOf( "cons snippet;" ) > xml.indexOf("then") );
        assertTrue( xml.indexOf( "rule" ) != -1 );
        assertTrue( xml.indexOf("end") > xml.indexOf("rule "));
        assertTrue( xml.indexOf( "#rule comments" ) > -1);

    }

    public void testSalienceCalculator()
    {
        int rowNumber = 2;
        int salience = Rule.calcSalience( rowNumber );
        assertEquals( 65533,
                      salience );
    }

    public void testColNumToColName()
    {
        String colName = Rule.convertColNumToColName( 1 );
        assertEquals( "B",
                      colName );

        colName = Rule.convertColNumToColName( 10 );
        assertEquals( "K",
                      colName );

        colName = Rule.convertColNumToColName( 42 );
        assertEquals( "AQ",
                      colName );

        colName = Rule.convertColNumToColName( 27 );
        assertEquals( "AB",
                      colName );

        colName = Rule.convertColNumToColName( 53 );
        assertEquals( "BB",
                      colName );

    }

    public void testNotEscapeChars()
    {
    	//bit of a legacy from the olde XML dayes of yesteryeare
        Condition cond = new Condition( );
        cond.setSnippet( "a < b" );
        DRLOutput out = new DRLOutput();
        cond.renderDRL(out);
        
        assertTrue(out.toString().indexOf("a < b") != -1);
        
    }
    
    /**
     * This checks that if the rule has "nil" salience, then 
     * no salience value should be put in the rule definition.
     * This allows default salience to work as advertised.
     *
     */
    public void testNilSalience() {
        Rule rule = new Rule("MyRule", null, 1);
        
        DRLOutput out = new DRLOutput();
        rule.renderDRL(out);
        String xml = out.toString();
        int idx = xml.indexOf("salience");
        assertEquals(-1, idx);
        
        rule = new Rule("MyRule", new Integer(42), 1);
        out = new DRLOutput();
        rule.renderDRL(out);
        xml = out.toString();
        idx = xml.indexOf("salience");
        assertTrue(idx > -1);        
    }

}