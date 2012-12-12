package org.drools.builder;


import org.drools.compiler.io.memory.MemoryFileSystem;
import org.drools.core.util.FileManager;
import org.drools.kproject.ReleaseIdImpl;
import org.drools.kproject.models.KieBaseModelImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.KieBase;
import org.kie.KieServices;
import org.kie.builder.ReleaseId;
import org.kie.builder.KieBaseModel;
import org.kie.builder.KieBuilder;
import org.kie.builder.KieFileSystem;
import org.kie.builder.KieModule;
import org.kie.builder.KieModuleModel;
import org.kie.builder.KieRepository;
import org.kie.builder.Message.Level;
import org.kie.builder.impl.KieBuilderImpl;
import org.kie.builder.impl.KieFileSystemImpl;
import org.kie.conf.AssertBehaviorOption;
import org.kie.conf.EventProcessingOption;
import org.kie.runtime.KieContainer;
import org.kie.runtime.KieSession;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class KieBuilderTest {
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
    
    @Test
    public void testInMemory() throws ClassNotFoundException, InterruptedException, IOException {
        String namespace = "org.kie.test";

        ReleaseId releaseId = KieServices.Factory.get().newReleaseId(namespace, "memory", "1.0-SNAPSHOT");
        
        KieModuleModel kProj = createKieProject(namespace);
        
        KieFileSystem kfs = KieServices.Factory.get().newKieFileSystem();
        generateAll(kfs, namespace, releaseId, kProj);
        
        createAndTestKieContainer(releaseId, createKieBuilder(kfs), namespace );
    }    

    @Test
    public void testOnDisc() throws ClassNotFoundException, InterruptedException, IOException {
        String namespace = "org.kie.test";

        KieModuleModel kProj = createKieProject(namespace);
        
        ReleaseId releaseId = KieServices.Factory.get().newReleaseId(namespace, "memory", "1.0-SNAPSHOT");
        
        KieFileSystem kfs = KieServices.Factory.get().newKieFileSystem();
        generateAll(kfs, namespace, releaseId, kProj);
        MemoryFileSystem mfs = ((KieFileSystemImpl)kfs).asMemoryFileSystem();
        
        File file = fileManager.getRootDirectory() ;
        mfs.writeAsFs( file );
        
        createAndTestKieContainer(releaseId, createKieBuilder(kfs), namespace);
    }
    
    @Test
    public void testKieModuleDepednencies() throws ClassNotFoundException, InterruptedException, IOException {
        KieServices ks = KieServices.Factory.get();
        
        String namespace1 = "org.kie.test1";
        ReleaseId releaseId1 = KieServices.Factory.get().newReleaseId(namespace1, "memory", "1.0-SNAPSHOT");
        KieModuleModel kProj1 = createKieProject(namespace1);        
        KieFileSystem kfs1 = KieServices.Factory.get().newKieFileSystem();
        generateAll(kfs1, namespace1, releaseId1, kProj1);

        KieBuilder kb1 = createKieBuilder(kfs1);
        kb1.buildAll();        
        if ( kb1.getResults().hasMessages(Level.ERROR) ) {
            fail("Unable to build KieJar\n" + kb1.getResults( ).toString() );
        }
        KieRepository kr = ks.getRepository();
        KieModule kModule1 = kr.getKieModule(releaseId1);
        assertNotNull( kModule1 );
        
        
        String namespace2 = "org.kie.test2";
        ReleaseId releaseId2 = KieServices.Factory.get().newReleaseId(namespace2, "memory", "1.0-SNAPSHOT");
        KieModuleModel kProj2 = createKieProject(namespace2);        
        KieBaseModelImpl kieBase2 = ( KieBaseModelImpl ) kProj2.getKieBaseModels().get( namespace2 );
        kieBase2.addInclude( namespace1 );
        
        KieFileSystem kfs2 = KieServices.Factory.get().newKieFileSystem();
        generateAll(kfs2, namespace2, releaseId2, kProj2);
        

        KieBuilder kb2 = createKieBuilder(kfs2);
        kb2.setDependencies( kModule1 );
        kb2.buildAll();        
        if ( kb2.getResults().hasMessages(Level.ERROR) ) {
            fail("Unable to build KieJar\n" + kb2.getResults( ).toString() );
        }
        KieModule kModule2= kr.getKieModule(releaseId2);
        assertNotNull( kModule2);
        
        KieContainer kContainer = ks.newKieContainer(releaseId2);
        KieBase kBase = kContainer.getKieBase( namespace2 );
        
        KieSession kSession = kBase.newKieSession();
        List list = new ArrayList();
        kSession.setGlobal( "list", list );
        kSession.fireAllRules();

        assertEquals( 2, list.size() );
        if ("org.kie.test1.Message".equals(list.get(0).getClass().getName())) {
            assertEquals( "org.kie.test2.Message", list.get(1).getClass().getName() );
        } else {
            assertEquals( "org.kie.test2.Message", list.get(0).getClass().getName() );
            assertEquals( "org.kie.test1.Message", list.get(1).getClass().getName() );
        }
    }
    
    @Test
    public void testNoPomXml() throws ClassNotFoundException, InterruptedException, IOException {
        String namespace = "org.kie.test";

        KieModuleModel kProj = createKieProject(namespace);
        
        ReleaseId releaseId = KieServices.Factory.get().getRepository().getDefaultReleaseId();
        
        KieFileSystem kfs = KieServices.Factory.get().newKieFileSystem();
        generateKProjectXML( kfs, namespace, kProj );
        generateMessageClass( kfs, namespace );
        generateRule( kfs, namespace );
        
        MemoryFileSystem mfs = ((KieFileSystemImpl)kfs).asMemoryFileSystem();
               
        createAndTestKieContainer(releaseId, createKieBuilder(kfs), namespace );
    }
    
    @Test
    public void testNoProjectXml() throws ClassNotFoundException, InterruptedException, IOException {
        String namespace = "org.kie.test";
        
        ReleaseId releaseId = KieServices.Factory.get().newReleaseId(namespace, "memory", "1.0-SNAPSHOT");
        
        KieFileSystem kfs = KieServices.Factory.get().newKieFileSystem();
        generatePomXML(kfs, releaseId);
        generateMessageClass( kfs, namespace );
        generateRule( kfs, namespace );
        
        MemoryFileSystem mfs = ((KieFileSystemImpl)kfs).asMemoryFileSystem();
               
        createAndTestKieContainer(releaseId, createKieBuilder(kfs), null );
    }    
    
    @Test
    public void testEmptyProjectXml() throws ClassNotFoundException, InterruptedException, IOException {
        String namespace = "org.kie.test";
        
        KieServices ks = KieServices.Factory.get();
        
        KieModuleModel kProj = ks.newKieModuleModel();
        
        
        ReleaseId releaseId = KieServices.Factory.get().newReleaseId(namespace, "memory", "1.0-SNAPSHOT");
        
        KieFileSystem kfs = KieServices.Factory.get().newKieFileSystem();
        generateKProjectXML( kfs, namespace, kProj );
        generatePomXML(kfs, releaseId);
        generateMessageClass( kfs, namespace );
        generateRule( kfs, namespace );
        
        MemoryFileSystem mfs = ((KieFileSystemImpl)kfs).asMemoryFileSystem();
               
        createAndTestKieContainer(releaseId, createKieBuilder(kfs), null );
    }      
    
    public void testNoPomAndProjectXml() throws ClassNotFoundException, InterruptedException, IOException {
        String namespace = "org.kie.test";
        
        ReleaseId releaseId = KieServices.Factory.get().getRepository().getDefaultReleaseId();
        
        KieFileSystem kfs = KieServices.Factory.get().newKieFileSystem();
        generateMessageClass( kfs, namespace );
        generateRule( kfs, namespace );
        
        MemoryFileSystem mfs = ((KieFileSystemImpl)kfs).asMemoryFileSystem();
               
        createAndTestKieContainer(releaseId, createKieBuilder(kfs), null );
    }
    
    @Test
    public void testInvalidPomXmlGAV() throws ClassNotFoundException, InterruptedException, IOException {
        String namespace = "org.kie.test";

        KieModuleModel kProj = createKieProject(namespace);
        
        ReleaseId releaseId = new ReleaseIdImpl( "", "", "" );
        
        KieFileSystem kfs = KieServices.Factory.get().newKieFileSystem();
        generatePomXML(kfs, releaseId);
        
        generateMessageClass( kfs, namespace );
        generateRule( kfs, namespace );
        
        MemoryFileSystem mfs = ((KieFileSystemImpl)kfs).asMemoryFileSystem();
          
        KieBuilder kieBuilder = createKieBuilder(kfs);
        kieBuilder.buildAll();
        assertTrue ( kieBuilder.getResults().hasMessages(Level.ERROR) );
    }   
    
    @Test
    public void testInvalidPomXmlContent() throws ClassNotFoundException, InterruptedException, IOException {
        String namespace = "org.kie.test";

        KieModuleModel kProj = createKieProject(namespace);
        
        ReleaseId releaseId = KieServices.Factory.get().newReleaseId(namespace, "memory", "1.0-SNAPSHOT");
        
        KieFileSystem kfs = KieServices.Factory.get().newKieFileSystem();
        kfs.write( "pom.xml", "xxxx" );
        generateKProjectXML( kfs, namespace, kProj );
        generateMessageClass( kfs, namespace );
        generateRule( kfs, namespace );        
          
        KieBuilder kieBuilder = createKieBuilder(kfs);
        kieBuilder.buildAll();
        assertTrue ( kieBuilder.getResults().hasMessages(Level.ERROR) );
    }     
    
    @Test
    public void testInvalidProjectXml() throws ClassNotFoundException, InterruptedException, IOException {
        String namespace = "org.kie.test";

        KieModuleModel kProj = createKieProject(namespace);
        
        ReleaseId releaseId = KieServices.Factory.get().newReleaseId(namespace, "memory", "1.0-SNAPSHOT");
        
        KieFileSystem kfs = KieServices.Factory.get().newKieFileSystem();
        generatePomXML(kfs, releaseId);
        kfs.writeKModuleXML("xxxx" );
        generateMessageClass( kfs, namespace );
        generateRule( kfs, namespace );
        
        KieBuilder kieBuilder = createKieBuilder(kfs);
        kieBuilder.buildAll();
        assertTrue ( kieBuilder.getResults().hasMessages(Level.ERROR) );
    }     
    
    
    public KieModuleModel createKieProject(String namespace) {
        KieServices ks = KieServices.Factory.get();
        
        KieModuleModel kProj = ks.newKieModuleModel();
        KieBaseModel kBase1 = kProj.newKieBaseModel(namespace)
                                   .setEqualsBehavior( AssertBehaviorOption.EQUALITY )
                                   .setEventProcessingMode( EventProcessingOption.STREAM );        
        
        return kProj;
    }    
    
    public void generateAll(KieFileSystem kfs,  String namespace, ReleaseId releaseId, KieModuleModel kProj) {
        generatePomXML(kfs, releaseId);
        generateKProjectXML( kfs, namespace, kProj );
        generateMessageClass( kfs, namespace );
        generateRule( kfs, namespace );
        
    }

    public void generatePomXML(KieFileSystem kfs, ReleaseId releaseId) {
        kfs.writePomXML( KieBuilderImpl.generatePomXml(releaseId) );
    }    
    
    public void generateKProjectXML(KieFileSystem kfs, String namespace, KieModuleModel kProj) {
        kfs.writeKModuleXML( kProj.toXML() );
    }
    
    public void generateMessageClass(KieFileSystem kfs, String namespace) {
        kfs.write("src/main/java/" + namespace.replace('.', '/') + "/Message.java", getMessageClass( namespace ) );
    }
    
    public void generateRule(KieFileSystem kfs, String namespace) {
        kfs.write("src/main/resources/" + namespace.replace('.', '/') + "/rule1.drl", getRule(namespace, "r1") );
    }    
    
    public KieBuilder createKieBuilder(KieFileSystem kfs) {
        KieServices ks = KieServices.Factory.get();       
        return ks.newKieBuilder( kfs );        
    }

    public KieBuilder createKieBuilder(File file) {
        KieServices ks = KieServices.Factory.get();       
        return ks.newKieBuilder( file );        
    }    
    
    public void createAndTestKieContainer(ReleaseId releaseId, KieBuilder kb, String kBaseName) throws IOException,
            ClassNotFoundException,
            InterruptedException {
        KieServices ks = KieServices.Factory.get();
        
        kb.buildAll();
        
        if ( kb.getResults().hasMessages(Level.ERROR) ) {
            fail("Unable to build KieModule\n" + kb.getResults( ).toString() );
        }
        KieRepository kr = ks.getRepository();
        KieModule kJar = kr.getKieModule(releaseId);
        assertNotNull( kJar );
        
        KieContainer kContainer = ks.newKieContainer(releaseId);
        KieBase kBase = kBaseName != null ? kContainer.getKieBase( kBaseName ) : kContainer.getKieBase();

        KieSession kSession = kBase.newKieSession();
        List list = new ArrayList();
        kSession.setGlobal( "list", list );
        kSession.fireAllRules();

        assertEquals( 1, list.size() );
        assertEquals( "org.kie.test.Message", list.get(0).getClass().getName() );       
    }
    
    public String getRule(String namespace,
                          String ruleName) {
        String s = "package " + namespace + "\n" +
                "import " + namespace  + ".Message;\n"+
                "global java.util.List list;\n" +
                "rule " + ruleName + " when \n" +
                "then \n" +
                "  Message msg = new Message('hello');" +
                "  list.add(msg); " +
                "end \n" +
                "";
        return s;
    }
    
    public String getMessageClass(String namespace) {
        String s = "package " + namespace  + ";\n" +
                   "import java.lang.*;\n" +
                   "public class Message  {\n" +
                   "    private String text; \n " +
                   "    public Message(String text) { \n" +
                   "        this.text = text; \n" +
                   "    } \n" +
                   "    \n" +
                   "    public String getText() { \n" +
                   "        return this.text;\n" +
                   "    }\n" +
                   "}\n";

        return s;
    }    
}
