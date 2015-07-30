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

package org.drools.compiler.kproject;

import org.drools.compiler.commons.jci.compilers.CompilationResult;
import org.drools.compiler.commons.jci.compilers.EclipseJavaCompiler;
import org.drools.compiler.commons.jci.compilers.EclipseJavaCompilerSettings;
import org.drools.compiler.compiler.io.File;
import org.drools.compiler.compiler.io.Folder;
import org.drools.compiler.compiler.io.Resource;
import org.drools.compiler.compiler.io.memory.MemoryFile;
import org.drools.compiler.compiler.io.memory.MemoryFileSystem;
import org.drools.compiler.kie.builder.impl.KieFileSystemImpl;
import org.drools.compiler.kie.builder.impl.MemoryKieModule;
import org.drools.compiler.kproject.models.KieModuleModelImpl;
import org.drools.core.util.FileManager;
import org.drools.core.util.IoUtils;
import org.junit.After;
import org.junit.Before;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.Message.Level;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.builder.model.KieSessionModel.KieSessionType;
import org.kie.api.conf.EqualityBehaviorOption;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.StatelessKieSession;
import org.kie.api.runtime.conf.ClockTypeOption;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.*;

public class AbstractKnowledgeTest {

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
    
    public FileManager getFileManager() {
        return fileManager;
    }
    
    public void testEntry(KProjectTestClass testClass, String jarName) {
        List<String> list = new ArrayList<String>();

        StatelessKieSession stlsKsession = testClass.getKBase1KSession1();
        stlsKsession.setGlobal( "list", list );
        stlsKsession.execute( "dummy" );
        assertEquals( 2, list.size() );
        assertTrue( list.contains( jarName + ".test1:rule1:1.0-SNAPSHOT" ) );
        assertTrue( list.contains( jarName + ".test1:rule2:1.0-SNAPSHOT" ) );

        list.clear();
        KieSession stflKsession = testClass.getKBase1KSession2();
        stflKsession.setGlobal( "list", list );
        stflKsession.fireAllRules();
        assertEquals( 2, list.size() );
        assertTrue( list.contains( jarName + ".test1:rule1:1.0-SNAPSHOT" ) );
        assertTrue( list.contains( jarName + ".test1:rule2:1.0-SNAPSHOT" ) );

        list.clear();
        stflKsession = testClass.getKBase2KSession3();
        stflKsession.setGlobal( "list", list );
        stflKsession.fireAllRules();
        assertEquals( 2, list.size() );

        assertTrue( list.contains( jarName + ".test2:rule1:1.0-SNAPSHOT" ) );
        assertTrue( list.contains( jarName + ".test2:rule2:1.0-SNAPSHOT" ) );

        list.clear();
        stlsKsession = testClass.getKBase3KSession4();
        stlsKsession.setGlobal( "list", list );
        stlsKsession.execute( "dummy" );
        assertEquals( 4, list.size() );
        assertTrue( list.contains( jarName + ".test1:rule1:1.0-SNAPSHOT" ) );
        assertTrue( list.contains( jarName + ".test1:rule2:1.0-SNAPSHOT" ) );
        assertTrue( list.contains( jarName + ".test2:rule1:1.0-SNAPSHOT" ) );
        assertTrue( list.contains( jarName + ".test2:rule2:1.0-SNAPSHOT" ) );
    }    

    public KieModuleModel createKieModule(String namespace,
                                          boolean createJar) throws IOException,
                                                                    ClassNotFoundException,
                                                                    InterruptedException {
        return createKieModule( namespace, createJar, "1.0-SNAPSHOT" );
        
    }

