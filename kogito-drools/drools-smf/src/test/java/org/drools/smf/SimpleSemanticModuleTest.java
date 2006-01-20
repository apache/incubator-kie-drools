package org.drools.smf;

/*
 * $Id: SimpleSemanticModuleTest.java,v 1.15 2005/05/07 04:39:30 dbarnett Exp $
 *
 * Copyright 2004-2005 (C) The Werken Company. All Rights Reserved.
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

import org.drools.DroolsTestCase;
import org.drools.rule.Rule;
import org.drools.rule.RuleSet;
import org.drools.spi.Condition;
import org.drools.spi.Consequence;
import org.drools.spi.Duration;
import org.drools.spi.ObjectType;
import org.drools.spi.PredicateEvaluator;
import org.drools.spi.ReturnValueEvaluator;
import org.drools.spi.RuleBaseContext;

public class SimpleSemanticModuleTest extends DroolsTestCase
{

    public void testAddGetRuleFactory()
    {

        SimpleSemanticModule module = new SimpleSemanticModule( "http://cheese.org" );

        RuleFactory factory = new MockRuleFactory( );

        module.addRuleFactory( "mockCheese",
                               factory );

        assertSame( module.getRuleFactory( "mockCheese" ),
                    factory );

        assertEquals( module.getRuleFactoryNames( ).size( ),
                      1 );

    }

    public void testAddGetObjectTypeFactory()
    {

        SimpleSemanticModule module = new SimpleSemanticModule( "http://cheese.org" );

        ObjectTypeFactory factory = new MockObjectTypeFactory( );

        module.addObjectTypeFactory( "mockCheese",
                                     factory );

        assertSame( module.getObjectTypeFactory( "mockCheese" ),
                    factory );

        assertEquals( module.getObjectTypeFactoryNames( ).size( ),
                      1 );

    }

    public void testAddGetConditionFactory()
    {

        SimpleSemanticModule module = new SimpleSemanticModule( "http://cheese.org" );

        ConditionFactory factory = new MockConditionFactory( );

        module.addConditionFactory( "mockCheese",
                                    factory );

        assertSame( module.getConditionFactory( "mockCheese" ),
                    factory );

        assertEquals( module.getConditionFactoryNames( ).size( ),
                      1 );

    }

    public void testAddGetConsequenceFactory()
    {

        SimpleSemanticModule module = new SimpleSemanticModule( "http://cheese.org" );

        ConsequenceFactory factory = new MockConsequenceFactory( );

        module.addConsequenceFactory( "mockCheese",
                                      factory );

        assertSame( module.getConsequenceFactory( "mockCheese" ),
                    factory );

        assertEquals( module.getConsequenceFactoryNames( ).size( ),
                      1 );

    }

    public void testAddGetDurationFactory()
    {

        SimpleSemanticModule module = new SimpleSemanticModule( "http://cheese.org" );

        DurationFactory factory = new MockDurationFactory( );

        module.addDurationFactory( "mockCheese",
                                   factory );

        assertSame( module.getDurationFactory( "mockCheese" ),
                    factory );

        assertEquals( module.getDurationFactoryNames( ).size( ),
                      1 );

    } 
    
    public void testAddGetPredicateEvalFactory() {
        SimpleSemanticModule module = new SimpleSemanticModule( "http://cheese.org" );
        
        PredicateEvaluatorFactory factory = new MockPredicateEvaluatorFactory();
        
        module.addPredicateEvaluatorFactory("mockCheese", factory);

        assertSame( module.getPredicateEvaluatorFactory( "mockCheese" ),
                    factory );

        assertEquals( module.getPredicateEvaluatorFactoryNames( ).size( ),
                      1 );        
        
    }
    
    public void testAddGetReturnValueEvalFactory() {
        SimpleSemanticModule module = new SimpleSemanticModule( "http://cheese.org" );
        
        ReturnValueEvaluatorFactory factory = new MockReturnValueEvaluatorFactory();
        
        module.addReturnValueEvaluatorFactory("mockCheese", factory);

        assertSame( module.getReturnValueEvaluatorFactory( "mockCheese" ),
                    factory );

        assertEquals( module.getReturnValueEvaluatorFactoryNames( ).size( ),
                      1 );        
                
    }
    
    
    private class MockRuleFactory
        implements
        RuleFactory
    {

        public Rule newRule(RuleSet ruleSet,
                            RuleBaseContext context,
                            Configuration config) throws FactoryException
        {

            return null;

        }

    }

    private class MockObjectTypeFactory
        implements
        ObjectTypeFactory
    {

        public ObjectType newObjectType(Rule rule,
                                        RuleBaseContext context,
                                        Configuration config)
        {

            return null;

        }

    }

    private class MockConditionFactory
        implements
        ConditionFactory
    {

        public Condition[] newCondition(Rule rule,
                                      RuleBaseContext context,
                                      Configuration c)
        {

            return null;

        }

    }

    private class MockConsequenceFactory
        implements
        ConsequenceFactory
    {

        public Consequence newConsequence(Rule rule,
                                          RuleBaseContext context,
                                          Configuration c)
        {

            return null;

        }

    }

    private class MockDurationFactory
        implements
        DurationFactory
    {

        public Duration newDuration(Rule rule,
                                    RuleBaseContext context,
                                    Configuration c)
        {

            return null;

        }

    }
    
    private class MockPredicateEvaluatorFactory
        implements
        PredicateEvaluatorFactory   {

        public PredicateEvaluator[] newPredicateEvaluator(Rule rule,
                                                          RuleBaseContext context,
                                                          Configuration config) throws FactoryException {
            return null;
        }
        
    }
    
    private class MockReturnValueEvaluatorFactory
        implements
        ReturnValueEvaluatorFactory   {

        public ReturnValueEvaluator[] newReturnValueEvaluator(Rule rule,
                                                          RuleBaseContext context,
                                                          Configuration config) throws FactoryException {
            return null;
        }
    
    }    

}
