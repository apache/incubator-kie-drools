/*
 * Copyright 2015 JBoss Inc
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

package org.drools.compiler.integrationtests;

import org.drools.compiler.CommonTestMethodBase;
import org.drools.compiler.Message;
import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieModule;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.Results;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.conf.ClockTypeOption;
import org.kie.internal.io.ResourceFactory;

public class KieBuilderTest extends CommonTestMethodBase {

    @Test
    public void testResourceInclusion() {
        String drl1 = "package org.drools.compiler\n" +
                "rule R1 when\n" +
                "   $m : Message()\n" +
                "then\n" +
                "end\n";

        String drl2 = "package org.drools.compiler\n" +
                "rule R2 when\n" +
                "   $m : Message( message == \"Hi Universe\" )\n" +
                "then\n" +
                "end\n";

        String drl3 = "package org.drools.compiler\n" +
                "rule R3 when\n" +
                "   $m : Message( message == \"Hello World\" )\n" +
                "then\n" +
                "end\n";

        String drl4 = "package org.drools.compiler\n" +
                "rule R4 when\n" +
                "   $m : Message( message == \"Hello Earth\" )\n" +
                "then\n" +
                "end\n";

        String kmodule = "<kmodule xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" \n" +
                "         xmlns=\"http://jboss.org/kie/6.0.0/kmodule\">\n" +
                "  <kbase name=\"kbase1\" default=\"true\" eventProcessingMode=\"stream\" equalsBehavior=\"identity\" scope=\"javax.enterprise.context.ApplicationScoped\">\n" +
                "    <ksession name=\"ksession1\" type=\"stateful\" default=\"true\" clockType=\"realtime\" scope=\"javax.enterprise.context.ApplicationScoped\"/>\n" +
                "  </kbase>\n" +
                "</kmodule>";

        KieServices ks = KieServices.Factory.get();

        // Create an in-memory jar for version 1.0.0
        ReleaseId releaseId1 = ks.newReleaseId( "org.kie", "test-kie-builder", "1.0.0" );
        Resource r1 = ResourceFactory.newByteArrayResource( drl1.getBytes() ).setResourceType( ResourceType.DRL ).setSourcePath( "kbase1/drl1.drl" );
        Resource r2 = ResourceFactory.newByteArrayResource( drl2.getBytes() ).setResourceType( ResourceType.GDRL ).setSourcePath( "kbase1/drl2.gdrl" );
        Resource r3 = ResourceFactory.newByteArrayResource( drl3.getBytes() ).setResourceType( ResourceType.RDRL ).setSourcePath( "kbase1/drl3.rdrl" );
        Resource r4 = ResourceFactory.newByteArrayResource( drl4.getBytes() ).setResourceType( ResourceType.TDRL ).setSourcePath( "kbase1/drl4.tdrl" );
        KieModule km = createAndDeployJar( ks,
                                           kmodule,
                                           releaseId1,
                                           r1,
                                           r2,
                                           r3,
                                           r4 );

        InternalKieModule ikm = (InternalKieModule) km;
        assertNotNull( ikm.getResource( r1.getSourcePath() ) );
        assertNotNull( ikm.getResource( r2.getSourcePath() ) );
        assertNotNull( ikm.getResource( r3.getSourcePath() ) );
        assertNotNull( ikm.getResource( r4.getSourcePath() ) );

        // Create a session and fire rules
        KieContainer kc = ks.newKieContainer( km.getReleaseId() );
        KieSession ksession = kc.newKieSession();
        ksession.insert( new Message( "Hello World" ) );
        assertEquals( 2, ksession.fireAllRules() );
        ksession.dispose();
    }

    @Test
    public void testGetKieBaseAfterKieSessionCreation() {
        final String KBASE_NAME = "kieBase";
        final String KSESSION_NAME = "kieSession";

        String drl = "declare TestEvent\n" +
                "    @role( event )\n" +
                "    name : String\n" +
                "end\n" +
                "\n" +
                "declare window DeclaredTimeWindow\n" +
                "    TestEvent ( name == \"timeDec\" ) over window:time( 50ms ) from entry-point EventStream\n" +
                "end";

        KieServices ks = KieServices.Factory.get();
        KieFileSystem kfs = ks.newKieFileSystem();

        kfs.write( "src/main/resources/window.drl", drl );

        KieModuleModel kmoduleModel = ks.newKieModuleModel();
        kmoduleModel.newKieBaseModel( KBASE_NAME )
                .addPackage( "*" )
                .newKieSessionModel( KSESSION_NAME )
                .setDefault( true )
                .setClockType( ClockTypeOption.get( "pseudo" ) );

        kfs.writeKModuleXML( kmoduleModel.toXML() );

        KieBuilder builder = ks.newKieBuilder( kfs ).buildAll();
        assertEquals( 0, builder.getResults().getMessages().size() );
        ks.getRepository().addKieModule( builder.getKieModule() );

        KieSession kieSession = ks.newKieContainer( ks.getRepository().getDefaultReleaseId() ).newKieSession( KSESSION_NAME );
        assertNotNull( kieSession );

        KieBase kieBase = ks.newKieContainer( ks.getRepository().getDefaultReleaseId() ).getKieBase( KBASE_NAME );
        assertNotNull( kieBase );
    }

    @Test
    public void testReportKBuilderErrorWhenUsingAJavaClassWithNoPkg() throws Exception {
        // BZ-995018
        String java = "public class JavaClass { }\n";
        KieServices ks = KieServices.Factory.get();

        KieFileSystem kfs = ks.newKieFileSystem()
                .write( "src/main/java/JavaClass.java", java );

        Results results = ks.newKieBuilder( kfs ).buildAll().getResults();

        System.out.println( results.getMessages() );

        assertEquals( 1, results.getMessages().size() );
    }

    @Test
    public void testKieBuilderWithDotFiles() {
        // BZ-1044409
        final String KBASE_NAME = "kieBase";
        final String KSESSION_NAME = "kieSession";

        //This can be any DRL, or any file recognised by drools-compiler
        String drl = "declare TestEvent\n" +
                "  name : String\n" +
                "end\n";

        String dotDrl = "Meta-data used by kie-wb";

        KieServices ks = KieServices.Factory.get();
        KieFileSystem kfs = ks.newKieFileSystem();

        kfs.write( "src/main/resources/drlFile.drl", drl );
        kfs.write( "src/main/resources/.drlFile.drl", dotDrl );

        KieModuleModel kmoduleModel = ks.newKieModuleModel();
        kmoduleModel.newKieBaseModel( KBASE_NAME )
                .addPackage( "*" )
                .newKieSessionModel( KSESSION_NAME )
                .setDefault( true );

        kfs.writeKModuleXML( kmoduleModel.toXML() );

        KieBuilder builder = ks.newKieBuilder( kfs ).buildAll();
        for ( org.kie.api.builder.Message m : builder.getResults().getMessages() ) {
            System.out.println( m );
        }
        assertEquals( 0, builder.getResults().getMessages().size() );

        ks.getRepository().addKieModule( builder.getKieModule() );

        KieSession kieSession = ks.newKieContainer( ks.getRepository().getDefaultReleaseId() ).newKieSession( KSESSION_NAME );
        assertNotNull( kieSession );

        KieBase kieBase = ks.newKieContainer( ks.getRepository().getDefaultReleaseId() ).getKieBase( KBASE_NAME );
        assertNotNull( kieBase );
    }

}
