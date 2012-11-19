package org.drools.kproject;

import com.thoughtworks.xstream.XStream;
import org.drools.builder.impl.KnowledgeContainerImpl;
import org.junit.Test;
import org.kie.builder.KnowledgeBuilderFactory;
import org.kie.builder.KnowledgeContainer;
import org.kie.conf.AssertBehaviorOption;
import org.kie.conf.EventProcessingOption;
import org.kie.runtime.StatefulKnowledgeSession;
import org.kie.runtime.StatelessKnowledgeSession;
import org.kie.runtime.conf.ClockTypeOption;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class KnowledgeContainerTest extends AbstractKnowledgeTest {

    @Test
    public void testKContainer() throws Exception {
        KnowledgeContainer kContainer = KnowledgeBuilderFactory.newKnowledgeContainer();

        // create a kjar and deploy it
        // the deploy method causes the compilation of all the KnowledgeBases defined in the kjar
        // TODO Q: is it ok? to compile all the kbases during the deploy? would it be better to have a lazy compilation?
        // the method returns true if and only if ALL the kbases can be compiled without errors
        // TODO Q: how to report the compilation problems of a specific kbase? I'd prefer to avoid Exception if possible
        File kJar1 = createKJar(kContainer, "test1.jar", "rule1", "rule2");
        kContainer.deploy(kJar1);

        // create a ksesion and check it works as expected
        StatefulKnowledgeSession ksession = kContainer.getStatefulKnowlegeSession("org.test.KSession1");
        checkKSession(ksession, "rule1", "rule2");

        // deploy a second kjar
        // since it contains the definition of a kbase with the same QName of one already
        // defined with the former deploy it will replace the old one
        // TODO Q: I am not considering the name of the single drl files inside the kjar
        //         as (I think) the kagent does. Is that correct?
        File kJar2 = createKJar(kContainer, "test2.jar", "rule2", "rule3");
        kContainer.deploy(kJar2);

        // create a ksesion and check it works as expected
        StatefulKnowledgeSession ksession2 = kContainer.getStatefulKnowlegeSession("org.test.KSession1");
        checkKSession(ksession2, "rule2", "rule3");
    }

    @Test
    public void testMultpleJarAndFileResources() throws Exception {
        createKProjectJar( "jar1", true );
        createKProjectJar( "jar2", true );
        createKProjectJar( "jar3", true );
        createKProjectJar( "fol4", false );

        File file1 = fileManager.newFile( "jar1.jar" );
        File file2 = fileManager.newFile( "jar2.jar" );
        File file3 = fileManager.newFile( "jar3.jar" );
        File fol4 = fileManager.newFile( "fol4" );

        KnowledgeContainer kContainer = KnowledgeBuilderFactory.newKnowledgeContainer();
        kContainer.deploy(file1, file2, file3, fol4);

        testKBaseUnit(kContainer, "jar1");
        testKBaseUnit(kContainer, "jar2");
        testKBaseUnit(kContainer, "jar3");
        testKBaseUnit(kContainer, "fol4");
    }

    private void checkKSession(StatefulKnowledgeSession ksession, String... rules) {
        List<String> list = new ArrayList<String>();
        ksession.setGlobal( "list", list );
        ksession.fireAllRules();
        ksession.dispose();

        assertEquals( rules.length, list.size() );
        for (String rule : rules) {
            assertTrue( list.contains( rule ) );
        }
    }

    private File createKJar(KnowledgeContainer kContainer, String kjarName, String... rules) throws IOException {
        List<String> files = new ArrayList<String>();

        for (String rule : rules) {
            String file = "org/test/" + rule + ".drl";
            fileManager.write(fileManager.newFile("src/kbases/org.test.KBase1/" + file), createDRL(rule));
            files.add(file);
        }

        KProject kproj = new KProjectImpl();
        KBase kBase1 = kproj.newKBase( "org.test", "KBase1" )
                .setFiles( files )
                .setEqualsBehavior( AssertBehaviorOption.EQUALITY )
                .setEventProcessingMode( EventProcessingOption.STREAM );

        KSession ksession1 = kBase1.newKSession( "org.test", "KSession1" )
                .setType( "stateful" )
                .setAnnotations( asList( "@ApplicationScoped; @Inject" ) )
                .setClockType( ClockTypeOption.get("realtime") );

        fileManager.write(fileManager.newFile(KnowledgeContainerImpl.KPROJECT_RELATIVE_PATH), ((KProjectImpl)kproj).toXML() );

        return kContainer.buildKJar(fileManager.getRootDirectory(), fileManager.getRootDirectory(), kjarName);
    }

    private String createDRL(String ruleName) {
        return "package org.kie.test\n" +
                "global java.util.List list\n" +
                "rule " + ruleName + "\n" +
                "when\n" +
                "then\n" +
                "list.add( drools.getRule().getName() );\n" +
                "end\n";
    }

    public void testKBaseUnit(KnowledgeContainer kContainer, String jarName) {
        List<String> list = new ArrayList<String>();

        StatelessKnowledgeSession stlsKsession = kContainer.getStatelessKnowlegeSession(jarName + ".test1.KSession1");
        stlsKsession.setGlobal( "list", list );
        stlsKsession.execute( "dummy" );
        assertEquals( 2, list.size() );
        assertTrue( list.contains( jarName + ".test1:rule1" ) );
        assertTrue( list.contains( jarName + ".test1:rule2" ) );

        list.clear();
        StatefulKnowledgeSession stflKsession = kContainer.getStatefulKnowlegeSession(jarName + ".test1.KSession2");
        stflKsession.setGlobal( "list", list );
        stflKsession.fireAllRules();
        assertEquals( 2, list.size() );
        assertTrue( list.contains( jarName + ".test1:rule1" ) );
        assertTrue( list.contains( jarName + ".test1:rule2" ) );

        list.clear();
        stflKsession = kContainer.getStatefulKnowlegeSession(jarName + ".test2.KSession3");
        stflKsession.setGlobal( "list", list );
        stflKsession.fireAllRules();
        assertEquals( 2, list.size() );

        assertTrue( list.contains( jarName + ".test2:rule1" ) );
        assertTrue( list.contains( jarName + ".test2:rule2" ) );

        list.clear();
        stlsKsession = kContainer.getStatelessKnowlegeSession(jarName + ".test3.KSession4");
        stlsKsession.setGlobal( "list", list );
        stlsKsession.execute( "dummy" );
        assertEquals( 4, list.size() );
        assertTrue( list.contains( jarName + ".test1:rule1" ) );
        assertTrue( list.contains( jarName + ".test1:rule2" ) );
        assertTrue( list.contains( jarName + ".test2:rule1" ) );
        assertTrue( list.contains( jarName + ".test2:rule2" ) );
    }
}
