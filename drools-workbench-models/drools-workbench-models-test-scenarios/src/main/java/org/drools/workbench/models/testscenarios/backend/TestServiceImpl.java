/*
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.workbench.models.testscenarios.backend;

import org.drools.core.base.TypeResolver;
import org.drools.workbench.models.testscenarios.shared.Scenario;
import org.junit.runner.Description;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import org.kie.api.runtime.KieSession;

public class TestServiceImpl
        implements
        TestService<Scenario> {

    @Override
    public void run( Scenario scenario,
                     KieSession ksession,
                     TypeResolver resolver,
                     RunListener listener ) {
        try {

            long time = System.nanoTime();

            // execute the test scenario
            ScenarioRunner4JUnit runner = new ScenarioRunner4JUnit( scenario,
                                                                    ksession );
            JUnitCore junit = new JUnitCore();
            junit.addListener( listener );
            junit.run( runner );

            Result result = new Result();
            listener.testRunFinished(result);

        } catch ( Exception e ) {
            reportUnrecoverableError( "Error running scenario " + scenario.getName(),
                                      listener,
                                      e );
        }
    }

    private void reportUnrecoverableError( String message,
                                           RunListener listener,
                                           Exception e ) {
        try {
            Description description = Description.createSuiteDescription( message );
            listener.testFailure( new Failure( description,
                                               e ) );
        } catch ( Exception e2 ) {
            // intentionally left empty as there is nothing to do
        }
    }

}
