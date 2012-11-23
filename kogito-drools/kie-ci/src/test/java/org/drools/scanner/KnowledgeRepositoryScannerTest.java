package org.drools.scanner;

import org.drools.builder.impl.KnowledgeContainerImpl;
import org.drools.core.util.FileManager;
import org.drools.kproject.KBase;
import org.drools.kproject.KProject;
import org.drools.kproject.KProjectImpl;
import org.drools.kproject.KSession;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.Ignore;
import org.kie.builder.KnowledgeContainer;
import org.kie.builder.KnowledgeContainerFactory;
import org.kie.builder.KnowledgeRepositoryScanner;
import org.kie.conf.AssertBehaviorOption;
import org.kie.conf.EventProcessingOption;
import org.kie.runtime.StatefulKnowledgeSession;
import org.kie.runtime.conf.ClockTypeOption;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class KnowledgeRepositoryScannerTest {

    protected FileManager fileManager;

    @Before
    public void setUp() throws Exception {
        this.fileManager = new FileManager();
        this.fileManager.setUp();
    }

    @After
    public void tearDown() throws Exception {
        this.fileManager.tearDown();
    }

    @Test @Ignore
    public void testKScanner() throws Exception {
        KnowledgeContainer kContainer = KnowledgeContainerFactory.newKnowledgeContainer();
        File kJar1 = createKJar(kContainer, "test1.jar", "rule1", "rule2");
        kContainer.deploy(kJar1);

        Repository repository = new Repository();
        repository.deployArtifact("org.drools", "scanner-test", "1.0-SNAPSHOT", kJar1, createKPom());

        // -1 means no automatic scheduled scanning
        KnowledgeRepositoryScanner scanner = KnowledgeContainerFactory.newKnowledgeScanner(kContainer, -1);

        // set a fake PomParser to allow to discover the dependency that normally should be declared in the pom file
        ((KnowledgeRepositoryScannerImpl)scanner).setPomParser(new PomParser() {
            public List<DependencyDescriptor> getPomDirectDependencies() {
                return new ArrayList<DependencyDescriptor>() {{
                    add(new DependencyDescriptor("org.drools", "scanner-test", "1.0-SNAPSHOT", "jar"));
                }};
            }
        });

        // create a ksesion and check it works as expected
        StatefulKnowledgeSession ksession = kContainer.getStatefulKnowlegeSession("KSession1");
        checkKSession(ksession, "rule1", "rule2");

        resetFileManager();

        // create a new kjar
        File kJar2 = createKJar(kContainer, "test2.jar", "rule2", "rule3");

        // deploy it on maven
        repository.deployArtifact("org.drools", "scanner-test", "1.0-SNAPSHOT", kJar2, createKPom());

        // scan the maven repo to get the new kjar version and deploy it on the kcontainer
        scanner.scanNow();

        // create a ksesion and check it works as expected
        StatefulKnowledgeSession ksession2 = kContainer.getStatefulKnowlegeSession("KSession1");
        checkKSession(ksession2, "rule2", "rule3");
    }

    private void resetFileManager() {
        this.fileManager.tearDown();
        this.fileManager = new FileManager();
        this.fileManager.setUp();
    }

    private File createKPom() throws IOException {
        String pom =
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<project xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                "         xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd\">\n" +
                "  <modelVersion>4.0.0</modelVersion>\n" +
                "\n" +
                "  <groupId>org.drools</groupId>\n" +
                "  <artifactId>scanner-test</artifactId>\n" +
                "  <version>1.0-SNAPSHOT</version>\n" +
                "\n" +
                "</project>";

        File pomFile = fileManager.newFile("pom.xml");
        fileManager.write(pomFile, pom );
        return pomFile;
    }

    private File createKJar(KnowledgeContainer kContainer, String kjarName, String... rules) throws IOException {
        for (String rule : rules) {
            String file = "org/test/" + rule + ".drl";
            fileManager.write(fileManager.newFile("src/kbases/KBase1/" + file), createDRL(rule));
        }

        KProject kproj = new KProjectImpl();
        KBase kBase1 = kproj.newKBase("KBase1")
                .setEqualsBehavior( AssertBehaviorOption.EQUALITY )
                .setEventProcessingMode( EventProcessingOption.STREAM );

        KSession ksession1 = kBase1.newKSession( "KSession1" )
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
}
