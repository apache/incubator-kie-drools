package org.drools.scanner;

import org.drools.builder.impl.KnowledgeContainerImpl;
import org.drools.commons.jci.compilers.CompilationResult;
import org.drools.commons.jci.compilers.EclipseJavaCompiler;
import org.drools.commons.jci.compilers.EclipseJavaCompilerSettings;
import org.drools.core.util.FileManager;
import org.drools.kproject.Folder;
import org.drools.kproject.KieProjectImpl;
import org.drools.kproject.memory.MemoryFileSystem;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.builder.KieBaseModel;
import org.kie.builder.KieProject;
import org.kie.builder.KieSessionModel;
import org.kie.builder.KnowledgeContainer;
import org.kie.builder.KnowledgeContainerFactory;
import org.kie.builder.KnowledgeRepositoryScanner;
import org.kie.conf.AssertBehaviorOption;
import org.kie.conf.EventProcessingOption;
import org.kie.runtime.StatefulKnowledgeSession;
import org.kie.runtime.conf.ClockTypeOption;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

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

        MavenRepository repository = new MavenRepository();
        repository.deployArtifact("org.drools", "scanner-test", "1.0-SNAPSHOT", kJar1, createKPom());

        // -1 means no automatic scheduled scanning
        KnowledgeRepositoryScanner scanner = KnowledgeContainerFactory.newKnowledgeScanner(kContainer, -1);
