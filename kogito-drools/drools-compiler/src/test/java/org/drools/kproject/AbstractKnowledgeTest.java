package org.drools.kproject;

import org.drools.commons.jci.compilers.CompilationResult;
import org.drools.commons.jci.compilers.EclipseJavaCompiler;
import org.drools.commons.jci.compilers.EclipseJavaCompilerSettings;
import org.drools.core.util.FileManager;
import org.drools.kproject.memory.MemoryFile;
import org.drools.kproject.memory.MemoryFileSystem;
import org.junit.After;
import org.junit.Before;
import org.kie.builder.GAV;
import org.kie.builder.KieBaseModel;
import org.kie.builder.KieBuilder;
import org.kie.builder.KieFactory;
import org.kie.builder.KieFileSystem;
import org.kie.builder.KieJar;
import org.kie.builder.KieProjectModel;
import org.kie.builder.KieServices;
import org.kie.builder.KieSessionModel;
import org.kie.builder.Message.Level;
import org.kie.builder.impl.KieFileSystemImpl;
import org.kie.builder.impl.MemoryKieJar;
import org.kie.KnowledgeBase;
import org.kie.conf.AssertBehaviorOption;
import org.kie.conf.EventProcessingOption;
import org.kie.runtime.StatefulKnowledgeSession;
import org.kie.runtime.StatelessKnowledgeSession;
import org.kie.runtime.conf.ClockTypeOption;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.fail;

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

    public void createKProjectJar(String namespace,
                                  boolean createJar) throws IOException,
            ClassNotFoundException,
            InterruptedException {
        KieProjectModel kproj = new KieProjectModelImpl();

        KieBaseModel kieBaseModel1 = kproj.newKieBaseModel(namespace + ".KBase1")
                .setEqualsBehavior( AssertBehaviorOption.EQUALITY )
                .setEventProcessingMode( EventProcessingOption.STREAM );

        KieSessionModel ksession1 = kieBaseModel1.newKieSessionModel(namespace + ".KSession1")
                .setType( "stateless" )
                .setClockType( ClockTypeOption.get("realtime") );

        KieSessionModel ksession2 = kieBaseModel1.newKieSessionModel(namespace + ".KSession2")
                .setType( "stateful" )
                .setClockType( ClockTypeOption.get( "pseudo" ) );

        KieBaseModel kieBaseModel2 = kproj.newKieBaseModel(namespace + ".KBase2")
                .setEqualsBehavior( AssertBehaviorOption.IDENTITY )
                .setEventProcessingMode( EventProcessingOption.CLOUD );

        KieSessionModel ksession3 = kieBaseModel2.newKieSessionModel(namespace + ".KSession3")
                .setType( "stateful" )
                .setClockType( ClockTypeOption.get( "pseudo" ) );

        KieBaseModel kieBaseModel3 = kproj.newKieBaseModel(namespace + ".KBase3")
                .addInclude( kieBaseModel1.getName() )
                .addInclude( kieBaseModel2.getName() )
                .setEqualsBehavior( AssertBehaviorOption.IDENTITY )
                .setEventProcessingMode( EventProcessingOption.CLOUD );

        KieSessionModel ksession4 = kieBaseModel3.newKieSessionModel(namespace + ".KSession4")
                .setType( "stateless" )
                .setClockType( ClockTypeOption.get( "pseudo" ) );
  
        
        KieFileSystemImpl kfs =  ( KieFileSystemImpl ) KieFactory.Factory.get().newKieFileSystem();
        kfs.write( "src/main/resources/META-INF/beans.xml", generateBeansXML( kproj ) ); 
        kfs.writeProjectXML( ((KieProjectModelImpl)kproj).toXML()  );
        
        GAV gav = KieFactory.Factory.get().newGav( namespace, "art1", "1.0-SNAPSHOT" );
        kfs.generateAndWritePomXML( gav );        

        String kBase1R1 = getRule( namespace + ".test1", "rule1" );
        String kBase1R2 = getRule( namespace + ".test1", "rule2" );

        String kbase2R1 = getRule( namespace + ".test2", "rule1" );
        String kbase2R2 = getRule( namespace + ".test2", "rule2" );
                
        String fldKB1 = "src/main/resources/" + kieBaseModel1.getName().replace( '.', '/' );
        String fldKB2 = "src/main/resources/" + kieBaseModel2.getName().replace( '.', '/' );
        
        kfs.write( fldKB1 + "/rule1.drl", kBase1R1.getBytes() );
        kfs.write( fldKB1 + "/rule2.drl", kBase1R2.getBytes() );
        kfs.write( fldKB2 + "/rule1.drl", kbase2R1.getBytes() );
        kfs.write( fldKB2 + "/rule2.drl", kbase2R2.getBytes() );
        
        kfs.write( "src/main/java/org/drools/cdi/test/KProjectTestClass" + namespace + ".java" ,generateKProjectTestClass( kproj, namespace ) );        
        
        
        KieServices ks = KieServices.Factory.get();       
        KieBuilder kBuilder = ks.newKieBuilder( kfs );
        
        kBuilder.build();
        if ( kBuilder.hasResults( Level.ERROR  ) ) {
            fail( "should not have errors" + kBuilder.getResults() );
        }
        MemoryKieJar kieJar = ( MemoryKieJar ) kBuilder.getKieJar();
        MemoryFileSystem trgMfs = kieJar.getMemoryFileSystem();
        
        if ( createJar ) {            
            trgMfs.writeAsJar(fileManager.getRootDirectory(), namespace);
        } else {
            java.io.File file = fileManager.newFile( namespace );            
            trgMfs.writeAsFs( file );
        }
        
    }

    public String getRule(String packageName,
                          String ruleName) {
        String s = "package " + packageName + "\n" +
                "global java.util.List list;\n" +
                "rule " + ruleName + " when \n" +
                "then \n" +
                "  list.add(\"" + packageName + ":" + ruleName + "\"); " +
                "end \n" +
                "";
        return s;
    }

    public String generateBeansXML(KieProjectModel kproject) {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<beans xmlns=\"http://java.sun.com/xml/ns/javaee\"  xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"  xsi:schemaLocation=\"http://java.sun.com/xml/ns/javaee http://java.sun.com/xml/ns/javaee/beans_1_0.xsd\">\n" +
                "</beans>";
    }

    public String generateKProjectTestClass(KieProjectModel kproject,
                                            String namespace) {

        return "package org.drools.cdi.test;\n" +
                "import javax.inject.Named;\n" +
                "import javax.inject.Inject;\n" +
                "import javax.inject.Inject;\n" +
                "import javax.enterprise.event.Observes;\n" +
                "import org.jboss.weld.environment.se.events.ContainerInitialized;\n" +
                "import " + KnowledgeBase.class.getName() + ";\n" +
                "import " + StatefulKnowledgeSession.class.getName() + ";\n" +
                "import " + StatelessKnowledgeSession.class.getName() + ";\n" +
                "import " + org.kie.cdi.KBase.class.getName() + ";\n" +
                "import " + org.kie.cdi.KSession.class.getName() + ";\n" +
                "import " + KPTest.class.getName() + ";\n" +

                "@KPTest(\"" + namespace + "\") \n" +
                "public class KProjectTestClass" + namespace + " implements org.drools.kproject.KProjectTestClass {\n" +
                "    private @Inject @KBase(\"" + namespace + ".KBase1\")  " +
                "    KnowledgeBase kBase1; \n" +
                "    public KnowledgeBase getKBase1() {\n" +
                "        return kBase1;\n" +
                "    }\n" +
                "    private @Inject @KBase(\"" + namespace + ".KBase2\") " +
                "    KnowledgeBase kBase2; \n" +
                "    public KnowledgeBase getKBase2() {\n" +
                "        return kBase2;\n" +
                "    }\n" +
                "    private @Inject @KBase(\"" + namespace + ".KBase3\") \n" +
                "    KnowledgeBase kBase3; \n" +
                "    public KnowledgeBase getKBase3() {\n" +
                "        return kBase3;\n" +
                "    }\n" +
                "    private @Inject @KSession(\"" + namespace + ".KSession1\") StatelessKnowledgeSession kBase1kSession1; \n" +
                "    public StatelessKnowledgeSession getKBase1KSession1() {\n" +
                "        return kBase1kSession1;\n" +
                "    }\n" +
                "    private @Inject @KSession(\"" + namespace + ".KSession2\") StatefulKnowledgeSession kBase1kSession2; \n" +
                "    public StatefulKnowledgeSession getKBase1KSession2() {\n" +
                "        return kBase1kSession2;\n" +
                "    }\n" +
                "    private @Inject @KSession(\"" + namespace + ".KSession3\") StatefulKnowledgeSession kBase2kSession3; \n" +
                "    public StatefulKnowledgeSession getKBase2KSession3() {\n" +
                "        return kBase2kSession3;\n" +
                "    }\n" +
                "    private @Inject @KSession(\"" + namespace + ".KSession4\") StatelessKnowledgeSession kBase3kSession4; \n" +
                "    public StatelessKnowledgeSession getKBase3KSession4() {\n" +
                "        return kBase3kSession4;\n" +
                "    }\n" +
                "}\n";
    }

    public List<String> compile(KieProjectModel kproj,
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
                           KieProjectModel kproj) {
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
                    FileManager.write( new java.io.File( file1, ((File) rs).getName()), bytes);
                } catch ( IOException e ) {
                    fail( "Unable to write project to file system\n" + e.getMessage() );
                }
            }
        }
    }
}
