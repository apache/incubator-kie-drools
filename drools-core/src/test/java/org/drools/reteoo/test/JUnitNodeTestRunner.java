/**
 * Copyright 2010 JBoss Inc
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

package org.drools.reteoo.test;

import java.util.ArrayList;
import java.util.List;

import org.drools.RuntimeDroolsException;
import org.drools.reteoo.test.dsl.NodeTestDef;
import org.drools.reteoo.test.dsl.NodeTestCase;
import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;

/**
 * A class that runs node tests as JUnit tests
 * 
 * @author etirelli
 */
public class JUnitNodeTestRunner extends Runner {

    // The description of the test suite
    private Description        descr;
    private List<NodeTestCase> testCases = new ArrayList<NodeTestCase>();

    public JUnitNodeTestRunner(Class< ? > clazz) {
        try {
            NodeTestCasesSource ntsuite = (NodeTestCasesSource) clazz.newInstance();
            testCases = ntsuite.getTestCases();
        } catch ( Exception e ) {
            e.printStackTrace();
            throw new RuntimeDroolsException( "Error instantiating node test runner: "+e, e );
        }
        this.descr = Description.createSuiteDescription( "Node test case suite" );

        for ( NodeTestCase tcase : testCases ) {
            Description tcaseDescr = Description.createSuiteDescription( tcase.getName() );
            tcase.setDescription( tcaseDescr );
            this.descr.addChild( tcaseDescr );
            for ( NodeTestDef ntest : tcase.getTests() ) {
                Description ntestDescr = Description.createTestDescription( clazz,
                                                                            ntest.getName() );
                tcaseDescr.addChild( ntestDescr );
                ntest.setDescription( ntestDescr );
            }
        }
    }

    /* (non-Javadoc)
     * @see org.junit.runner.Runner#getDescription()
     */
    @Override
    public Description getDescription() {
        return descr;
    }

    /* (non-Javadoc)
     * @see org.junit.runner.Runner#run(org.junit.runner.notification.RunNotifier)
     */
    @Override
    public void run(RunNotifier notifier) {
        ReteDslTestEngine tester = new ReteDslTestEngine();
        for ( NodeTestCase tcase : testCases ) {
            tester.run( tcase,
                        notifier );
        }
    }

}
