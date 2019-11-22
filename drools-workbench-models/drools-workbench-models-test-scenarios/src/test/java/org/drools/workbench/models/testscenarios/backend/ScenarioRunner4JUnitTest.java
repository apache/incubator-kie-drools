/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

import java.util.HashMap;

import org.drools.core.impl.KnowledgeBaseImpl;
import org.drools.core.impl.StatefulKnowledgeSessionImpl;
import org.drools.reflective.classloader.ProjectClassLoader;
import org.drools.workbench.models.testscenarios.shared.Scenario;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import org.junit.runner.notification.RunNotifier;
import org.kie.api.runtime.KieSession;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ScenarioRunner4JUnitTest {

    private StatefulKnowledgeSessionImpl ksession;

    @Before
    public void setUp() throws Exception {
        ksession = mock( StatefulKnowledgeSessionImpl.class );
        KnowledgeBaseImpl knowledgeBase = mock( KnowledgeBaseImpl.class );
        when( ksession.getKieBase() ).thenReturn( knowledgeBase );

        ProjectClassLoader classLoader = ProjectClassLoader.createProjectClassLoader();
        when( knowledgeBase.getRootClassLoader() ).thenReturn( classLoader );
    }

    @Test
    public void testBasic() throws Exception {

        HashMap<String, KieSession> ksessions = new HashMap<String, KieSession>();
        ksessions.put( "someId", ksession );
        Scenario scenario = new Scenario();
        scenario.getKSessions().add( "someId" );
        ScenarioRunner4JUnit runner4JUnit = new ScenarioRunner4JUnit( scenario, ksessions );

        RunNotifier notifier = new RunNotifier();
        RunListener runListener = spy( new RunListener() );
        notifier.addListener( runListener );

        runner4JUnit.run( notifier );

        verify( runListener, never() ).testFailure( any( Failure.class ) );
        verify( runListener ).testFinished( any( Description.class ) );
        verify( ksession ).reset();
    }

    @Test
    public void testPMMLRunner() throws Exception {

        HashMap<String, KieSession> ksessions = new HashMap<>();
        ksessions.put( "someId", ksession );
        Scenario scenario = new Scenario();
        scenario.setModelName("myModel");
        scenario.getKSessions().add( "someId" );
        ScenarioRunner4JUnit runner4JUnit = new ScenarioRunner4JUnit( scenario, ksessions );

        RunNotifier notifier = new RunNotifier();
        RunListener runListener = spy( new RunListener() );
        notifier.addListener( runListener );

        runner4JUnit.run( notifier );

        verify( runListener, never() ).testFailure( any( Failure.class ) );
        verify( runListener ).testFinished( any( Description.class ) );
        verify( ksession ).reset();
    }

    @Test
    public void testIDNotSet() throws Exception {

        HashMap<String, KieSession> ksessions = new HashMap<String, KieSession>();
        ksessions.put( null, ksession );
        ScenarioRunner4JUnit runner4JUnit = new ScenarioRunner4JUnit( new Scenario(), ksessions );

        RunNotifier notifier = new RunNotifier();
        RunListener runListener = spy( new RunListener() );
        notifier.addListener( runListener );

        runner4JUnit.run( notifier );

        verify( runListener, never() ).testFailure( any( Failure.class ) );
        verify( runListener ).testFinished( any( Description.class ) );
        verify( ksession ).reset();
    }

    @Test
    public void testNoKieSession() throws Exception {

        ScenarioRunner4JUnit runner4JUnit = new ScenarioRunner4JUnit( new Scenario(), new HashMap<String, KieSession>() );

        RunNotifier notifier = new RunNotifier();
        RunListener runListener = spy( new RunListener() );
        notifier.addListener( runListener );

        runner4JUnit.run( notifier );

        verify( runListener ).testFailure( any( Failure.class ) );
    }

    @Test
    public void testNoKieWithGivenIDSession() throws Exception {

        HashMap<String, KieSession> ksessions = new HashMap<String, KieSession>();
        ksessions.put( "someID", ksession );
        Scenario scenario = new Scenario();
        scenario.getKSessions().add( "someOtherID" );
        ScenarioRunner4JUnit runner4JUnit = new ScenarioRunner4JUnit( scenario, ksessions );

        RunNotifier notifier = new RunNotifier();
        RunListener runListener = spy( new RunListener() );
        notifier.addListener( runListener );

        runner4JUnit.run( notifier );

        verify( runListener ).testFailure( any( Failure.class ) );
    }
}