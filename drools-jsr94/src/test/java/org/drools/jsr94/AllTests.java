package org.drools.jsr94;


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



import junit.framework.Test;
import junit.framework.TestSuite;

import org.drools.jsr94.rules.MultipleRepositoryTestCase;
import org.drools.jsr94.rules.RuleRuntimeTestCase;
import org.drools.jsr94.rules.RuleServiceProviderTestCase;
import org.drools.jsr94.rules.StatefulRuleSessionTestCase;
import org.drools.jsr94.rules.StatelessRuleSessionTestCase;
import org.drools.jsr94.rules.admin.LocalRuleExecutionSetProviderTestCase;
import org.drools.jsr94.rules.admin.RuleAdministratorTestCase;
import org.drools.jsr94.rules.admin.RuleExecutionSetProviderTestCase;
import org.drools.jsr94.rules.admin.RuleExecutionSetTestCase;
import org.drools.jsr94.rules.admin.RuleTestCase;



/**

 * Runs all the tests in the <code>org.drools.jsr94</code> hierarchy.

 * JUnit must be set to fork these tests, so it is much faster to run them

 * under a single forked <code>TestSuite</code> than to run them each

 * individually under several separate forked <code>TestCase</code>s.

 * <p/>

 * The negative side of this is that any new <code>TestCase</code>s in the

 * <code>org.drools.jsr94</code> hierarchy must also be added to this class

 * otherwise Maven will not run them as part of its normal test cycle.

 */

public class AllTests

{

    public static Test suite()

    {

        TestSuite suite = new TestSuite( "Drools JSR-94 Test Suite" );



      //  suite.addTestSuite(DroolsBenchmarkTestCase.class);

        

        suite.addTestSuite(RuleRuntimeTestCase.class);

        suite.addTestSuite(RuleServiceProviderTestCase.class);

        suite.addTestSuite(StatefulRuleSessionTestCase.class);

        suite.addTestSuite(StatelessRuleSessionTestCase.class);


        suite.addTestSuite(LocalRuleExecutionSetProviderTestCase.class);

        suite.addTestSuite(RuleAdministratorTestCase.class);

        suite.addTestSuite(RuleExecutionSetProviderTestCase.class);

        suite.addTestSuite(RuleExecutionSetTestCase.class);

        suite.addTestSuite(RuleTestCase.class);

        suite.addTestSuite(MultipleRepositoryTestCase.class);


        return suite;

   }

}

