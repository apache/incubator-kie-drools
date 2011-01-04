<<<<<<< HEAD:drools-templates/src/test/java/org/drools/template/model/RuleRenderTest.java
package org.drools.template.model;

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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * @author <a href="mailto:michael.neale@gmail.com"> Michael Neale</a>
 *
 * Tests how the rule parse tree renders itself to a rule XML fragment.
 */
public class RuleRenderTest {

    @Test
    public void testRuleRender() {
        final Rule rule = new Rule( "myrule",
                              new Integer( 42 ),
                              1 );
        rule.setComment( "rule comments" );

        final Condition cond = new Condition();
        cond.setComment( "cond comment" );
        cond.setSnippet( "cond snippet" );
        rule.addCondition( cond );

        final Consequence cons = new Consequence();
        cons.setComment( "cons comment" );
        cons.setSnippet( "cons snippet;" );
        rule.addConsequence( cons );
        rule.addConsequence( cons );

        final DRLOutput out = new DRLOutput();
        rule.renderDRL( out );
        final String drl = out.getDRL();
        assertNotNull( drl );

        assertTrue( drl.indexOf( "cond snippet" ) != -1 );
        assertTrue( drl.indexOf( "cons snippet" ) != -1 );
        assertTrue( drl.indexOf( "salience 42" ) != -1 );
        assertTrue( drl.indexOf( "salience 42" ) < drl.indexOf( "when" ) );
        assertTrue( drl.indexOf( "cond snippet" ) < drl.indexOf( "then" ) );
        assertTrue( drl.indexOf( "cons snippet;" ) > drl.indexOf( "then" ) );
        assertTrue( drl.indexOf( "rule" ) != -1 );
        assertTrue( drl.indexOf( "end" ) > drl.indexOf( "rule " ) );
        assertTrue( drl.indexOf( "#rule comments" ) > -1 );

    }

    @Test
    public void testAttributes() throws Exception {
        Rule rule = new Rule("la", new Integer(42), 2);

        rule.setActivationGroup( "foo" );
        rule.setNoLoop( "true" );
        rule.setRuleFlowGroup( "ruleflowgroup" );
        rule.setDuration("42");
        DRLOutput out = new DRLOutput();
        rule.renderDRL( out );

        String result = out.toString();

        assertTrue(result.indexOf( "ruleflow-group \"ruleflowgroup\"" ) > -1 );
        assertTrue(result.indexOf( "no-loop true" ) > -1);
        assertTrue(result.indexOf( "activation-group \"foo\"" ) > -1);
        assertTrue(result.indexOf( "duration 42" ) > -1);

    }

    @Test
    public void testSalienceCalculator() {
        final int rowNumber = 2;
        final int salience = Rule.calcSalience( rowNumber );
        assertEquals( 65533,
                      salience );
    }

    @Test
    public void testColNumToColName() {
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

    @Test
    public void testNotEscapeChars() {
        //bit of a legacy from the olde XML dayes of yesteryeare
        final Condition cond = new Condition();
        cond.setSnippet( "a < b" );
        final DRLOutput out = new DRLOutput();
        cond.renderDRL( out );

        assertTrue( out.toString().indexOf( "a < b" ) != -1 );

    }

    /**
     * This checks that if the rule has "nil" salience, then
     * no salience value should be put in the rule definition.
     * This allows default salience to work as advertised.
     *
     */
    @Test
    public void testNilSalience() {
        Rule rule = new Rule( "MyRule",
                              null,
                              1 );

        DRLOutput out = new DRLOutput();
        rule.renderDRL( out );
        String xml = out.toString();
        int idx = xml.indexOf( "salience" );
        assertEquals( -1,
                      idx );

        rule = new Rule( "MyRule",
                         new Integer( 42 ),
                         1 );
        out = new DRLOutput();
        rule.renderDRL( out );
        xml = out.toString();
        idx = xml.indexOf( "salience" );
        assertTrue( idx > -1 );
    }

}
=======
package org.drools.template.model;

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
public class RuleRenderTest extends TestCase {

    public void testRuleRender() {
        final Rule rule = new Rule( "myrule",
                              new Integer( 42 ),
                              1 );
        rule.setComment( "rule comments" );

        final Condition cond = new Condition();
        cond.setComment( "cond comment" );
        cond.setSnippet( "cond snippet" );
        rule.addCondition( cond );

        final Consequence cons = new Consequence();
        cons.setComment( "cons comment" );
        cons.setSnippet( "cons snippet;" );
        rule.addConsequence( cons );
        rule.addConsequence( cons );

        final DRLOutput out = new DRLOutput();
        rule.renderDRL( out );
        final String drl = out.getDRL();
        assertNotNull( drl );

        assertTrue( drl.indexOf( "cond snippet" ) != -1 );
        assertTrue( drl.indexOf( "cons snippet" ) != -1 );
        assertTrue( drl.indexOf( "salience 42" ) != -1 );
        assertTrue( drl.indexOf( "salience 42" ) < drl.indexOf( "when" ) );
        assertTrue( drl.indexOf( "cond snippet" ) < drl.indexOf( "then" ) );
        assertTrue( drl.indexOf( "cons snippet;" ) > drl.indexOf( "then" ) );
        assertTrue( drl.indexOf( "rule" ) != -1 );
        assertTrue( drl.indexOf( "end" ) > drl.indexOf( "rule " ) );
        assertTrue( drl.indexOf( "#rule comments" ) > -1 );

    }

    public void testAttributes() throws Exception {
        Rule rule = new Rule("la", new Integer(42), 2);

        rule.setActivationGroup( "foo" );
        rule.setNoLoop( true );
        rule.setRuleFlowGroup( "ruleflowgroup" );
        rule.setDuration( 42L );
        DRLOutput out = new DRLOutput();
        rule.renderDRL( out );

        String result = out.toString();

        assertTrue(result.indexOf( "ruleflow-group \"ruleflowgroup\"" ) > -1 );
        assertTrue(result.indexOf( "no-loop true" ) > -1);
        assertTrue(result.indexOf( "activation-group \"foo\"" ) > -1);
        assertTrue(result.indexOf( "duration 42" ) > -1);

    }

    public void testSalienceCalculator() {
        final int rowNumber = 2;
        final int salience = Rule.calcSalience( rowNumber );
        assertEquals( 65533,
                      salience );
    }


    public void testNotEscapeChars() {
        //bit of a legacy from the olde XML dayes of yesteryeare
        final Condition cond = new Condition();
        cond.setSnippet( "a < b" );
        final DRLOutput out = new DRLOutput();
        cond.renderDRL( out );

        assertTrue( out.toString().indexOf( "a < b" ) != -1 );

    }

    /**
     * This checks that if the rule has "nil" salience, then
     * no salience value should be put in the rule definition.
     * This allows default salience to work as advertised.
     *
     */
    public void testNilSalience() {
        Rule rule = new Rule( "MyRule",
                              null,
                              1 );

        DRLOutput out = new DRLOutput();
        rule.renderDRL( out );
        String xml = out.toString();
        int idx = xml.indexOf( "salience" );
        assertEquals( -1,
                      idx );

        rule = new Rule( "MyRule",
                         new Integer( 42 ),
                         1 );
        out = new DRLOutput();
        rule.renderDRL( out );
        xml = out.toString();
        idx = xml.indexOf( "salience" );
        assertTrue( idx > -1 );
    }

}
>>>>>>> JBRULES-2858:drools-templates/src/test/java/org/drools/template/model/RuleRenderTest.java