    public KieModuleModel createKieModule(String namespace,
                                          boolean createJar,
                                          String version) throws IOException,
                                                       ClassNotFoundException,
                                                       InterruptedException {
        KieModuleModel kproj = new KieModuleModelImpl();

        KieBaseModel kieBaseModel1 = kproj.newKieBaseModel(namespace + ".KBase1")
                .setEqualsBehavior( EqualityBehaviorOption.EQUALITY )
                .setEventProcessingMode( EventProcessingOption.STREAM )
                .addPackage( namespace + ".KBase1" )
                .setDefault( true );
            

        kieBaseModel1.newKieSessionModel( namespace + ".KSession1" )
                     .setType( KieSessionType.STATELESS )
                     .setClockType( ClockTypeOption.get( "realtime" ) )
                     .setDefault( true );

        kieBaseModel1.newKieSessionModel( namespace + ".KSession2")
                     .setType( KieSessionType.STATEFUL )
                     .setClockType( ClockTypeOption.get( "pseudo" ) );

        kieBaseModel1.newKieSessionModel( namespace + ".KSessionDefault")
                     .setType( KieSessionType.STATEFUL )
                     .setClockType( ClockTypeOption.get( "pseudo" ) )
                     .setDefault( true );

        KieBaseModel kieBaseModel2 = kproj.newKieBaseModel( namespace + ".KBase2")
                                          .setEqualsBehavior( EqualityBehaviorOption.IDENTITY )
                                          .addPackage( namespace + ".KBase2")
                .setEventProcessingMode( EventProcessingOption.CLOUD );

        kieBaseModel2.newKieSessionModel(namespace + ".KSession3")
                .setType( KieSessionType.STATEFUL )
                .setClockType( ClockTypeOption.get( "pseudo" ) );

        KieBaseModel kieBaseModel3 = kproj.newKieBaseModel(namespace + ".KBase3")
                .addInclude( kieBaseModel1.getName() )
                                          .addInclude( kieBaseModel2.getName() )
                                          .setEqualsBehavior( EqualityBehaviorOption.IDENTITY )
                .setEventProcessingMode( EventProcessingOption.CLOUD );

        kieBaseModel3.newKieSessionModel(namespace + ".KSession4")
                .setType( KieSessionType.STATELESS )
                .setClockType( ClockTypeOption.get( "pseudo" ) );

        KieServices ks = KieServices.Factory.get();

        KieFileSystemImpl kfs =  ( KieFileSystemImpl ) ks.newKieFileSystem();
        kfs.write( "src/main/resources/META-INF/beans.xml", generateBeansXML( ) ); 
        kfs.writeKModuleXML( ((KieModuleModelImpl)kproj).toXML()  );
        
        ReleaseId releaseId = ks.newReleaseId(namespace, "art1", version);
        kfs.generateAndWritePomXML(releaseId);

        String kBase1R1 = getRule( namespace + ".test1", "rule1", version );
        String kBase1R2 = getRule( namespace + ".test1", "rule2", version );

        String kbase2R1 = getRule( namespace + ".test2", "rule1", version );
        String kbase2R2 = getRule( namespace + ".test2", "rule2", version );
                
        String fldKB1 = "src/main/resources/" + kieBaseModel1.getName().replace( '.', '/' );
        String fldKB2 = "src/main/resources/" + kieBaseModel2.getName().replace( '.', '/' );
        
        kfs.write( fldKB1 + "/rule1.drl", kBase1R1.getBytes() );
        kfs.write( fldKB1 + "/rule2.drl", kBase1R2.getBytes() );
        kfs.write( fldKB2 + "/rule1.drl", kbase2R1.getBytes() );
        kfs.write( fldKB2 + "/rule2.drl", kbase2R2.getBytes() );
        
        kfs.write( "src/main/java/org/drools/compiler/cdi/test/KProjectTestClass" + namespace + ".java" ,generateKProjectTestClass( kproj, namespace ) );
        
        
        KieBuilder kBuilder = ks.newKieBuilder( kfs );
        
        kBuilder.buildAll();
        if ( kBuilder.getResults().hasMessages(Level.ERROR) ) {
            fail( "should not have errors" + kBuilder.getResults() );
        }
        MemoryKieModule kieModule = ( MemoryKieModule ) kBuilder.getKieModule();
        MemoryFileSystem trgMfs = kieModule.getMemoryFileSystem();
        
        if ( createJar ) {            
            trgMfs.writeAsJar(fileManager.getRootDirectory(), namespace + "-" + version);
        } else {
            java.io.File file = fileManager.newFile( namespace + "-" +  version );            
            trgMfs.writeAsFs( file );
        }
        
        return kproj;
    }

    public String getRule(String packageName,
                          String ruleName,
                          String version) {
        String s = "package " + packageName + "\n" +
                "global java.util.List list;\n" +
                "rule " + ruleName + " when \n" +
                "then \n" +
                "  list.add(\"" + packageName + ":" + ruleName + ":" + version + "\"); " +
                "end \n" +
                "";
        return s;
    }

