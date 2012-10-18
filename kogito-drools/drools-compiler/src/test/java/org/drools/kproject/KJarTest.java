package org.drools.kproject;

import com.thoughtworks.xstream.XStream;
import org.drools.KBaseUnit;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.KnowledgeJarBuilder;
import org.drools.builder.impl.KnowledgeJarBuilderImpl;
import org.drools.conf.AssertBehaviorOption;
import org.drools.conf.EventProcessingOption;
import org.drools.core.util.FileManager;
import org.drools.definition.type.FactType;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.conf.ClockTypeOption;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

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

        KBaseUnit unit = KnowledgeBaseFactory.getKBaseUnit("org.test.KBase1");
        if ( unit.hasErrors() ) {
            fail( unit.getErrors().toString() );
        }

        KnowledgeBase kbase = unit.getKnowledgeBase();
        StatefulKnowledgeSession ksession = unit.newStatefulKnowledegSession( "org.test.KSession1" );
        useKSession(kbase, ksession);
    }


    @Test
    public void testSessionFactory() throws Exception {
        createKJar();

        StatefulKnowledgeSession ksession = KnowledgeBaseFactory.getStatefulKnowlegeSession("org.test.KSession1");

        useKSession(ksession.getKnowledgeBase(), ksession);
    }

    @Test
    public void testKBaseAndSessionFactories() throws Exception {
        createKJar();

        KnowledgeBase kbase = KnowledgeBaseFactory.getKnowledgeBase("org.test.KBase1");
        StatefulKnowledgeSession ksession = KnowledgeBaseFactory.getStatefulKnowlegeSession("org.test.KSession1");

        // fails because the ksession has been created from a different kbase
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


        fileManager.write(fileManager.newFile("src/kbases/org.test.KBase1/org/test/rule.drl"), rule);
        fileManager.write(fileManager.newFile("src/kbases/org.test.KBase1/org/test/decA.drl"), declarationA);
        fileManager.write(fileManager.newFile("src/kbases/org.test.KBase1/org/test/decB.drl"), declarationB);

        KProject kproj = new KProjectImpl();
        KBase kBase1 = kproj.newKBase( "org.test", "KBase1" )
                .setFiles( asList( "org/test/rule.drl", "org/test/decA.drl", "org/test/decB.drl" ) )
                .setEqualsBehavior( AssertBehaviorOption.EQUALITY )
                .setEventProcessingMode( EventProcessingOption.STREAM );

        KSession ksession1 = kBase1.newKSession( "org.test", "KSession1" )
                .setType( "stateful" )
                .setAnnotations( asList( "@ApplicationScoped; @Inject" ) )
                .setClockType( ClockTypeOption.get("realtime") );

        XStream xstream = new XStream();
        fileManager.write(fileManager.newFile(KnowledgeJarBuilderImpl.KPROJECT_RELATIVE_PATH), xstream.toXML( kproj ));

        KnowledgeJarBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeJarBuilder();

        // input and output folder are the same
        File kJar = kbuilder.buildKJar(fileManager.getRootDirectory(), fileManager.getRootDirectory(), "test.jar");

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
