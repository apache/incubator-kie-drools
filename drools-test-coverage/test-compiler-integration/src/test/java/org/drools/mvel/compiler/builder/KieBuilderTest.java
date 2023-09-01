/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.mvel.compiler.builder;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.drools.compiler.compiler.io.memory.MemoryFileSystem;
import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.drools.compiler.kie.builder.impl.KieBuilderImpl;
import org.drools.compiler.kie.builder.impl.KieFileSystemImpl;
import org.drools.compiler.kproject.models.KieBaseModelImpl;
import org.drools.core.util.FileManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieModule;
import org.kie.api.builder.KieRepository;
import org.kie.api.builder.Message.Level;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.conf.EqualityBehaviorOption;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.util.maven.support.PomModel;
import org.kie.util.maven.support.ReleaseIdImpl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

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

        ReleaseId releaseId = KieServices.Factory.get().newReleaseId( namespace, "memory", "1.0" );
        
        KieModuleModel kProj = createKieProject( namespace );
        
        KieFileSystem kfs = KieServices.Factory.get().newKieFileSystem();
        generateAll( kfs, namespace, releaseId, kProj );
        
        createAndTestKieContainer( releaseId, createKieBuilder( kfs ), namespace );
    }    

    @Test
    public void testOnDisc() throws ClassNotFoundException, InterruptedException, IOException {
        String namespace = "org.kie.test";

        KieModuleModel kProj = createKieProject(namespace);
        
        ReleaseId releaseId = KieServices.Factory.get().newReleaseId(namespace, "memory", "1.0");
        
        KieFileSystem kfs = KieServices.Factory.get().newKieFileSystem();
        generateAll(kfs, namespace, releaseId, kProj);
        MemoryFileSystem mfs = (( KieFileSystemImpl )kfs).asMemoryFileSystem();
        
        File file = fileManager.getRootDirectory() ;
        mfs.writeAsFs( file );
        
        createAndTestKieContainer(releaseId, createKieBuilder(kfs), namespace);
    }
    
    @Test
    public void testKieModuleDependencies() throws ClassNotFoundException, InterruptedException, IOException {
        KieServices ks = KieServices.Factory.get();
        
        String namespace1 = "org.kie.test1";
        ReleaseId releaseId1 = KieServices.Factory.get().newReleaseId(namespace1, "memory", "1.0");
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
        assertThat(kModule1).isNotNull();
        
        
        String namespace2 = "org.kie.test2";
        ReleaseId releaseId2 = KieServices.Factory.get().newReleaseId(namespace2, "memory", "1.0");
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
        assertThat(kModule2).isNotNull();
        
        KieContainer kContainer = ks.newKieContainer(releaseId2);
        KieBase kBase = kContainer.getKieBase( namespace2 );
        
        KieSession kSession = kBase.newKieSession();
        List list = new ArrayList();
        kSession.setGlobal( "list", list );
        kSession.fireAllRules();

        assertThat(list.size()).isEqualTo(2);
        if ("org.kie.test1.Message".equals(list.get(0).getClass().getName())) {
            assertThat(list.get(1).getClass().getName()).isEqualTo("org.kie.test2.Message");
        } else {
            assertThat(list.get(0).getClass().getName()).isEqualTo("org.kie.test2.Message");
            assertThat(list.get(1).getClass().getName()).isEqualTo("org.kie.test1.Message");
        }
    }

    @Test
    public void testNotExistingInclude() throws Exception {
        String drl = "package org.drools.mvel.compiler.integrationtests\n" +
                     "declare CancelFact\n" +
                     " cancel : boolean = true\n" +
                     "end\n" +
                     "rule R1 when\n" +
                     " $m : CancelFact( cancel == true )\n" +
                     "then\n" +
                     "end\n";

        KieServices ks = KieServices.Factory.get();

        KieFileSystem kfs = ks.newKieFileSystem().write( "src/main/resources/r1.drl", drl );

        KieModuleModel module = ks.newKieModuleModel();

        final String defaultBaseName = "defaultKBase";
        KieBaseModel defaultBase = module.newKieBaseModel(defaultBaseName)
                                         .addInclude( "notExistingKB1" )
                                         .addInclude( "notExistingKB2" );
        defaultBase.setDefault(true);
        defaultBase.addPackage( "*" );
        defaultBase.newKieSessionModel("defaultKSession").setDefault( true );

        kfs.writeKModuleXML( module.toXML() );
        KieBuilder kb = ks.newKieBuilder( kfs ).buildAll();
        assertThat(kb.getResults().getMessages().size()).isEqualTo(2);
    }

    @Test
    public void testNoPomXml() throws ClassNotFoundException, InterruptedException, IOException {
        String namespace = "org.kie.test";

        KieModuleModel kProj = createKieProject( namespace );
        
        ReleaseId releaseId = KieServices.Factory.get().getRepository().getDefaultReleaseId();
        
        KieFileSystem kfs = KieServices.Factory.get().newKieFileSystem();
        generateKProjectXML( kfs, namespace, kProj );
        generateMessageClass( kfs, namespace );
        generateRule( kfs, namespace );
        
        MemoryFileSystem mfs = ((KieFileSystemImpl)kfs).asMemoryFileSystem();
               
        createAndTestKieContainer( releaseId, createKieBuilder( kfs ), namespace );
    }
    
    @Test
    public void testNoProjectXml() throws ClassNotFoundException, InterruptedException, IOException {
        String namespace = "org.kie.test";
        
        ReleaseId releaseId = KieServices.Factory.get().newReleaseId( namespace, "memory", "1.0" );
        
        KieFileSystem kfs = KieServices.Factory.get().newKieFileSystem();
        generatePomXML( kfs, releaseId );
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
        
        
        ReleaseId releaseId = KieServices.Factory.get().newReleaseId(namespace, "memory", "1.0");
        
        KieFileSystem kfs = KieServices.Factory.get().newKieFileSystem();
        generateKProjectXML( kfs, namespace, kProj );
        generatePomXML(kfs, releaseId);
        generateMessageClass( kfs, namespace );
        generateRule( kfs, namespace );
        
        MemoryFileSystem mfs = ((KieFileSystemImpl)kfs).asMemoryFileSystem();
               
        createAndTestKieContainer(releaseId, createKieBuilder(kfs), null );
    }

    @Test
    public void testNoPomAndProjectXml() throws ClassNotFoundException, InterruptedException, IOException {
        String namespace = "org.kie.test";
        
        ReleaseId releaseId = KieServices.Factory.get().getRepository().getDefaultReleaseId();
        
        KieFileSystem kfs = KieServices.Factory.get().newKieFileSystem();
        generateMessageClass( kfs, namespace );
        generateRule( kfs, namespace );
        
        MemoryFileSystem mfs = ((KieFileSystemImpl)kfs).asMemoryFileSystem();
               
        createAndTestKieContainer( releaseId, createKieBuilder( kfs ), null );
    }
    
    @Test
    public void testInvalidPomXmlGAV() throws ClassNotFoundException, InterruptedException, IOException {
        String namespace = "org.kie.test";

        KieModuleModel kProj = createKieProject(namespace);
        
        ReleaseId releaseId = new ReleaseIdImpl( "", "", "" );
        
        KieFileSystem kfs = KieServices.Factory.get().newKieFileSystem();
        generatePomXML( kfs, releaseId );
        
        generateMessageClass( kfs, namespace );
        generateRule( kfs, namespace );
        
        MemoryFileSystem mfs = ((KieFileSystemImpl)kfs).asMemoryFileSystem();
          
        KieBuilder kieBuilder = createKieBuilder( kfs );
        kieBuilder.buildAll();
        assertThat(kieBuilder.getResults().hasMessages(Level.ERROR)).isTrue();
    }   
    
    @Test
    public void testInvalidPomXmlContent() throws ClassNotFoundException, InterruptedException, IOException {
        String namespace = "org.kie.test";

        KieModuleModel kProj = createKieProject(namespace);
        
        ReleaseId releaseId = KieServices.Factory.get().newReleaseId(namespace, "memory", "1.0");
        
        KieFileSystem kfs = KieServices.Factory.get().newKieFileSystem();
        kfs.write( "pom.xml", "xxxx" );
        generateKProjectXML( kfs, namespace, kProj );
        generateMessageClass( kfs, namespace );
        generateRule( kfs, namespace );        
          
        KieBuilder kieBuilder = createKieBuilder(kfs);
        kieBuilder.buildAll();
        assertThat(kieBuilder.getResults().hasMessages(Level.ERROR)).isTrue();
    }     
    
    @Test
    public void testInvalidProjectXml() throws ClassNotFoundException, InterruptedException, IOException {
        String namespace = "org.kie.test";

        KieModuleModel kProj = createKieProject( namespace );
        
        ReleaseId releaseId = KieServices.Factory.get().newReleaseId( namespace, "memory", "1.0" );
        
        KieFileSystem kfs = KieServices.Factory.get().newKieFileSystem();
        generatePomXML( kfs, releaseId );
        kfs.writeKModuleXML( "xxxx" );
        generateMessageClass( kfs, namespace );
        generateRule( kfs, namespace );
        
        KieBuilder kieBuilder = createKieBuilder(kfs);
        kieBuilder.buildAll();
        assertThat(kieBuilder.getResults().hasMessages(Level.ERROR)).isTrue();
    }

    @Test
    public void testSetPomModelReuse() throws IOException {
        String namespace = "org.kie.test";

        ReleaseId releaseId = KieServices.Factory.get().newReleaseId( namespace,
                                                                      "pomModelReuse",
                                                                      "1.0" );

        String pom = KieBuilderImpl.generatePomXml( releaseId );
        KieFileSystem kfs = KieServices.Factory.get().newKieFileSystem();
        kfs.writePomXML( pom );

        //Create a KieBuilder instance
        KieBuilder kieBuilder1 = createKieBuilder( kfs );
        kieBuilder1.buildAll();

        //Get PomModel to re-use in second KieBuilder instance
        PomModel pomModel = ( (KieBuilderImpl) kieBuilder1 ).getPomModel();

        kfs.writePomXML( pom );

        //Create another KieBuilder instance with the same KieFileSystem, setting PomModel
        KieBuilder kieBuilder2 = createKieBuilder( kfs );
        ( (KieBuilderImpl) kieBuilder2 ).setPomModel( pomModel );
        kieBuilder2.buildAll();

        //Read pom.xml from first KieBuilder's KieModule
        InternalKieModule kieModule1 = (InternalKieModule) ( (KieBuilderImpl) kieBuilder1 ).getKieModuleIgnoringErrors();
        final Reader reader1 = kieModule1.getResource( "META-INF/maven/org.kie.test/pomModelReuse/pom.xml" ).getReader();
        int charCode;
        String readPom1 = "";
        while ( ( charCode = reader1.read() ) != -1 ) {
            readPom1 = readPom1 + (char) charCode;
        }
        reader1.close();

        assertThat(readPom1).isEqualTo(pom);

        //Read pom.xml from second KieBuilder's KieModule
        InternalKieModule kieModule2 = (InternalKieModule) ( (KieBuilderImpl) kieBuilder2 ).getKieModuleIgnoringErrors();
        final Reader reader2 = kieModule2.getResource( "META-INF/maven/org.kie.test/pomModelReuse/pom.xml" ).getReader();
        String readPom2 = "";
        while ( ( charCode = reader2.read() ) != -1 ) {
            readPom2 = readPom2 + (char) charCode;
        }
        reader1.close();

        assertThat(readPom2).isEqualTo(pom);
    }

    public KieModuleModel createKieProject(String namespace) {
        KieServices ks = KieServices.Factory.get();
        
        KieModuleModel kProj = ks.newKieModuleModel();
        KieBaseModel kBase1 = kProj.newKieBaseModel(namespace)
                                   .setEqualsBehavior( EqualityBehaviorOption.EQUALITY )
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
        kfs.write("src/main/resources/" + namespace.replace('.', '/') + "/rule1.drl", getRule(namespace, namespace, "r1") );
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
        assertThat(kJar).isNotNull();
        
        KieContainer kContainer = ks.newKieContainer(releaseId);
        KieBase kBase = kBaseName != null ? kContainer.getKieBase( kBaseName ) : kContainer.getKieBase();

        KieSession kSession = kBase.newKieSession();
        List list = new ArrayList();
        kSession.setGlobal( "list", list );
        kSession.fireAllRules();

        assertThat(list.size()).isEqualTo(1);
        assertThat(list.get(0).getClass().getName()).isEqualTo("org.kie.test.Message");       
    }
    
    public String getRule(String namespace,
                          String messageNS,
                          String ruleName) {
        String s = "package " + namespace + "\n" +
                "import " + messageNS  + ".Message;\n"+
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
