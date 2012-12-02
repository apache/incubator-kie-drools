package org.drools.builder;


import org.drools.core.util.FileManager;
import org.drools.kproject.GAVImpl;
import org.drools.kproject.KieBaseModelImpl;
import org.drools.kproject.memory.MemoryFileSystem;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.builder.GAV;
import org.kie.builder.KieBaseModel;
import org.kie.builder.KieBuilder;
import org.kie.builder.KieContainer;
import org.kie.builder.KieFactory;
import org.kie.builder.KieFileSystem;
import org.kie.builder.KieJar;
import org.kie.builder.KieProjectModel;
import org.kie.builder.KieRepository;
import org.kie.builder.KieServices;
import org.kie.builder.Message.Level;
import org.kie.builder.Results;
import org.kie.builder.impl.KieBuilderImpl;
import org.kie.builder.impl.KieFileSystemImpl;
import org.kie.conf.AssertBehaviorOption;
import org.kie.conf.EventProcessingOption;
import org.kie.runtime.KieBase;
import org.kie.runtime.KieSession;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
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

        GAV gav = KieFactory.Factory.get().newGav( namespace, "memory", "1.0-SNAPSHOT" );
        
        KieProjectModel kProj = createKieProject(namespace);
        
        KieFileSystem kfs = KieFactory.Factory.get().newKieFileSystem();
        generateAll(kfs, namespace, gav, kProj);
        
        createAndTestKieContainer(gav, createKieBuilder(kfs), namespace );
    }    

    @Test
    public void testOnDisc() throws ClassNotFoundException, InterruptedException, IOException {
        String namespace = "org.kie.test";

        KieProjectModel kProj = createKieProject(namespace);
        
        GAV gav = KieFactory.Factory.get().newGav( namespace, "memory", "1.0-SNAPSHOT" );
        
        KieFileSystem kfs = KieFactory.Factory.get().newKieFileSystem();
        generateAll(kfs, namespace, gav, kProj);
        MemoryFileSystem mfs = ((KieFileSystemImpl)kfs).asMemoryFileSystem();
        
        File file = fileManager.getRootDirectory() ;
        mfs.writeAsFs( file );
        
        createAndTestKieContainer(gav, createKieBuilder(kfs), namespace);
    }
    
    @Test
    public void testNoPomXml() throws ClassNotFoundException, InterruptedException, IOException {
        String namespace = "org.kie.test";

        KieProjectModel kProj = createKieProject(namespace);
        
        GAV gav = KieServices.Factory.get().getKieRepository().getDefaultGAV();
        
        KieFileSystem kfs = KieFactory.Factory.get().newKieFileSystem();
        generateKProjectXML( kfs, namespace, kProj );
        generateMessageClass( kfs, namespace );
        generateRule( kfs, namespace );
        
        MemoryFileSystem mfs = ((KieFileSystemImpl)kfs).asMemoryFileSystem();
               
        createAndTestKieContainer(gav, createKieBuilder(kfs), namespace );
    }
    
    @Test
    public void testNoProjectXml() throws ClassNotFoundException, InterruptedException, IOException {
        String namespace = "org.kie.test";
        
        GAV gav = KieFactory.Factory.get().newGav( namespace, "memory", "1.0-SNAPSHOT" );
        
        KieFileSystem kfs = KieFactory.Factory.get().newKieFileSystem();
        generatePomXML(kfs, gav);
        generateMessageClass( kfs, namespace );
        generateRule( kfs, namespace );
        
        MemoryFileSystem mfs = ((KieFileSystemImpl)kfs).asMemoryFileSystem();
               
        createAndTestKieContainer(gav, createKieBuilder(kfs), KieBaseModelImpl.DEFAULT_KIEBASE_NAME );
    }    
    
    public void testNoPomAndProjectXml() throws ClassNotFoundException, InterruptedException, IOException {
        String namespace = "org.kie.test";
        
        GAV gav = KieServices.Factory.get().getKieRepository().getDefaultGAV();
        
        KieFileSystem kfs = KieFactory.Factory.get().newKieFileSystem();
        generateMessageClass( kfs, namespace );
        generateRule( kfs, namespace );
        
        MemoryFileSystem mfs = ((KieFileSystemImpl)kfs).asMemoryFileSystem();
               
        createAndTestKieContainer(gav, createKieBuilder(kfs), KieBaseModelImpl.DEFAULT_KIEBASE_NAME );
    }      
    
    @Test
    public void testInvalidPomXmlGAV() throws ClassNotFoundException, InterruptedException, IOException {
        String namespace = "org.kie.test";

        KieProjectModel kProj = createKieProject(namespace);
        
        GAV gav = new GAVImpl( "", "", "" );                
        
        KieFileSystem kfs = KieFactory.Factory.get().newKieFileSystem();
        generatePomXML(kfs, gav);
        
        generateMessageClass( kfs, namespace );
        generateRule( kfs, namespace );
        
        MemoryFileSystem mfs = ((KieFileSystemImpl)kfs).asMemoryFileSystem();
          
        KieBuilder kieBuilder = createKieBuilder(kfs);
        kieBuilder.build();
        assertTrue ( kieBuilder.hasResults( Level.ERROR ) );
    }   
    
    @Test
    public void testInvalidPomXmlContent() throws ClassNotFoundException, InterruptedException, IOException {
        String namespace = "org.kie.test";

        KieProjectModel kProj = createKieProject(namespace);
        
        GAV gav = KieFactory.Factory.get().newGav( namespace, "memory", "1.0-SNAPSHOT" );            
        
        KieFileSystem kfs = KieFactory.Factory.get().newKieFileSystem();
        kfs.write( "pom.xml", "xxxx" );
        generateKProjectXML( kfs, namespace, kProj );
        generateMessageClass( kfs, namespace );
        generateRule( kfs, namespace );        
          
        KieBuilder kieBuilder = createKieBuilder(kfs);
        kieBuilder.build();
        assertTrue ( kieBuilder.hasResults( Level.ERROR ) );
    }     
    
    @Test
    public void testInvalidProjectXml() throws ClassNotFoundException, InterruptedException, IOException {
        String namespace = "org.kie.test";

        KieProjectModel kProj = createKieProject(namespace);
        
        GAV gav = KieFactory.Factory.get().newGav( namespace, "memory", "1.0-SNAPSHOT" );                
        
        KieFileSystem kfs = KieFactory.Factory.get().newKieFileSystem();
        generatePomXML(kfs, gav);       
        kfs.write("src/main/resources/META-INF/kproject.xml","xxxx" ); 
        generateMessageClass( kfs, namespace );
        generateRule( kfs, namespace );
        
        KieBuilder kieBuilder = createKieBuilder(kfs);
        kieBuilder.build();
        assertTrue ( kieBuilder.hasResults( Level.ERROR ) );
    }     
    
    
    public KieProjectModel createKieProject(String namespace) {        
        KieFactory kf = KieFactory.Factory.get();
        
        KieProjectModel kProj = kf.newKieProject();
        KieBaseModel kBase1 = kProj.newKieBaseModel(namespace)
                                   .setEqualsBehavior( AssertBehaviorOption.EQUALITY )
                                   .setEventProcessingMode( EventProcessingOption.STREAM );        
        
        return kProj;
    }    
    
    public void generateAll(KieFileSystem kfs,  String namespace, GAV gav, KieProjectModel kProj) {
        generatePomXML(kfs, gav);
        generateKProjectXML( kfs, namespace, kProj );
        generateMessageClass( kfs, namespace );
        generateRule( kfs, namespace );
        
    }

    public void generatePomXML(KieFileSystem kfs, GAV gav) {
        kfs.write( "pom.xml", KieBuilderImpl.generatePomXml( gav ) );
    }    
    
    public void generateKProjectXML(KieFileSystem kfs, String namespace, KieProjectModel kProj) {
        kfs.write("src/main/resources/META-INF/kproject.xml", kProj.toXML() );
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
    
    public void createAndTestKieContainer(GAV gav, KieBuilder kb, String kBaseName) throws IOException,
            ClassNotFoundException,
            InterruptedException {
        KieServices ks = KieServices.Factory.get();
        
        kb.build();
        
        if ( kb.hasResults( Level.ERROR  ) ) {
            fail("Unable to build KieJar\n" + kb.getResults( ).toString() );
        }
        KieRepository kr = ks.getKieRepository();
        KieJar kJar = kr.getKieJar( gav );
        assertNotNull( kJar );
        
        KieContainer kContainer = ks.getKieContainer( gav );
        KieBase kBase = kContainer.getKieBase( kBaseName );
        
        KieSession kSession = kBase.newKieSession();
        List list = new ArrayList();
        kSession.setGlobal( "list", list );
        kSession.fireAllRules();
        
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
