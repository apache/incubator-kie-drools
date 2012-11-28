package org.drools.kproject;

import org.drools.builder.impl.KnowledgeContainerImpl;
import org.drools.core.util.FileManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.builder.KieBaseModel;
import org.kie.KBaseUnit;
import org.kie.builder.KieProject;
import org.kie.builder.KieSessionModel;
import org.kie.KnowledgeBase;
import org.kie.KnowledgeBaseFactory;
import org.kie.builder.KnowledgeContainer;
import org.kie.builder.KnowledgeContainerFactory;
import org.kie.conf.AssertBehaviorOption;
import org.kie.conf.EventProcessingOption;
import org.kie.definition.type.FactType;
import org.kie.runtime.StatefulKnowledgeSession;
import org.kie.runtime.conf.ClockTypeOption;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;

import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.fail;

public class KJarTest {

    private FileManager fileManager;

    private ClassLoader contextClassLoader;

    @Before
    public void setUp() throws Exception {
        KnowledgeContainerImpl.clearCache();
        this.fileManager = new FileManager().setUp();
        contextClassLoader = Thread.currentThread().getContextClassLoader();
    }

    @After
    public void tearDown() throws Exception {
        this.fileManager.tearDown();
        Thread.currentThread().setContextClassLoader(contextClassLoader);
    }

    @Test
    public void testKBUnit() throws Exception {
        createKJar();

        KBaseUnit unit = KnowledgeBaseFactory.getKBaseUnit("KBase1");
        if ( unit.hasErrors() ) {
            fail( unit.getErrors().toString() );
        }

        KnowledgeBase kbase = unit.getKnowledgeBase();
        StatefulKnowledgeSession ksession = unit.newStatefulKnowledegSession( "KSession1" );
        useKSession(kbase, ksession);
    }


    @Test
    public void testSessionFactory() throws Exception {
        createKJar();

        StatefulKnowledgeSession ksession = KnowledgeBaseFactory.getStatefulKnowlegeSession("KSession1");

        useKSession(ksession.getKnowledgeBase(), ksession);
    }

    @Test
    public void testKBaseAndSessionFactories() throws Exception {
        createKJar();

        KnowledgeBase kbase = KnowledgeBaseFactory.getKnowledgeBase("KBase1");
        StatefulKnowledgeSession ksession = KnowledgeBaseFactory.getStatefulKnowlegeSession("KSession1");

        useKSession(kbase, ksession);
    }

    private void createKJar() throws IOException {
        String rule = "package org.drools.test\n" +
                "rule R1 when\n" +
                "   $fieldA : FactA( $fieldB : fieldB )\n" +
                "   FactB( this == $fieldB, fieldA == $fieldA )\n" +
                "then\n" +
                "end";

        String declarationA = "package org.drools.test\n" +
                "declare FactA\n" +
                "    fieldB: FactB\n" +
                "end\n";

        String declarationB = "package org.drools.test\n" +
                "declare FactB\n" +
                "    fieldA: FactA\n" +
                "end\n";


        fileManager.write(fileManager.newFile("src/kbases/KBase1/org/test/rule.drl"), rule);
        fileManager.write(fileManager.newFile("src/kbases/KBase1/org/test/decA.drl"), declarationA);
        fileManager.write(fileManager.newFile("src/kbases/KBase1/org/test/decB.drl"), declarationB);

        KieProject kproj = new KieProjectImpl();
        KieBaseModel kieBaseModel1 = kproj.newKieBaseModel("KBase1")
                .setEqualsBehavior( AssertBehaviorOption.EQUALITY )
                .setEventProcessingMode( EventProcessingOption.STREAM );

        KieSessionModel ksession1 = kieBaseModel1.newKieSessionModel("KSession1")
                .setType( "stateful" )
                .setAnnotations( asList( "@ApplicationScoped; @Inject" ) )
                .setClockType( ClockTypeOption.get("realtime") );

        fileManager.write( fileManager.newFile(KnowledgeContainerImpl.KPROJECT_RELATIVE_PATH), ((KieProjectImpl)kproj).toXML() );

        KnowledgeContainer kcontainer = KnowledgeContainerFactory.newKnowledgeContainer();

        // input and output folder are the same
        File kJar = kcontainer.buildKJar(fileManager.getRootDirectory(), fileManager.getRootDirectory(), "test.jar");

        // we now have a KJAR
        // you now have two choices, write the KJAR to disk and use URLClassLoader
        // or use a custom class loader which the FS is added to. you can eithre use
        // the ServiceRegistrImpl, which the factory will access, or set CurrentContextClasslader

        // ServiceRegistryImpl.put(ClassLoader.class.getName());

        URLClassLoader urlClassLoader = new URLClassLoader( new URL[] { kJar.toURI().toURL() } );
        Thread.currentThread().setContextClassLoader( urlClassLoader );
    }

    private void useKSession(KnowledgeBase kbase, StatefulKnowledgeSession ksession) throws InstantiationException, IllegalAccessException {
        FactType aType = kbase.getFactType( "org.drools.test", "FactA" );
        Object a = aType.newInstance();
        FactType bType = kbase.getFactType( "org.drools.test", "FactB" );
        Object b = bType.newInstance();
        aType.set( a, "fieldB", b );
        bType.set( b, "fieldA", a );

        ksession.insert( a );
        ksession.insert( b );

        int rules = ksession.fireAllRules();
        assertEquals( 1, rules );
    }
}
