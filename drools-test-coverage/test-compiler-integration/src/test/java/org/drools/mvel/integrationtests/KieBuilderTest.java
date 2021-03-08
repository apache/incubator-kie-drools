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

package org.drools.mvel.integrationtests;

import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

import org.assertj.core.api.Assertions;
import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.drools.compiler.kproject.models.KieModuleModelImpl;
import org.drools.mvel.compiler.Message;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieUtil;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
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
import org.kie.api.runtime.Channel;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.StatelessKieSession;
import org.kie.api.runtime.conf.ClockTypeOption;
import org.kie.internal.builder.InternalKieBuilder;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@RunWith(Parameterized.class)
public class KieBuilderTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public KieBuilderTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
     // TODO: EM failed with some tests. File JIRAs
        return TestParametersUtil.getKieBaseCloudConfigurations(false);
    }

    @Test
    public void testResourceInclusion() {
        final String drl1 = "package org.drools.mvel.compiler\n" +
                "rule R1 when\n" +
                "   $m : Message()\n" +
                "then\n" +
                "end\n";

        final String drl2 = "package org.drools.mvel.compiler\n" +
                "rule R2 when\n" +
                "   $m : Message( message == \"Hi Universe\" )\n" +
                "then\n" +
                "end\n";

        final String drl3 = "package org.drools.mvel.compiler\n" +
                "rule R3 when\n" +
                "   $m : Message( message == \"Hello World\" )\n" +
                "then\n" +
                "end\n";

        final String drl4 = "package org.drools.mvel.compiler\n" +
                "rule R4 when\n" +
                "   $m : Message( message == \"Hello Earth\" )\n" +
                "then\n" +
                "end\n";

        final String kmodule = "<kmodule xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" \n" +
                "         xmlns=\"http://www.drools.org/xsd/kmodule\">\n" +
                "  <kbase name=\"kbase1\" default=\"true\" eventProcessingMode=\"stream\" equalsBehavior=\"identity\" scope=\"javax.enterprise.context.ApplicationScoped\">\n" +
                "    <ksession name=\"ksession1\" type=\"stateful\" default=\"true\" clockType=\"realtime\" scope=\"javax.enterprise.context.ApplicationScoped\"/>\n" +
                "  </kbase>\n" +
                "</kmodule>";

        final KieServices ks = KieServices.Factory.get();

        // Create an in-memory jar for version 1.0.0
        final ReleaseId releaseId1 = ks.newReleaseId( "org.kie", "test-kie-builder", "1.0.0" );
        final Resource r1 = ResourceFactory.newByteArrayResource( drl1.getBytes() ).setResourceType( ResourceType.DRL ).setSourcePath( "kbase1/drl1.drl" );
        final Resource r2 = ResourceFactory.newByteArrayResource( drl2.getBytes() ).setResourceType( ResourceType.GDRL ).setSourcePath( "kbase1/drl2.gdrl" );
        final Resource r3 = ResourceFactory.newByteArrayResource( drl3.getBytes() ).setResourceType( ResourceType.RDRL ).setSourcePath( "kbase1/drl3.rdrl" );
        final Resource r4 = ResourceFactory.newByteArrayResource( drl4.getBytes() ).setResourceType( ResourceType.TDRL ).setSourcePath( "kbase1/drl4.tdrl" );

        KieModule km = KieUtil.buildAndInstallKieModuleIntoRepo(kieBaseTestConfiguration, releaseId1, KieModuleModelImpl.fromXML(kmodule), r1, r2, r3, r4);

        final InternalKieModule ikm = (InternalKieModule) km;
        assertNotNull( ikm.getResource( r1.getSourcePath() ) );
        assertNotNull( ikm.getResource( r2.getSourcePath() ) );
        assertNotNull( ikm.getResource( r3.getSourcePath() ) );
        assertNotNull( ikm.getResource( r4.getSourcePath() ) );

        // Create a session and fire rules
        final KieContainer kc = ks.newKieContainer( km.getReleaseId() );
        final KieSession ksession = kc.newKieSession();
        ksession.insert( new Message( "Hello World" ) );
        assertEquals( 2, ksession.fireAllRules() );
        ksession.dispose();
    }

    @Test
    public void testValidXsdTargetNamespace() {
        final String drl1 = "package org.drools.mvel.compiler\n" +
                "rule R1 when\n" +
                "   $m : Message()\n" +
                "then\n" +
                "end\n";

        final String kmodule = "<kmodule xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" \n" +
                "         xmlns=\"http://www.drools.org/xsd/kmodule\">\n" +
                "  <kbase name=\"kbase1\">\n" +
                "    <ksession name=\"ksession1\" default=\"true\"/>\n" +
                "  </kbase>\n" +
                "</kmodule>";

        final KieServices ks = KieServices.Factory.get();

        final ReleaseId releaseId1 = ks.newReleaseId( "org.kie", "test-kie-builder", "1.0.0" );
        final Resource r1 = ResourceFactory.newByteArrayResource( drl1.getBytes() ).setResourceType( ResourceType.DRL ).setSourcePath( "kbase1/drl1.drl" );

        KieModule km = KieUtil.buildAndInstallKieModuleIntoRepo(kieBaseTestConfiguration, releaseId1, KieModuleModelImpl.fromXML(kmodule), r1);

        ks.newKieContainer( km.getReleaseId() );
    }

    @Test
    public void testInvalidXsdTargetNamespace() {
        final String drl1 = "package org.drools.compiler\n" +
                "rule R1 when\n" +
                "   $m : Message()\n" +
                "then\n" +
                "end\n";

        final String kmodule = "<kmodule xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" \n" +
                "         xmlns=\"http://www.drools.org/xsd/doesNotExist\">\n" +
                "  <kbase name=\"kbase1\">\n" +
                "    <ksession name=\"ksession1\" default=\"true\"/>\n" +
                "  </kbase>\n" +
                "</kmodule>";

        final KieServices ks = KieServices.Factory.get();

        // Create an in-memory jar for version 1.0.0
        final ReleaseId releaseId1 = ks.newReleaseId( "org.kie", "test-kie-builder", "1.0.0" );
        final Resource r1 = ResourceFactory.newByteArrayResource( drl1.getBytes() ).setResourceType( ResourceType.DRL ).setSourcePath( "kbase1/drl1.drl" );

        Assertions.assertThatThrownBy(() -> KieUtil.buildAndInstallKieModuleIntoRepo(kieBaseTestConfiguration, releaseId1, KieModuleModelImpl.fromXML(kmodule), r1))
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("XSD validation failed");
    }

    @Test
    public void testOldXsdTargetNamespace() {
        final String drl1 = "package org.drools.mvel.compiler\n" +
                "rule R1 when\n" +
                "   $m : Message()\n" +
                "then\n" +
                "end\n";

        final String kmodule = "<kmodule xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" \n" +
                "         xmlns=\"http://jboss.org/kie/6.0.0/kmodule\">\n" +
                "  <kbase name=\"kbase1\">\n" +
                "    <ksession name=\"ksession1\" default=\"true\"/>\n" +
                "  </kbase>\n" +
                "</kmodule>";

        final KieServices ks = KieServices.Factory.get();

        // Create an in-memory jar for version 1.0.0
        final ReleaseId releaseId1 = ks.newReleaseId( "org.kie", "test-kie-builder", "1.0.0" );
        final Resource r1 = ResourceFactory.newByteArrayResource( drl1.getBytes() ).setResourceType( ResourceType.DRL ).setSourcePath( "kbase1/drl1.drl" );

        KieModule km = KieUtil.buildAndInstallKieModuleIntoRepo(kieBaseTestConfiguration, releaseId1, KieModuleModelImpl.fromXML(kmodule), r1);

        ks.newKieContainer( km.getReleaseId() );
    }

    @Test
    public void testGetKieBaseAfterKieSessionCreation() {
        final String KBASE_NAME = "kieBase";
        final String KSESSION_NAME = "kieSession";

        final String drl = "declare TestEvent\n" +
                "    @role( event )\n" +
                "    name : String\n" +
                "end\n" +
                "\n" +
                "declare window DeclaredTimeWindow\n" +
                "    TestEvent ( name == \"timeDec\" ) over window:time( 50ms ) from entry-point EventStream\n" +
                "end";

        final KieServices ks = KieServices.Factory.get();
        final KieFileSystem kfs = ks.newKieFileSystem();

        kfs.write( "src/main/resources/window.drl", drl );

        final KieModuleModel kmoduleModel = ks.newKieModuleModel();
        kmoduleModel.newKieBaseModel( KBASE_NAME )
                .addPackage( "*" )
                .newKieSessionModel( KSESSION_NAME )
                .setDefault( true )
                .setClockType( ClockTypeOption.PSEUDO );

        kfs.writeKModuleXML( kmoduleModel.toXML() );

        final KieBuilder builder = KieUtil.getKieBuilderFromKieFileSystem(kieBaseTestConfiguration, kfs, false);
        assertEquals( 0, builder.getResults().getMessages().size() );
        ks.getRepository().addKieModule( builder.getKieModule() );

        final KieSession kieSession = ks.newKieContainer( ks.getRepository().getDefaultReleaseId() ).newKieSession( KSESSION_NAME );
        assertNotNull( kieSession );

        final KieBase kieBase = ks.newKieContainer( ks.getRepository().getDefaultReleaseId() ).getKieBase( KBASE_NAME );
        assertNotNull( kieBase );
    }

    @Test
    public void testReportKBuilderErrorWhenUsingAJavaClassWithNoPkg() {
        // BZ-995018
        final String java = "public class JavaClass { }\n";
        final KieServices ks = KieServices.Factory.get();

        final KieFileSystem kfs = ks.newKieFileSystem()
                .write( "src/main/java/JavaClass.java", java );

        final KieBuilder kieBuilder = KieUtil.getKieBuilderFromKieFileSystem(kieBaseTestConfiguration, kfs, false);
        Results results = kieBuilder.getResults();

        System.out.println( results.getMessages() );

        assertEquals( 1, results.getMessages().size() );
    }

    @Test
    public void testJavaSourceFileAndDrlDeploy() {
        final String java = "package org.drools.mvel.compiler;\n" +
                "public class JavaSourceMessage { }\n";
        final String drl = "package org.drools.mvel.compiler;\n" +
                "import org.drools.mvel.compiler.JavaSourceMessage;" +
                "rule R1 when\n" +
                "   $m : JavaSourceMessage()\n" +
                "then\n" +
                "end\n";

        final String kmodule = "<kmodule xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" \n" +
                "         xmlns=\"http://www.drools.org/xsd/kmodule\">\n" +
                "  <kbase name=\"kbase1\">\n" +
                "    <ksession name=\"ksession1\" default=\"true\"/>\n" +
                "  </kbase>\n" +
                "</kmodule>";

        final KieServices ks = KieServices.Factory.get();

        final ReleaseId releaseId1 = ks.newReleaseId( "org.kie", "test-kie-builder", "1.0.0" );
        final Resource javaResource = ResourceFactory.newByteArrayResource( java.getBytes() ).setResourceType( ResourceType.JAVA )
                .setSourcePath( "org/drools/mvel/compiler/JavaSourceMessage.java" );
        final Resource drlResource = ResourceFactory.newByteArrayResource( drl.getBytes() ).setResourceType( ResourceType.DRL )
                .setSourcePath( "kbase1/drl1.drl" );

        KieModule km = KieUtil.buildAndInstallKieModuleIntoRepo(kieBaseTestConfiguration, releaseId1, KieModuleModelImpl.fromXML(kmodule), javaResource, drlResource);

        final KieContainer kieContainer = ks.newKieContainer(km.getReleaseId());
        try {
            final Class<?> messageClass = kieContainer.getClassLoader().loadClass("org.drools.mvel.compiler.JavaSourceMessage");
            assertNotNull(messageClass);
        } catch (final ClassNotFoundException e) {
            throw new IllegalStateException("Loading the java class failed.", e);
        }
    }

    @Test
    public void testJavaSourceFileAndDrlDeployWithClassFilter() {
        final String allowedJava = "package org.drools.mvel.compiler;\n" +
                "public class JavaSourceMessage { }\n";
        final String filteredJava = "package org.drools.mvel.compiler;\n" +
                "public class ClassCausingClassNotFoundException { non.existing.Type foo() { return null; } }\n";
        final String drl = "package org.drools.compiler;\n" +
                "import org.drools.mvel.compiler.JavaSourceMessage;" +
                "rule R1 when\n" +
                "   $m : JavaSourceMessage()\n" +
                "then\n" +
                "end\n";

        final String kmodule = "<kmodule xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" \n" +
                "         xmlns=\"http://www.drools.org/xsd/kmodule\">\n" +
                "  <kbase name=\"kbase1\">\n" +
                "    <ksession name=\"ksession1\" default=\"true\"/>\n" +
                "  </kbase>\n" +
                "</kmodule>";

        final KieServices ks = KieServices.Factory.get();

        final ReleaseId releaseId1 = ks.newReleaseId( "org.kie", "test-kie-builder", "1.0.0" );
        final Resource allowedJavaResource = ResourceFactory.newByteArrayResource( allowedJava.getBytes() ).setResourceType( ResourceType.JAVA )
                .setSourcePath( "org/drools/mvel/compiler/JavaSourceMessage.java" );
        final Resource filteredJavaResource = ResourceFactory.newByteArrayResource( filteredJava.getBytes() ).setResourceType( ResourceType.JAVA )
                .setSourcePath( "org/drools/mvel/compiler/ClassCausingClassNotFoundException.java" );
        final Resource drlResource = ResourceFactory.newByteArrayResource( drl.getBytes() ).setResourceType( ResourceType.DRL )
                .setSourcePath( "kbase1/drl1.drl" );

        final Predicate<String> filter = fileName -> !fileName.endsWith( "org/drools/mvel/compiler/ClassCausingClassNotFoundException.java" );

        KieModule km = null;
        try {
            // KieBuilderImpl.buildAll( Predicate<String> classFilter ) only works with KieModuleKieProject. So not parameterized
            KieFileSystem kfs = KieUtil.getKieFileSystemWithKieModule(KieModuleModelImpl.fromXML(kmodule), releaseId1, allowedJavaResource, filteredJavaResource, drlResource);
            KieBuilder kieBuilder = ks.newKieBuilder(kfs);
            ((InternalKieBuilder)kieBuilder).buildAll(filter);
            km = kieBuilder.getKieModule();
            ks.getRepository().addKieModule(km);
        } catch ( final IllegalStateException ise ) {
            if ( ise.getMessage().contains( "org/drools/mvel/compiler/ClassCausingClassNotFoundException.java" ) ) {
                fail( "Build failed because source file was not filtered out." );
            } else {
                throw ise;
            }
        }

        final KieContainer kieContainer = ks.newKieContainer(km.getReleaseId());
        try {
            final Class<?> messageClass = kieContainer.getClassLoader().loadClass("org.drools.mvel.compiler.JavaSourceMessage");
            assertNotNull(messageClass);
        } catch (final ClassNotFoundException e) {
            throw new IllegalStateException("Loading the java class failed.", e);
        }
    }

    @Test
    public void testKieBuilderWithDotFiles() {
        // BZ-1044409
        final String KBASE_NAME = "kieBase";
        final String KSESSION_NAME = "kieSession";

        //This can be any DRL, or any file recognised by drools-compiler
        final String drl = "declare TestEvent\n" +
                "  name : String\n" +
                "end\n";

        final String dotDrl = "Meta-data used by kie-wb";

        final KieServices ks = KieServices.Factory.get();
        final KieFileSystem kfs = ks.newKieFileSystem();

        kfs.write( "src/main/resources/drlFile.drl", drl );
        kfs.write( "src/main/resources/.drlFile.drl", dotDrl );

        final KieModuleModel kmoduleModel = ks.newKieModuleModel();
        kmoduleModel.newKieBaseModel( KBASE_NAME )
                .addPackage( "*" )
                .newKieSessionModel( KSESSION_NAME )
                .setDefault( true );

        kfs.writeKModuleXML( kmoduleModel.toXML() );

        final KieBuilder builder = KieUtil.getKieBuilderFromKieFileSystem(kieBaseTestConfiguration, kfs, false);

        for ( final org.kie.api.builder.Message m : builder.getResults().getMessages() ) {
            System.out.println( m );
        }
        assertEquals( 0, builder.getResults().getMessages().size() );

        ks.getRepository().addKieModule( builder.getKieModule() );

        final KieSession kieSession = ks.newKieContainer( ks.getRepository().getDefaultReleaseId() ).newKieSession( KSESSION_NAME );
        assertNotNull( kieSession );

        final KieBase kieBase = ks.newKieContainer( ks.getRepository().getDefaultReleaseId() ).getKieBase( KBASE_NAME );
        assertNotNull( kieBase );
    }

    @Test
    public void testMultipleKBaseWithDrlError() {
        // RHBRMS-2651
        final String drl = "package org.drools.compiler;\n" +
                     "rule \"test\"\n" +
                     "  when\n" +
                     "    Smurf\n" +
                     "  then\n" +
                     "end";

        final KieServices ks = KieServices.Factory.get();

        final KieModuleModel kproj = ks.newKieModuleModel();
        kproj.newKieBaseModel( "kbase1" ).newKieSessionModel( "ksession1" ).setDefault( true );
        kproj.newKieBaseModel( "kbase2" ).newKieSessionModel( "ksession2" );

        final ReleaseId releaseId = ks.newReleaseId( "org.kie", "test-kie-builder", "1.0.0" );
        final KieFileSystem kfs = ks.newKieFileSystem().generateAndWritePomXML( releaseId ).writeKModuleXML( kproj.toXML() );

        final Resource drlResource = ResourceFactory.newByteArrayResource( drl.getBytes() ).setResourceType( ResourceType.DRL )
                                              .setSourcePath( "kbase1/drl1.drl" );

        kfs.write( "src/main/resources/org/drools/compiler/drl1.drl", drlResource );

        final KieBuilder kb = KieUtil.getKieBuilderFromKieFileSystem(kieBaseTestConfiguration, kfs, false);

        final List<org.kie.api.builder.Message> messages = kb.getResults().getMessages( org.kie.api.builder.Message.Level.ERROR );
        assertEquals( 4, messages.size() );

        assertTrue( messages.get(0).toString().contains( "kbase1" ) );
        assertTrue( messages.get(1).toString().contains( "kbase1" ) );
        assertTrue( messages.get(2).toString().contains( "kbase2" ) );
        assertTrue( messages.get(3).toString().contains( "kbase2" ) );
    }

    @Test
    public void testBuildWithKBaseAndKSessionWithIdenticalNames() {
        // RHBRMS-2689
        final String kmodule = "<kmodule xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" \n" +
                         "         xmlns=\"http://www.drools.org/xsd/kmodule\">\n" +
                         "  <kbase name=\"name\">\n" +
                         "    <ksession name=\"name\" default=\"true\"/>\n" +
                         "  </kbase>\n" +
                         "</kmodule>";

        checkKModule( kmodule, 0 );
    }

    @Test
    public void testBuildWithDuplicatedKSessionNames() {
        // RHBRMS-2689
        final String kmodule = "<kmodule xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" \n" +
                         "         xmlns=\"http://www.drools.org/xsd/kmodule\">\n" +
                         "  <kbase name=\"kbase1\">\n" +
                         "    <ksession name=\"ksessionA\" default=\"true\"/>\n" +
                         "  </kbase>\n" +
                         "  <kbase name=\"kbase2\">\n" +
                         "    <ksession name=\"ksessionA\"/>\n" +
                         "  </kbase>\n" +
                         "</kmodule>";

        checkKModule( kmodule, 1 );
    }

    @Test
    public void testBuildWithDuplicatedKBaseNames() {
        // RHBRMS-2689
        final String kmodule = "<kmodule xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" \n" +
                         "         xmlns=\"http://www.drools.org/xsd/kmodule\">\n" +
                         "  <kbase name=\"kbase1\">\n" +
                         "    <ksession name=\"ksessionA\" default=\"true\"/>\n" +
                         "  </kbase>\n" +
                         "  <kbase name=\"kbase1\">\n" +
                         "    <ksession name=\"ksessionB\"/>\n" +
                         "  </kbase>\n" +
                         "</kmodule>";

        checkKModule( kmodule, 1 );
    }

    private void checkKModule( final String kmodule, final int expectedErrors ) {
        final KieServices ks = KieServices.Factory.get();
        final ReleaseId releaseId = ks.newReleaseId( "org.kie", "test-kie-builder", "1.0.0" );
        final KieFileSystem kfs = ks.newKieFileSystem().generateAndWritePomXML( releaseId ).writeKModuleXML( kmodule );
        final KieBuilder kieBuilder = KieUtil.getKieBuilderFromKieFileSystem(kieBaseTestConfiguration, kfs, false);
        final Results results = kieBuilder.getResults();
        assertEquals( expectedErrors, results.getMessages( org.kie.api.builder.Message.Level.ERROR ).size() );
        assertNotNull(((InternalKieBuilder) kieBuilder ).getKieModuleIgnoringErrors());
    }

    @Test
    public void testAddMissingResourceToPackageBuilder() throws Exception {
        final KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();

        Assertions.assertThatThrownBy(() -> kbuilder.add(ResourceFactory.newClassPathResource("some.rf"), ResourceType.DRL))
                .isInstanceOf(RuntimeException.class);

        Assertions.assertThatThrownBy(() -> kbuilder.add(ResourceFactory.newClassPathResource("some.rf"), ResourceType.DRF))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    public void testDeclarativeChannelRegistration() {
        final String drl1 = "package org.drools.mvel.compiler\n" +
                "rule R1 when\n" +
                "   $m : Message()\n" +
                "then\n" +
                "end\n";

        final String kmodule = "<kmodule xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" \n" +
                "         xmlns=\"http://www.drools.org/xsd/kmodule\">\n" +
                "  <kbase name=\"kbase1\">\n" +
                "    <ksession name=\"ksession1\" default=\"true\">\n" +
                "       <channels>\n" +
                "         <channel name=\"testChannel\" type=\"org.drools.mvel.integrationtests.KieBuilderTest$MockChannel\" />\n" +
                "       </channels>\n" +
                "    </ksession>" +
                "  </kbase>\n" +
                "</kmodule>";

        final KieServices ks = KieServices.Factory.get();

        final ReleaseId releaseId1 = ks.newReleaseId( "org.kie", "test-kie-builder", "1.0.0" );
        final Resource r1 = ResourceFactory.newByteArrayResource( drl1.getBytes() ).setResourceType( ResourceType.DRL ).setSourcePath( "kbase1/drl1.drl" );

        KieModule km = KieUtil.buildAndInstallKieModuleIntoRepo(kieBaseTestConfiguration, releaseId1, KieModuleModelImpl.fromXML(kmodule), r1);

        KieContainer kieContainer = ks.newKieContainer( km.getReleaseId());
        
        KieSession kieSession = kieContainer.newKieSession();
        assertEquals(1, kieSession.getChannels().size());
        assertTrue(kieSession.getChannels().keySet().contains("testChannel"));
    }
    
    @Test
    public void testStatelessSessionDeclarativeChannelRegistration() {
        final String drl1 = "package org.drools.mvel.compiler\n" +
                "rule R1 when\n" +
                "   $m : Message()\n" +
                "then\n" +
                "end\n";

        final String kmodule = "<kmodule xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" \n" +
                "         xmlns=\"http://www.drools.org/xsd/kmodule\">\n" +
                "  <kbase name=\"kbase1\">\n" +
                "    <ksession name=\"ksession1\" default=\"true\" type=\"stateless\">\n" +
                "       <channels>\n" +
                "         <channel name=\"testChannel\" type=\"org.drools.mvel.integrationtests.KieBuilderTest$MockChannel\" />\n" +
                "       </channels>\n" +
                "    </ksession>" +
                "  </kbase>\n" +
                "</kmodule>";

        final KieServices ks = KieServices.Factory.get();

        final ReleaseId releaseId1 = ks.newReleaseId( "org.kie", "test-kie-builder", "1.0.0" );
        final Resource r1 = ResourceFactory.newByteArrayResource( drl1.getBytes() ).setResourceType( ResourceType.DRL ).setSourcePath( "kbase1/drl1.drl" );

        KieModule km = KieUtil.buildAndInstallKieModuleIntoRepo(kieBaseTestConfiguration, releaseId1, KieModuleModelImpl.fromXML(kmodule), r1);

        KieContainer kieContainer = ks.newKieContainer( km.getReleaseId());
        
        StatelessKieSession statelessKieSession = kieContainer.newStatelessKieSession();
        assertEquals(1, statelessKieSession.getChannels().size());
        assertTrue(statelessKieSession.getChannels().keySet().contains("testChannel"));
    }
    
    public static class MockChannel implements Channel {

		@Override
		public void send(Object object) {
			//NO=OP
		}
    	
    }

}