    public static String generateBeansXML() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<beans xmlns=\"http://java.sun.com/xml/ns/javaee\"  xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"  xsi:schemaLocation=\"http://java.sun.com/xml/ns/javaee http://java.sun.com/xml/ns/javaee/beans_1_0.xsd\">\n" +
                "</beans>";
    }

    public String generateKProjectTestClass(KieModuleModel kproject,
                                            String namespace) {

        return "package org.drools.compiler.cdi.test;\n" +
                "import javax.inject.Named;\n" +
                "import javax.inject.Inject;\n" +
                "import javax.inject.Inject;\n" +
                "import javax.enterprise.event.Observes;\n" +
                "import org.jboss.weld.environment.se.events.ContainerInitialized;\n" +
                "import " + KieBase.class.getName() + ";\n" +
                "import " + KieSession.class.getName() + ";\n" +
                "import " + StatelessKieSession.class.getName() + ";\n" +
                "import " + org.kie.api.cdi.KBase.class.getName() + ";\n" +
                "import " + org.kie.api.cdi.KSession.class.getName() + ";\n" +
                "import " + KPTest.class.getName() + ";\n" +
                "import " + KProjectTestClass.class.getName() + ";\n" +

                "@KPTest(\"" + namespace + "\") \n" +
                "public class KProjectTestClass" + namespace + " implements KProjectTestClass {\n" +
                "    private @Inject @KBase(\"" + namespace + ".KBase1\")  " +
                "    KieBase kBase1; \n" +
                "    public KieBase getKBase1() {\n" +
                "        return kBase1;\n" +
                "    }\n" +
                "    private @Inject @KBase(\"" + namespace + ".KBase2\") " +
                "    KieBase kBase2; \n" +
                "    public KieBase getKBase2() {\n" +
                "        return kBase2;\n" +
                "    }\n" +
                "    private @Inject @KBase(\"" + namespace + ".KBase3\") \n" +
                "    KieBase kBase3; \n" +
                "    public KieBase getKBase3() {\n" +
                "        return kBase3;\n" +
                "    }\n" +
                "    private @Inject @KSession(\"" + namespace + ".KSession1\") StatelessKieSession kBase1kSession1; \n" +
                "    public StatelessKieSession getKBase1KSession1() {\n" +
                "        return kBase1kSession1;\n" +
                "    }\n" +
                "    private @Inject @KSession(\"" + namespace + ".KSession2\") KieSession kBase1kSession2; \n" +
                "    public KieSession getKBase1KSession2() {\n" +
                "        return kBase1kSession2;\n" +
                "    }\n" +
                "    private @Inject @KSession(\"" + namespace + ".KSession3\") KieSession kBase2kSession3; \n" +
                "    public KieSession getKBase2KSession3() {\n" +
                "        return kBase2kSession3;\n" +
                "    }\n" +
                "    private @Inject @KSession(\"" + namespace + ".KSession4\") StatelessKieSession kBase3kSession4; \n" +
                "    public StatelessKieSession getKBase3KSession4() {\n" +
                "        return kBase3kSession4;\n" +
                "    }\n" +
                "}\n";
    }

    public List<String> compile(KieModuleModel kproj,
                                MemoryFileSystem srcMfs,
                                MemoryFileSystem trgMfs,
                                List<String> classes) {
        for ( KieBaseModel kbase : kproj.getKieBaseModels().values() ) {
            Folder srcFolder = srcMfs.getFolder( "src/main/resources/" + kbase.getName() );
            Folder trgFolder = trgMfs.getFolder(kbase.getName());

            copyFolder( srcMfs, srcFolder, trgMfs, trgFolder, kproj );
        }

        Folder srcFolder = srcMfs.getFolder( "META-INF" );
        Folder trgFolder = trgMfs.getFolder( "META-INF" );
        trgFolder.create();

        copyFolder( srcMfs, srcFolder, trgMfs, trgFolder, kproj );

        EclipseJavaCompilerSettings settings = new EclipseJavaCompilerSettings();
        settings.setSourceVersion( "1.5" );
        settings.setTargetVersion( "1.5" );
        EclipseJavaCompiler compiler = new EclipseJavaCompiler( settings, "" );
        CompilationResult res = compiler.compile( classes.toArray( new String[classes.size()] ), trgMfs, trgMfs );

        if ( res.getErrors().length > 0 ) {
            fail( res.getErrors()[0].getMessage() );
        }

        List<String> classes2 = new ArrayList<String>( classes.size() );
        for ( String str : classes ) {
            classes2.add( filenameToClassname( str ) );
        }

        return classes2;
    }

    public static String filenameToClassname(String filename) {
        return filename.substring( 0, filename.lastIndexOf( ".java" ) ).replace( '/', '.' ).replace( '\\', '.' );
    }

    public void copyFolder(MemoryFileSystem srcMfs,
                           Folder srcFolder,
                           MemoryFileSystem trgMfs,
                           Folder trgFolder,
                           KieModuleModel kproj) {
        if ( !trgFolder.exists() ) {
            trgMfs.getFolder( trgFolder.getPath() ).create();
        }

        Collection<Resource> col = (Collection<Resource>) srcFolder.getMembers();
        if (col == null) {
            return;
        }

        for ( Resource rs : col ) {
            if ( rs instanceof Folder ) {
                copyFolder( srcMfs, (Folder) rs, trgMfs, trgFolder.getFolder( ((Folder) rs).getName() ), kproj );
            } else {
                MemoryFile trgFile = (MemoryFile) trgFolder.getFile( ((File) rs).getName() );

                try {
                    trgMfs.setFileContents( trgFile, srcMfs.getFileContents( (MemoryFile) rs ) );
                } catch ( IOException e ) {
                    throw new RuntimeException( e );
                }
            }
        }
    }

    public void writeFs(String outFilename, MemoryFileSystem mfs) {
        java.io.File file = fileManager.newFile( outFilename );
        file.mkdir();
        writeFs(mfs, mfs.getRootFolder(), file);
    }

    public void writeFs(MemoryFileSystem mfs,
                        Folder f,
                        java.io.File file1) {
        for ( Resource rs : f.getMembers() ) {
            if ( rs instanceof Folder ) {
                java.io.File file2 = new java.io.File( file1, ((Folder) rs).getName());
                file2.mkdir();
                writeFs( mfs, (Folder) rs, file2 );
            } else {
                byte[] bytes = mfs.getFileContents( (MemoryFile) rs );

                try {
                    IoUtils.write(new java.io.File(file1, ((File) rs).getName()), bytes);
                } catch ( IOException e ) {
                    fail( "Unable to write project to file system\n" + e.getMessage() );
                }
            }
        }
    }
}
