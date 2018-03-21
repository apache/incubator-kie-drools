/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.compiler.integrationtests.incrementalcompilation;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.drools.compiler.CommonTestMethodBase;
import org.drools.compiler.FactA;
import org.drools.compiler.Message;
import org.drools.compiler.Person;
import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.drools.core.ClassObjectFilter;
import org.drools.core.ClockType;
import org.drools.core.command.runtime.rule.FireAllRulesCommand;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.event.DefaultAgendaEventListener;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.impl.KnowledgeBaseImpl;
import org.drools.core.reteoo.EntryPointNode;
import org.drools.core.reteoo.ObjectTypeNode;
import org.drools.core.reteoo.Rete;
import org.drools.core.reteoo.RuleTerminalNode;
import org.drools.core.time.impl.PseudoClockScheduler;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.Service;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieModule;
import org.kie.api.builder.Message.Level;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.Results;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.builder.model.KieSessionModel;
import org.kie.api.command.BatchExecutionCommand;
import org.kie.api.command.Command;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.definition.KiePackage;
import org.kie.api.definition.rule.Rule;
import org.kie.api.definition.type.FactType;
import org.kie.api.definition.type.Role;
import org.kie.api.event.rule.AfterMatchFiredEvent;
import org.kie.api.io.ResourceType;
import org.kie.api.logger.KieRuntimeLogger;
import org.kie.api.marshalling.KieMarshallers;
import org.kie.api.marshalling.Marshaller;
import org.kie.api.runtime.Globals;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.StatelessKieSession;
import org.kie.api.runtime.conf.ClockTypeOption;
import org.kie.api.runtime.conf.TimedRuleExecutionOption;
import org.kie.api.runtime.conf.TimerJobFactoryOption;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.api.time.SessionPseudoClock;
import org.kie.internal.builder.IncrementalResults;
import org.kie.internal.builder.InternalKieBuilder;
import org.kie.internal.builder.conf.PropertySpecificOption;
import org.kie.internal.command.CommandFactory;

import static java.util.Arrays.asList;

