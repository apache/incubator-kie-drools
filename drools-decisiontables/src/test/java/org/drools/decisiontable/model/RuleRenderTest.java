package org.drools.decisiontable.model;


/*
 * Copyright 2005 (C) The Werken Company. All Rights Reserved.
 *
 * Redistribution and use of this software and associated documentation
 * ("Software"), with or without modification, are permitted provided that the
 * following conditions are met:
 *
 * 1. Redistributions of source code must retain copyright statements and
 * notices. Redistributions must also contain a copy of this document.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * 3. The name "drools" must not be used to endorse or promote products derived
 * from this Software without prior written permission of The Werken Company.
 * For written permission, please contact bob@werken.com.
 *
 * 4. Products derived from this Software may not be called "drools" nor may
 * "drools" appear in their names without prior written permission of The Werken
 * Company. "drools" is a registered trademark of The Werken Company.
 *
 * 5. Due credit should be given to The Werken Company.
 * (http://drools.werken.com/).
 *
 * THIS SOFTWARE IS PROVIDED BY THE WERKEN COMPANY AND CONTRIBUTORS ``AS IS''
 * AND ANY EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE WERKEN COMPANY OR ITS CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
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

        
        String xml = rule.toXML( );
        assertNotNull( xml );

        assertTrue( xml.indexOf( "cond snippet" ) != -1 );
        assertTrue( xml.indexOf( "cons snippet" ) != -1 );
        assertTrue( xml.indexOf( "salience=\"42\"" ) != -1 );
        assertTrue( xml.indexOf( "cons snippet;\n\t\tcons snippet;" ) != -1 );

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

    public void testEscapeChars()
    {
        // not needed, as chars should NOT be escaped - using CDATA instead
        // so now am asserting that it is not escaped !
        Condition cond = new Condition( );
        cond.setSnippet( "a < b" );
        assertFalse( cond.toXML( ).indexOf( "a &lt; b" ) != -1 );

        Consequence cons = new Consequence( );
        cons.setSnippet( "a > b" );
        assertFalse( cons.toXML( ).indexOf( "a &gt; b" ) != -1 );
    }
    
    /**
     * This checks that if the rule has "nil" salience, then 
     * no salience value should be put in the rule definition.
     * This allows default salience to work as advertised.
     *
     */
    public void testNilSalience() {
        Rule rule = new Rule("MyRule", null, 1);
        String xml = rule.toXML();
        int idx = xml.indexOf("salience");
        assertEquals(-1, idx);
        
        rule = new Rule("MyRule", new Integer(42), 1);
        xml = rule.toXML();
        idx = xml.indexOf("salience");
        assertTrue(idx > -1);        
    }

}