/*
        // set a fake PomParser to allow to discover the dependency that normally should be declared in the pom file
        ((KieRepositoryScannerImpl)scanner).setPomParser(new PomParser() {
            public List<DependencyDescriptor> getPomDirectDependencies() {
                return new ArrayList<DependencyDescriptor>() {{
                    add(new DependencyDescriptor("org.drools", "scanner-test", "1.0-SNAPSHOT", "jar"));
                }};
            }
        });
*/
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

    @Test @Ignore
    public void testKScannerWithKJarContainingClasses() throws Exception {
        File kJar1 = createKJarWithClass("test1", 2, 7);

        KnowledgeContainer kContainer = KnowledgeContainerFactory.newKnowledgeContainer();
        kContainer.deploy(kJar1);

        if (kContainer.getKBaseUnit("KBase1").hasErrors()) {
            fail("Failure Compiling KBase1");
        }

        MavenRepository repository = new MavenRepository();
        repository.deployArtifact("org.drools", "scanner-test", "1.0-SNAPSHOT", kJar1, createKPom());

        // -1 means no automatic scheduled scanning
        KnowledgeRepositoryScanner scanner = KnowledgeContainerFactory.newKnowledgeScanner(kContainer, -1);
/*
        // set a fake PomParser to allow to discover the dependency that normally should be declared in the pom file
        ((KieRepositoryScannerImpl)scanner).setPomParser(new PomParser() {
            public List<DependencyDescriptor> getPomDirectDependencies() {
                return new ArrayList<DependencyDescriptor>() {{
                    add(new DependencyDescriptor("org.drools", "scanner-test", "1.0-SNAPSHOT", "jar"));
                }};
            }
        });
*/
        // create a ksesion and check it works as expected
        StatefulKnowledgeSession ksession = kContainer.getStatefulKnowlegeSession("KSession1");
        checkKSession(ksession, 14);

        resetFileManager();

        // create a new kjar
        File kJar2 = createKJarWithClass("test1", 3, 5);

        // deploy it on maven
        repository.deployArtifact("org.drools", "scanner-test", "1.0-SNAPSHOT", kJar2, createKPom());

        // scan the maven repo to get the new kjar version and deploy it on the kcontainer
        scanner.scanNow();

        // create a ksesion and check it works as expected
        StatefulKnowledgeSession ksession2 = kContainer.getStatefulKnowlegeSession("KSession1");
        checkKSession(ksession2, 15);
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

        KieProject kproj = new KieProjectImpl();
        KieBaseModel kieBaseModel1 = kproj.newKieBaseModel("KBase1")
                .setEqualsBehavior( AssertBehaviorOption.EQUALITY )
                .setEventProcessingMode( EventProcessingOption.STREAM );

        KieSessionModel ksession1 = kieBaseModel1.newKieSessionModel("KSession1")
                .setType( "stateful" )
                .setClockType( ClockTypeOption.get("realtime") );

        fileManager.write(fileManager.newFile(KnowledgeContainerImpl.KPROJECT_RELATIVE_PATH), kproj.toXML());

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

    private void checkKSession(StatefulKnowledgeSession ksession, Object... results) {
        List<String> list = new ArrayList<String>();
        ksession.setGlobal( "list", list );
        ksession.fireAllRules();
        ksession.dispose();

        assertEquals(results.length, list.size());
        for (Object result : results) {
            assertTrue( list.contains( result ) );
        }
    }

    private File createKJarWithClass(String kjarName, int value, int factor) throws IOException {
        MemoryFileSystem mfs = new MemoryFileSystem();

        KieProject kproj = new KieProjectImpl();
        KieBaseModel kieBaseModel1 = kproj.newKieBaseModel("KBase1")
                .setEqualsBehavior( AssertBehaviorOption.EQUALITY )
                .setEventProcessingMode( EventProcessingOption.STREAM );

        KieSessionModel ksession1 = kieBaseModel1.newKieSessionModel("KSession1")
                .setType( "stateful" )
                .setClockType( ClockTypeOption.get("realtime") );

        Folder metaInf = mfs.getFolder( "META-INF" );
        metaInf.create();
        org.drools.kproject.File kprojectFile = metaInf.getFile( "kproject.xml" );
        kprojectFile.create(new ByteArrayInputStream(kproj.toXML().getBytes()));

        String fldKB1 = kieBaseModel1.getName();
        mfs.getFolder( fldKB1 ).create();
        mfs.getFile( fldKB1 + "/rule1.drl" ).create(new ByteArrayInputStream(createDRLForJavaSource(value).getBytes()));

        createClass(mfs, factor);

        return mfs.writeAsJar(fileManager.getRootDirectory(), kjarName);
    }

    private void createClass(MemoryFileSystem mfs, int factor) {
        try {
            mfs.getFolder( "org/kie/test" ).create();
            mfs.getFile( "org/kie/test/Bean.java" ).create(new ByteArrayInputStream(createJavaSource(factor).getBytes()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        compile(mfs, "org/kie/test/Bean.java");
    }

    private String createJavaSource(int factor) {
        return "package org.kie.test;\n" +
                "public class Bean {\n" +
                "   private final int value;\n" +
                "   public Bean(int value) {\n" +
                "       this.value = value;\n" +
                "   }\n" +
                "   public int getValue() {\n" +
                "       return value * " + factor + ";\n" +
                "   }\n" +
                "}";
    }

    private String createDRLForJavaSource(int value) {
        return "package org.kie.test\n" +
                //"import org.kie.test.Bean;\n" +
                "global java.util.List list\n" +
                "rule Init\n" +
                "when\n" +
                "then\n" +
                "insert( new Bean(" + value + ") );\n" +
                "end\n" +
                "rule R1\n" +
                "when\n" +
                "   $b : Bean()\n" +
                "then\n" +
                "   list.add( $b.getValue() );\n" +
                "end\n";
    }

    public void compile(MemoryFileSystem mfs, String... sourceFile) {
        EclipseJavaCompilerSettings settings = new EclipseJavaCompilerSettings();
        settings.setSourceVersion( "1.5" );
        settings.setTargetVersion( "1.5" );
        EclipseJavaCompiler compiler = new EclipseJavaCompiler( settings );
        CompilationResult res = compiler.compile( sourceFile, mfs, mfs );

        if ( res.getErrors().length > 0 ) {
            fail( res.getErrors()[0].getMessage() );
        }
    }
}
