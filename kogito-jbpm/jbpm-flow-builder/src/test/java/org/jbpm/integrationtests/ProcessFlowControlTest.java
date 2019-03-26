/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.integrationtests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.drools.compiler.builder.impl.KnowledgeBuilderImpl.PackageMergeException;
import org.drools.core.common.InternalAgenda;
import org.drools.core.event.DefaultAgendaEventListener;
import org.drools.core.impl.KnowledgeBaseFactory;
import org.jbpm.test.util.AbstractBaseTest;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.event.rule.AgendaEventListener;
import org.kie.api.event.rule.MatchCancelledEvent;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.api.runtime.rule.Match;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProcessFlowControlTest extends AbstractBaseTest {
    
    private static final Logger logger = LoggerFactory.getLogger(ProcessFlowControlTest.class);
  
    protected KieBase getRuleBase(final KieBaseConfiguration config) throws Exception {
        return KnowledgeBaseFactory.newKnowledgeBase( config );
    }

    @Test
    public void testRuleFlowConstraintDialects() throws Exception {
        builder.addRuleFlow( new InputStreamReader( getClass().getResourceAsStream( "test_ConstraintDialects.rfm" ) ) );

        logger.error( builder.getErrors().toString() );

        assertEquals( 0,
                      builder.getErrors().getErrors().length );

        KieSession session = createKieSession(true, builder.getPackages());
        List<Integer> inList = new ArrayList<Integer>();
        List<Integer> outList = new ArrayList<Integer>();
        session.setGlobal( "inList",
                           inList );
        session.setGlobal( "outList",
                           outList );

        inList.add( 1 );
        inList.add( 3 );
        inList.add( 6 );
        inList.add( 25 );

        FactHandle handle = session.insert( inList );
        session.startProcess( "ConstraintDialects" );
        assertEquals( 4,
                      outList.size() );
        assertEquals( "MVELCodeConstraint was here",
                      outList.get( 0 ) );
        assertEquals( "JavaCodeConstraint was here",
                      outList.get( 1 ) );
        assertEquals( "MVELRuleConstraint was here",
                      outList.get( 2 ) );
        assertEquals( "JavaRuleConstraint was here",
                      outList.get( 3 ) );

        outList.clear();
        inList.remove( new Integer( 1 ) );
        session.update( handle,
                        inList );
        session.startProcess( "ConstraintDialects" );
        assertEquals( 3,
                      outList.size() );
        assertEquals( "JavaCodeConstraint was here",
                      outList.get( 0 ) );
        assertEquals( "MVELRuleConstraint was here",
                      outList.get( 1 ) );
        assertEquals( "JavaRuleConstraint was here",
                      outList.get( 2 ) );

        outList.clear();
        inList.remove( new Integer( 6 ) );
        session.update( handle,
                        inList );
        session.startProcess( "ConstraintDialects" );
        assertEquals( 2,
                      outList.size() );
        assertEquals( "JavaCodeConstraint was here",
                      outList.get( 0 ) );
        assertEquals( "JavaRuleConstraint was here",
                      outList.get( 1 ) );

        outList.clear();
        inList.remove( new Integer( 3 ) );
        session.update( handle,
                        inList );
        session.startProcess( "ConstraintDialects" );
        assertEquals( 1,
                      outList.size() );
        assertEquals( "JavaRuleConstraint was here",
                      outList.get( 0 ) );

        outList.clear();
        inList.remove( new Integer( 25 ) );
        session.update( handle,
                        inList );
        try {
            session.startProcess( "ConstraintDialects" );
            fail( "This should have thrown an exception" );
        } catch ( Exception e ) {
        }
    }

    @Test
    public void testRuleFlow() throws Exception {
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "ruleflow.drl" ) ) );
        builder.addRuleFlow( new InputStreamReader( getClass().getResourceAsStream( "ruleflow.rfm" ) ) );

        KieSession workingMemory = createKieSession(true, builder.getPackages());
        
        final List<String> list = new ArrayList<String>();
        workingMemory.setGlobal( "list",
                                 list );

        workingMemory.fireAllRules();
        assertEquals( 0,
                      list.size() );

        final ProcessInstance processInstance = workingMemory.startProcess( "0" );
        assertEquals( ProcessInstance.STATE_COMPLETED,
                processInstance.getState() );
        assertEquals( 4,
                      list.size() );
        assertEquals( "Rule1",
                      list.get( 0 ) );
        list.subList(1,2).contains( "Rule2" );
        list.subList(1,2).contains( "Rule3" );
        assertEquals( "Rule4",
                      list.get( 3 ) );
    }

    @Test
    public void testRuleFlowUpgrade() throws Exception {
        // Set the system property so that automatic conversion can happen
        System.setProperty( "drools.ruleflow.port",
                            "true" );

        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "ruleflow.drl" ) ) );
        builder.addRuleFlow( new InputStreamReader( getClass().getResourceAsStream( "ruleflow40.rfm" ) ) );
        
        KieSession workingMemory = createKieSession(true, builder.getPackages());
        
        final List<String> list = new ArrayList<String>();
        workingMemory.setGlobal( "list",
                                 list );

        workingMemory.fireAllRules();
        assertEquals( 0,
                      list.size() );

        final ProcessInstance processInstance = workingMemory.startProcess( "0" );
        assertEquals( ProcessInstance.STATE_COMPLETED,
                processInstance.getState() );
        assertEquals( 4,
                      list.size() );
        assertEquals( "Rule1",
                      list.get( 0 ) );
        list.subList(1,2).contains( "Rule2" );
        list.subList(1,2).contains( "Rule3" );
        assertEquals( "Rule4",
                      list.get( 3 ) );
        
        // Reset the system property so that automatic conversion should not happen
        System.setProperty( "drools.ruleflow.port",
                            "false" );
    }

    @Test
    public void testRuleFlowClear() throws Exception {
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_ruleflowClear.drl" ) ) );
        builder.addRuleFlow( new InputStreamReader( getClass().getResourceAsStream( "test_ruleflowClear.rfm" ) ) );
        
        KieSession workingMemory = createKieSession(true, builder.getPackages());
        
        final List<String> list = new ArrayList<String>();
        workingMemory.setGlobal( "list",
                                 list );

        final List<Match> activations = new ArrayList<Match>();
        AgendaEventListener listener = new DefaultAgendaEventListener() {
            public void matchCancelled(MatchCancelledEvent event) { 
                activations.add( event.getMatch() );
            }
        };

        workingMemory.addEventListener( listener );
        InternalAgenda agenda = (InternalAgenda) workingMemory.getAgenda();
//        assertEquals( 0,
//                      agenda.getRuleFlowGroup( "flowgroup-1" ).size() );

        // We need to call fireAllRules here to get the InitialFact into the system, to the eval(true)'s kick in
        workingMemory.fireAllRules();
        agenda.evaluateEagerList();

        // Now we have 4 in the RuleFlow, but not yet in the agenda
        assertEquals( 4,
                      agenda.sizeOfRuleFlowGroup( "flowgroup-1" ) );

        // Check they aren't in the Agenda
        assertEquals( 0,
                      agenda.getAgendaGroup( "MAIN" ).size() );

        // Check we have 0 activation cancellation events
        assertEquals( 0,
                      activations.size() );

        ((InternalAgenda) workingMemory.getAgenda()).clearAndCancelRuleFlowGroup( "flowgroup-1" );

        // Check the AgendaGroup and RuleFlowGroup  are now empty
        assertEquals( 0,
                      agenda.getAgendaGroup( "MAIN" ).size() );
        assertEquals( 0,
                      agenda.sizeOfRuleFlowGroup( "flowgroup-1" ) );

        // Check we have four activation cancellation events
        assertEquals( 4,
                      activations.size() );
    }

    @Test
    public void testRuleFlowInPackage() throws Exception {
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "ruleflow.drl" ) ) );
        builder.addRuleFlow( new InputStreamReader( getClass().getResourceAsStream( "ruleflow.rfm" ) ) );

        final KieSession workingMemory = createKieSession(true, builder.getPackages());
        final List<String> list = new ArrayList<String>();
        workingMemory.setGlobal( "list",
                                 list );

        workingMemory.fireAllRules();
        assertEquals( 0,
                      list.size() );

        final ProcessInstance processInstance = workingMemory.startProcess( "0" );
        assertEquals( ProcessInstance.STATE_COMPLETED,
                processInstance.getState() );
        assertEquals( 4,
                      list.size() );
        assertEquals( "Rule1",
                      list.get( 0 ) );
        list.subList(1,2).contains( "Rule2" );
        list.subList(1,2).contains( "Rule3" );
        assertEquals( "Rule4",
                      list.get( 3 ) );

    }

    @Test
    public void testLoadingRuleFlowInPackage1() throws Exception {
        // adding ruleflow before adding package
        builder.addRuleFlow( new InputStreamReader( getClass().getResourceAsStream( "ruleflow.rfm" ) ) );
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "ruleflow.drl" ) ) );
        builder.getPackages();
    }

    @Test
    public void testLoadingRuleFlowInPackage2() throws Exception {
        // only adding ruleflow
        builder.addRuleFlow( new InputStreamReader( getClass().getResourceAsStream( "ruleflow.rfm" ) ) );
        builder.getPackages();
    }

    @Test
    public void testLoadingRuleFlowInPackage3() throws Exception {
        // only adding ruleflow without any generated rules
        builder.addRuleFlow( new InputStreamReader( getClass().getResourceAsStream( "empty_ruleflow.rfm" ) ) );
        builder.getPackages();
    }

    @Test
    @Ignore
    public void FIXME_testLoadingRuleFlowInPackage4() throws Exception {
        // adding ruleflows of different package
        builder.addRuleFlow( new InputStreamReader( getClass().getResourceAsStream( "empty_ruleflow.rfm" ) ) );
        try {
            builder.addRuleFlow( new InputStreamReader( getClass().getResourceAsStream( "ruleflow.rfm" ) ) );
            throw new Exception( "An exception should have been thrown." );
        } catch ( PackageMergeException e ) {
            // do nothing
        }
    }

    @Test
    @Ignore
    public void FIXME_testLoadingRuleFlowInPackage5() throws Exception {
        // adding ruleflow of different package than rules
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "ruleflow.drl" ) ) );
        try {
            builder.addRuleFlow( new InputStreamReader( getClass().getResourceAsStream( "empty_ruleflow.rfm" ) ) );
            throw new Exception( "An exception should have been thrown." );
        } catch ( PackageMergeException e ) {
            // do nothing
        }
    }

    @Test
    @Ignore
    public void FIXME_testLoadingRuleFlowInPackage6() throws Exception {
        // adding rules of different package than ruleflow
        builder.addRuleFlow( new InputStreamReader( getClass().getResourceAsStream( "empty_ruleflow.rfm" ) ) );
        try {
            builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "ruleflow.drl" ) ) );
            throw new Exception( "An exception should have been thrown." );
        } catch ( PackageMergeException e ) {
            // do nothing
        }
    }

    @Test
    public void testRuleFlowActionDialects() throws Exception {
        builder.addRuleFlow( new InputStreamReader( getClass().getResourceAsStream( "test_ActionDialects.rfm" ) ) );

        final KieSession session = createKieSession(true, builder.getPackages());
        
        List<String> list = new ArrayList<String>();
        session.setGlobal( "list",
                           list );

        session.startProcess( "ActionDialects" );

        assertEquals( 2,
                      list.size() );
        assertEquals( "mvel was here",
                      list.get( 0 ) );
        assertEquals( "java was here",
                      list.get( 1 ) );
    }

    @Test
    public void testLoadingRuleFlowNoPackageName() throws Exception {
        // loading a ruleflow with errors (null package name cause 3 errors)
        builder.addRuleFlow( new InputStreamReader( getClass().getResourceAsStream( "error_ruleflow.rfm" ) ) );
        assertEquals( 3,
                      builder.getErrors().getErrors().length );
    }

}