import static org.drools.core.util.DroolsTestUtil.rulestoMap;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class IncrementalCompilationTest extends CommonTestMethodBase {

    @Test
    public void testLoadOrderAfterRuleRemoval() throws Exception {
        String header = "package org.drools.compiler\n";

        String drl1 = "rule R1 when\n" +
                "   $m : Message( message == \"Hello World1\" )\n" +
                "then\n" +
                "end\n";

        String drl2 = "rule R2 when\n" +
                "   $m : Message( message == \"Hello World2\" )\n" +
                "then\n" +
                "end\n";

        String drl3 = "rule R3 when\n" +
                "   $m : Message( message == \"Hello World3\" )\n" +
                "then\n" +
                "end\n";

        String drl4 = "rule R4 when\n" +
                "   $m : Message( message == \"Hello World4\" )\n" +
                "then\n" +
                "end\n";

        KieServices ks = KieServices.Factory.get();
        ReleaseId releaseId1 = ks.newReleaseId( "org.kie", "test-upgrade", "1" );
        KieModule km = createAndDeployJar( ks, releaseId1, header );
        KieContainer kc = ks.newKieContainer( km.getReleaseId() );

        createAndDeployAndTest( kc, "2", header, drl1 + drl2 + drl3, "R1", "R2", "R3" );

        createAndDeployAndTest( kc, "3", header, drl1 + drl3, "R1", "R3" );

        createAndDeployAndTest( kc, "4", header, drl2 + drl1 + drl4, "R2", "R1", "R4" );

        createAndDeployAndTest( kc, "5", header, drl2 + drl1, "R2", "R1" );

        createAndDeployAndTest( kc, "6", header, "" );

        createAndDeployAndTest( kc, "7", header, drl3, "R3" );
    }

    public static void createAndDeployAndTest( KieContainer kc,
                                               String version,
                                               String header,
                                               String drls,
                                               String... ruleNames ) {
        if ( ruleNames == null ) {
            ruleNames = new String[ 0 ];
        }
        KieServices ks = KieServices.Factory.get();

        StringBuilder sbuilder = new StringBuilder();
        sbuilder.append( header );
        sbuilder.append( drls );

        ReleaseId releaseId1 = ks.newReleaseId( "org.kie", "test-upgrade", version );
        KieModule km = createAndDeployJar( ks, releaseId1, sbuilder.toString() );

        kc.updateToVersion( km.getReleaseId() );

        KiePackage kpkg = kc.getKieBase().getKiePackage( "org.drools.compiler" );
        assertEquals( ruleNames.length, kpkg.getRules().size() );
        Map<String, Rule> rules = rulestoMap( kpkg.getRules() );

        int i = 0;
        for ( String ruleName : ruleNames ) {
            assertEquals( ruleName, i++, ( (RuleImpl) rules.get( ruleName ) ).getLoadOrder() );
        }
    }

    @Test
    public void testKJarUpgrade() throws Exception {
        String drl1 = "package org.drools.compiler\n" +
                "rule R1 when\n" +
                "   $m : Message()\n" +
                "then\n" +
                "end\n";

        String drl2_1 = "package org.drools.compiler\n" +
                "rule R2_1 when\n" +
                "   $m : Message( message == \"Hi Universe\" )\n" +
                "then\n" +
                "end\n";

        String drl2_2 = "package org.drools.compiler\n" +
                "rule R2_2 when\n" +
                "   $m : Message( message == \"Hello World\" )\n" +
                "then\n" +
                "end\n";

        KieServices ks = KieServices.Factory.get();

        // Create an in-memory jar for version 1.0.0
        ReleaseId releaseId1 = ks.newReleaseId( "org.kie", "test-upgrade", "1.0.0" );
        KieModule km = createAndDeployJar( ks, releaseId1, drl1, drl2_1 );

        // Create a session and fire rules
        KieContainer kc = ks.newKieContainer( km.getReleaseId() );
        KieSession ksession = kc.newKieSession();
        ksession.insert( new Message( "Hello World" ) );
        assertEquals( 1, ksession.fireAllRules() );
        ksession.dispose();

        // Create a new jar for version 1.1.0
        ReleaseId releaseId2 = ks.newReleaseId( "org.kie", "test-upgrade", "1.1.0" );
        km = createAndDeployJar( ks, releaseId2, drl1, drl2_2 );

        // try to update the container to version 1.1.0
        kc.updateToVersion( releaseId2 );

        // create and use a new session
        ksession = kc.newKieSession();
        ksession.insert( new Message( "Hello World" ) );
        assertEquals( 2, ksession.fireAllRules() );
    }

    @Test
    public void testKJarUpgradeSameSession() throws Exception {
        String drl1 = "package org.drools.compiler\n" +
                "rule R1 when\n" +
                "   $m : Message()\n" +
                "then\n" +
                "end\n";

        String drl2_1 = "package org.drools.compiler\n" +
                "rule R2_1 when\n" +
                "   $m : Message( message == \"Hi Universe\" )\n" +
                "then\n" +
                "end\n";

        String drl2_2 = "package org.drools.compiler\n" +
                "rule R2_2 when\n" +
                "   $m : Message( message == \"Hello World\" )\n" +
                "then\n" +
                "end\n";

        KieServices ks = KieServices.Factory.get();

        // Create an in-memory jar for version 1.0.0
        ReleaseId releaseId1 = ks.newReleaseId( "org.kie", "test-upgrade", "1.0.0" );
        KieModule km = createAndDeployJar( ks, releaseId1, drl1, drl2_1 );

        // Create a session and fire rules
        KieContainer kc = ks.newKieContainer( km.getReleaseId() );
        KieSession ksession = kc.newKieSession();
        ksession.insert( new Message( "Hello World" ) );
        assertEquals( 1, ksession.fireAllRules() );

        // Create a new jar for version 1.1.0
        ReleaseId releaseId2 = ks.newReleaseId( "org.kie", "test-upgrade", "1.1.0" );
        km = createAndDeployJar( ks, releaseId2, drl1, drl2_2 );

        // try to update the container to version 1.1.0
        kc.updateToVersion( releaseId2 );

        // continue working with the session
        ksession.insert( new Message( "Hello World" ) );
        assertEquals( 3, ksession.fireAllRules() );
    }

    public static KieModule createAndDeployJar( KieServices ks,
                                                ReleaseId releaseId,
                                                String... drls ) {
        byte[] jar = createKJar( ks, releaseId, null, drls );
        return deployJar( ks, jar );
    }

    @Test
    public void testDeletedFile() throws Exception {
        String drl1 = "package org.drools.compiler\n" +
                "rule R1 when\n" +
                "   $m : Message()\n" +
                "then\n" +
                "end\n";

        String drl2 = "package org.drools.compiler\n" +
                "rule R2_2 when\n" +
                "   $m : Message( message == \"Hello World\" )\n" +
                "then\n" +
                "end\n";

        KieServices ks = KieServices.Factory.get();

        // Create an in-memory jar for version 1.0.0
        ReleaseId releaseId1 = ks.newReleaseId( "org.kie", "test-delete", "1.0.0" );
        KieModule km = createAndDeployJar( ks, releaseId1, drl1, drl2 );

        KieContainer kieContainer = ks.newKieContainer( releaseId1 );
        KieContainer kieContainer2 = ks.newKieContainer( releaseId1 );

        KieSession ksession = kieContainer.newKieSession();
        ksession.insert( new Message( "Hello World" ) );
        assertEquals( 2, ksession.fireAllRules() );

        ReleaseId releaseId2 = ks.newReleaseId( "org.kie", "test-delete", "1.0.1" );
        km = createAndDeployJar( ks, releaseId2, null, drl2 );

        kieContainer.updateToVersion( releaseId2 );

        // test with the old ksession ...
        ksession.insert( new Message( "Hello World" ) );
        assertEquals( 1, ksession.fireAllRules() );

        // ... and with a brand new one
        ksession = kieContainer.newKieSession();
        ksession.insert( new Message( "Hello World" ) );
        assertEquals( 1, ksession.fireAllRules() );

        // check that the second kieContainer hasn't been affected by the update of the first one
        KieSession ksession2 = kieContainer2.newKieSession();
        ksession2.insert( new Message( "Hello World" ) );
        assertEquals( 2, ksession2.fireAllRules() );
    }

    @Test
    public void testIncrementalCompilationWithAddedError() throws Exception {
        String drl1 = "package org.drools.compiler\n" +
                "rule R1 when\n" +
                "   $m : Message()\n" +
                "then\n" +
                "end\n";

        String drl2_1 = "package org.drools.compiler\n" +
                "rule R2_1 when\n" +
                "   $m : Message( message == \"Hi Universe\" )\n" +
                "then\n" +
                "end\n";

        String drl2_2 = "package org.drools.compiler\n" +
                "rule R2_2 when\n" +
                "   $m : Message( nonExistentField == \"Hello World\" )\n" +
                "then\n" +
                "end\n";

        KieServices ks = KieServices.Factory.get();

        KieFileSystem kfs = ks.newKieFileSystem()
                .write( "src/main/resources/r1.drl", drl1 )
                .write( "src/main/resources/r2.drl", drl2_1 );

        KieBuilder kieBuilder = ks.newKieBuilder( kfs ).buildAll();
        KieContainer kieContainer = ks.newKieContainer( ks.getRepository().getDefaultReleaseId() );

        KieSession ksession = kieContainer.newKieSession();
        ksession.insert( new Message( "Hello World" ) );
        assertEquals( 1, ksession.fireAllRules() );

        kfs.write( "src/main/resources/r2.drl", drl2_2 );
        IncrementalResults results = ( (InternalKieBuilder) kieBuilder ).createFileSet( "src/main/resources/r2.drl" ).build();

        assertEquals( 1, results.getAddedMessages().size() );
        assertEquals( 0, results.getRemovedMessages().size() );

        kieContainer.updateToVersion( ks.getRepository().getDefaultReleaseId() );
        ksession = kieContainer.newKieSession();
        ksession.insert( new Message( "Hello World" ) );
        assertEquals( 1, ksession.fireAllRules() );
    }

    @Test
    public void testIncrementalCompilationWithRemovedError() throws Exception {
        String drl1 = "package org.drools.compiler\n" +
                "rule R1 when\n" +
                "   $m : Message()\n" +
                "then\n" +
                "end\n";

        String drl2_1 = "package org.drools.compiler\n" +
                "rule R2_1 when\n" +
                "   $m : Message( nonExistentField == \"Hello World\" )\n" +
                "then\n" +
                "end\n";

        String drl2_2 = "package org.drools.compiler\n" +
                "rule R2_2 when\n" +
                "   $m : Message( message == \"Hello World\" )\n" +
                "then\n" +
                "end\n";

        KieServices ks = KieServices.Factory.get();

        KieFileSystem kfs = ks.newKieFileSystem()
                .write( "src/main/resources/r1.drl", drl1 )
                .write( "src/main/resources/r2.drl", drl2_1 );

        KieBuilder kieBuilder = ks.newKieBuilder( kfs ).buildAll();
        assertEquals( 1, kieBuilder.getResults().getMessages( org.kie.api.builder.Message.Level.ERROR ).size() );

        kfs.write( "src/main/resources/r2.drl", drl2_2 );
        IncrementalResults results = ( (InternalKieBuilder) kieBuilder ).createFileSet( "src/main/resources/r2.drl" ).build();

        assertEquals( 0, results.getAddedMessages().size() );
        assertEquals( 1, results.getRemovedMessages().size() );

        KieContainer kieContainer = ks.newKieContainer( ks.getRepository().getDefaultReleaseId() );
        KieSession ksession = kieContainer.newKieSession();
        ksession.insert( new Message( "Hello World" ) );
        assertEquals( 2, ksession.fireAllRules() );
    }

    @Test
    public void testIncrementalCompilationAddErrorThenRemoveError() throws Exception {
        //Valid
        String drl1 = "package org.drools.compiler\n" +
                "rule R1 when\n" +
                "   $m : Message()\n" +
                "then\n" +
                "end\n";

        //Field is unknown ("nonExistentField" not "message")
        String drl2_1 = "package org.drools.compiler\n" +
                "rule R2_1 when\n" +
                "   $m : Message( nonExistentField == \"Hello World\" )\n" +
                "then\n" +
                "end\n";

        //Valid
        String drl2_2 = "package org.drools.compiler\n" +
                "rule R2_2 when\n" +
                "   $m : Message( message == \"Hello World\" )\n" +
                "then\n" +
                "end\n";

        KieServices ks = KieServices.Factory.get();

        KieFileSystem kfs = ks.newKieFileSystem()
                .write( "src/main/resources/r1.drl", drl1 );

        KieBuilder kieBuilder = ks.newKieBuilder( kfs ).buildAll();
        assertEquals( 0, kieBuilder.getResults().getMessages( org.kie.api.builder.Message.Level.ERROR ).size() );

        //Add file with error - expect 1 "added" error message
        kfs.write( "src/main/resources/r2.drl", drl2_1 );
        IncrementalResults addResults = ( (InternalKieBuilder) kieBuilder ).createFileSet( "src/main/resources/r2.drl" ).build();

        assertEquals( 1, addResults.getAddedMessages().size() );
        assertEquals( 0, addResults.getRemovedMessages().size() );

        //Update flawed file with correct version - expect 0 "added" error messages and removal of 1 previous error
        kfs.write( "src/main/resources/r2.drl", drl2_2 );
        IncrementalResults removeResults = ( (InternalKieBuilder) kieBuilder ).createFileSet( "src/main/resources/r2.drl" ).build();

        assertEquals( 0, removeResults.getAddedMessages().size() );
        assertEquals( 1, removeResults.getRemovedMessages().size() );
    }

    @Test
    public void testIncrementalCompilationAddErrorThenRemoveIt() throws Exception {
        //Fact Type is unknown ("NonExistentClass" not "Message")
        String drl1 = "package org.drools.compiler\n" +
                "rule R1 when\n" +
                "   $m : NonExistentClass()\n" +
                "then\n" +
                "end\n";

        //Field is unknown ("nonExistentField" not "message")
        String drl2_1 = "package org.drools.compiler\n" +
                "rule R2_1 when\n" +
                "   $m : Message( nonExistentField == \"Hello World\" )\n" +
                "then\n" +
                "end\n";

        //Valid
        String drl2_2 = "package org.drools.compiler\n" +
                "rule R2_2 when\n" +
                "   $m : Message( message == \"Hello World\" )\n" +
                "then\n" +
                "end\n";

        KieServices ks = KieServices.Factory.get();

        KieFileSystem kfs = ks.newKieFileSystem()
                .write( "src/main/resources/r1.drl", drl1 );

        //Initial file contains errors
        KieBuilder kieBuilder = ks.newKieBuilder( kfs ).buildAll();
        assertEquals( 1, kieBuilder.getResults().getMessages( org.kie.api.builder.Message.Level.ERROR ).size() );

        //Add file with error - expect 1 "added" error message
        kfs.write( "src/main/resources/r2.drl", drl2_1 );
        IncrementalResults addResults = ( (InternalKieBuilder) kieBuilder ).createFileSet( "src/main/resources/r2.drl" ).build();

        assertEquals( 1, addResults.getAddedMessages().size() );
        assertEquals( 0, addResults.getRemovedMessages().size() );

        //Update flawed file with correct version - expect 0 "added" error messages and removal of 1 previous error relating to updated file
        kfs.write( "src/main/resources/r2.drl", drl2_2 );
        IncrementalResults removeResults = ( (InternalKieBuilder) kieBuilder ).createFileSet( "src/main/resources/r2.drl" ).build();

        assertEquals( 0, removeResults.getAddedMessages().size() );
        assertEquals( 1, removeResults.getRemovedMessages().size() );
    }

    @Test
    public void testIncrementalCompilationWithDuplicatedRule() throws Exception {
        String drl1 = "package org.drools.compiler\n" +
                "rule R1 when\n" +
                "   $m : Message()\n" +
                "then\n" +
                "end\n";

        String drl2 = "package org.drools.compiler\n" +
                "rule R2 when\n" +
                "   $m : Message( message == \"Hello World\" )\n" +
                "then\n" +
                "end\n";

        KieServices ks = KieServices.Factory.get();

        KieFileSystem kfs = ks.newKieFileSystem()
                .write( "src/main/resources/r1.drl", drl1 );

        KieBuilder kieBuilder = ks.newKieBuilder( kfs ).buildAll();
        assertEquals( 0, kieBuilder.getResults().getMessages( org.kie.api.builder.Message.Level.ERROR ).size() );

        kfs.write( "src/main/resources/r2_1.drl", drl2 );
        IncrementalResults addResults = ( (InternalKieBuilder) kieBuilder ).createFileSet( "src/main/resources/r2_1.drl" ).build();

        assertEquals( 0, addResults.getAddedMessages().size() );
        assertEquals( 0, addResults.getRemovedMessages().size() );

        kfs.write( "src/main/resources/r2_2.drl", drl2 );
        IncrementalResults removeResults = ( (InternalKieBuilder) kieBuilder ).createFileSet( "src/main/resources/r2_2.drl" ).build();

        assertEquals( 1, removeResults.getAddedMessages().size() );
        assertEquals( 0, removeResults.getRemovedMessages().size() );
    }

    @Test
    public void testIncrementalCompilationWithDuplicatedRuleInSameDRL() throws Exception {
        String drl1 = "package org.drools.compiler\n" +
                "rule R1 when\n" +
                "   $m : Message()\n" +
                "then\n" +
                "end\n" +

                "rule R1 when\n" +
                "   $m : Message( message == \"Hello World\" )\n" +
                "then\n" +
                "end\n";

        KieServices ks = KieServices.Factory.get();

        KieFileSystem kfs = ks.newKieFileSystem()
                .write( "src/main/resources/r1.drl", drl1 );

        KieBuilder kieBuilder = ks.newKieBuilder( kfs ).buildAll();
        assertFalse( kieBuilder.getResults().getMessages( org.kie.api.builder.Message.Level.ERROR ).isEmpty() );
    }

    @Test
    public void testIncrementalCompilationAddErrorBuildAllMessages() throws Exception {
        //Valid
        String drl1 = "package org.drools.compiler\n" +
                "rule R1 when\n" +
                "   $m : Message()\n" +
                "then\n" +
                "end\n";

        //Field is unknown ("nonExistentField" not "message")
        String drl2_1 = "package org.drools.compiler\n" +
                "rule R2_1 when\n" +
                "   $m : Message( nonExistentField == \"Hello World\" )\n" +
                "then\n" +
                "end\n";

        KieServices ks = KieServices.Factory.get();

        KieFileSystem kfs = ks.newKieFileSystem()
                .write( "src/main/resources/r1.drl", drl1 );

        KieBuilder kieBuilder = ks.newKieBuilder( kfs ).buildAll();
        assertEquals( 0, kieBuilder.getResults().getMessages( org.kie.api.builder.Message.Level.ERROR ).size() );

        //Add file with error - expect 1 "added" error message
        kfs.write( "src/main/resources/r2.drl", drl2_1 );
        IncrementalResults addResults = ( (InternalKieBuilder) kieBuilder ).createFileSet( "src/main/resources/r2.drl" ).build();

        assertEquals( 1, addResults.getAddedMessages().size() );
        assertEquals( 0, addResults.getRemovedMessages().size() );

        //Check errors on a full build
        assertEquals( 1, ks.newKieBuilder( kfs ).buildAll().getResults().getMessages().size() );
    }

    @Test
    public void testIncrementalCompilationAddErrorThenEmptyWithoutError() throws Exception {
        // BZ-1009369

        //Invalid. Type "Smurf" is unknown
        String drl1 = "Smurf";

        //Valid
        String drl2 = "package org.drools.compiler\n" +
                "rule R2_2 when\n" +
                "   $m : Message( message == \"Hello World\" )\n" +
                "then\n" +
                "end\n";

        KieServices ks = KieServices.Factory.get();

        //Add file with error - expect 2 build messages
        KieFileSystem kfs = ks.newKieFileSystem()
                .write( "src/main/resources/r1.drl", drl1 );

        KieBuilder kieBuilder = ks.newKieBuilder( kfs ).buildAll();
        assertEquals( 2, kieBuilder.getResults().getMessages( org.kie.api.builder.Message.Level.ERROR ).size() );

        //Add empty file - expect no "added" messages and no "removed" messages
        kfs.write( "src/main/resources/r2.drl",
                   "" );
        IncrementalResults addResults1 = ( (InternalKieBuilder) kieBuilder ).createFileSet( "src/main/resources/r2.drl" ).build();
        assertEquals( 0, addResults1.getAddedMessages().size() );
        assertEquals( 0, addResults1.getRemovedMessages().size() );

        //Update file with no errors - expect no "added" messages and no "removed" messages
        kfs.write( "src/main/resources/r2.drl",
                   drl2 );
        IncrementalResults addResults2 = ( (InternalKieBuilder) kieBuilder ).createFileSet( "src/main/resources/r2.drl" ).build();
        assertEquals( 0, addResults2.getAddedMessages().size() );
        assertEquals( 0, addResults2.getRemovedMessages().size() );
    }

    @Test
    public void testRuleRemoval() throws Exception {
        String drl1 = "package org.drools.compiler\n" +
                "rule R1 when\n" +
                "   $m : Message()\n" +
                "then\n" +
                "end\n";

        String drl2 = "rule R2 when\n" +
                "   $m : Message( message == \"Hi Universe\" )\n" +
                "then\n" +
                "end\n";

        String drl3 = "rule R3 when\n" +
                "   $m : Message( message == \"Hello World\" )\n" +
                "then\n" +
                "end\n";

        KieServices ks = KieServices.Factory.get();

        // Create an in-memory jar for version 1.0.0
        ReleaseId releaseId1 = ks.newReleaseId( "org.kie", "test-upgrade", "1.0.0" );
        KieModule km = createAndDeployJar( ks, releaseId1, drl1 + drl2 + drl3 );

        // Create a session and fire rules
        KieContainer kc = ks.newKieContainer( km.getReleaseId() );
        KiePackage kpkg = kc.getKieBase().getKiePackage( "org.drools.compiler" );
        assertEquals( 3, kpkg.getRules().size() );
        Map<String, Rule> rules = rulestoMap( kpkg.getRules() );

        assertNotNull( rules.get( "R1" ) );
        assertNotNull( rules.get( "R2" ) );
        assertNotNull( rules.get( "R3" ) );

        RuleTerminalNode rtn1_1 = (RuleTerminalNode) ( (KnowledgeBaseImpl) kc.getKieBase() ).getReteooBuilder().getTerminalNodes( "org.drools.compiler.R1" )[ 0 ];
        RuleTerminalNode rtn2_1 = (RuleTerminalNode) ( (KnowledgeBaseImpl) kc.getKieBase() ).getReteooBuilder().getTerminalNodes( "org.drools.compiler.R2" )[ 0 ];
        RuleTerminalNode rtn3_1 = (RuleTerminalNode) ( (KnowledgeBaseImpl) kc.getKieBase() ).getReteooBuilder().getTerminalNodes( "org.drools.compiler.R3" )[ 0 ];

        // Create a new jar for version 1.1.0
        ReleaseId releaseId2 = ks.newReleaseId( "org.kie", "test-upgrade", "1.1.0" );
        km = createAndDeployJar( ks, releaseId2, drl1 + drl3 );

        // try to update the container to version 1.1.0
        kc.updateToVersion( releaseId2 );

        KnowledgeBaseImpl rb_2 = ( (KnowledgeBaseImpl) kc.getKieBase() );

        RuleTerminalNode rtn1_2 = (RuleTerminalNode) rb_2.getReteooBuilder().getTerminalNodes( "org.drools.compiler.R1" )[ 0 ];
        RuleTerminalNode rtn3_2 = (RuleTerminalNode) rb_2.getReteooBuilder().getTerminalNodes( "org.drools.compiler.R3" )[ 0 ];
        assertNull( rb_2.getReteooBuilder().getTerminalNodes( "org.drools.compiler.R2" ) );

        assertSame( rtn3_1, rtn3_2 );
        assertSame( rtn1_1, rtn1_2 );

        kpkg = kc.getKieBase().getKiePackage( "org.drools.compiler" );
        assertEquals( 2, kpkg.getRules().size() );
        rules = rulestoMap( kpkg.getRules() );

        assertNotNull( rules.get( "R1" ) );
        assertNull( rules.get( "R2" ) );
        assertNotNull( rules.get( "R3" ) );
    }

    @Test
    public void testIncrementalCompilationWithSnapshots() throws Exception {
        // DROOLS-358
        ReleaseId releaseId = KieServices.Factory.get().newReleaseId( "org.test", "test", "1.0.0-SNAPSHOT" );
        testIncrementalCompilation( releaseId, releaseId, false );
    }

    @Test
    public void testIncrementalCompilationWithFixedVersions() throws Exception {
        // DROOLS-358
        ReleaseId releaseId1 = KieServices.Factory.get().newReleaseId( "org.test", "test", "1.0.1" );
        ReleaseId releaseId2 = KieServices.Factory.get().newReleaseId( "org.test", "test", "1.0.2" );
        testIncrementalCompilation( releaseId1, releaseId2, false );
    }

    @Test
    public void testIncrementalCompilationWithDeclaredType() throws Exception {
        // DROOLS-358
        ReleaseId releaseId1 = KieServices.Factory.get().newReleaseId( "org.test", "test", "1.0.1" );
        ReleaseId releaseId2 = KieServices.Factory.get().newReleaseId( "org.test", "test", "1.0.2" );
        testIncrementalCompilation( releaseId1, releaseId2, true );
    }

    private void testIncrementalCompilation( ReleaseId releaseId1,
                                             ReleaseId releaseId2,
                                             boolean useDeclaredType ) {
        String drl1 = "package org.drools.compiler\n" +
                "global java.util.List list\n" +
                "rule R0 when then list.add( \"000\" ); end \n" +
                "" +
                "rule R1 when\n" +
                " $s : String() " +
                "then\n" +
                " list.add( \"a\" + $s );" +
                "end\n";

        String drl2 = useDeclaredType
                ?
                "package org.drools.compiler\n" +
                        "global java.util.List list\n" +
                        "declare StringWrapper\n" +
                        " s : String\n" +
                        "end\n" +
                        "rule RInit when\n" +
                        " $s : String() \n" +
                        "then\n" +
                        " insert( new StringWrapper( $s ) );" +
                        "end\n" +
                        "rule R2 when\n" +
                        " $s : StringWrapper() \n" +
                        "then\n" +
                        " list.add( \"b\" + $s.getS() );" +
                        "end\n"
                :
                "package org.drools.compiler\n" +
                        "global java.util.List list\n" +
                        "rule R2 when\n" +
                        " $s : String() \n" +
                        "then\n" +
                        " list.add( \"b\" + $s );" +
                        "end\n";

        KieServices ks = KieServices.Factory.get();

        createAndDeployJar(ks, releaseId1, drl1);

        KieContainer kc = ks.newKieContainer( releaseId1 );

        KieSession ksession = kc.newKieSession();
        List<String> list = new ArrayList<String>();
        ksession.setGlobal( "list", list );
        ksession.insert( "Foo" );
        ksession.fireAllRules();

        assertEquals( 2, list.size() );
        assertTrue( list.containsAll( asList( "000", "aFoo" ) ) );
        list.clear();

        createAndDeployJar(ks, releaseId2, drl1, drl2);

        Results updateResults = kc.updateToVersion( releaseId2 );
        assertEquals( 0, updateResults.getMessages().size() );

        ksession.insert( "Bar" );
        ksession.fireAllRules();

        assertEquals( 3, list.size() );
        assertTrue( list.containsAll( asList( "bBar", "bFoo", "aBar" ) ) );
    }

    @Test
    public void testIncrementalCompilationWithRedeclares() {
        // DROOLS-363
        String drl1 = "package org.drools.compiler\n" +
                "global java.util.List list\n" +
                "" +
                "declare Fooz id : int end \n" +
                "" +
                "rule R0 when then insert( new Fooz( 1 ) ); end \n" +
                "" +
                "";

        String drl2 = "package org.drools.compiler\n" +
                "global java.util.List list\n" +
                "" +
                "declare Fooz id : int end \n" +
                "" +
                "declare Barz end \n" +
                "" +
                "rule R2 when then insert( new Fooz( 2 ) ); end \n" +
                "" +
                "rule R1 when\n" +
                " $f : Fooz() " +
                "then\n" +
                " list.add( $f.getId() );" +
                " System.out.println( \"Foo in \" + $f + \" >> \" + System.identityHashCode( $f.getClass() ) ); \n" +
                "end\n" +
                "";

        KieServices ks = KieServices.Factory.get();

        ReleaseId releaseId1 = ks.newReleaseId( "org.kie", "test-upgrade", "1.0.0" );
        ReleaseId releaseId2 = ks.newReleaseId( "org.kie", "test-upgrade", "1.1.0" );

        createAndDeployJar(ks, releaseId1, drl1);

        KieContainer kc = ks.newKieContainer( releaseId1 );
        KieSession ksession = kc.newKieSession();
        List<String> list = new ArrayList<String>();
        ksession.setGlobal( "list", list );
        ksession.fireAllRules();

        createAndDeployJar(ks, releaseId2, drl1, drl2);
        Results updateResults = kc.updateToVersion( releaseId2 );
        assertEquals( 0, updateResults.getMessages().size() );

        ksession.fireAllRules();
        assertEquals( 2, list.size() );
    }

    @Test
    public void testIncrementalCompilationWithAmbiguousRedeclares() {
        String drl1 = "package domestic; " +

                "import foreign.*; " +

                "declare foreign.Score " +
                "    id       : String " +
                "end ";

        String drl2 = "\n" +
                "package domestic; " +

                "import foreign.*; " +

                "declare foreign.Score " +
                "    id       : String " +
                "end\n" +

                "declare Score " +
                "    value : double " +
                "end " +

                "";

        KieServices ks = KieServices.Factory.get();
        KieFileSystem kfs = ks.newKieFileSystem();
        ReleaseId id = ks.newReleaseId( "org.test", "foo", "1.0-SNAPSHOT" );

        KieBuilder kieBuilder = ks.newKieBuilder( kfs );

        kfs.generateAndWritePomXML( id );
        kfs.write( ks.getResources()
                           .newReaderResource( new StringReader( drl1 ) )
                           .setResourceType( ResourceType.DRL )
                           .setSourcePath( "drl1.drl" ) );

        kieBuilder.buildAll();

        KieContainer kc = ks.newKieContainer( id );
        KieSession ksession = kc.newKieSession();
        ksession.fireAllRules();

        kfs.write( ks.getResources()
                           .newReaderResource( new StringReader( drl2 ) )
                           .setResourceType( ResourceType.DRL )
                           .setSourcePath( "drl2.drl" ) );

        IncrementalResults results = ( (InternalKieBuilder) kieBuilder ).incrementalBuild();
        System.out.println( results.getAddedMessages() );
        assertEquals( 0, results.getAddedMessages().size() );

        Results updateResults = kc.updateToVersion( id );
        assertEquals( 0, updateResults.getMessages().size() );

    }

    @Test
    public void testIncrementalCompilationWithModuleOverride() {
        String drl1 = "package org.test.compiler; " +
                "global java.util.List list; " +

                "rule A when $s : String() then System.out.println( 'AAA' + $s ); list.add( 'A' + $s ); end " +
                "";

        String drl2 = "package totally.unrelated.pack; " +
                "global java.util.List list; " +

                "rule B when $s : String() then System.out.println( 'BBB' + $s ); list.add( 'B' + $s ); end " +
                "";

        String drl3 = "package totally.unrelated.pack; " +
                "global java.util.List list; " +

                "rule C when $s : String() then System.out.println( 'CCC' + $s ); list.add( 'C' + $s ); end " +
                "";

        KieServices ks = KieServices.Factory.get();
        KieFileSystem kfs = ks.newKieFileSystem();
        ReleaseId id = ks.newReleaseId( "org.test", "foo", "1.0-SNAPSHOT" );

        KieBuilder kieBuilder = ks.newKieBuilder( kfs );
        kfs.generateAndWritePomXML( id );
        kfs.write( ks.getResources()
                           .newReaderResource( new StringReader( drl1 ) )
                           .setResourceType( ResourceType.DRL )
                           .setSourcePath( "drl1.drl" ) );

        kieBuilder.buildAll();

        KieContainer kc = ks.newKieContainer( id );
        KieSession ksession = kc.newKieSession();
        List<String> list = new ArrayList<String>();
        ksession.setGlobal( "list", list );

        ksession.insert( "X" );
        ksession.fireAllRules();
        assertTrue( list.contains( "AX" ) );

        KieFileSystem kfs2 = ks.newKieFileSystem();
        KieBuilder kieBuilder2 = ks.newKieBuilder( kfs2 );
        kfs2.generateAndWritePomXML( id );
        kfs2.write( ks.getResources()
                            .newReaderResource( new StringReader( drl2 ) )
                            .setResourceType( ResourceType.DRL )
                            .setSourcePath( "drla.drl" ) );

        kieBuilder2.buildAll();

        KieContainer kc2 = ks.newKieContainer( id );
        KieSession ksession2 = kc2.newKieSession();
        ksession2.setGlobal( "list", list );

        ksession2.insert( "X" );
        ksession2.fireAllRules();

        kfs2.write( ks.getResources()
                            .newReaderResource( new StringReader( drl3 ) )
                            .setResourceType( ResourceType.DRL )
                            .setSourcePath( "drlb.drl" ) );

        IncrementalResults results = ( (InternalKieBuilder) kieBuilder2 ).incrementalBuild();
        assertEquals( 0, results.getAddedMessages().size() );

        kc2.updateToVersion( id );
        ksession2.fireAllRules();

        assertEquals( Arrays.asList( "AX", "BX", "CX" ), list );

    }

    @Test
    public void testIncrementalCompilationWithMissingKSession() throws Exception {
        //https://bugzilla.redhat.com/show_bug.cgi?id=1066059
        String pom = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<project xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd\" xmlns=\"http://maven.apache.org/POM/4.0.0\"\n" +
                "    xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
                "  <modelVersion>4.0.0</modelVersion>\n" +
                "  <groupId>org.kie</groupId>\n" +
                "  <artifactId>test</artifactId>\n" +
                "  <version>1.0</version>\n" +
                "  <packaging>jar</packaging>\n" +
                "  <name>test</name>\n" +
                "</project>";

        String kmodule = "<kmodule xmlns=\"http://www.drools.org/xsd/kmodule\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
                "<kbase name=\"kbase\" includes=\"nonExistent\"/>\n" +
                "</kmodule>";

        String drl2_1 = "package org.drools.compiler\n" +
                "rule R2_1 when\n" +
                "   $m : Message( message == \"Hi Universe\" )\n" +
                "then\n" +
                "end\n";

        String drl2_2 = "package org.drools.compiler\n" +
                "rule R2_2 when\n" +
                "   $m : Message( nonExistentField == \"Hello World\" )\n" +
                "then\n" +
                "end\n";

        KieServices ks = KieServices.Factory.get();

        KieFileSystem kfs = ks.newKieFileSystem()
                .write( "pom.xml", pom )
                .write( "src/main/resources/META-INF/kmodule.xml", kmodule )
                .write( "src/main/resources/r2.drl", drl2_1 );

        KieBuilder kieBuilder = ks.newKieBuilder( kfs ).buildAll();

        kfs.write( "src/main/resources/r2.drl", drl2_2 );
        IncrementalResults results = ( (InternalKieBuilder) kieBuilder ).createFileSet( "src/main/resources/r2.drl" ).build();

        // since there's a missing include tha kiebase is not built at all
        assertEquals( 0, results.getAddedMessages().size() );
        assertEquals( 0, results.getRemovedMessages().size() );
    }

    @Test
    public void testIncrementalCompilationWithIncludes() throws Exception {
        // DROOLS-462

        String drl1 = "global java.util.List list\n" +
                "rule R1 when\n" +
                " $s : String() " +
                "then\n" +
                " list.add( \"a\" + $s );" +
                "end\n";

        String drl2 = "global java.util.List list\n" +
                "rule R1 when\n" +
                " $s : String() " +
                "then\n" +
                " list.add( \"b\" + $s );" +
                "end\n";

        ReleaseId releaseId = KieServices.Factory.get().newReleaseId( "org.test", "test", "1.0.0-SNAPSHOT" );
        KieServices ks = KieServices.Factory.get();

        KieModuleModel kproj = ks.newKieModuleModel();
        KieBaseModel kieBaseModel1 = kproj.newKieBaseModel( "KBase1" )
                .addPackage( "org.pkg1" );
        kieBaseModel1.newKieSessionModel( "KSession1" );
        KieBaseModel kieBaseModel2 = kproj.newKieBaseModel( "KBase2" )
                .addPackage( "org.pkg2" )
                .addInclude( "KBase1" );
        kieBaseModel2.newKieSessionModel( "KSession2" );

        KieFileSystem kfs = ks.newKieFileSystem()
                .generateAndWritePomXML( releaseId )
                .write( "src/main/resources/KBase1/org/pkg1/r1.drl", drl1 )
                .writeKModuleXML( kproj.toXML() );

        KieBuilder kieBuilder = ks.newKieBuilder( kfs );

        kieBuilder.buildAll();
        assertEquals( 0, kieBuilder.getResults().getMessages().size() );

        KieContainer kc = ks.newKieContainer( releaseId );

        KieSession ksession = kc.newKieSession( "KSession2" );
        List<String> list = new ArrayList<String>();
        ksession.setGlobal( "list", list );
        ksession.insert( "Foo" );
        ksession.fireAllRules();

        assertEquals( 1, list.size() );
        assertEquals( "aFoo", list.get( 0 ) );
        list.clear();

        kfs.delete( "src/main/resources/KBase1/org/pkg1/r1.drl" );
        kfs.write( "src/main/resources/KBase1/org/pkg1/r2.drl", drl2 );

        IncrementalResults results = ( (InternalKieBuilder) kieBuilder ).incrementalBuild();
        assertEquals( 0, results.getAddedMessages().size() );

        Results updateResults = kc.updateToVersion( releaseId );
        assertEquals( 0, updateResults.getMessages().size() );

        ksession.insert( "Bar" );
        ksession.fireAllRules();

        assertEquals( 2, list.size() );
        assertTrue( list.containsAll( asList( "bBar", "bFoo" ) ) );
    }

    @Test
    public void testIncrementalCompilationWithInvalidDRL() throws Exception {
        String drl1 = "Smurf";

        String drl2_1 = "package org.drools.compiler\n" +
                "rule R2\n" +
                "when\n" +
                "   $m : NonExistentClass()\n" +
                "then\n" +
                "end\n";

        String drl2_2 = "package org.drools.compiler\n" +
                "rule R2\n" +
                "when\n" +
                "   $m : Message()\n" +
                "then\n" +
                "end\n";

        KieServices ks = KieServices.Factory.get();
        KieFileSystem kfs = ks.newKieFileSystem();

        //First file contains errors
        kfs.write( "src/main/resources/r1.drl", drl1 );

        KieBuilder kieBuilder = ks.newKieBuilder( kfs ).buildAll();
        Results results1 = kieBuilder.getResults();
        assertEquals( 2,
                      results1.getMessages().size() );

        //Second file also contains errors.. expect some added messages
        kfs.write( "src/main/resources/r2.drl", drl2_1 );
        IncrementalResults results2 = ( (InternalKieBuilder) kieBuilder ).createFileSet( "src/main/resources/r2.drl" ).build();

        assertEquals( 1,
                      results2.getAddedMessages().size() );
        assertEquals( 0,
                      results2.getRemovedMessages().size() );

        //Correct second file... expect original errors relating to the file to be removed
        kfs.write( "src/main/resources/r2.drl", drl2_2 );
        IncrementalResults results3 = ( (InternalKieBuilder) kieBuilder ).createFileSet( "src/main/resources/r2.drl" ).build();

        assertEquals( 0,
                      results3.getAddedMessages().size() );
        assertEquals( 1,
                      results3.getRemovedMessages().size() );

        //Remove first file... expect related errors to be removed
        kfs.delete( "src/main/resources/r1.drl" );
        IncrementalResults results4 = ( (InternalKieBuilder) kieBuilder ).createFileSet( "src/main/resources/r1.drl" ).build();

        assertEquals( 0,
                      results4.getAddedMessages().size() );
        assertEquals( 2,
                      results4.getRemovedMessages().size() );

    }

    @Test
    public void testKJarUpgradeSameSessionAddingGlobal() throws Exception {
        // DROOLS-523
        String drl1 = "package org.drools.compiler\n" +
                      "global java.lang.String foo\n" +
                      "rule R1 when\n" +
                      "   $m : Message()\n" +
                      "then\n" +
                      "end\n";

        String drl2_1 = "package org.drools.compiler\n" +
                        "rule R2_1 when\n" +
                        "   $m : Message( message == \"Hi Universe\" )\n" +
                        "then\n" +
                        "end\n";

        String drl2_2 = "package org.drools.compiler\n" +
                        "global java.lang.String foo\n" +
                        "rule R2_2 when\n" +
                        "   $m : Message( message == foo )\n" +
                        "then\n" +
                        "end\n";

        KieServices ks = KieServices.Factory.get();

        // Create an in-memory jar for version 1.0.0
        ReleaseId releaseId1 = ks.newReleaseId( "org.kie", "test-upgrade", "1.0.0" );
        KieModule km = createAndDeployJar( ks, releaseId1, drl1, drl2_1 );

        // Create a session and fire rules
        KieContainer kc = ks.newKieContainer( km.getReleaseId() );
        KieSession ksession = kc.newKieSession();
        ksession.insert( new Message( "Hello World" ) );
        assertEquals( 1, ksession.fireAllRules() );

        // Create a new jar for version 1.1.0
        ReleaseId releaseId2 = ks.newReleaseId( "org.kie", "test-upgrade", "1.1.0" );
        km = createAndDeployJar( ks, releaseId2, drl1, drl2_2 );

        // try to update the container to version 1.1.0
        kc.updateToVersion( releaseId2 );

        ksession.setGlobal( "foo", "Hello World" );

        // continue working with the session
        ksession.insert( new Message( "Hello World" ) );
        assertEquals( 2, ksession.fireAllRules() );
    }

    public static class FooEvent {
        private long mytime;

        public FooEvent(long mytime) {
            this.mytime = mytime;
        }

        public long getMytime() {
            return mytime;
        }
    }

    @Test
    public void testUpdateWithDeclarationPresent() throws Exception {
        // DROOLS-560
        String header = "package org.drools.compiler\n"
                        + "import org.drools.compiler.integrationtests.incrementalcompilation.IncrementalCompilationTest.FooEvent\n";

        String declaration = "declare FooEvent\n"
                             + " @timestamp( mytime )\n"
                             + " @role( event )\n"
                             + "end\n";

        String rule1 = "rule R1 when\n" +
                       " $e : FooEvent( )\n" +
                       "then\n" +
                       " insert(new Message(\"Hello R1\"));\n" +
                       "end\n";

        String rule2 = "rule R1 when\n" +
                       " $e : FooEvent( )\n" +
                       "then\n" +
                       " insert(new Message(\"Hello R2\"));\n" +
                       "end\n";

        String file1 = header + declaration + rule1;
        String file2 = header + declaration + rule2;

        KieServices ks = KieServices.Factory.get();

        // Create an in-memory jar for version 1.0.0
        ReleaseId releaseId1 = ks.newReleaseId( "org.kie", "test-upgrade", "1.0.0" );
        KieModule km = createAndDeployJar( ks, releaseId1, file1 );

        // Create a session and fire rules
        KieContainer kc = ks.newKieContainer( km.getReleaseId() );
        KieSession ksession = kc.newKieSession();
        ksession.insert( new FooEvent( 0 ) );
        assertEquals( 1, ksession.fireAllRules() );

        // Create a new jar for version 1.1.0
        ReleaseId releaseId2 = ks.newReleaseId( "org.kie", "test-upgrade", "1.1.0" );
        km = createAndDeployJar( ks, releaseId2, file2 );

        // try to update the container to version 1.1.0
        Results results = kc.updateToVersion( releaseId2 );

        assertFalse("Errors detected on updateToVersion: " + results.getMessages(org.kie.api.builder.Message.Level.ERROR), results.hasMessages(org.kie.api.builder.Message.Level.ERROR));

        // continue working with the session
        ksession.insert( new FooEvent( 1 ) );
        assertEquals( 2, ksession.fireAllRules() );
    }

    @Test
    public void testKJarUpgradeWithDSL() throws Exception {
        // DROOLS-718
        String dsl = "[when][]There is a Message=Message()\n" +
                      "[when][]-with message \"{factId}\"=message==\"{factId}\"\n" +
                      "\n" +
                      "[then][]Print \"{message}\"=System.out.println(\"{message}\");\n";

        String drl2_1 = "package org.drools.compiler\n" +
                        "rule \"bla\"\n" +
                        "when\n" +
                        "\tThere is a Message\t   \n" +
                        "\t-with message \"Hi Universe\"\n" +
                        "then\n" +
                        "\tPrint \"Found a Message Hi Universe.\"\n" +
                        "end\n";

        String drl2_2 = "package org.drools.compiler\n" +
                        "rule \"bla\"\n" +
                        "when\n" +
                        "\tThere is a Message\t   \n" +
                        "\t-with message \"Hello World\"\n" +
                        "then\n" +
                        "\tPrint \"Found a Message Hello World.\"\n" +
                        "end\n";

        KieServices ks = KieServices.Factory.get();

        // Create an in-memory jar for version 1.0.0
        ReleaseId releaseId1 = ks.newReleaseId( "org.kie", "test-upgrade", "1.0.0" );
        KieModule km = createAndDeployJarWithDSL( ks, releaseId1, dsl, drl2_1 );

        // Create a session and fire rules
        KieContainer kc = ks.newKieContainer( km.getReleaseId() );
        KieSession ksession = kc.newKieSession();
        ksession.insert( new Message( "Hello World" ) );
        assertEquals( 0, ksession.fireAllRules() );
        ksession.dispose();

        // Create a new jar for version 1.1.0
        ReleaseId releaseId2 = ks.newReleaseId( "org.kie", "test-upgrade", "1.1.0" );
        km = createAndDeployJarWithDSL( ks, releaseId2, dsl, drl2_2 );

        // try to update the container to version 1.1.0
        kc.updateToVersion( releaseId2 );

        // create and use a new session
        ksession = kc.newKieSession();
        ksession.insert( new Message( "Hello World" ) );
        assertEquals( 1, ksession.fireAllRules() );
    }

    public static KieModule createAndDeployJarWithDSL( KieServices ks, ReleaseId releaseId, String... drls ) {
        KieFileSystem kfs = ks.newKieFileSystem();
        kfs.generateAndWritePomXML(releaseId);

        for (int i = 0; i < drls.length; i++) {
            String extension = i == 0 ? "dsl" : "rdslr";
            if (drls[i] != null) {
                kfs.write("src/main/resources/r" + i + "." + extension, drls[i]);
            }
        }
        KieBuilder kb = ks.newKieBuilder(kfs).buildAll();
        if( kb.getResults().hasMessages( org.kie.api.builder.Message.Level.ERROR ) ) {
            for( org.kie.api.builder.Message result : kb.getResults().getMessages() ) {
                System.out.println(result.getText());
            }
            return null;
        }
        InternalKieModule kieModule = (InternalKieModule) ks.getRepository()
                                                            .getKieModule(releaseId);
        byte[] jar = kieModule.getBytes();
        return deployJar( ks, jar );
    }

    @Test
    public void testRemoveRuleAndThenFactInStreamMode() throws Exception {
        // DROOLS-731
        String header = "package org.some.test\n" +
                        "import org.drools.compiler.FactA\n";

        String declaration = "declare FactA\n" +
                             "@role(event)" +
                             "end\n";

        String rule2 = "rule R when\n" +
                       "  $FactA : FactA ($FactA_field2 : field2 == 105742)\n" +
                       "  not FactA($FactA_field2 == 105742)\n" +
                       "then\n" +
                       "end\n";

        String file2 = header + declaration + rule2;

        KieServices ks = KieServices.Factory.get();

        // Create an in-memory jar for version 1.0.0
        ReleaseId releaseId1 = ks.newReleaseId("org.kie", "test-upgrade", "1.0.0");
        KieModule km = createAndDeployJarInStreamMode( ks, releaseId1, file2 );

        // Create a session and fire rules
        KieContainer kc = ks.newKieContainer( km.getReleaseId() );
        KieSession ksession = kc.newKieSession();

        FactA factA = new FactA(105742);
        factA.setField1( "entry:" + 105742 );
        FactHandle fh = ksession.insert( factA );

        // Create a new jar for version 1.1.0
        ReleaseId releaseId2 = ks.newReleaseId( "org.kie", "test-upgrade", "1.1.0" );
        km = createAndDeployJarInStreamMode(ks, releaseId2);

        // try to update the container to version 1.1.0
        kc.updateToVersion( releaseId2 );

        ksession.delete(fh);
    }

    public static KieModule createAndDeployJarInStreamMode(KieServices ks,
                                                           ReleaseId releaseId,
                                                           String... drls) {
        KieFileSystem kfs = ks.newKieFileSystem();
        kfs.generateAndWritePomXML(releaseId);
        KieModuleModel module = ks.newKieModuleModel();

        KieBaseModel defaultBase = module.newKieBaseModel( "kBase1" );
        defaultBase.setEventProcessingMode(EventProcessingOption.STREAM).setDefault( true );
        defaultBase.newKieSessionModel("defaultKSession").setDefault(true);
        kfs.writeKModuleXML( module.toXML() );

        for (int i = 0; i < drls.length; i++) {
            kfs.write("src/main/resources/rules" + i + ".drl", drls[i]);
        }

        KieBuilder kb = ks.newKieBuilder( kfs );
        kb.buildAll();
        if (kb.getResults().hasMessages(org.kie.api.builder.Message.Level.ERROR)) {
            System.out.println(kb.getResults().toString());
        }
        return kb.getKieModule();
    }

    @Test @Ignore("this test takes too long and cannot be emulated with a pseudo clock")
    public void testIncrementalCompilationWithFireUntilHalt() throws Exception {
        // DROOLS-782
        final String drl1 = getCronRule(3) + getCronRule(6);
        final String drl2 = getCronRule( 8 ) + getCronRule(10) + getCronRule(5);

        final KieServices ks = KieServices.Factory.get();

        // Create an in-memory jar for version 1.0.0
        final ReleaseId releaseId1 = ks.newReleaseId( "org.kie", "test-fireUntilHalt", "1.0.0" );
        //KieModule km = createAndDeployJar( ks, releaseId1, testRuleAdd1, testRuleAdd2 );
        KieModule km = createAndDeployJar( ks, releaseId1, drl1 );

        // Create a session and fire rules
        final KieContainer kc = ks.newKieContainer( km.getReleaseId() );
        final KieSession kieSession = kc.newKieSession();

        try {
            new Thread(kieSession::fireUntilHalt).start();

            Thread.sleep( 10000 );

            // Create a new jar for version 1.1.0
            final ReleaseId releaseId2 = ks.newReleaseId( "org.kie", "test-fireUntilHalt", "1.1.0" );
            km = createAndDeployJar( ks, releaseId2, drl2 );

            // try to update the container to version 1.1.0
            final Results results = kc.updateToVersion( releaseId2 );

            assertFalse( "Errors detected on updateToVersion: " + results.getMessages( org.kie.api.builder.Message.Level.ERROR ),
                         results.hasMessages( org.kie.api.builder.Message.Level.ERROR ) );

            Thread.sleep( 10000 );
        } finally {
            kieSession.halt();
        }
    }

    private String getCronRule(int seconds) {
        return "rule R" + seconds + " " +
               "timer (cron: */" + seconds + " * * * * ?) " +
               "when then System.out.println('Hey there, I print every " + seconds + " seconds'); " +
               "end\n";
    }

    @Test
    public void testKJarUpgradeSameSessionRemovingGlobal() throws Exception {
        // DROOLS-752
        String drl1 = "package org.drools.compiler\n" +
                      "global java.lang.String foo\n" +
                      "global java.lang.String bar\n" +
                      "rule R1 when\n" +
                      "   $m : Message()\n" +
                      "then\n" +
                      "end\n";

        String drl2 = "package org.drools.compiler\n" +
                      "global java.lang.String foo\n" +
                      "global java.lang.String baz\n" +
                      "rule R2 when\n" +
                      "   $m : Message( )\n" +
                      "then\n" +
                      "end\n";

        KieServices ks = KieServices.Factory.get();

        ReleaseId releaseId1 = ks.newReleaseId( "org.kie", "test-upgrade", "1.0.0" );
        KieModule km = createAndDeployJar( ks, releaseId1, drl1 );

        KieContainer kc = ks.newKieContainer( km.getReleaseId() );
        KieSession ksession = kc.newKieSession();
        ksession.setGlobal( "foo", "foo" );
        ksession.setGlobal( "bar", "bar" );

        ReleaseId releaseId2 = ks.newReleaseId( "org.kie", "test-upgrade", "1.1.0" );
        km = createAndDeployJar( ks, releaseId2, drl2 );

        kc.updateToVersion( releaseId2 );

        ksession.setGlobal( "baz", "baz" );

        Globals globals = ksession.getGlobals();
        assertEquals( 2, globals.getGlobalKeys().size() );

        assertEquals( "foo", ksession.getGlobal( "foo" ) );
        assertNull( ksession.getGlobal( "bar" ) );
        assertEquals( "baz", ksession.getGlobal( "baz" ) );
    }

    @Test
    public void testUpdateVersionWithKSessionLogger() {
        // DROOLS-790
        String drl1 =
                "import java.util.List\n" +
                "import java.util.ArrayList\n" +
                "\n" +
                "rule \"Test1\"\n" +
                "\n" +
                "when\n" +
                "   $a : Integer()\n" +
                "then\n" +
                "   insert(new ArrayList());\n" +
                "end\n";

        String drl2 = "rule \"Test2\"\n" +
                      "when\n" +
                      "   $b : List()\n" +
                      " then\n" +
                      "   $b.isEmpty();\n" +
                      "end";

        KieServices ks = KieServices.Factory.get();

        ReleaseId releaseId1 = ks.newReleaseId( "org.kie", "test-upgrade", "1.0.0" );
        KieModule km = createAndDeployJar( ks, releaseId1, drl1 );
        KieContainer kc = ks.newKieContainer( km.getReleaseId() );

        StatelessKieSession statelessKieSession = kc.newStatelessKieSession();
        KieRuntimeLogger kieRuntimeLogger = ks.getLoggers().newConsoleLogger( statelessKieSession );

        List<Command> cmds = new ArrayList<Command>();
        cmds.add( CommandFactory.newInsertElements( new ArrayList() ) );
        FireAllRulesCommand fireAllRulesCommand = (FireAllRulesCommand) CommandFactory.newFireAllRules();
        cmds.add( fireAllRulesCommand );
        cmds.add( CommandFactory.newGetObjects( "returnedObjects" ) );
        BatchExecutionCommand batchExecutionCommand = CommandFactory.newBatchExecution( cmds );

        statelessKieSession.execute( batchExecutionCommand );
        kieRuntimeLogger.close();

        ReleaseId releaseId2 = ks.newReleaseId( "org.kie", "test-upgrade", "1.1.0" );
        km = createAndDeployJar( ks, releaseId2, drl1 + drl2 );

        kc.updateToVersion( km.getReleaseId() );
    }

    @Test
    public void testChangeParentRule() {
        String drl1 =
            "global java.util.List list;" +
            "rule B extends A when\n" +
            "    $s : String()\n" +
            "then\n" +
            "    list.add( $s );\n" +
            "end\n" +
            "\n" +
            "rule A when\n" +
            "    $i : Integer( this > 3 )\n" +
            "then\n" +
            "end";

        String drl2 =
            "global java.util.List list;" +
            "rule B extends A when\n" +
            "    $s : String()\n" +
            "then\n" +
            "    list.add( $s );\n" +
            "end\n" +
            "\n" +
            "rule A when\n" +
            "    $i : Integer( this > 2 )\n" +
            "then\n" +
            "end";

        String drl3 =
            "global java.util.List list;" +
            "rule B extends A when\n" +
            "    $s : String()\n" +
            "then\n" +
            "    list.add( $s );\n" +
            "end\n" +
            "\n" +
            "rule A when\n" +
            "    $i : Integer( this > 5 )\n" +
            "then\n" +
            "end";

        KieServices ks = KieServices.Factory.get();

        ReleaseId releaseId1 = ks.newReleaseId( "org.kie", "test-upgrade", "1.0.0" );
        KieModule km = createAndDeployJar( ks, releaseId1, drl1 );

        KieContainer kc = ks.newKieContainer( km.getReleaseId() );
        KieSession ksession = kc.newKieSession();

        List<String> list = new ArrayList<String>();
        ksession.setGlobal( "list", list );

        ksession.insert( 4 );
        ksession.insert( "test" );
        ksession.fireAllRules();
        assertEquals( 1, list.size() );

        list.clear();

        ReleaseId releaseId2 = ks.newReleaseId( "org.kie", "test-upgrade", "1.1.0" );
        createAndDeployJar( ks, releaseId2, drl2 );
        kc.updateToVersion( releaseId2 );

        ksession.fireAllRules();
        assertEquals( 1, list.size() );

        list.clear();

        ReleaseId releaseId3 = ks.newReleaseId( "org.kie", "test-upgrade", "1.2.0" );
        createAndDeployJar( ks, releaseId3, drl3 );
        kc.updateToVersion( releaseId3 );

        ksession.fireAllRules();
        assertEquals( 0, list.size() );
    }

    @Test
    public void testRuleRemovalAfterUpdate() {
        // DROOLS-801
        String drl = "rule Rule1\n" +
                     "  when\n" +
                     "    Integer()\n" +
                     "    String()\n" +
                     "    Long()\n" +
                     "    not (Double())\n" +
                     "  then \n" +
                     "end\n" +
                     "\n" +
                     "rule Rule2\n" +
                     "  when\n" +
                     "    Integer()\n" +
                     "    String()\n" +
                     "  then \n" +
                     "end";

        KieServices ks = KieServices.Factory.get();

        ReleaseId releaseId1 = ks.newReleaseId( "org.kie", "test-upgrade", "1.0.0" );
        KieModule km = createAndDeployJar( ks, releaseId1 );

        KieContainer kc = ks.newKieContainer( km.getReleaseId() );
        KieSession ksession = kc.newKieSession();

        ksession.insert( "test" );

        ReleaseId releaseId2 = ks.newReleaseId( "org.kie", "test-upgrade", "1.1.0" );
        createAndDeployJar( ks, releaseId2, drl );
        kc.updateToVersion( releaseId2 );

        FactHandle handle = ksession.insert( 1 );
        ksession.fireAllRules();

        ksession.update( handle, 1 );
        ksession.fireAllRules();

        ReleaseId releaseId3 = ks.newReleaseId( "org.kie", "test-upgrade", "1.2.0" );
        createAndDeployJar( ks, releaseId3 );
        kc.updateToVersion( releaseId3 );
    }

    @Test
    public void testIncrementalTypeDeclarationOnInterface() {
        // DROOLS-861
        String drl1 =
                "import " + Service.class.getCanonicalName() + "\n" +
                "rule A when\n" +
                "    Service( )\n" +
                "then\n" +
                "end";

        String drl2 =
                "import " + Service.class.getCanonicalName() + "\n" +
                "declare Service @role( event ) end\n" +
                "rule A when\n" +
                "    Service( )\n" +
                "then\n" +
                "end";

        KieServices ks = KieServices.Factory.get();

        ReleaseId releaseId1 = ks.newReleaseId( "org.kie", "test-upgrade", "1.0.0" );
        KieModule km = createAndDeployJar( ks, releaseId1, drl1 );

        KieContainer kc = ks.newKieContainer( releaseId1 );
        KieSession ksession = kc.newKieSession();

        ReleaseId releaseId2 = ks.newReleaseId( "org.kie", "test-upgrade", "1.1.0" );
        createAndDeployJar( ks, releaseId2, drl2 );
        kc.updateToVersion( releaseId2 );
    }

    public static class MyEvent {
        private final int id;

        public MyEvent( int id ) {
            this.id = id;
        }

        public int getId() {
            return id;
        }

        @Override
        public String toString() {
            return "MyEvent: " + id;
        }
    }

    @Test
    public void testChangeWindowTime() {
        // DROOLS-853
        String drl1 =
                "import " + MyEvent.class.getCanonicalName() + "\n" +
                "global java.util.concurrent.atomic.AtomicInteger result\n" +
                "declare MyEvent @expires(5m) @role( event ) end\n" +
                "rule A when\n" +
                "    accumulate( $e : MyEvent() over window:time(10s), $result : count($e) )\n" +
                "then" +
                "    System.out.println(\"Result-1: \" + $result);\n" +
                "    result.set( $result.intValue() );\n" +
                "end";

        String drl2 =
                "import " + MyEvent.class.getCanonicalName() + "\n" +
                "global java.util.concurrent.atomic.AtomicInteger result\n" +
                "declare MyEvent @expires(5m) @role( event ) end\n" +
                "rule A when\n" +
                "    accumulate( $e : MyEvent() over window:time(5s), $result : count($e) )\n" +
                "then" +
                "    System.out.println(\"Result-2: \" + $result);\n" +
                "    result.set( $result.intValue() );\n" +
                "end";

        KieServices ks = KieServices.Factory.get();

        KieModuleModel kproj = ks.newKieModuleModel();
        KieBaseModel kieBaseModel1 = kproj.newKieBaseModel( "KBase1" ).setDefault( true )
                                          .setEventProcessingMode(EventProcessingOption.STREAM);
        KieSessionModel ksession1 = kieBaseModel1.newKieSessionModel("KSession1").setDefault(true)
                                                 .setType(KieSessionModel.KieSessionType.STATEFUL)
                                                 .setClockType( ClockTypeOption.get( ClockType.PSEUDO_CLOCK.getId() ));

        ReleaseId releaseId1 = ks.newReleaseId( "org.kie", "test-upgrade", "1.0.0" );
        deployJar( ks, createKJar( ks, kproj, releaseId1, null, drl1 ) );

        KieContainer kc = ks.newKieContainer( releaseId1 );
        KieSession ksession = kc.newKieSession();

        PseudoClockScheduler clock = ksession.getSessionClock();

        AtomicInteger result = new AtomicInteger( 0 );
        ksession.setGlobal( "result", result );

        ksession.insert( new MyEvent( 1 ) );
        clock.advanceTime(4, TimeUnit.SECONDS);
        ksession.insert( new MyEvent( 2 ) );
        clock.advanceTime(4, TimeUnit.SECONDS);
        ksession.insert( new MyEvent( 3 ) );
        ksession.fireAllRules();
        assertEquals( 3, result.get() );

        // expires 1
        clock.advanceTime(3, TimeUnit.SECONDS);
        ksession.fireAllRules();
        assertEquals( 2, result.get() );

        ReleaseId releaseId2 = ks.newReleaseId( "org.kie", "test-upgrade", "1.1.0" );
        deployJar( ks, createKJar( ks, kproj, releaseId2, null, drl2 ) );
        kc.updateToVersion( releaseId2 );

        // shorter window: 2 is out
        ksession.fireAllRules();
        assertEquals( 1, result.get() );

        ksession.insert( new MyEvent( 4 ) );
        ksession.insert( new MyEvent( 5 ) );
        ksession.fireAllRules();
        assertEquals( 3, result.get() );

        // expires 3
        clock.advanceTime(3, TimeUnit.SECONDS);
        ksession.fireAllRules();
        assertEquals( 2, result.get() );

        // expires 4 & 5
        clock.advanceTime(3, TimeUnit.SECONDS);
        ksession.fireAllRules();
        assertEquals( 0, result.get() );
    }

    @Test
    public void testNonHashablePropertyWithIncrementalCompilation() {
        // DROOLS-870
        String drl1 =
                "rule \"HelloGreetingService\"\n" +
                "    when\n" +
                "        $name : String(this == \"first\")\n" +
                "    then\n" +
                "        System.out.println(String.format(\"Hello %s!\", $name));\n" +
                "end\n" +
                "rule \"CiaoGreetingService\"\n" +
                "    when\n" +
                "        $name : String(this == \"second\")\n" +
                "    then\n" +
                "        System.out.println(String.format(\"Ciao %s!\", $name));\n" +
                "end\n";

        String drl2 =
                "rule \"HelloGreetingService\"\n" +
                "    when\n" +
                "        $name : String(this == \"first\")\n" +
                "    then\n" +
                "        System.out.println(String.format(\"Modified Hello %s!\", $name));\n" +
                "end\n" +
                "rule \"CiaoGreetingService\"\n" +
                "    when\n" +
                "        $name : String(this == \"second\")\n" +
                "    then\n" +
                "        System.out.println(String.format(\"Modified Ciao %s!\", $name));\n" +
                "end\n";

        KieServices ks = KieServices.Factory.get();

        ReleaseId releaseId1 = ks.newReleaseId( "org.kie", "test-upgrade", "1.0.0" );
        KieModule km = createAndDeployJar( ks, releaseId1, drl1 );

        KieContainer kc = ks.newKieContainer( releaseId1 );
        KieSession ksession = kc.newKieSession();

        ReleaseId releaseId2 = ks.newReleaseId( "org.kie", "test-upgrade", "1.1.0" );
        createAndDeployJar( ks, releaseId2, drl2 );
        kc.updateToVersion( releaseId2 );
    }

    @Test
    public void testIncrementalCompilationWithSlidingWindow() {
        // DROOLS-881
        String drl1 =
                "import " + MyEvent.class.getCanonicalName() + "\n" +
                "declare MyEvent @role( event ) end\n" +
                "rule A when\n" +
                "    Number($number : intValue)\n" +
                "              from accumulate( MyEvent($id : id) over window:time(10s), sum($id) )\n" +
                "then\n" +
                "    System.out.println(\"1. SUM : \" + $number);\n" +
                "end\n" +
                "\n" +
                "rule B when\n" +
                "    Number($number : intValue)\n" +
                "              from accumulate( MyEvent($id : id) over window:time(10s), count($id) )\n" +
                "then\n" +
                "    System.out.println(\"1. CNT : \" + $number);\n" +
                "end";

        String drl2 =
                "import " + MyEvent.class.getCanonicalName() + "\n" +
                "declare MyEvent @role( event ) end\n" +
                "rule A when\n" +
                "    Number($number : intValue)\n" +
                "              from accumulate( MyEvent($id : id) over window:time(10s), sum($id) )\n" +
                "then\n" +
                "    System.out.println(\"2. SUM : \" + $number);\n" +
                "end\n" +
                "\n" +
                "rule B when\n" +
                "    Number($number : intValue)\n" +
                "              from accumulate( MyEvent($id : id) over window:time(10s), count($id) )\n" +
                "then\n" +
                "    System.out.println(\"2. CNT : \" + $number);\n" +
                "end";

        KieServices ks = KieServices.Factory.get();

        KieModuleModel kproj = ks.newKieModuleModel();
        KieBaseModel kieBaseModel1 = kproj.newKieBaseModel( "KBase1" ).setDefault( true )
                                          .setEventProcessingMode(EventProcessingOption.STREAM);
        KieSessionModel ksession1 = kieBaseModel1.newKieSessionModel("KSession1").setDefault(true)
                                                 .setType(KieSessionModel.KieSessionType.STATEFUL)
                                                 .setClockType( ClockTypeOption.get( ClockType.PSEUDO_CLOCK.getId() ));

        ReleaseId releaseId1 = ks.newReleaseId( "org.kie", "test-upgrade", "1.0.0" );
        deployJar( ks, createKJar( ks, kproj, releaseId1, null, drl1 ) );
        ReleaseId releaseId2 = ks.newReleaseId( "org.kie", "test-upgrade", "1.1.0" );
        deployJar( ks, createKJar( ks, kproj, releaseId2, null, drl2 ) );

        KieContainer kc = ks.newKieContainer( releaseId1 );
        KieSession ksession = kc.newKieSession();

        PseudoClockScheduler clock = ksession.getSessionClock();

        ksession.insert( new MyEvent( 1 ) );
        ksession.fireAllRules();

        clock.advanceTime(7, TimeUnit.SECONDS);
        kc.updateToVersion( releaseId2 );

        ksession.fireAllRules();

        clock.advanceTime(7, TimeUnit.SECONDS);
        kc.updateToVersion( releaseId1 );

        ksession.fireAllRules();
    }

    @Test
    public void testConcurrentKJarDeployment() throws Exception {
        // DROOLS-923
        int parallelThreads = 10;
        ExecutorService executor = Executors.newFixedThreadPool( parallelThreads );

        CompletionService<Boolean> ecs = new ExecutorCompletionService<Boolean>(executor);
        for (Callable<Boolean> s : Deployer.getDeployer( parallelThreads )) {
            ecs.submit(s);
        }
        for (int i = 0; i < parallelThreads; ++i) {
            assertTrue( ecs.take().get() );
        }
    }

    public static class Deployer implements Callable<Boolean> {

        private static final KieServices ks = KieServices.Factory.get();

        private final int i;

        public Deployer( int i ) {
            this.i = i;
        }

        public Boolean call() throws Exception {
            String drl =
                    "rule R when\n" +
                    "   Integer( this == " + i + " )\n" +
                    "then\n" +
                    "end\n";

            ReleaseId releaseId1 = ks.newReleaseId( "org.kie", "test-" + i, "1.0.0" );
            try {
                for (int i = 0; i < 10; i++) {
                    createAndDeployJar( ks, releaseId1, drl );
                    ks.getRepository().removeKieModule( releaseId1 );
                }
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }

        public static Collection<Callable<Boolean>> getDeployer( int nr ) {
            Collection<Callable<Boolean>> solvers = new ArrayList<Callable<Boolean>>();
            for ( int i = 0; i < nr; ++i ) {
                solvers.add( new Deployer(i) );
            }
            return solvers;
        }
    }

    @Test
    public void testSegmentSplitOnIncrementalCompilation() throws Exception {
        // DROOLS-930
        String drl =
                "import " + Person.class.getCanonicalName() + "\n" +
                "global java.util.List list\n" +
                "rule R1 when\n" +
                "  $s : String()" +
                "  Person( name == $s ) \n" +
                "then\n" +
                "  list.add(\"R1\");\n" +
                "end\n" +
                "rule R2 when\n" +
                "  $s : String()" +
                "  Person( name == $s ) \n" +
                "then\n" +
                "  list.add(\"R2\");\n" +
                "end\n" +
                "rule R3 when\n" +
                "  $s : String()" +
                "  Person( name != $s ) \n" +
                "then\n" +
                "  list.add(\"R3\");\n" +
                "end\n";

        KieServices ks = KieServices.Factory.get();

        ReleaseId releaseId1 = ks.newReleaseId("org.kie", "test-upgrade", "1.1.1");
        KieModule km = createAndDeployJar(ks, releaseId1);

        KieContainer kc = ks.newKieContainer(km.getReleaseId());
        KieSession ksession = kc.newKieSession();

        kc.updateToVersion(releaseId1);

        ksession.insert( new Person( "John", 26 ) );
        ksession.insert( "John" );
        ksession.fireAllRules();

        ReleaseId releaseId2 = ks.newReleaseId("org.kie", "test-upgrade", "1.1.2");
        km = createAndDeployJar(ks, releaseId2, drl);

        kc.updateToVersion(releaseId2);

        List<String> list = new ArrayList<String>();
        ksession.setGlobal( "list", list );
        ksession.fireAllRules();

        assertEquals(2, list.size());
        assertTrue( list.containsAll( asList("R1", "R2") ) );
    }

    @Test
    public void testSegmentMergeOnRuleRemovalWithNotExistingSegment() throws Exception {
        // DROOLS-950
        String drl1 =
                "rule R1 when\n" +
                "    $i : Integer()\n" +
                "    $s : String( length == $i )\n" +
                "then\n" +
                "end\n" +
                "rule R2 when\n" +
                "    $i : Integer()\n" +
                "    $l : Long( this > $i )\n" +
                "then\n" +
                "end\n";

        KieServices ks = KieServices.Factory.get();

        ReleaseId releaseId1 = ks.newReleaseId("org.kie", "test-upgrade", "1.1.1");
        KieModule km = createAndDeployJar(ks, releaseId1, drl1);

        KieContainer kc = ks.newKieContainer(km.getReleaseId());
        KieSession ksession = kc.newKieSession();

        kc.updateToVersion(releaseId1);

        ksession.insert( "Test" );
        ksession.insert( 4L );
        ksession.fireAllRules();

        ReleaseId releaseId2 = ks.newReleaseId("org.kie", "test-upgrade", "1.1.2");
        km = createAndDeployJar(ks, releaseId2);

        kc.updateToVersion(releaseId2);
    }

    @Test
    public void testRemoveRuleWithRia() throws Exception {
        // DROOLS-954
        String drl1 =
                "import " + List.class.getCanonicalName() + "\n" +
                "rule R when\n" +
                "    $list : List()\n" +
                "    exists Integer( this == 1 ) from $list\n" +
                "    exists Integer( this == 2 ) from $list\n" +
                "then \n" +
                "end\n";

        KieServices ks = KieServices.Factory.get();

        ReleaseId releaseId1 = ks.newReleaseId("org.kie", "test-upgrade", "1.1.1");
        KieModule km = createAndDeployJar(ks, releaseId1, drl1);

        KieContainer kc = ks.newKieContainer(km.getReleaseId());
        KieSession ksession = kc.newKieSession();

        ReleaseId releaseId2 = ks.newReleaseId("org.kie", "test-upgrade", "1.1.2");
        km = createAndDeployJar(ks, releaseId2);

        kc.updateToVersion(releaseId2);

        Rete rete = ( (InternalKnowledgeBase) (InternalKnowledgeBase) ksession.getKieBase() ).getRete();
        EntryPointNode entryPointNode = rete.getEntryPointNodes().values().iterator().next();
        for (ObjectTypeNode otns : entryPointNode.getObjectTypeNodes().values()) {
            assertEquals( 0, otns.getObjectSinkPropagator().getSinks().length );
        }
    }

    @Test
    public void testRetractLogicalAssertedObjectOnRuleRemoval() throws Exception {
        // DROOLS-951
        String drl1 =
                "rule R1 when\n" +
                "    exists( Integer() )\n" +
                "then\n" +
                "    insertLogical( \"found1\" );" +
                "end\n";
        String drl2 = "package org.drools.compiler\n" +
                "rule R2 when\n" +
                "    exists( Integer() )\n" +
                "then\n" +
                "    insertLogical( \"found2\" );" +
                "end\n";
        String drl3 = "package org.drools.compiler\n" +
                "rule R3 when\n" +
                "    exists( Integer() )\n" +
                "then\n" +
                "    insertLogical( \"found3\");" +
                "end\n";

        KieServices ks = KieServices.Factory.get();

        ReleaseId releaseId1 = ks.newReleaseId("org.kie", "test-upgrade", "1.1.1");
        KieModule km = createAndDeployJar(ks, releaseId1, drl1, drl2, drl3);

        KieContainer kc = ks.newKieContainer(km.getReleaseId());
        KieSession ksession = kc.newKieSession();

        ksession.insert( 4 );
        ksession.fireAllRules();
        assertEquals( 3, ksession.getObjects( new ClassObjectFilter( String.class ) ).size() );

        ReleaseId releaseId2 = ks.newReleaseId("org.kie", "test-upgrade", "1.1.2");
        km = createAndDeployJar(ks, releaseId2, drl1, drl2);
        kc.updateToVersion(releaseId2);
        ksession.fireAllRules();
        assertEquals( 2, ksession.getObjects( new ClassObjectFilter( String.class ) ).size() );

        ReleaseId releaseId3 = ks.newReleaseId("org.kie", "test-upgrade", "1.1.3");
        km = createAndDeployJar(ks, releaseId3, drl1);
        kc.updateToVersion(releaseId3);
        ksession.fireAllRules();
        assertEquals( 1, ksession.getObjects( new ClassObjectFilter( String.class ) ).size() );

        ReleaseId releaseId4 = ks.newReleaseId("org.kie", "test-upgrade", "1.1.4");
        km = createAndDeployJar(ks, releaseId4);
        kc.updateToVersion(releaseId4);
        ksession.fireAllRules();
        assertEquals( 0, ksession.getObjects( new ClassObjectFilter( String.class ) ).size() );
    }

    @Test
    public void testRetractLogicalAssertedObjectOnRuleRemovalWithSameObject() throws Exception {
        // DROOLS-951
        String drl1 =
                "rule R1 when\n" +
                "    exists( Integer() )\n" +
                "then\n" +
                "    insertLogical( \"found\" );" +
                "end\n";
        String drl2 = "package org.drools.compiler\n" +
                "rule R2 when\n" +
                "    exists( Integer() )\n" +
                "then\n" +
                "    insertLogical( \"found\" );" +
                "end\n";
        String drl3 = "package org.drools.compiler\n" +
                "rule R3 when\n" +
                "    exists( Integer() )\n" +
                "then\n" +
                "    insertLogical( \"found\");" +
                "end\n";

        KieServices ks = KieServices.Factory.get();

        ReleaseId releaseId1 = ks.newReleaseId("org.kie", "test-upgrade", "1.1.1");
        KieModule km = createAndDeployJar(ks, releaseId1, drl1, drl2, drl3);

        KieContainer kc = ks.newKieContainer(km.getReleaseId());
        KieSession ksession = kc.newKieSession();

        ksession.insert( 4 );
        ksession.fireAllRules();
        assertEquals( 1, ksession.getObjects( new ClassObjectFilter( String.class ) ).size() );

        ReleaseId releaseId2 = ks.newReleaseId("org.kie", "test-upgrade", "1.1.2");
        km = createAndDeployJar(ks, releaseId2, drl1, drl2);
        kc.updateToVersion(releaseId2);
        ksession.fireAllRules();
        assertEquals( 1, ksession.getObjects( new ClassObjectFilter( String.class ) ).size() );

        ReleaseId releaseId3 = ks.newReleaseId("org.kie", "test-upgrade", "1.1.3");
        km = createAndDeployJar(ks, releaseId3, drl1);
        kc.updateToVersion(releaseId3);
        ksession.fireAllRules();
        assertEquals( 1, ksession.getObjects( new ClassObjectFilter( String.class ) ).size() );

        ReleaseId releaseId4 = ks.newReleaseId("org.kie", "test-upgrade", "1.1.4");
        km = createAndDeployJar(ks, releaseId4);
        kc.updateToVersion(releaseId4);
        ksession.fireAllRules();
        assertEquals( 0, ksession.getObjects( new ClassObjectFilter( String.class ) ).size() );
    }

    @Test
    public void testDrlRenamingWithEvents() throws Exception {
        // DROOLS-965
        String drl1 =
                "import " + SimpleEvent.class.getCanonicalName() + ";\n" +
                "\n" +
                "global java.util.concurrent.atomic.AtomicInteger counter1;\n" +
                "global java.util.concurrent.atomic.AtomicInteger counter2;\n" +
                "\n" +
                "declare SimpleEvent\n" +
                "    @role( event )\n" +
                "    @timestamp( timestamp )\n" +
                "    @expires( 2d )\n" +
                "end\n" +
                "\n" +
                "rule R1 when\n" +
                "    $s:SimpleEvent(code==\"MY_CODE\")\n" +
                "then\n" +
                "    counter1.incrementAndGet();\n" +
                "end\n" +
                "\n" +
                "rule R2 when\n" +
                "    $s:SimpleEvent(code==\"MY_CODE\")\n" +
                "    not SimpleEvent(this != $s, this after [0,10s] $s)\n" +
                "then\n" +
                "    counter2.incrementAndGet();\n" +
                "end\n";

        KieServices ks = KieServices.Factory.get();

        KieModuleModel kproj = ks.newKieModuleModel();
        KieBaseModel kieBaseModel1 = kproj.newKieBaseModel( "KBase1" ).setDefault( true )
                                          .setEventProcessingMode(EventProcessingOption.STREAM);
        KieSessionModel ksession1 = kieBaseModel1.newKieSessionModel("KSession1").setDefault(true)
                                                 .setType(KieSessionModel.KieSessionType.STATEFUL)
                                                 .setClockType( ClockTypeOption.get( ClockType.PSEUDO_CLOCK.getId() ) );

        ReleaseId releaseId1 = ks.newReleaseId( "org.kie", "test-upgrade", "1.1.1" );
        KieModule km = deployJar( ks, createKJar( ks, kproj, releaseId1, null, drl1 ) );

        KieContainer kc = ks.newKieContainer(km.getReleaseId());
        KieSession ksession = kc.newKieSession();
        PseudoClockScheduler clock = ksession.getSessionClock();

        AtomicInteger counter1 = new AtomicInteger( 0 );
        AtomicInteger counter2 = new AtomicInteger( 0 );
        ksession.setGlobal( "counter1", counter1 );
        ksession.setGlobal( "counter2", counter2 );

        ksession.insert( new SimpleEvent("1", "MY_CODE", 0) );
        ksession.fireAllRules();
        clock.advanceTime(5, TimeUnit.SECONDS);
        ksession.insert( new SimpleEvent("2", "MY_CODE", 5) );
        ksession.fireAllRules();

        assertEquals( 2, counter1.get() );
        assertEquals( 0, counter2.get() );

        ReleaseId releaseId2 = ks.newReleaseId("org.kie", "test-upgrade", "1.1.2");
        // the null drl placeholder is used to have the same drl with a different file name
        // this causes the removal and readdition of both rules
        km = deployJar( ks, createKJar( ks, kproj, releaseId2, null, (String)null, drl1 ) );
        kc.updateToVersion(releaseId2);

        clock = ksession.getSessionClock();
        clock.advanceTime(16, TimeUnit.SECONDS);
        ksession.insert( new SimpleEvent("3", "MY_CODE", 21) );
        ksession.fireAllRules();

        assertEquals( 5, counter1.get() );
        assertEquals( 1, counter2.get() );
    }

    public static class SimpleEvent {
        private final String id;
        private final String code;
        private final long timestamp;

        public SimpleEvent(String eventId, String code, long timestamp) {
            this.id = eventId;
            this.code = code;
            this.timestamp = timestamp * 1000L;
        }

        public String getId() {
            return id;
        }

        public String getCode() {
            return code;
        }

        public Date getTimestamp() {
            return new Date(timestamp);
        }

        @Override
        public String toString() {
            return "SimpleEvent(" + id + ")";
        }
    }

    @Test
    public void testUpdateWithNewDrlAndChangeInOldOne() throws Exception {
        // BZ-1275378
        String drl1 = "package org.kie.test\n" +
                      "global java.util.List list\n" +
                      "rule rule1\n" +
                      "when\n" +
                      "then\n" +
                      "list.add( drools.getRule().getName() );\n" +
                      "end\n";

        KieServices ks = KieServices.Factory.get();

        ReleaseId releaseId1 = ks.newReleaseId( "org.kie", "test-upgrade", "1.1.1" );
        KieModule km = createAndDeployJar( ks, releaseId1, drl1 );

        KieContainer kc = ks.newKieContainer( km.getReleaseId() );
        KieSession ksession = kc.newKieSession();

        List<String> list = new ArrayList<String>();
        ksession.setGlobal( "list", list );
        ksession.fireAllRules();

        assertEquals( 1, list.size() );
        assertTrue( list.contains( "rule1" ) );

        drl1 = "package org.kie.test\n" +
               "global java.util.List list\n" +
               "rule rule1\n" +
               "when\n" +
               "Object()\n" +
               "then\n" +
               "list.add( drools.getRule().getName() );\n" +
               "end\n";

        String drl2 = "package org.kie.test\n" +
                      "global java.util.List list\n" +
                      "rule rule2\n" +
                      "when\n" +
                      "then\n" +
                      "list.add( drools.getRule().getName() );\n" +
                      "end\n";

        ReleaseId releaseId2 = ks.newReleaseId("org.kie", "test-upgrade", "1.1.2");
        km = createAndDeployJar( ks, releaseId2, drl1, drl2 );
        kc.updateToVersion(releaseId2);

        list = new ArrayList<String>();
        ksession.setGlobal( "list", list );
        ksession.fireAllRules();

        assertEquals( 1, list.size() );
        assertTrue( list.contains( "rule2" ) );
    }

    @Test
    public void testIncrementalCompilationWithEagerRules() throws Exception {
        // DROOLS-978
        String drl1 =
                "rule R1 when\n" +
                "then\n" +
                "  insert(\"test\");\n" +
                "end\n" +
                "\n" +
                "rule R2\n" +
                "no-loop true\n" +
                "when\n" +
                "then\n" +
                "  insert(1);\n" +
                "end\n";

        String drl2 =
                "rule R3\n" +
                "no-loop true\n" +
                "when\n" +
                "then\n" +
                "  insert(1);\n" +
                "end\n";

        KieServices ks = KieServices.Factory.get();

        ReleaseId releaseId1 = ks.newReleaseId( "org.kie", "test-upgrade", "1.1.1" );
        KieModule km = createAndDeployJar( ks, releaseId1, drl1 );

        KieContainer kc = ks.newKieContainer(km.getReleaseId());
        KieSession ksession = kc.newKieSession();

        ksession.fireAllRules();

        ReleaseId releaseId2 = ks.newReleaseId("org.kie", "test-upgrade", "1.1.2");
        km = createAndDeployJar( ks, releaseId2, drl2 );
        kc.updateToVersion(releaseId2);

        ksession.fireAllRules();
    }

    @Test
    public void testMultipleIncrementalCompilationWithExistentialRules() {
        // DROOLS-988
        List<String> drls = new ArrayList<String>();
        drls.add(getExistenzialRule("R0", "> 10"));

        // Create a session with the above rule
        KieServices ks = KieServices.Factory.get();
        ReleaseId releaseId = ks.newReleaseId("org.kie", "test-upgrade", "1.0.0");
        KieModule km = createAndDeployJar(ks, releaseId, drls.toArray(new String[drls.size()]));
        KieContainer kc = ks.newKieContainer(km.getReleaseId());
        KieSession ksession = kc.newKieSession();

        AtomicInteger counter = new AtomicInteger( 0 );
        ksession.setGlobal( "counter", counter );

        ksession.insert(3);
        ksession.fireAllRules();
        assertEquals(0, counter.get());

        for (int i = 1; i < 11; i++) {
            ReleaseId newReleaseId = ks.newReleaseId("org.kie", "test-upgrade", "1.1." + i);
            drls.add(getExistenzialRule("R" + i, "< 10"));
            km = createAndDeployJar(ks, newReleaseId, drls.toArray(new String[drls.size()]));
            kc.updateToVersion(newReleaseId);
            ksession.fireAllRules();
            assertEquals(i, counter.get());
        }
    }

    private String getExistenzialRule(String rulename, String condition) {
        return "global java.util.concurrent.atomic.AtomicInteger counter\n" +
               "rule " + rulename + " when\n" +
               "  exists (Integer( this " + condition + ") )\n" +
               "then\n" +
               "  counter.incrementAndGet();\n" +
               "end";
    }

    @Test
    public void testRuleRemovalWithOR() throws Exception {
        // DROOLS-1007
        String drl1 =
                "rule R1 when\n" +
                "    $s : String()\n" +
                "	 (Integer(this == $s.length) or Long(this == $s.length))\n" +
                "then\n" +
                "end\n";

        KieServices ks = KieServices.Factory.get();

        ReleaseId releaseId1 = ks.newReleaseId( "org.kie", "test-upgrade", "1.1.1" );
        KieModule km = createAndDeployJar( ks, releaseId1, drl1 );

        KieContainer kc = ks.newKieContainer(km.getReleaseId());
        KieSession ksession = kc.newKieSession();

        ksession.fireAllRules();

        ReleaseId releaseId2 = ks.newReleaseId("org.kie", "test-upgrade", "1.1.2");
        km = createAndDeployJar( ks, releaseId2 );

        try {
            kc.updateToVersion( releaseId2 );
        } catch (Exception e) {
            fail( "Incremental update should succeed, but failed with " + e.getLocalizedMessage() );
        }

        ksession.fireAllRules();
    }

    @Test
    public void testSplitAfterQuery() throws Exception {
        String drl1 =
                "global java.util.List list; " +
                "query foo( Integer $i ) " +
                "   $i := Integer( this < 10 ) " +
                "end\n" +
                "\n" +

                "rule r2 when " +
                "   foo( $i; ) " +
                "   Integer( this == 20 ) " +
                "then " +
                "   System.out.println(\"20 \" + $i);" +
                "   list.add( 20 + $i );\n" +
                "end\n" +

                "rule r3 when " +
                "   $i : Integer( this == 1 ) " +
                "then " +
                "   System.out.println($i);" +
                "   update( kcontext.getKieRuntime().getFactHandle( $i ), $i + 1 );" +
                "end\n" +
                "\n";

        String drl2 =
                "global java.util.List list; " +
                "query foo( Integer $i ) " +
                "   $i := Integer( this < 10 ) " +
                "end\n" +
                "\n" +

                "rule r1 when " +
                "   foo( $i ; ) " +
                "   Integer( this == 10 ) " +
                "then " +
                "   System.out.println(\"10 \" + $i);" +
                "   list.add( 10 + $i );\n" +
                "end\n" +

                "rule r2 when " +
                "   foo( $i; ) " +
                "   Integer( this == 20 ) " +
                "then " +
                "   System.out.println(\"20 \" + $i);" +
                "   list.add( 20 + $i );\n" +
                "end\n" +

                "rule r3 when " +
                "   $i : Integer( this == 1 ) " +
                "then " +
                "   System.out.println($i);" +
                "   update( kcontext.getKieRuntime().getFactHandle( $i ), $i + 1 );" +
                "end\n" +
                "\n";

        KieServices ks = KieServices.Factory.get();

        ReleaseId releaseId1 = ks.newReleaseId( "org.kie", "test-upgrade", "1.1.1" );
        KieModule km = createAndDeployJar( ks, releaseId1, drl1 );

        KieContainer kc = ks.newKieContainer(km.getReleaseId());
        KieSession ksession = kc.newKieSession();

        List<Integer> list = new ArrayList<Integer>();
        ksession.setGlobal( "list", list );

        ksession.insert( 1 );
        ksession.insert( 20 );

        ksession.fireAllRules();

        ReleaseId releaseId2 = ks.newReleaseId("org.kie", "test-upgrade", "1.1.2");
        km = createAndDeployJar( ks, releaseId2, drl2 );
        kc.updateToVersion(releaseId2);

        ksession.fireAllRules();

        assertEquals( 2, list.size() );
        assertEquals( 21, (int)list.get(0) );
        assertEquals( 22, (int)list.get(1) );
    }

    @Test
    public void testGetFactTypeOnIncrementalUpdate() throws Exception {
        // DROOLS-980 - DROOLS-2195
        String drl1 =
                "package org.mytest\n" +
                "declare Person\n" +
                "   name : String\n" +
                "   age : Integer\n" +
                "end\n" +
                "\n" +
                "rule R when Person(age > 30) then end\n";

        String drl2 =
                "package org.mytest\n" +
                "declare Person\n" +
                "   name : String\n" +
                "   age : Integer\n" +
                "   address : String\n" +
                "end\n" +
                "\n" +
                "rule R when Person(age > 40) then end\n";

        KieServices ks = KieServices.Factory.get();

        ReleaseId releaseId1 = ks.newReleaseId( "org.kie", "test-upgrade", "1.1.1" );
        KieModule km = createAndDeployJar( ks, releaseId1, drl1 );

        KieContainer kc = ks.newKieContainer(km.getReleaseId());
        KieBase kbase = kc.getKieBase();

        FactType ftype = kbase.getFactType("org.mytest", "Person");
        assertNotNull( ftype.getField( "name" ) );
        assertNotNull( ftype.getField( "age" ) );

        Object fact = ftype.newInstance();
        ftype.set(fact, "name", "me");
        ftype.set(fact, "age", 42);

        ReleaseId releaseId2 = ks.newReleaseId("org.kie", "test-upgrade", "1.1.2");
        km = createAndDeployJar( ks, releaseId2, drl2 );
        kc.updateToVersion(releaseId2);

        ftype = kbase.getFactType("org.mytest", "Person");
        assertNotNull( ftype.getField( "name" ) );
        assertNotNull( ftype.getField( "age" ) );
        assertNotNull( ftype.getField( "address" ) );

        fact = ftype.newInstance();
        ftype.set(fact, "name", "me again");
        ftype.set(fact, "age", 43);
        ftype.set(fact, "address", "World");
    }

    @Test
    public void testRuleRemovalWithSubnetworkAndOR() throws Exception {
        // DROOLS-1025
        String drl1 =
                "global java.util.concurrent.atomic.AtomicInteger globalInt\n" +
                "rule R1 when\n" +
                "    $s : String()\n" +
                "	 (or exists Integer(this == 1)\n" +
                "	     exists Integer(this == 2) )\n" +
                "	 exists Integer() from globalInt.get()\n" +
                "then\n" +
                "end\n";

        KieServices ks = KieServices.Factory.get();

        ReleaseId releaseId1 = ks.newReleaseId( "org.kie", "test-upgrade", "1.1.1" );
        KieModule km = createAndDeployJar( ks, releaseId1, drl1 );

        KieContainer kc = ks.newKieContainer(km.getReleaseId());
        KieSession ksession = kc.newKieSession();

        ksession.setGlobal( "globalInt", new AtomicInteger(0) );
        ksession.insert( 1 );
        ksession.insert( "1" );

        ksession.fireAllRules();

        ReleaseId releaseId2 = ks.newReleaseId("org.kie", "test-upgrade", "1.1.2");
        km = createAndDeployJar( ks, releaseId2 );

        try {
            kc.updateToVersion( releaseId2 );
        } catch (Exception e) {
            e.printStackTrace();
            fail( "Incremental update should succeed, but failed with " + e.getLocalizedMessage() );
        }

        ksession.fireAllRules();
    }

    @Test
    public void testIncrementalCompilationWithClassFieldReader() {
        // BZ-1318532
        String personSrc = "package org.test;" +
                           "import java.util.ArrayList;" +
                           "import java.util.List;" +
                           "public class Person {" +
                           "    private String name;" +
                           "    private List<String> addresses = new ArrayList<String>();" +
                           "    public Person() {}" +
                           "    public Person(final String name) { this.name = name; }" +
                           "    public List<String> getAddresses() { return addresses; }" +
                           "    public void setAddresses(List<String> addresses) { this.addresses = addresses; }" +
                           "    public void addAddress(String address) { this.addresses.add(address); }" +
                           "    public String getName() { return this.name; }" +
                           "    public void setName(final String name) { this.name = name; }" +
                           "}";

        String drl1 = "package org.drools.compiler\n" +
                      "" +
                      "import org.test.Person;\n" +
                      "" +
                      "rule R0 salience 100 when\n" +
                      " String()" +
                      "then\n" +
                      " Person person = new Person(\"John\");" +
                      " person.addAddress(\"A street\");" +
                      " insert(person);" +
                      "end\n" +
                      "" +
                      "rule R1 when\n" +
                      " $p : Person($addresses : addresses) " +
                      " $a : Address() from $addresses " +
                      "then\n" +
                      "end\n" +
                      "";

        String drl2 = drl1 + " "; // just a trivial update

        KieServices ks = KieServices.Factory.get();
        KieFileSystem kfs = ks.newKieFileSystem();
        ReleaseId id = ks.newReleaseId( "org.test", "myTest", "1.0.0" );

        KieBuilder kieBuilder = ks.newKieBuilder( kfs );

        kfs.generateAndWritePomXML( id );

        kfs.write("src/main/java/org/test/Person.java",
                  ks.getResources().newReaderResource(new StringReader(personSrc)));

        kfs.write( ks.getResources()
                     .newReaderResource( new StringReader( drl1 ) )
                     .setResourceType( ResourceType.DRL )
                     .setSourcePath( "drl1.drl" ) );

        kieBuilder.buildAll();

        KieContainer kc = ks.newKieContainer( id );
        KieSession ksession = kc.newKieSession();

        kfs.write( ks.getResources()
                     .newReaderResource( new StringReader( drl2 ) )
                     .setResourceType( ResourceType.DRL )
                     .setSourcePath( "drl1.drl" ) ); // update the drl1.drl file

        IncrementalResults results = ( (InternalKieBuilder) kieBuilder ).incrementalBuild();

        // don't call updateToVersion(). This test intends to prove that incrementalBuild doesn't do harm to existing ksession.

        ksession.insert( "start" );
        int fired = ksession.fireAllRules();

    }

    @Test
    public void testIncrementalCompilationRemovingParentRule() throws Exception {
        // DROOLS-1031
        String drl1 = "package org.drools.compiler\n" +
                      "rule R1 when\n" +
                      "   $s : String()\n" +
                      "then\n" +
                      "end\n";

        String drl2 = "package org.drools.compiler\n" +
                      "rule R2 extends R1 when\n" +
                      "   $i : Integer()\n" +
                      "then\n" +
                      "end\n";

        KieServices ks = KieServices.Factory.get();

        KieFileSystem kfs = ks.newKieFileSystem()
                              .write( "src/main/resources/r1.drl", drl1 )
                              .write( "src/main/resources/r2.drl", drl2 );

        KieBuilder kieBuilder = ks.newKieBuilder( kfs ).buildAll();
        KieContainer kieContainer = ks.newKieContainer( ks.getRepository().getDefaultReleaseId() );

        KieSession ksession = kieContainer.newKieSession();
        ksession.insert( "test" );
        ksession.insert( 1 );
        assertEquals( 2, ksession.fireAllRules() );

        kfs.delete( "src/main/resources/r1.drl" );
        IncrementalResults results = ( (InternalKieBuilder) kieBuilder ).createFileSet( "src/main/resources/r1.drl", "src/main/resources/r2.drl" ).build();

        assertEquals( 1, results.getAddedMessages().size() );
        assertEquals( 0, results.getRemovedMessages().size() );

        kieContainer.updateToVersion( ks.getRepository().getDefaultReleaseId() );
        ksession = kieContainer.newKieSession();
        ksession.insert( "test" );
        ksession.insert( 1 );
        assertEquals( 2, ksession.fireAllRules() );
    }

    @Test
    public void testIncrementalCompilationChangeingParentRule() throws Exception {
        // DROOLS-1031
        String drl1_1 =
                      "rule R1 when\n" +
                      "   $s : String( this == \"s1\" )\n" +
                      "then\n" +
                      "end\n";

        String drl1_2 =
                      "rule R1 when\n" +
                      "   $s : String( this == \"s2\" )\n" +
                      "then\n" +
                      "end\n";

        String drl2 =
                      "rule R2 extends R1 when\n" +
                      "   $i : Integer()\n" +
                      "then\n" +
                      "end\n";

        KieServices ks = KieServices.Factory.get();

        ReleaseId releaseId1 = ks.newReleaseId( "org.kie", "test-extends", "1.1.1" );
        KieModule km = createAndDeployJar( ks, releaseId1, drl1_1 + drl2 );

        KieContainer kc = ks.newKieContainer(km.getReleaseId());
        KieSession ksession = kc.newKieSession();

        ksession.insert( 1 );
        ksession.insert( "s2" );
        assertEquals( 0, ksession.fireAllRules() );

        ReleaseId releaseId2 = ks.newReleaseId("org.kie", "test-extends", "1.1.2");
        km = createAndDeployJar( ks, releaseId2, drl1_2 + drl2 );

        kc.updateToVersion( releaseId2 );
        assertEquals( 2, ksession.fireAllRules() );
    }

    @Test
    public void testRemoveRuleWithNonInitializedPath() {
        // DROOLS-1177
        String drl1 =
                "import " + MyEvent.class.getCanonicalName() + "\n" +
                "declare MyEvent @role( event ) end\n" +
                "rule \"RG_TEST_1\"\n" +
                "    when\n" +
                "       $dummy: MyEvent (id == 1)\n" +
                "		$other: MyEvent (this != $dummy)\n" +
                "    then\n" +
                "        retract($other);\n" +
                "end\n" +
                "rule \"RG_TEST_2\"\n" +
                "    when\n" +
                "       $dummy: MyEvent (id == 1)\n" +
                "    then\n" +
                "        retract($dummy);\n" +
                "end\n";

        String drl2 =
                "import " + MyEvent.class.getCanonicalName() + "\n" +
                "declare IMyEvent @role( event ) end\n" +
                "rule \"RG_TEST_2\"\n" +
                "    when\n" +
                "       $dummy: MyEvent (id == 1)\n" +
                "    then\n" +
                "        retract($dummy);\n" +
                "end\n";

        KieServices ks = KieServices.Factory.get();

        KieModuleModel kproj = ks.newKieModuleModel();
        KieBaseModel kieBaseModel1 = kproj.newKieBaseModel( "KBase1" ).setDefault( true )
                                          .setEventProcessingMode(EventProcessingOption.STREAM);
        KieSessionModel ksession1 = kieBaseModel1.newKieSessionModel("KSession1").setDefault(true)
                                                 .setType(KieSessionModel.KieSessionType.STATEFUL);

        ReleaseId releaseId1 = ks.newReleaseId( "org.kie", "test-upgrade", "1.0.0" );
        deployJar( ks, createKJar( ks, kproj, releaseId1, null, drl1 ) );

        KieContainer kc = ks.newKieContainer( releaseId1 );
        KieSession ksession = kc.newKieSession();

        ksession.insert( new MyEvent( 0 ) );

        ReleaseId releaseId2 = ks.newReleaseId( "org.kie", "test-upgrade", "1.1.0" );
        deployJar( ks, createKJar( ks, kproj, releaseId2, null, drl2 ) );
        kc.updateToVersion( releaseId2 );
    }

    @Test
    public void testIncrementalCompilationWithTimerNode() throws Exception {
        // DROOLS-1195
        String drl1 =  "package fr.edf.distribution.brms.common.test\n" +
                       "import " + DummyEvent.class.getCanonicalName() + "\n" +
                       "declare DummyEvent\n" +
                       "    @role( event )\n" +
                       "    @timestamp( eventTimestamp )\n" +
                       "end\n" +
                       "rule \"RG_TEST_TIMER\"\n" +
                       "timer (int: 0 1; start=$expirationTimestamp , repeat-limit=0 )\n" +
                       "    when\n" +
                       "       $dummy: DummyEvent (id == 'timer', $expirationTimestamp : systemTimestamp )\n" +
                       "    then\n " +
                       "System.out.println(\"1\");\n" +
                       "end\n";

        String drl2 =  "package fr.edf.distribution.brms.common.test\n" +
                       "import " + DummyEvent.class.getCanonicalName() + "\n" +
                       "declare DummyEvent\n" +
                       "    @role( event )\n" +
                       "    @timestamp( eventTimestamp )\n" +
                       "end\n" +
                       "rule \"RG_TEST_TIMER_NEW\"\n" +
                       "timer (int: 0 1; start=$expirationTimestamp , repeat-limit=0 )\n" +
                       "    when\n" +
                       "       $dummy: DummyEvent (id == 'timer', $expirationTimestamp : systemTimestamp )\n" +
                       "		DummyEvent (id == 'timer_match')\n" +
                       "    then\n " +
                       "System.out.println(\"1\");\n" +
                       "end\n" +
                       "rule \"RG_OTHER_RULE\"\n" +
                       "    when\n" +
                       "       $dummy: DummyEvent ( id == 'timer' )\n" +
                       "    then\n " +
                       "System.out.println(\"2\");\n" +
                       "end\n";

        long now = System.currentTimeMillis();
        KieServices ks = KieServices.Factory.get();

        KieModuleModel kproj = ks.newKieModuleModel();
        KieBaseModel kieBaseModel1 = kproj.newKieBaseModel( "KBase1" ).setDefault( true )
                                          .setEventProcessingMode(EventProcessingOption.STREAM);
        KieSessionModel ksession1 = kieBaseModel1.newKieSessionModel("KSession1").setDefault(true)
                                                 .setType(KieSessionModel.KieSessionType.STATEFUL)
                                                 .setClockType( ClockTypeOption.get( ClockType.PSEUDO_CLOCK.getId() ) );

        ReleaseId releaseId1 = ks.newReleaseId( "org.kie", "test-upgrade", "1.0.0" );
        deployJar( ks, createKJar( ks, kproj, releaseId1, null, drl1 ) );

        KieContainer kc = ks.newKieContainer(releaseId1);
        KieSession kieSession = kc.newKieSession();

        DummyEvent dummyEvent = new DummyEvent();
        dummyEvent.setId("timer");
        dummyEvent.setEventTimestamp(now);
        dummyEvent.setSystemTimestamp(now + TimeUnit.HOURS.toMillis(1));

        DummyEvent other = new DummyEvent();
        other.setId("timer_match");
        other.setEventTimestamp(now);

        kieSession.insert(dummyEvent);
        kieSession.insert(other);

        ReleaseId releaseId2 = ks.newReleaseId("org.kie", "test-upgrade", "2.0.0");
        deployJar( ks, createKJar( ks, kproj, releaseId2, null, drl2 ) );

        kc.updateToVersion(releaseId2);

        PseudoClockScheduler scheduler = kieSession.getSessionClock();
        scheduler.setStartupTime(now);
        scheduler.advanceTime(1, TimeUnit.DAYS);
        assertEquals( 2, kieSession.fireAllRules() );
    }

    public static class DummyEvent {

        private String id;
        private long eventTimestamp;
        private long systemTimestamp;

        public DummyEvent() {}

        public DummyEvent(String id) {
            this.id = id;
        }

        public long getEventTimestamp() {
            return eventTimestamp;
        }

        public void setEventTimestamp(long eventTimestamp) {
            this.eventTimestamp = eventTimestamp;
        }

        public long getSystemTimestamp() {
            return systemTimestamp;
        }

        public void setSystemTimestamp(long systemTimestamp) {
            this.systemTimestamp = systemTimestamp;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }

    public static class OtherDummyEvent {

        private String id;
        private long eventTimestamp;
        private long systemTimestamp;

        public OtherDummyEvent() {}

        public OtherDummyEvent(String id) {
            this.id = id;
        }

        public long getEventTimestamp() {
            return eventTimestamp;
        }

        public void setEventTimestamp(long eventTimestamp) {
            this.eventTimestamp = eventTimestamp;
        }

        public long getSystemTimestamp() {
            return systemTimestamp;
        }

        public void setSystemTimestamp(long systemTimestamp) {
            this.systemTimestamp = systemTimestamp;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }

    @Test
    public void testEventDeclarationInSeparatedDRL() throws Exception {
        // DROOLS-1241
        String drl1 =
                "import " + SimpleEvent.class.getCanonicalName() + ";\n"+
                "declare SimpleEvent\n" +
                "    @role( event )\n" +
                "    @timestamp( timestamp )\n" +
                "    @expires( 2d )\n" +
                "end\n";

        String drl2 =
                "import " + SimpleEvent.class.getCanonicalName() + ";\n" +
                "global java.util.List list;\n"+
                "rule R1 when\n" +
                "    $s:SimpleEvent(code==\"MY_CODE\") over window:time( 1s )\n" +
                "then\n" +
                "    list.add(\"MY_CODE\");\n" +
                "end\n";

        String drl3 =
                "import " + SimpleEvent.class.getCanonicalName() + ";\n"+
                "global java.util.List list;\n"+
                "rule R2 when\n" +
                "    $s:SimpleEvent(code==\"YOUR_CODE\") over window:time( 1s )\n" +
                "then\n" +
                "    list.add(\"YOUR_CODE\");\n" +
                "end\n";

        KieServices ks = KieServices.Factory.get();

        KieModuleModel kproj = ks.newKieModuleModel();
        KieBaseModel kieBaseModel1 = kproj.newKieBaseModel( "KBase1" ).setDefault( true )
                                          .setEventProcessingMode( EventProcessingOption.STREAM );
        KieSessionModel ksession1 = kieBaseModel1.newKieSessionModel( "KSession1" ).setDefault( true )
                                                 .setType( KieSessionModel.KieSessionType.STATEFUL )
                                                 .setClockType( ClockTypeOption.get( ClockType.PSEUDO_CLOCK.getId() ) );

        ReleaseId releaseId1 = ks.newReleaseId( "org.kie", "test-cep-upgrade", "1.1.1" );
        KieModule km = deployJar( ks, createKJar( ks, kproj, releaseId1, null, drl1, drl2 ) );

        KieContainer kc = ks.newKieContainer(km.getReleaseId());

        KieSession ksession = kc.newKieSession();
        List<String> list = new ArrayList<String>();
        ksession.setGlobal( "list", list );

        ksession.insert( new SimpleEvent("1", "MY_CODE", 0) );
        ksession.insert( new SimpleEvent("2", "YOUR_CODE", 0) );
        ksession.fireAllRules();

        assertEquals( 1, list.size() );
        assertEquals( "MY_CODE", list.get(0) );
        list.clear();

        ReleaseId releaseId2 = ks.newReleaseId("org.kie", "test-cep-upgrade", "1.1.2");
        // the null drl placeholder is used to have the same drl with a different file name
        // this causes the removal and readdition of both rules
        km = deployJar( ks, createKJar( ks, kproj, releaseId2, null, drl1, drl2, drl3 ) );
        Results results = kc.updateToVersion(releaseId2);
        assertEquals( 0, results.getMessages().size() );

        ksession.fireAllRules();

        assertEquals( 1, list.size() );
        assertEquals( "YOUR_CODE", list.get(0) );
    }

    @Test
    public void testKeepBuilderConfAfterIncrementalUpdate() throws Exception {
        // DROOLS-1282
        String drl1 = "import " + DummyEvent.class.getCanonicalName() + "\n" +
                      "rule R1 when\n" +
                      "  DummyEvent() @watch(id)\n" +
                      "then end\n";

        String drl2 = "import " + DummyEvent.class.getCanonicalName() + "\n" +
                      "rule R1 when\n" +
                      "  DummyEvent() @watch(*)\n" +
                      "then end\n";

        KieServices ks = KieServices.Factory.get();

        KieModuleModel kproj = ks.newKieModuleModel()
                                 .setConfigurationProperty( PropertySpecificOption.PROPERTY_NAME,
                                                            PropertySpecificOption.ALWAYS.toString() );

        ReleaseId releaseId1 = ks.newReleaseId( "org.kie", "test-property-reactive-upgrade", "1" );

        KieModule km = deployJar( ks, createKJar( ks, kproj, releaseId1, null, drl1 ) );
        KieContainer container = ks.newKieContainer(releaseId1);
        container.newKieSession();

        ReleaseId releaseId2 = ks.newReleaseId( "org.kie", "test-property-reactive-upgrade", "2" );
        km = deployJar( ks, createKJar( ks, kproj, releaseId2, null, drl2 ) );

        Results results = container.updateToVersion(releaseId2);
        assertEquals( 0, results.getMessages().size() );
    }

    @Test
    public void testRemovePackageFromKieBaseModel() throws Exception {
        // DROOLS-1287
        KieServices ks = KieServices.Factory.get();
        ReleaseId releaseId1 = ks.newReleaseId( "org.kie", "test-remove-pkg", "1.0" );
        ReleaseId releaseId2 = ks.newReleaseId( "org.kie", "test-remove-pkg", "1.1" );

        createKJarWIthPackages( ks, releaseId1, "pkg1", "pkg2" );
        KieContainer container = ks.newKieContainer(releaseId1);
        KieSession ksession = container.newKieSession();

        List<String> list = new ArrayList<String>();
        ksession.setGlobal( "list", list );

        ksession.insert( "test" );
        ksession.fireAllRules();

        assertEquals( 2, list.size() );
        assertTrue( list.containsAll( asList("R1", "R2") ) );

        createKJarWIthPackages( ks, releaseId2, "pkg2" );

        Results results = container.updateToVersion(releaseId2);
        assertEquals( 0, results.getMessages().size() );

        ksession = container.newKieSession();
        list = new ArrayList<String>();
        ksession.setGlobal( "list", list );

        ksession.insert( "test" );
        ksession.fireAllRules();

        assertEquals( 1, list.size() );
        assertTrue( list.containsAll( asList("R2") ) );
    }

    @Test
    public void testAddPackageToKieBaseModel() throws Exception {
        // DROOLS-1287
        KieServices ks = KieServices.Factory.get();
        ReleaseId releaseId1 = ks.newReleaseId( "org.kie", "test-remove-pkg", "1.0" );
        ReleaseId releaseId2 = ks.newReleaseId( "org.kie", "test-remove-pkg", "1.1" );

        createKJarWIthPackages( ks, releaseId1, "pkg2" );
        KieContainer container = ks.newKieContainer(releaseId1);
        KieSession ksession = container.newKieSession();

        List<String> list = new ArrayList<String>();
        ksession.setGlobal( "list", list );

        ksession.insert( "test" );
        ksession.fireAllRules();

        assertEquals( 1, list.size() );
        assertTrue( list.containsAll( asList("R2") ) );

        createKJarWIthPackages( ks, releaseId2, "pkg1", "pkg2" );

        Results results = container.updateToVersion(releaseId2);
        assertEquals( 0, results.getMessages().size() );

        ksession = container.newKieSession();
        list = new ArrayList<String>();
        ksession.setGlobal( "list", list );

        ksession.insert( "test" );
        ksession.fireAllRules();

        assertEquals( 2, list.size() );
        assertTrue( list.containsAll( asList("R1", "R2") ) );
    }
    
    @Test
    public void testAlphaNodeSharingIsOK() throws Exception {
       // inspired by drools-usage Fmt9wZUFi8g
       // check timer -scheduled activations are preserved if rule untouched by incremental compilation even with alpha node sharing.
       StringBuffer drl = new StringBuffer();
       drl.append("package org.drools.compiler\n");
       drl.append("global java.util.List list;\n");
       drl.append("global java.util.List list2;\n");
       drl.append("rule R1\n");
       drl.append(" timer (int: 3s)\n");
       drl.append(" when\n");
       drl.append("   $m : String()\n");
       drl.append(" then\n");
       drl.append("   list.add( $m );\n");
       drl.append("   retract( $m );\n");
       drl.append("end\n");
       drl.append("rule RS\n");
       drl.append(" timer (int: 3s)\n");
       drl.append(" salience 1\n");
       drl.append(" when\n");
       drl.append("   $i : Integer()\n");
       drl.append("   $m : String()\n");
       drl.append(" then\n");
       drl.append("   System.out.println($i + \" \"+ $m);");
       drl.append("   list2.add($i + \" \"+ $m);\n");
       drl.append("end\n");

       StringBuffer drl2 = new StringBuffer();
       drl2.append("package org.drools.compiler\n");
       drl2.append("global java.util.List list;\n");
       drl2.append("global java.util.List list2;\n");
       drl2.append("rule R1\n");
       drl2.append(" timer (int: 3s)\n");
       drl2.append(" when\n");
       drl2.append("   $m : String()\n");
       drl2.append(" then\n");
       drl2.append("   list.add( $m );\n");
       drl2.append("   list.add( $m );\n");
       drl2.append("   retract( $m );\n");
       drl2.append("end\n");
       drl2.append("rule RS\n");
       drl2.append(" timer (int: 3s)\n");
       drl2.append(" salience 1\n");
       drl2.append(" when\n");
       drl2.append("   $i : Integer()\n");
       drl2.append("   $m : String()\n");
       drl2.append(" then\n");
       drl2.append("   System.out.println($i + \" \"+ $m);");
       drl2.append("   list2.add($i + \" \"+ $m);\n");
       drl2.append("end\n");

       KieServices ks = KieServices.Factory.get();

       ReleaseId releaseId1 = ks.newReleaseId("org.kie", "test-upgrade", "1.0.0");
       KieModule km = createAndDeployJarInStreamMode(ks, releaseId1, drl.toString());

       KieContainer kc = ks.newKieContainer(km.getReleaseId());

       KieSessionConfiguration ksconf = ks.newKieSessionConfiguration();
       ksconf.setOption(TimedRuleExecutionOption.YES);
       ksconf.setOption(TimerJobFactoryOption.get("trackable"));
       ksconf.setOption(ClockTypeOption.get("pseudo"));

       KieSession ksession = kc.newKieSession(ksconf);

       SessionPseudoClock timeService = ksession.getSessionClock();
       timeService.advanceTime(new Date().getTime(), TimeUnit.MILLISECONDS);

       List list = new ArrayList();
       ksession.setGlobal("list", list);
       
       List list2 = new ArrayList();
       ksession.setGlobal("list2", list2);
       
       ksession.insert(new String("A"));
       ksession.insert(1);
       ksession.fireAllRules();

       assertEquals("1. Initial run: no message expected after rule fired immediately after fireAllRules due to duration of 5 sec", 0, list.size());
       assertEquals("1. Initial run: no message expected after rule fired immediately after fireAllRules due to duration of 5 sec", 0, list2.size());
       
       ReleaseId releaseId2 = ks.newReleaseId("org.kie", "test-upgrade", "1.0.1");
       km = createAndDeployJarInStreamMode(ks, releaseId2, drl2.toString());
       kc.updateToVersion(releaseId2);
       timeService.advanceTime(3200, TimeUnit.MILLISECONDS);

       assertEquals("1. R1 is NOT preserved", 0, list.size());
       assertEquals("1. RS is preserved", 1, list2.size());
    }


    private void createKJarWIthPackages( KieServices ks, ReleaseId releaseId1, String... pkgs ) {
        String drl1 = "global java.util.List list;\n" +
                      "rule R1 when\n" +
                      "  String()\n" +
                      "then\n" +
                      "  list.add(\"R1\");" +
                      "end\n";

        String drl2 = "global java.util.List list;\n" +
                      "rule R2 when\n" +
                      "  String()\n" +
                      "then\n" +
                      "  list.add(\"R2\");" +
                      "end\n";

        KieModuleModel kproj = ks.newKieModuleModel();
        KieBaseModel kieBaseModel1 = kproj.newKieBaseModel( "kbase1" ).setDefault( true );
        for (String pkg : pkgs) {
            kieBaseModel1.addPackage( pkg );
        }
        KieSessionModel ksession1 = kieBaseModel1.newKieSessionModel( "ksession1" ).setDefault( true );

        KieFileSystem kfs = ks.newKieFileSystem();
        kfs.writeKModuleXML(kproj.toXML());
        kfs.generateAndWritePomXML(releaseId1);
        kfs.write("src/main/resources/pkg1/r1.drl", drl1);
        kfs.write("src/main/resources/pkg2/r2.drl", drl2);

        KieModule km = deployJar( ks, buildKJar( ks, kfs, releaseId1 ) );
    }

    @Test
    public void testIncrementalCompilationWithNewEvent() throws Exception {
        // DROOLS-1395
        String drl1 =  "package fr.edf.distribution.brms.common.test\n" +
                       "import " + DummyEvent.class.getCanonicalName() + "\n" +
                       "declare DummyEvent\n" +
                       "    @role( event )\n" +
                       "    @timestamp( eventTimestamp )\n" +
                       "end\n" +
                       "rule \"RG_TEST_1\"\n" +
                       "    when\n" +
                       "       $event: DummyEvent ()\n" +
                       "    then\n" +
                       "        System.out.println(\"RG_TEST_1 fired\");\n" +
                       "        retract($event);\n" +
                       "end";

        String drl2 =  "package fr.edf.distribution.brms.common.test\n" +
                       "import " + DummyEvent.class.getCanonicalName() + "\n" +
                       "import " + OtherDummyEvent.class.getCanonicalName() + "\n" +
                       "declare DummyEvent\n" +
                       "    @role( event )\n" +
                       "    @timestamp( eventTimestamp )\n" +
                       "end\n" +
                       "declare OtherDummyEvent\n" +
                       "    @role( event )\n" +
                       "    @timestamp( eventTimestamp )\n" +
                       "end\n" +
                       "rule \"RG_TEST_2\"\n" +
                       "    when\n" +
                       "       $event: DummyEvent ()\n" +
                       "       $other : OtherDummyEvent(id == $event.id, this after $event)\n" +
                       "    then\n" +
                       "        System.out.println(\"RG_TEST_2 fired\");\n" +
                       "        retract($other);\n" +
                       "end\n" +
                       "\n" +
                       "rule \"RG_TEST_1\"\n" +
                       "    when\n" +
                       "       $event: DummyEvent ()\n" +
                       "    then\n" +
                       "        System.out.println(\"RG_TEST_1 fired\");\n" +
                       "        retract($event);\n" +
                       "end\n";

        KieServices ks = KieServices.Factory.get();

        KieModuleModel kproj = ks.newKieModuleModel();
        KieBaseModel kieBaseModel1 = kproj.newKieBaseModel( "KBase1" ).setDefault( true )
                                          .setEventProcessingMode(EventProcessingOption.STREAM);
        KieSessionModel ksession1 = kieBaseModel1.newKieSessionModel("KSession1").setDefault(true)
                                                 .setType(KieSessionModel.KieSessionType.STATEFUL)
                                                 .setClockType( ClockTypeOption.get( ClockType.PSEUDO_CLOCK.getId() ) );

        ReleaseId releaseId1 = ks.newReleaseId( "org.kie", "test-upgrade", "1.0.0" );
        deployJar( ks, createKJar( ks, kproj, releaseId1, null, drl1 ) );

        KieContainer kc = ks.newKieContainer(releaseId1);
        KieSession kieSession = kc.newKieSession();

        DummyEvent evt = new DummyEvent("evt");
        kieSession.insert(evt);
        assertEquals(1, kieSession.fireAllRules());

        ReleaseId releaseId2 = ks.newReleaseId("org.kie", "test-upgrade", "2.0.0");
        deployJar( ks, createKJar( ks, kproj, releaseId2, null, drl2 ) );

        kc.updateToVersion(releaseId2);

        evt = new DummyEvent("evt");
        kieSession.insert(evt);
        OtherDummyEvent other = new OtherDummyEvent("evt");
        kieSession.insert(other);
        assertEquals(1, kieSession.fireAllRules());
    }
    
    @Test
    public void testKJarUpgradeWithSpace() throws Exception {
        // DROOLS-1399
        String drl_1 = "package org.drools.compiler\n" +
                "rule Rx when\n" +
                "   $m : Message( message == \"Hello World\" )\n" +
                "then\n" +
                "end\n";

        String drl_2 = "package org.drools.compiler\n " + // <<- notice the EXTRA SPACE is the only change in this other version.
                "rule Rx when\n" +
                "   $m : Message( message == \"Hello World\" )\n" +
                "then\n" +
                "end\n";

        KieServices ks = KieServices.Factory.get();

        ReleaseId releaseId1 = ks.newReleaseId( "org.kie", "test-upgrade", "1.0.0" );
        KieModule km = createAndDeployJar( ks, releaseId1, drl_1 );

        KieContainer kc = ks.newKieContainer( km.getReleaseId() );
        KieSession ksession = kc.newKieSession();
        ksession.insert( new Message( "Hello World" ) );
        assertEquals( 1, ksession.fireAllRules() );

        ReleaseId releaseId2 = ks.newReleaseId( "org.kie", "test-upgrade", "1.1.0" );
        km = createAndDeployJar( ks, releaseId2, drl_2 );

        kc.updateToVersion( releaseId2 );

        // rule Rx is UNchanged and should NOT fire again
        assertEquals( 0, ksession.fireAllRules() );
    }
    
    @Test
    public void testKJarUpgradeDRLWithSpace2() throws Exception {
        // DROOLS-1399 bis
        String drl_1 = "package org.drools.compiler\n" +
                "rule Rx when\n" +
                "   $m : Message( message == \"Hello World\" )\n" +
                "then\n" +
                "end\n";

        String drl_2 = "package org.drools.compiler\n" + 
                "rule Rx when\n" +
                "   $m : Message( message == \"Hello World\"  )\n" + // <<- notice the EXTRA SPACE is the only change in this other version.
                "then\n" +
                "end\n";

        KieServices ks = KieServices.Factory.get();

        ReleaseId releaseId1 = ks.newReleaseId( "org.kie", "test-upgrade", "1.0.0" );
        KieModule km = createAndDeployJar( ks, releaseId1, drl_1 );

        KieContainer kc = ks.newKieContainer( km.getReleaseId() );
        KieSession ksession = kc.newKieSession();
        ksession.insert( new Message( "Hello World" ) );
        assertEquals( 1, ksession.fireAllRules() );

        ReleaseId releaseId2 = ks.newReleaseId( "org.kie", "test-upgrade", "1.1.0" );
        km = createAndDeployJar( ks, releaseId2, drl_2 );

        kc.updateToVersion( releaseId2 );

        // rule Rx is UNchanged and should NOT fire again
        assertEquals( 0, ksession.fireAllRules() );
    }
    
    @Test
    public void testKJarUpgradeDRLWithSpace3() throws Exception {
        // DROOLS-1399 ter
        String drl_1 = "package org.drools.compiler\n" +
                "rule Rs when $s : String() then System.out.println($s); end\n" +
                "rule Rx when\n" +
                "   $m : Message( message == \"Hello World\" )\n" +
                "then\n" +
                "  System.out.println($m); \n"+
                "end\n";

        String drl_2 = "package org.drools.compiler\n" + 
                "rule Rs when $s : String( this == \"x\") then System.out.println($s); end\n" + // <<- notice rule changed
                "rule Rx when\n" +
                "   $m : Message( message == \"Hello World\"  )\n" + // <<- notice the EXTRA SPACE is the an ADDITIONAL change in this other version.
                "then\n" +
                "  System.out.println($m); \n"+
                "end\n";

        KieServices ks = KieServices.Factory.get();

        ReleaseId releaseId1 = ks.newReleaseId( "org.kie", "test-upgrade", "1.0.0" );
        KieModule km = createAndDeployJar( ks, releaseId1, drl_1 );

        KieContainer kc = ks.newKieContainer( km.getReleaseId() );
        KieSession ksession = kc.newKieSession();
        final List<String> fired = new ArrayList<>();
        ksession.addEventListener(new DefaultAgendaEventListener() {
            @Override
            public void afterMatchFired(AfterMatchFiredEvent event) {
                fired.add(event.getMatch().getRule().getName());
            }
        });
        
        ksession.insert( new Message( "Hello World" ) );
        ksession.insert( "x" );
        assertEquals( 2, ksession.fireAllRules() );
        assertTrue( fired.contains("Rs") );
        assertTrue( fired.contains("Rx") );
        
        fired.clear();

        ReleaseId releaseId2 = ks.newReleaseId( "org.kie", "test-upgrade", "1.1.0" );
        km = createAndDeployJar( ks, releaseId2, drl_2 );

        kc.updateToVersion( releaseId2 );

        // rule Rx is UNchanged and should NOT fire again
        // rule Rs is changed and should match again, and fire again.
        assertEquals( 1, ksession.fireAllRules() );
        assertTrue( fired.contains("Rs") );
        assertFalse( fired.contains("Rx") );
    }
    
    @Test
    public void testKJarUpgradeDRLWithSpace4() throws Exception {
        // DROOLS-1399 quater
        String drl_1 = "package org.drools.compiler\n" +
                "rule Rx when\n" +
                "   $m : Message( message == \"Hello  World\" )\n" +
                "then\n" +
                "end\n";

        String drl_2 = "package org.drools.compiler\n" + 
                "rule Rx when\n" +
                "   $m : Message( message == \"Hello World\" )\n" + // <<- notice the EXTRA SPACE typo was removed
                "then\n" +
                "end\n";

        KieServices ks = KieServices.Factory.get();

        ReleaseId releaseId1 = ks.newReleaseId( "org.kie", "test-upgrade", "1.0.0" );
        KieModule km = createAndDeployJar( ks, releaseId1, drl_1 );

        KieContainer kc = ks.newKieContainer( km.getReleaseId() );
        KieSession ksession = kc.newKieSession();
        ksession.insert( new Message( "Hello World" ) );
        assertEquals( 0, ksession.fireAllRules() );

        ReleaseId releaseId2 = ks.newReleaseId( "org.kie", "test-upgrade", "1.1.0" );
        km = createAndDeployJar( ks, releaseId2, drl_2 );

        kc.updateToVersion( releaseId2 );

        // rule Rx is UNchanged and should NOT fire again
        assertEquals( 1, ksession.fireAllRules() );
    }
    
    @Test
    public void testKJarUpgradeDRLWithSpace5() throws Exception {
        // DROOLS-1399 quinquies
        String drl_1 = "package org.drools.compiler\n" +
                "rule Rx when\n" +
                "   $m : Message( message == \"Hello'  World\" )\n" +
                "then\n" +
                "end\n";

        String drl_2 = "package org.drools.compiler\n" + 
                "rule Rx when\n" +
                "   $m : Message( message == \"Hello' World\" )\n" + // <<- notice the EXTRA SPACE typo was removed
                "then\n" +
                "end\n";

        KieServices ks = KieServices.Factory.get();

        ReleaseId releaseId1 = ks.newReleaseId( "org.kie", "test-upgrade", "1.0.0" );
        KieModule km = createAndDeployJar( ks, releaseId1, drl_1 );

        KieContainer kc = ks.newKieContainer( km.getReleaseId() );
        KieSession ksession = kc.newKieSession();
        ksession.insert( new Message( "Hello' World" ) ); // <<- notice the ' character
        assertEquals( 0, ksession.fireAllRules() );

        ReleaseId releaseId2 = ks.newReleaseId( "org.kie", "test-upgrade", "1.1.0" );
        km = createAndDeployJar( ks, releaseId2, drl_2 );

        kc.updateToVersion( releaseId2 );

        // rule Rx is UNchanged and should NOT fire again
        assertEquals( 1, ksession.fireAllRules() );
    }
    
    @Test
    public void testKJarUpgradeWithSpace_usingSingleQuote() throws Exception {
        // DROOLS-1399 (using single quote)
        String drl_1 = "package org.drools.compiler\n" +
                "rule Rx when\n" +
                "   $m : Message( message == 'Hello World' )\n" +
                "then\n" +
                "end\n";

        String drl_2 = "package org.drools.compiler\n " + // <<- notice the EXTRA SPACE is the only change in this other version.
                "rule Rx when\n" +
                "   $m : Message( message == 'Hello World' )\n" +
                "then\n" +
                "end\n";

        KieServices ks = KieServices.Factory.get();

        ReleaseId releaseId1 = ks.newReleaseId( "org.kie", "test-upgrade", "1.0.0" );
        KieModule km = createAndDeployJar( ks, releaseId1, drl_1 );

        KieContainer kc = ks.newKieContainer( km.getReleaseId() );
        KieSession ksession = kc.newKieSession();
        ksession.insert( new Message( "Hello World" ) );
        assertEquals( 1, ksession.fireAllRules() );

        ReleaseId releaseId2 = ks.newReleaseId( "org.kie", "test-upgrade", "1.1.0" );
        km = createAndDeployJar( ks, releaseId2, drl_2 );

        kc.updateToVersion( releaseId2 );

        // rule Rx is UNchanged and should NOT fire again
        assertEquals( 0, ksession.fireAllRules() );
    }
    
    @Test
    public void testKJarUpgradeDRLWithSpace2_usingSingleQuote() throws Exception {
        // DROOLS-1399 bis (using single quote)
        String drl_1 = "package org.drools.compiler\n" +
                "rule Rx when\n" +
                "   $m : Message( message == 'Hello World' )\n" +
                "then\n" +
                "end\n";

        String drl_2 = "package org.drools.compiler\n" + 
                "rule Rx when\n" +
                "   $m : Message( message == 'Hello World'  )\n" + // <<- notice the EXTRA SPACE is the only change in this other version.
                "then\n" +
                "end\n";

        KieServices ks = KieServices.Factory.get();

        ReleaseId releaseId1 = ks.newReleaseId( "org.kie", "test-upgrade", "1.0.0" );
        KieModule km = createAndDeployJar( ks, releaseId1, drl_1 );

        KieContainer kc = ks.newKieContainer( km.getReleaseId() );
        KieSession ksession = kc.newKieSession();
        ksession.insert( new Message( "Hello World" ) );
        assertEquals( 1, ksession.fireAllRules() );

        ReleaseId releaseId2 = ks.newReleaseId( "org.kie", "test-upgrade", "1.1.0" );
        km = createAndDeployJar( ks, releaseId2, drl_2 );

        kc.updateToVersion( releaseId2 );

        // rule Rx is UNchanged and should NOT fire again
        assertEquals( 0, ksession.fireAllRules() );
    }
    
    @Test
    public void testKJarUpgradeDRLWithSpace3_usingSingleQuote() throws Exception {
        // DROOLS-1399 ter (using single quote)
        String drl_1 = "package org.drools.compiler\n" +
                "rule Rs when $s : String() then System.out.println($s); end\n" +
                "rule Rx when\n" +
                "   $m : Message( message == 'Hello World' )\n" +
                "then\n" +
                "  System.out.println($m); \n"+
                "end\n";

        String drl_2 = "package org.drools.compiler\n" + 
                "rule Rs when $s : String( this == 'x') then System.out.println($s); end\n" + // <<- notice rule changed
                "rule Rx when\n" +
                "   $m : Message( message == 'Hello World'  )\n" + // <<- notice the EXTRA SPACE is the an ADDITIONAL change in this other version.
                "then\n" +
                "  System.out.println($m); \n"+
                "end\n";

        KieServices ks = KieServices.Factory.get();

        ReleaseId releaseId1 = ks.newReleaseId( "org.kie", "test-upgrade", "1.0.0" );
        KieModule km = createAndDeployJar( ks, releaseId1, drl_1 );

        KieContainer kc = ks.newKieContainer( km.getReleaseId() );
        KieSession ksession = kc.newKieSession();
        final List<String> fired = new ArrayList<>();
        ksession.addEventListener(new DefaultAgendaEventListener() {
            @Override
            public void afterMatchFired(AfterMatchFiredEvent event) {
                fired.add(event.getMatch().getRule().getName());
            }
        });
        
        ksession.insert( new Message( "Hello World" ) );
        ksession.insert( "x" );
        assertEquals( 2, ksession.fireAllRules() );
        assertTrue( fired.contains("Rs") );
        assertTrue( fired.contains("Rx") );
        
        fired.clear();

        ReleaseId releaseId2 = ks.newReleaseId( "org.kie", "test-upgrade", "1.1.0" );
        km = createAndDeployJar( ks, releaseId2, drl_2 );

        kc.updateToVersion( releaseId2 );

        // rule Rx is UNchanged and should NOT fire again
        // rule Rs is changed and should match again, and fire again.
        assertEquals( 1, ksession.fireAllRules() );
        assertTrue( fired.contains("Rs") );
        assertFalse( fired.contains("Rx") );
    }
    
    @Test
    public void testKJarUpgradeDRLWithSpace4_usingSingleQuote() throws Exception {
        // DROOLS-1399 quater (using single quote)
        String drl_1 = "package org.drools.compiler\n" +
                "rule Rx when\n" +
                "   $m : Message( message == 'Hello  World' )\n" +
                "then\n" +
                "end\n";

        String drl_2 = "package org.drools.compiler\n" + 
                "rule Rx when\n" +
                "   $m : Message( message == 'Hello World' )\n" + // <<- notice the EXTRA SPACE typo was removed
                "then\n" +
                "end\n";

        KieServices ks = KieServices.Factory.get();

        ReleaseId releaseId1 = ks.newReleaseId( "org.kie", "test-upgrade", "1.0.0" );
        KieModule km = createAndDeployJar( ks, releaseId1, drl_1 );

        KieContainer kc = ks.newKieContainer( km.getReleaseId() );
        KieSession ksession = kc.newKieSession();
        ksession.insert( new Message( "Hello World" ) );
        assertEquals( 0, ksession.fireAllRules() );

        ReleaseId releaseId2 = ks.newReleaseId( "org.kie", "test-upgrade", "1.1.0" );
        km = createAndDeployJar( ks, releaseId2, drl_2 );

        kc.updateToVersion( releaseId2 );

        // rule Rx is UNchanged and should NOT fire again
        assertEquals( 1, ksession.fireAllRules() );
    }
    
    @Test
    public void testKJarUpgradeDRLWithSpace5_usingSingleQuote() throws Exception {
        // DROOLS-1399 quinquies (using single quote)
        String drl_1 = "package org.drools.compiler\n" +
                "rule Rx when\n" +
                "   $m : Message( message == 'Hello\\'  World' )\n" +
                "then\n" +
                "end\n";

        String drl_2 = "package org.drools.compiler\n" + 
                "rule Rx when\n" +
                "   $m : Message( message == 'Hello\\' World' )\n" + // <<- notice the EXTRA SPACE typo was removed
                "then\n" +
                "end\n";

        KieServices ks = KieServices.Factory.get();

        ReleaseId releaseId1 = ks.newReleaseId( "org.kie", "test-upgrade", "1.0.0" );
        KieModule km = createAndDeployJar( ks, releaseId1, drl_1 );

        KieContainer kc = ks.newKieContainer( km.getReleaseId() );
        KieSession ksession = kc.newKieSession();
        ksession.insert( new Message( "Hello' World" ) ); // <<- notice the ' character on this one
        assertEquals( 0, ksession.fireAllRules() );

        ReleaseId releaseId2 = ks.newReleaseId( "org.kie", "test-upgrade", "1.1.0" );
        km = createAndDeployJar( ks, releaseId2, drl_2 );

        kc.updateToVersion( releaseId2 );

        // rule Rx is UNchanged and should NOT fire again
        assertEquals( 1, ksession.fireAllRules() );
    }

    @Test
    public void testJavaClassRedefinition() {
        // DROOLS-1402
        String JAVA1 = "package org.test;" +
                       "    public class MyBean {\n" +
                       "        private String firstName;\n" +
                       "        public MyBean() { /* empty constructor */ }\n" +
                       "        public MyBean(String firstName) { this.firstName = firstName; }\n" +
                       "        public String getFirstName() { return firstName; }\n" +
                       "        public void setFirstName(String firstName) { this.firstName = firstName; }\n" +
                       "    }";

        String DRL1 = "package org.test;\n" +
                      "\n" +
                      "//from row number: 1\n" +
                      "rule \"Row 1 HelloRules\"\n" +
                      "    when\n" +
                      "        $b : MyBean( firstName == null )\n" +
                      "    then\n" +
                      "        System.out.println($b);" +
                      "end";

        String INIT_DRL = "package org.test; rule RINIT when eval(true) then insert(new MyBean()); end";
        String INIT_DRL_2 = "package org.test; rule RINIT when eval(1==1) then insert(new MyBean()); end";

        String JAVA2 = "package org.test;" +
                       "    public class MyBean {\n" +
                       "        private String firstName;\n" +
                       "        private String lastName;\n" +
                       "        public MyBean() { /* empty constructor */ }\n" +
                       "        public MyBean(String firstName) { this.firstName = firstName; }\n" +
                       "        public MyBean(String firstName, String lastName) { this.firstName = firstName; this.lastName = lastName; }\n" +
                       "        public String getFirstName() { return firstName; }\n" +
                       "        public void setFirstName(String firstName) { this.firstName = firstName; }\n" +
                       "        public String getLastName() { return lastName; }\n" +
                       "        public void setLastName(String lastName) { this.lastName = lastName; }\n" +
                       "    }";

        String DRL2 = "package org.test;\n" +
                      "\n" +
                      "//from row number: 1\n" +
                      "rule \"Row 1 HelloRules\"\n" +
                      "    when\n" +
                      "        $b : MyBean( firstName == null , lastName == null )\n" +
                      "    then\n" +
                      "        System.out.println($b);" +
                      "end";

        KieServices ks = KieServices.Factory.get();

        KieFileSystem kfs = ks.newKieFileSystem();
        ReleaseId id = ks.newReleaseId( "org.test", "myTest", "1.0.0" );

        KieBuilder kieBuilder = ks.newKieBuilder( kfs );

        kfs.generateAndWritePomXML( id );

        kfs.write("src/main/java/org/test/MyBean.java",
                  ks.getResources().newReaderResource(new StringReader(JAVA1)));

        kfs.write( ks.getResources()
                     .newReaderResource( new StringReader( DRL1 ) )
                     .setResourceType( ResourceType.DRL )
                     .setSourcePath( "rules.drl" ) );

        kfs.write( ks.getResources()
                     .newReaderResource( new StringReader( INIT_DRL ) )
                     .setResourceType( ResourceType.DRL )
                     .setSourcePath( "INIT_DRL.drl" ) );

        kieBuilder.buildAll();

        KieContainer kc = ks.newKieContainer( id );
        KieSession ksession = kc.newKieSession();

        int fired = ksession.fireAllRules();
        assertEquals( 2, fired );

        ReleaseId id2 = ks.newReleaseId( "org.test", "myTest", "2.0.0" );
        KieFileSystem kfs2 = ks.newKieFileSystem();

        KieBuilder kieBuilder2 = ks.newKieBuilder( kfs2 );

        kfs2.generateAndWritePomXML( id2 );

        kfs2.write("src/main/java/org/test/MyBean.java",
                   ks.getResources().newReaderResource(new StringReader(JAVA2)));

        kfs2.write( ks.getResources()
                      .newReaderResource( new StringReader( DRL2 ) )
                      .setResourceType( ResourceType.DRL )
                      .setSourcePath( "rules.drl" ) );

        kfs2.write( ks.getResources()
                      .newReaderResource( new StringReader( INIT_DRL_2 ) )
                      .setResourceType( ResourceType.DRL )
                      .setSourcePath( "INIT_DRL.drl" ) );

        kieBuilder2.buildAll();

        Results updateResults = kc.updateToVersion(id2);
        assertFalse(updateResults.hasMessages(Level.ERROR));
        
        fired = ksession.fireAllRules();
        assertEquals( 2, fired );
    }
    
    @Test
    public void testJavaClassRedefinitionJoined() {
        // DROOLS-1402
        String JAVA1 = "package org.test;" +
                       "    public class MyBean {\n" +
                       "        private String firstName;\n" +
                       "        public MyBean() { /* empty constructor */ }\n" +
                       "        public MyBean(String firstName) { this.firstName = firstName; }\n" +
                       "        public String getFirstName() { return firstName; }\n" +
                       "        public void setFirstName(String firstName) { this.firstName = firstName; }\n" +
                       "    }";

        String DRL1 = "package org.test;\n" +
                      "\n" +
                      "//from row number: 1\n" +
                      "rule \"Row 1 HelloRules\"\n" +
                      "    when\n" +
                      "        $b : MyBean( firstName == null )\n" +
                      "        $s : String()\n" +
                      "    then\n" +
                      "        System.out.println($s + \" \" + $b);" +
                      "end";

        String INIT_DRL = "package org.test; rule RINIT when eval(true) then insert(new MyBean()); end";
        String INIT_DRL_2 = "package org.test; rule RINIT when eval(1==1) then insert(new MyBean()); end";

        String JAVA2 = "package org.test;" +
                       "    public class MyBean {\n" +
                       "        private String firstName;\n" +
                       "        private String lastName;\n" +
                       "        public MyBean() { /* empty constructor */ }\n" +
                       "        public MyBean(String firstName) { this.firstName = firstName; }\n" +
                       "        public MyBean(String firstName, String lastName) { this.firstName = firstName; this.lastName = lastName; }\n" +
                       "        public String getFirstName() { return firstName; }\n" +
                       "        public void setFirstName(String firstName) { this.firstName = firstName; }\n" +
                       "        public String getLastName() { return lastName; }\n" +
                       "        public void setLastName(String lastName) { this.lastName = lastName; }\n" +
                       "    }";

        String DRL2 = "package org.test;\n" +
                      "\n" +
                      "//from row number: 1\n" +
                      "rule \"Row 1 HelloRules\"\n" +
                      "    when\n" +
                      "        $b : MyBean( firstName == null , lastName == null )\n" +
                      "        $s : String()\n" +
                      "    then\n" +
                      "        System.out.println($s + \" \" + $b);" +
                      "end";

        KieServices ks = KieServices.Factory.get();

        KieFileSystem kfs = ks.newKieFileSystem();
        ReleaseId id = ks.newReleaseId( "org.test", "myTest", "1.0.0" );

        KieBuilder kieBuilder = ks.newKieBuilder( kfs );

        kfs.generateAndWritePomXML( id );

        kfs.write( "src/main/java/org/test/MyBean.java",
                   ks.getResources().newReaderResource( new StringReader( JAVA1 ) ) );

        kfs.write( ks.getResources()
                     .newReaderResource( new StringReader( DRL1 ) )
                     .setResourceType( ResourceType.DRL )
                     .setSourcePath( "rules.drl" ) );

        kfs.write( ks.getResources()
                     .newReaderResource( new StringReader( INIT_DRL ) )
                     .setResourceType( ResourceType.DRL )
                     .setSourcePath( "INIT_DRL.drl" ) );

        kieBuilder.buildAll();

        KieContainer kc = ks.newKieContainer( id );
        KieSession ksession = kc.newKieSession();

        ksession.insert( "This string joins with" );
        int fired = ksession.fireAllRules();
        assertEquals( 2, fired );

        ReleaseId id2 = ks.newReleaseId( "org.test", "myTest", "2.0.0" );
        KieFileSystem kfs2 = ks.newKieFileSystem();

        KieBuilder kieBuilder2 = ks.newKieBuilder( kfs2 );

        kfs2.generateAndWritePomXML( id2 );

        kfs2.write( "src/main/java/org/test/MyBean.java",
                    ks.getResources().newReaderResource( new StringReader( JAVA2 ) ) );

        kfs2.write( ks.getResources()
                      .newReaderResource( new StringReader( DRL2 ) )
                      .setResourceType( ResourceType.DRL )
                      .setSourcePath( "rules.drl" ) );

        kfs2.write( ks.getResources()
                      .newReaderResource( new StringReader( INIT_DRL_2 ) )
                      .setResourceType( ResourceType.DRL )
                      .setSourcePath( "INIT_DRL.drl" ) );

        kieBuilder2.buildAll();

        Results updateResults = kc.updateToVersion( id2 );
        assertFalse( updateResults.hasMessages( Level.ERROR ) );

        fired = ksession.fireAllRules();
        assertEquals( 2, fired );
    }

    @Test(timeout = 10000L)
    public void testMultipleIncrementalCompilationsWithFireUntilHalt() throws Exception {
        // DROOLS-1406
        KieServices ks = KieServices.Factory.get();

        // Create an in-memory jar for version 1.0.0
        ReleaseId releaseId1 = ks.newReleaseId( "org.kie", "test-fireUntilHalt", "1.0" );
        KieModule km = createAndDeployJar( ks, releaseId1, getTestRuleForFireUntilHalt(0) );

        // Create a session and fire rules
        final KieContainer kc = ks.newKieContainer( km.getReleaseId() );
        final KieSession kieSession = kc.newKieSession();

        DebugList<String> list = new DebugList<String>();
        kieSession.setGlobal( "list", list );
        kieSession.insert( new Message( "X" ) );

        CountDownLatch done = new CountDownLatch( 1 );
        list.done = done;

        try {
            new Thread(kieSession::fireUntilHalt).start();

            done.await();
            assertEquals( 1, list.size() );
            assertEquals( "0 - X", list.get(0) );
            list.clear();

            for (int i = 1; i < 10; i++) {
                done = new CountDownLatch( 1 );
                list.done = done;

                ReleaseId releaseIdI = ks.newReleaseId( "org.kie", "test-fireUntilHalt", "1."+i );
                km = createAndDeployJar( ks, releaseIdI, getTestRuleForFireUntilHalt(i) );

                kc.updateToVersion( releaseIdI );

                done.await();
                assertEquals( 1, list.size() );
                assertEquals( i + " - X", list.get(0) );
                list.clear();
            }
        } finally {
            kieSession.halt();
        }
    }

    private String getTestRuleForFireUntilHalt(int i) {
        return "package org.drools.compiler\n" +
               "global java.util.List list;\n" +
               "rule Rx when\n" +
               "   Message( $m : message )\n" +
               "then\n" +
               "    list.add(\"" + i + " - \" + $m);\n" +
               "end\n";
    }

    public static class DebugList<T> extends ArrayList<T> {
        CountDownLatch done;

        @Override
        public synchronized boolean add( T t ) {
            boolean result = super.add( t );
            done.countDown();
            return result;
        }
    }

    @Test
    public void testIncrementalCompilationWithExtendsRule() throws Exception {
        // DROOLS-1405
        String drl1 =
                "rule \"test1\" when then end\n";

        String drl2 =
                "rule \"test2\" extends \"test1\" when then end\n" +
                "rule \"test3\" extends \"test1\" when then end\n";

        KieServices ks = KieServices.Factory.get();

        KieFileSystem kfs = ks.newKieFileSystem()
                              .write( "src/main/resources/r1.drl", drl1 );

        KieBuilder kieBuilder = ks.newKieBuilder( kfs ).buildAll();
        assertEquals( 0, kieBuilder.getResults().getMessages( org.kie.api.builder.Message.Level.ERROR ).size() );

        kfs.write( "src/main/resources/r2.drl", drl2 );
        IncrementalResults addResults = ( (InternalKieBuilder) kieBuilder ).createFileSet( "src/main/resources/r2.drl" ).build();

        assertEquals( 0, addResults.getAddedMessages().size() );
    }

    public static class BaseClass {
        String baseField;
        public String getBaseField() {
            return baseField;
        }
        public void setBaseField(String baseField) {
            this.baseField = baseField;
        }
    }

    @Test
    public void testUpdateWithPojoExtensionDifferentPackages() throws Exception {
        // DROOLS-1491
        String drlDeclare = "package org.drools.compiler.integrationtests\n" +
                            "declare DroolsApplications extends " + BaseClass.class.getCanonicalName() + "\n" +
                            "    droolsAppName: String\n" +
                            "end";
        String drlRule = "package org.drools.compiler.test\n" +
                         "rule R1 when\n" +
                         "   $fact : org.drools.compiler.integrationtests.DroolsApplications( droolsAppName == \"appName\" )\n" +
                         "then\n" +
                         "end";

        KieServices ks = KieServices.Factory.get();
        KieFileSystem kfs = ks.newKieFileSystem();
        ReleaseId releaseId1 = ks.newReleaseId( "org.kie", "test-upgrade", "1.0.0" );

        kfs.generateAndWritePomXML( releaseId1 );
        kfs.write( ks.getResources()
                     .newReaderResource( new StringReader( drlDeclare ) )
                     .setResourceType( ResourceType.DRL )
                     .setSourcePath( "drlDeclare.drl" ) );

        KieBuilder kieBuilder = ks.newKieBuilder( kfs );
        kieBuilder.buildAll();

        KieContainer kc = ks.newKieContainer( releaseId1 );

        KieSession ksession = kc.newKieSession();
        FactType factType = kc.getKieBase().getFactType( "org.drools.compiler.integrationtests", "DroolsApplications" );
        Object fact = factType.newInstance();
        factType.set( fact, "droolsAppName", "appName" );
        ksession.insert( fact );
        assertEquals( 0, ksession.fireAllRules() );

        ReleaseId releaseId2 = ks.newReleaseId( "org.kie", "test-upgrade", "1.1.0" );
        KieFileSystem kfs2 = ks.newKieFileSystem();


        kfs2.generateAndWritePomXML( releaseId2 );

        kfs2.write( ks.getResources()
                      .newReaderResource( new StringReader( drlDeclare ) )
                      .setResourceType( ResourceType.DRL )
                      .setSourcePath( "drlDeclare.drl" ) );

        kfs2.write( ks.getResources()
                      .newReaderResource( new StringReader( drlRule ) )
                      .setResourceType( ResourceType.DRL )
                      .setSourcePath( "drlRule.drl" ) );

        KieBuilder kieBuilder2 = ks.newKieBuilder( kfs2 );
        kieBuilder2.buildAll();

        Results updateResults = kc.updateToVersion( releaseId2 );

        assertEquals( 0, updateResults.getMessages().size() );

        assertEquals( 1, ksession.fireAllRules() );
    }

    @Test
    public void testPropertyReactivityOfAKnownClass() {
        String drl1 =
                "import " + TypeA.class.getCanonicalName() + "\n" +
                "import " + TypeB.class.getCanonicalName() + "\n" +
                "rule \"RULE_1\"\n" +
                "    when\n" +
                "        TypeA()" +
                "        TypeB()" +
                "    then\n" +
                "end\n";

        String drl2 =
                "import " + TypeB.class.getCanonicalName() + "\n" +
                "rule \"RULE_2\"\n" +
                "    when\n" +
                "        $b : TypeB() @watch(!*)" +
                "    then\n" +
                "        modify($b) { setValue(0) } \n" +
                "end\n";

        KieServices ks = KieServices.Factory.get();

        ReleaseId releaseId1 = ks.newReleaseId( "org.kie", "test-upgrade", "1.0.0" );
        KieModule km = createAndDeployJar( ks, releaseId1, drl1 );

        KieContainer kc = ks.newKieContainer( releaseId1 );
        KieSession ksession = kc.newKieSession();

        ReleaseId releaseId2 = ks.newReleaseId( "org.kie", "test-upgrade", "1.1.0" );
        createAndDeployJar( ks, releaseId2, drl2 );
        kc.updateToVersion( releaseId2 );

        ksession.insert(new TypeB(1));
        int fired = ksession.fireAllRules(10);

        assertEquals(1, fired);
    }

    @Test
    public void testPropertyReactivityOfAnOriginallyUnknownClass() {
        // DROOLS-1684
        String drl1 =
                "import " + TypeA.class.getCanonicalName() + "\n" +
                "rule \"RULE_1\"\n" +
                "    when\n" +
                "        TypeA()" +
                "    then\n" +
                "end\n";

        String drl2 =
                "import " + TypeB.class.getCanonicalName() + "\n" +
                "rule \"RULE_2\"\n" +
                "    when\n" +
                "        $b : TypeB() @watch(!*)" +
                "    then\n" +
                "        modify($b) { setValue(0) } \n" +
                "end\n";

        KieServices ks = KieServices.Factory.get();

        ReleaseId releaseId1 = ks.newReleaseId( "org.kie", "test-upgrade", "1.0.0" );
        KieModule km = createAndDeployJar( ks, releaseId1, drl1 );

        KieContainer kc = ks.newKieContainer( releaseId1 );
        KieSession ksession = kc.newKieSession();

        ReleaseId releaseId2 = ks.newReleaseId( "org.kie", "test-upgrade", "1.1.0" );
        createAndDeployJar( ks, releaseId2, drl2 );
        kc.updateToVersion( releaseId2 );

        ksession.insert(new TypeB(1));
        int fired = ksession.fireAllRules(10);

        assertEquals(1, fired);
    }

    public static class TypeA { }

    public static class TypeB {
        int value;

        public TypeB(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }
    }

    private static final String DECLARES_DRL =
            "package org.drools.example.api.kiedeclare;\n" +
            "declare Message\n" +
            "name : String\n" +
            "text : String\n" +
            "end \n";

    private static final String RULES1_DRL =
            "package org.drools.example.api.kiemodulemodel;\n" +
            "import org.drools.example.api.kiedeclare.*;\n" +
            "rule rule6 when \n" +
            "    $m : Message(text == \"What's the problem?\") \n" +
            "then\n" +
            "    delete( $m );\n" +
            "    insert( new Message(\"HAL\", \"reply 1\" ) ); \n" +
            "end \n";

    private static final String RULES2_DRL =
            "package org.drools.example.api.kiemodulemodel;\n" +
            "import org.drools.example.api.kiedeclare.*;\n" +
            "rule rule6 when \n" +
            "    $m : Message(text == \"What's the problem?\") \n" +
            "then\n" +
            "    delete( $m );\n" +
            "    insert( new Message(\"HAL\", \"reply 2\" ) ); \n" +
            "end \n";

    @Test
    public void testDeclaredTypeInDifferentPackage() {
        // DROOLS-1707
        KieServices ks = KieServices.Factory.get();

        ReleaseId releaseId1 = ks.newReleaseId( "org.kie", "test-upgrade", "1.0.0" );
        KieModule km = createAndDeployJar( ks, releaseId1, DECLARES_DRL, RULES1_DRL  );

        KieContainer kContainer = ks.newKieContainer(km.getReleaseId());
        doFire(kContainer.getKieBase(), "reply 1");

        ReleaseId releaseId2 = ks.newReleaseId( "org.kie", "test-upgrade", "1.1.0" );
        km = createAndDeployJar( ks, releaseId2, DECLARES_DRL, RULES2_DRL );

        kContainer.updateToVersion( releaseId2 );
        doFire(kContainer.getKieBase(), "reply 2");
    }

    @Test
    public void testDeclaredTypeInIncludedKieBase() {
        // DROOLS-1707
        KieServices ks = KieServices.Factory.get();

        KieModule kModule = buildKieModule("0.0.1", DECLARES_DRL, RULES1_DRL);
        KieContainer kContainer = ks.newKieContainer(kModule.getReleaseId());
        doFire(kContainer.getKieBase("kiemodulemodel"), "reply 1");

        KieModule kModule2 = buildKieModule("0.0.2", DECLARES_DRL, RULES2_DRL);
        kContainer.updateToVersion(kModule2.getReleaseId());
        doFire(kContainer.getKieBase("kiemodulemodel"), "reply 2");
    }

    private KieModule buildKieModule(String version, String declares, String rules) {
        KieServices ks = KieServices.Factory.get();
        KieFileSystem kfs = ks.newKieFileSystem();

        ReleaseId rid = ks.newReleaseId("org.drools", "kiemodulemodel-example", version);
        kfs.generateAndWritePomXML(rid);

        KieModuleModel kModuleModel = ks.newKieModuleModel();
        kModuleModel.newKieBaseModel("kiemodulemodel")
                    .addPackage("kiemodulemodel")
                    .addInclude("kiedeclare");
        kModuleModel.newKieBaseModel("kiedeclare")
                    .addPackage("kiedeclare");

        kfs.writeKModuleXML(kModuleModel.toXML());
        kfs.write("src/main/resources/kiedeclare/declares.drl", declares);
        kfs.write("src/main/resources/kiemodulemodel/rules.drl", rules);

        KieBuilder kb = ks.newKieBuilder(kfs).buildAll();
        return kb.getKieModule();
    }

    private void doFire(KieBase kbase, String reply) {
        FactType ftype = kbase.getFactType("org.drools.example.api.kiedeclare", "Message");

        KieSession kSession = kbase.newKieSession();

        kSession.insert(createMessage(ftype, "Dave", "What's the problem?"));
        assertEquals(1, kSession.fireAllRules());
        assertEquals(1, kSession.getObjects().size());

        Object fact = kSession.getObjects().iterator().next();
        assertEquals("HAL", ftype.get(fact, "name"));
        assertEquals(reply, ftype.get(fact, "text"));
        kSession.dispose();
    }

    private Object createMessage(FactType ftype, String name, String text) {
        Object fact = null;
        try {
            fact = ftype.newInstance();
        } catch (Exception e) {
            throw new RuntimeException( e );
        }

        ftype.set(fact, "name", name);
        ftype.set(fact, "text", text);

        assertEquals(name, ftype.get(fact, "name"));
        assertEquals(text, ftype.get(fact, "text"));

        return fact;
    }

    @Test
    public void testRemoveAndReaddJavaClass() {
        // DROOLS-1704
        String javaSource = "package org.drools.test;\n" +
                            "public class Person { }\n";

        KieServices ks = KieServices.Factory.get();

        ReleaseId releaseId1 = ks.newReleaseId( "org.kie", "test-upgrade", "1.0.0" );
        ReleaseId releaseId2 = ks.newReleaseId( "org.kie", "test-upgrade", "1.1.0" );
        ReleaseId releaseId3 = ks.newReleaseId( "org.kie", "test-upgrade", "1.2.0" );

        KieFileSystem kfs = ks.newKieFileSystem();
        kfs.generateAndWritePomXML(releaseId1);
        KieModuleModel kModuleModel = ks.newKieModuleModel();
        kfs.writeKModuleXML(kModuleModel.toXML());
        kfs.write("src/main/java/org/drools/test/Person.java", javaSource);
        ks.newKieBuilder(kfs).buildAll();

        KieContainer kContainer = ks.newKieContainer(releaseId1);
        try {
            Class.forName("org.drools.test.Person", true, kContainer.getClassLoader());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException( e );
        }
        kContainer.getKieBase();

        kfs = ks.newKieFileSystem();
        kfs.generateAndWritePomXML(releaseId2);
        kModuleModel = ks.newKieModuleModel();
        kfs.writeKModuleXML(kModuleModel.toXML());
        ks.newKieBuilder(kfs).buildAll();

        kContainer.updateToVersion(releaseId2);
        kContainer.getKieBase();

        kfs = ks.newKieFileSystem();
        kfs.generateAndWritePomXML(releaseId3);
        kModuleModel = ks.newKieModuleModel();
        kfs.writeKModuleXML(kModuleModel.toXML());
        kfs.write("src/main/java/org/drools/test/Person.java", javaSource);
        ks.newKieBuilder(kfs).buildAll();

        kContainer.updateToVersion(releaseId3);
    }

    @Test
    public void testChangedPackage() {
        // DROOLS-1742
        String drl1 = "package org.a\n" +
                "rule \"RG_1\"\n" +
                "    when\n" +
                "        Boolean()\n" +
                "        Integer()\n" +
                "    then\n" +
                "        System.out.println(\"RG_1\");" +
                "end\n";

        String drl2 = "package org.b\n" +
                "rule \"RG_2\"\n" +
                "    when\n" +
                "        Boolean()\n" +
                "        String()\n" +
                "    then\n" +
                "        System.out.println(\"RG_2\");" +
                "end\n";

        KieServices ks = KieServices.Factory.get();
        ReleaseId releaseId1 = ks.newReleaseId( "org.kie", "test-upgrade", "1.0.0" );
        ReleaseId releaseId2 = ks.newReleaseId( "org.kie", "test-upgrade", "1.1.0" );

        createAndDeployJar(ks, releaseId1, drl2, drl1);
        KieContainer kieContainer = ks.newKieContainer(releaseId1);
        KieSession kieSession = kieContainer.newKieSession();

        kieSession.insert("test");
        assertEquals(0, kieSession.fireAllRules());

        createAndDeployJar( ks, releaseId2, drl1);
        kieContainer.updateToVersion(releaseId2);

        assertEquals(0, kieSession.fireAllRules());
    }

    @Test
    public void testSegmentSplitAfterMerge() throws Exception {
        String drl1A = "package org.hightea.a\n" +
                "rule \"RG_1\"\n" +
                "    when\n" +
                "        Boolean()\n" +
                "        String()\n" +
                "    then\n" +
                "end" +
                "\n" ;

        String drl2A = "package org.hightea.b\n" +
                "rule \"RG_2\"\n" +
                "    when\n" +
                "        Boolean()\n" +
                "        Integer()\n" +
                "    then\n" +
                "end\n";

        String drl2B = "package org.hightea.b\n" +
                "rule \"RG_2\"\n" +
                "    when\n" +
                "        Boolean()\n" +
                "        Integer()\n" +
                "    then\n" +
                "		 //Simple comment to mark the rule as modified\n" +
                "end\n";

        KieServices ks = KieServices.Factory.get();
        ReleaseId releaseId1 = ks.newReleaseId( "org.kie", "test-upgrade", "1.0.0" );
        ReleaseId releaseId2 = ks.newReleaseId( "org.kie", "test-upgrade", "1.1.0" );

        createAndDeployJar( ks, releaseId1, drl1A, drl2A );
        KieContainer kieContainer = ks.newKieContainer(releaseId1);
        KieSession kieSession = kieContainer.newKieSession();

        kieSession.insert("A Test String");
        assertEquals(0, kieSession.fireAllRules());

        createAndDeployJar( ks, releaseId2, drl1A, drl2B );
        kieContainer.updateToVersion(releaseId2);

        assertEquals(0, kieSession.fireAllRules());
    }

    @Test
    public void testAddFieldToDeclaredType() throws Exception {
        // DROOLS-2197
        String declares1 = "declare Address\n" +
                "   streetName : String\n" +
                "   city : String\n" +
                "end";

        String declares2 = "declare Address\n" +
                "   streetName : String\n" +
                "   city : String\n" +
                "   flg: String\n" +
                "end";

        String rules1 = "rule R when\n" +
                "    a : Address( city == \"Antibes\" )\n" +
                "then\n" +
                "    a.setStreetName(\"Av. Jean Medecin\");\n" +
                "end";

        String rules2 = "rule R when\n" +
                "    a : Address( city == \" Paris \", flg == \"yes\" )\n" +
                "then\n" +
                "    a.setStreetName(\" Champs Elisees \");\n" +
                "end";

        KieServices ks = KieServices.Factory.get();
        ReleaseId releaseId1 = ks.newReleaseId( "org.kie", "test-upgrade", "1.0.0" );
        ReleaseId releaseId2 = ks.newReleaseId( "org.kie", "test-upgrade", "1.1.0" );

        createAndDeployJar( ks, releaseId1, declares1, rules1 );
        KieContainer kieContainer = ks.newKieContainer(releaseId1);
        KieSession kieSession = kieContainer.newKieSession();

        assertEquals(0, kieSession.fireAllRules());

        createAndDeployJar( ks, releaseId2, declares2, rules2 );
        kieContainer.updateToVersion(releaseId2);

        assertEquals(0, kieSession.fireAllRules());
    }

    @Test
    public void testIncremenatalCompilationAddingFieldToDeclaredType() {
        // DROOLS-2197
        String declares1 = "declare Address\n" +
                "   streetName : String\n" +
                "   city : String\n" +
                "end";

        String declares2 = "declare Address\n" +
                "   streetName : String\n" +
                "   city : String\n" +
                "   flg: String\n" +
                "end";

        String rules1 = "rule R when\n" +
                "    a : Address( city == \"Antibes\" )\n" +
                "then\n" +
                "    a.setStreetName(\"Av. Jean Medecin\");\n" +
                "end";

        String rules2 = "rule R when\n" +
                "    a : Address( city == \" Paris \" )\n" +
                "then\n" +
                "    a.setStreetName(\" Champs Elisees \");\n" +
                "end";

        KieServices ks = KieServices.Factory.get();
        KieFileSystem kfs = ks.newKieFileSystem();
        ReleaseId id = ks.newReleaseId( "org.test", "foo", "1.0-SNAPSHOT" );

        KieBuilder kieBuilder = ks.newKieBuilder( kfs );

        kfs.generateAndWritePomXML( id );
        kfs.write( ks.getResources()
                .newReaderResource( new StringReader( declares1 ) )
                .setResourceType( ResourceType.DRL )
                .setSourcePath( "declares.drl" ) );
        kfs.write( ks.getResources()
                .newReaderResource( new StringReader( rules1 ) )
                .setResourceType( ResourceType.DRL )
                .setSourcePath( "rules.drl" ) );

        kieBuilder.buildAll();

        KieContainer kc = ks.newKieContainer( id );
        KieSession ksession = kc.newKieSession();
        ksession.fireAllRules();

        kfs.write( ks.getResources()
                .newReaderResource( new StringReader( declares2 ) )
                .setResourceType( ResourceType.DRL )
                .setSourcePath( "declares.drl" ) );
        kfs.write( ks.getResources()
                .newReaderResource( new StringReader( rules2 ) )
                .setResourceType( ResourceType.DRL )
                .setSourcePath( "rules.drl" ) );

        IncrementalResults results = ( (InternalKieBuilder) kieBuilder ).incrementalBuild();
        System.out.println( results.getAddedMessages() );
        assertEquals( 0, results.getAddedMessages().size() );

        Results updateResults = kc.updateToVersion( id );
        assertEquals( 0, updateResults.getMessages().size() );
    }

    @Test
    public void testUnchangedAccumulate() throws Exception {
        // DROOLS-2194
        String drl1 =
                "import java.util.*;\n" +
                "rule B\n" +
                "when\n" +
                "    $eventCodeDistinctMois : Integer( intValue>0 ) from accumulate ( String( $id : this ),\n" +
                "                                                                init( Set set = new HashSet(); ),\n" +
                "                                                                action( set.add($id); ),\n" +
                "                                                                reverse( set.remove($id); ),\n" +
                "                                                                result( set.size()) )\n" +
                "then\n" +
                "end";

        String drl2 =  "rule C when then end\n";

        KieServices ks = KieServices.Factory.get();

        ReleaseId releaseId1 = ks.newReleaseId( "org.kie", "test-upgrade", "1.0.0" );
        KieModule km = createAndDeployJar( ks, releaseId1, drl1, drl2 );

        KieContainer kc = ks.newKieContainer(releaseId1);
        KieSession ksession = kc.newKieSession();

        FactHandle fh = ksession.insert("1");
        ksession.fireAllRules();

        ReleaseId releaseId2 = ks.newReleaseId( "org.kie", "test-upgrade", "1.1.0" );
        createAndDeployJar( ks, releaseId2, drl1 );
        kc.updateToVersion(releaseId2);

        ReleaseId releaseId3 = ks.newReleaseId( "org.kie", "test-upgrade", "1.2.0" );
        createAndDeployJar( ks, releaseId3, drl1, drl2 );
        kc.updateToVersion(releaseId3);

        ksession.delete( fh );
        ksession.fireAllRules();

        ksession.insert("2");
        ksession.fireAllRules();
    }

    @Test
    public void testGlobalRemovedFromOneDrl() throws Exception {
        // RHDM-311
        String drlAWithGlobal = "package org.x.a\nglobal Boolean globalBool\n";
        String drlANoGlobal = "package org.x.a\n";
        String drlBWithGlobal = "package org.x.b\nglobal Boolean globalBool\n";
        String drlBNoGlobal = "package org.x.b\n";

        KieServices ks = KieServices.Factory.get();

        ReleaseId releaseId1 = ks.newReleaseId( "org.kie", "test-upgrade", "1.0.0" );
        KieModule km = createAndDeployJar( ks, releaseId1, drlAWithGlobal, drlBWithGlobal );

        KieContainer kc = ks.newKieContainer( releaseId1 );
        KieSession ksession = kc.newKieSession();

        ksession.setGlobal( "globalBool", Boolean.FALSE );

        ReleaseId releaseId2 = ks.newReleaseId( "org.kie", "test-upgrade", "1.1.0" );
        createAndDeployJar( ks, releaseId2, drlANoGlobal, drlBWithGlobal );
        kc.updateToVersion( releaseId2 );

        ksession.setGlobal( "globalBool", Boolean.TRUE );

        ReleaseId releaseId3 = ks.newReleaseId( "org.kie", "test-upgrade", "1.2.0" );
        createAndDeployJar( ks, releaseId3, drlANoGlobal, drlBNoGlobal );
        kc.updateToVersion( releaseId3 );

        try {
            ksession.setGlobal( "globalBool", Boolean.TRUE );
            fail( "the global should be no longer present" );
        } catch (Exception e) {
            // expected
        }
    }

    @Test
    public void testGlobalRemovedAndAdded() throws Exception {
        // RHDM-311
        String drlAWithGlobal = "package org.x.a\nglobal Boolean globalBool\n";
        String drlANoGlobal = "package org.x.a\n";
        String drlBWithGlobal = "package org.x.b\nglobal Boolean globalBool\n";
        String drlBNoGlobal = "package org.x.b\n";

        KieServices ks = KieServices.Factory.get();

        ReleaseId releaseId1 = ks.newReleaseId( "org.kie", "test-upgrade", "1.0.0" );
        KieModule km = createAndDeployJar( ks, releaseId1, drlAWithGlobal, drlBNoGlobal );

        KieContainer kc = ks.newKieContainer( releaseId1 );
        KieSession ksession = kc.newKieSession();

        ksession.setGlobal( "globalBool", Boolean.FALSE );

        ReleaseId releaseId2 = ks.newReleaseId( "org.kie", "test-upgrade", "1.1.0" );
        createAndDeployJar( ks, releaseId2, drlANoGlobal, drlBWithGlobal );
        kc.updateToVersion( releaseId2 );

        ksession.setGlobal( "globalBool", Boolean.TRUE );
    }

    @Test
    public void testRuleRemovalAndEval() throws Exception {
        // DROOLS-2276
        String drl1 = "package org.drools.compiler\n" +
                "rule R1 when\n" +
                "   $m : Message()\n" +
                "   eval($m != null)\n" +
                "then\n" +
                "   System.out.println( \"Hello R1\" );\n" +
                "end\n";

        String drl2 = "package org.drools.compiler\n" +
                "rule R2 when\n" +
                "   $m : Message()\n" +
                "   eval($m != null)\n" +
                "then\n" +
                "   System.out.println( \"Hello R2\" );\n" +
                "end\n";

        String drl3 = "package org.drools.compiler\n" +
                "rule R3 when\n" +
                "   $m : Message()\n" +
                "   eval($m != null)\n" +
                "then\n" +
                "   System.out.println( \"Hello R3\" );\n" +
                "end\n";

        KieServices ks = KieServices.Factory.get();

        KieFileSystem kfs1 = ks.newKieFileSystem();
        kfs1.write("src/main/resources/rules/Sample1.drl", drl1);
        ReleaseId releaseId1 = ks.newReleaseId("com.sample", "my-sample-a", "1.0.0");
        kfs1.generateAndWritePomXML(releaseId1);
        ks.newKieBuilder( kfs1 ).buildAll();

        KieFileSystem kfs2 = ks.newKieFileSystem();
        kfs2.write("src/main/resources/rules/Sample2.drl", drl2);
        ReleaseId releaseId2 = ks.newReleaseId("com.sample", "my-sample-a", "2.0.0");
        kfs2.generateAndWritePomXML(releaseId2);
        ks.newKieBuilder( kfs2 ).buildAll();

        KieFileSystem kfs3 = ks.newKieFileSystem();
        kfs3.write("src/main/resources/rules/Sample3.drl", drl3);
        ReleaseId releaseId3 = ks.newReleaseId("com.sample", "my-sample-a", "3.0.0");
        kfs3.generateAndWritePomXML(releaseId3);
        ks.newKieBuilder( kfs3 ).buildAll();

        // Create a session and fire rules
        KieContainer kc = ks.newKieContainer( releaseId1 );
        KieSession ksession = kc.newKieSession();
        ksession.insert( new Message( "Hello World" ) );
        assertEquals( 1, ksession.fireAllRules() );

        kc.updateToVersion( releaseId2 );
        kc.updateToVersion( releaseId3 );
        assertEquals( 1, ksession.fireAllRules() );
    }

    @Role(Role.Type.EVENT)
    public static class BooleanEvent implements Serializable {
        private boolean enabled = false;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled( boolean enabled ) {
            this.enabled = enabled;
        }
    }

    @Test
    public void testAddRuleWithSlidingWindows() throws Exception {
        // DROOLS-2292
        String drl1 = "package org.drools.compiler\n" +
                "import " + List.class.getCanonicalName() + "\n" +
                "import " + BooleanEvent.class.getCanonicalName() + "\n" +
                "rule R1 when\n" +
                "    $e : BooleanEvent(!enabled)\n" +
                "    List(size >= 1) from collect ( BooleanEvent(!enabled) over window:time(1) )\n" +
                "    $toEdit : List() from collect( BooleanEvent(!enabled) over window:time(2) )\n" +
                "then\n" +
                "    modify( (BooleanEvent)$toEdit.get(0) ){ setEnabled( true ) }\n" +
                "end\n";

        KieServices ks = KieServices.Factory.get();

        KieModuleModel kproj = ks.newKieModuleModel();
        KieBaseModel kieBaseModel1 = kproj.newKieBaseModel( "KBase1" ).setDefault( true )
                .setEventProcessingMode( EventProcessingOption.STREAM );
        KieSessionModel ksession1 = kieBaseModel1.newKieSessionModel( "KSession1" ).setDefault( true )
                .setType( KieSessionModel.KieSessionType.STATEFUL )
                .setClockType( ClockTypeOption.get( ClockType.PSEUDO_CLOCK.getId() ) );

        ReleaseId releaseId1 = ks.newReleaseId( "org.kie", "test-upgrade", "1.0.0" );
        deployJar( ks, createKJar( ks, kproj, releaseId1, null ) );
        ReleaseId releaseId2 = ks.newReleaseId( "org.kie", "test-upgrade", "2.0.0" );
        deployJar( ks, createKJar( ks, kproj, releaseId2, null, drl1 ) );

        KieContainer kc = ks.newKieContainer( releaseId1 );
        KieSession kieSession = kc.newKieSession();

        kieSession.insert(new BooleanEvent());
        kieSession.fireAllRules();

        kc.updateToVersion( releaseId2 );

        kieSession.fireAllRules();

        KieMarshallers marshallers = ks.getMarshallers();
        Marshaller marshaller = marshallers.newMarshaller(kieSession.getKieBase());

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        marshaller.marshall(outputStream, kieSession);
    }

    @Test
    public void testGetFactTypeOnIncrementalUpdateWithNestedFacts() throws Exception {
        // DROOLS-2385
        String drl1 =
                "package org.drools.example.api.kiemodulemodel\n" +
                        "declare MyNestedFact\n" +
                        "   x : String\n" +
                        "end\n" +
                        "declare Message\n" +
                        "   text : String\n" +
                        "   nested : MyNestedFact\n" +
                        "end\n" +
                        "\n" +
                        "rule R when Message(text == \"What's the problem?\") then end\n";

        String drl2 =
                "package org.drools.example.api.kiemodulemodel\n" +
                        "declare MyNestedFact\n" +
                        "   x : String\n" +
                        "   y : int\n" +
                        "end\n" +
                        "declare Message\n" +
                        "   text : String\n" +
                        "   nested : MyNestedFact\n" +
                        "end\n" +
                        "\n" +
                        "rule R when Message(text == \"What's the problem?\", nested.y == 42) then end\n";

        KieServices ks = KieServices.Factory.get();

        ReleaseId releaseId1 = ks.newReleaseId( "org.kie", "test-upgrade", "1.1.1" );
        KieModule km = createAndDeployJar( ks, releaseId1, drl1 );

        KieContainer kc = ks.newKieContainer(km.getReleaseId());
        KieBase kbase = kc.getKieBase();

        FactType ftype = kbase.getFactType("org.drools.example.api.kiemodulemodel", "Message");
        assertNotNull( ftype.getField( "text" ) );

        Object fact = ftype.newInstance();
        ftype.set(fact, "text", "What's the problem?");

        FactType nestedftype = kbase.getFactType("org.drools.example.api.kiemodulemodel", "MyNestedFact");
        assertNotNull( nestedftype.getField( "x" ) );

        Object nestedfact = nestedftype.newInstance();
        ftype.set(fact, "nested", nestedfact);

        ReleaseId releaseId2 = ks.newReleaseId("org.kie", "test-upgrade", "1.1.2");
        km = createAndDeployJar( ks, releaseId2, drl2 );
        kc.updateToVersion(releaseId2);

        kbase = kc.getKieBase();
        ftype = kbase.getFactType("org.drools.example.api.kiemodulemodel", "Message");
        assertNotNull( ftype.getField( "text" ) );

        fact = ftype.newInstance();
        ftype.set(fact, "text", "What's the problem?");

        nestedftype = kbase.getFactType("org.drools.example.api.kiemodulemodel", "MyNestedFact");
        assertNotNull( nestedftype.getField( "x" ) );
        assertNotNull( nestedftype.getField( "y" ) );

        nestedfact = nestedftype.newInstance();
        nestedftype.set(nestedfact, "y", 42);

        ftype.set(fact, "nested", nestedfact);
    }
}
