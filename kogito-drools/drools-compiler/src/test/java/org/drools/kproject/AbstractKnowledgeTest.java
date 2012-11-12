package org.drools.kproject;

import com.thoughtworks.xstream.XStream;
import org.drools.commons.jci.compilers.CompilationResult;
import org.drools.commons.jci.compilers.EclipseJavaCompiler;
import org.drools.commons.jci.compilers.EclipseJavaCompilerSettings;
import org.drools.core.util.FileManager;
import org.drools.kproject.memory.MemoryFile;
import org.drools.kproject.memory.MemoryFileSystem;
import org.junit.After;
import org.junit.Before;
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
        KProject kproj = new KProjectImpl();

        kproj.setGroupArtifactVersion( new GroupArtifactVersion( "org.test", namespace, "0.1" ) );

        kproj.setKProjectPath( "src/main/resources/" );
        kproj.setKBasesPath( "src/kbases" );

        List<String> files = asList( namespace + "/test1/rule1.drl", namespace + "/test1/rule2.drl" );

        KBase kBase1 = kproj.newKBase( namespace + ".test1", "KBase1" )
                .setFiles( files )
                .setAnnotations( asList( "@ApplicationScoped; @Inject" ) )
                .setEqualsBehavior( AssertBehaviorOption.EQUALITY )
                .setEventProcessingMode( EventProcessingOption.STREAM );

        KSession ksession1 = kBase1.newKSession( namespace + ".test1", "KSession1" )
                .setType( "stateless" )
                .setAnnotations( asList( "@ApplicationScoped; @Inject" ) )
                .setClockType( ClockTypeOption.get("realtime") );

        KSession ksession2 = kBase1.newKSession( namespace + ".test1", "KSession2" )
                .setType( "stateful" )
                .setAnnotations( asList( "@ApplicationScoped; @Inject" ) )
                .setClockType( ClockTypeOption.get( "pseudo" ) );

        files = asList( namespace + "/test2/rule1.drl", namespace + "/test2/rule2.drl" );
        KBase kBase2 = kproj.newKBase( namespace + ".test2", "KBase2" )
                .setFiles( files )
                .setAnnotations( asList( "@ApplicationScoped" ) )
                .setEqualsBehavior( AssertBehaviorOption.IDENTITY )
                .setEventProcessingMode( EventProcessingOption.CLOUD );

        KSession ksession3 = kBase2.newKSession( namespace + ".test2", "KSession3" )
                .setType( "stateful" )
                .setAnnotations( asList( "@ApplicationScoped" ) )
                .setClockType( ClockTypeOption.get( "pseudo" ) );

        KBase kBase3 = kproj.newKBase( namespace + ".test3", "KBase3" )
                .setFiles( asList( new String[]{} ) )
                .addInclude( kBase1.getQName() )
                .addInclude( kBase2.getQName() )
                .setAnnotations( asList( "@ApplicationScoped" ) )
                .setEqualsBehavior( AssertBehaviorOption.IDENTITY )
                .setEventProcessingMode( EventProcessingOption.CLOUD );

        KSession ksession4 = kBase3.newKSession( namespace + ".test3", "KSession4" )
                .setType( "stateless" )
                .setAnnotations( asList( "@ApplicationScoped" ) )
                .setClockType( ClockTypeOption.get( "pseudo" ) );

        MemoryFileSystem mfs = new MemoryFileSystem();
        KProjectChangeLogCommiter.commit( kproj, mfs );

        Folder fld2 = mfs.getFolder( "META-INF" );
        fld2.create();
        File fle2 = fld2.getFile( "beans.xml" );
        fle2.create( new ByteArrayInputStream( generateBeansXML( kproj ).getBytes() ) );

        XStream xstream = new XStream();
        fle2 = fld2.getFile( "kproject.xml" );
        fle2.create( new ByteArrayInputStream( xstream.toXML( kproj ).getBytes() ) );

        String kBase1R1 = getRule( namespace + ".test1", "rule1" );
        String kBase1R2 = getRule( namespace + ".test1", "rule2" );

        String kbase2R1 = getRule( namespace + ".test2", "rule1" );
        String kbase2R2 = getRule( namespace + ".test2", "rule2" );

        String fldKB1 = kproj.getKBasesPath() + "/" + kBase1.getQName() + "/" + kBase1.getNamespace().replace( '.', '/' );
        String fldKB2 = kproj.getKBasesPath() + "/" + kBase2.getQName() + "/" + kBase2.getNamespace().replace( '.', '/' );

        mfs.getFolder( fldKB1 ).create();
        mfs.getFolder( fldKB2 ).create();

        mfs.getFile( fldKB1 + "/rule1.drl" ).create( new ByteArrayInputStream( kBase1R1.getBytes() ) );
        mfs.getFile( fldKB1 + "/rule2.drl" ).create( new ByteArrayInputStream( kBase1R2.getBytes() ) );
        mfs.getFile( fldKB2 + "/rule1.drl" ).create( new ByteArrayInputStream( kbase2R1.getBytes() ) );
        mfs.getFile( fldKB2 + "/rule2.drl" ).create( new ByteArrayInputStream( kbase2R2.getBytes() ) );

        MemoryFileSystem trgMfs = new MemoryFileSystem();
        MemoryFileSystem srcMfs = mfs;

        Folder fld1 = trgMfs.getFolder( "org/drools/cdi/test" );
        fld1.create();
        File fle1 = fld1.getFile( "KProjectTestClass" + namespace + ".java" );
        fle1.create( new ByteArrayInputStream( generateKProjectTestClass( kproj, namespace ).getBytes() ) );

        List<String> inputClasses = new ArrayList<String>();
        inputClasses.add( "org/drools/cdi/test/KProjectTestClass" + namespace + ".java" );

        //writeFs(namespace + "mod", srcMfs );
        final List<String> classes = compile( kproj, srcMfs, trgMfs, inputClasses );

        if ( createJar ) {
            trgMfs.writeAsJar(fileManager.getRootDirectory(), namespace);
        } else {
            writeFs( namespace, trgMfs );
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

    public String generateBeansXML(KProject kproject) {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<beans xmlns=\"http://java.sun.com/xml/ns/javaee\"  xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"  xsi:schemaLocation=\"http://java.sun.com/xml/ns/javaee http://java.sun.com/xml/ns/javaee/beans_1_0.xsd\">\n" +
                "</beans>";
    }

    public String generateKProjectTestClass(KProject kproject,
                                            String namespace) {

        return "package org.kie.cdi.test;\n" +
                "import javax.inject.Named;\n" +
                "import javax.inject.Inject;\n" +
                "import javax.inject.Inject;\n" +
                "import javax.enterprise.event.Observes;\n" +
                "import org.jboss.weld.environment.se.events.ContainerInitialized;\n" +
                "import " + KnowledgeBase.class.getName() + ";\n" +
                "import " + StatefulKnowledgeSession.class.getName() + ";\n" +
                "import " + StatelessKnowledgeSession.class.getName() + ";\n" +
                "import " + org.drools.cdi.KBase.class.getName() + ";\n" +
                "import " + org.drools.cdi.KSession.class.getName() + ";\n" +
                "import " + KPTest.class.getName() + ";\n" +

                "@KPTest(\"" + namespace + "\") \n" +
                "public class KProjectTestClass" + namespace + " implements org.kie.kproject.KProjectTestClass {\n" +
                "    private @Inject @KBase(\"" + namespace + ".test1.KBase1\")  " +
                "    KnowledgeBase kBase1; \n" +
                "    public KnowledgeBase getKBase1() {\n" +
                "        return kBase1;\n" +
                "    }\n" +
                "    private @Inject @KBase(\"" + namespace + ".test2.KBase2\") " +
                "    KnowledgeBase kBase2; \n" +
                "    public KnowledgeBase getKBase2() {\n" +
                "        return kBase2;\n" +
                "    }\n" +
                "    private @Inject @KBase(\"" + namespace + ".test3.KBase3\") \n" +
                "    KnowledgeBase kBase3; \n" +
                "    public KnowledgeBase getKBase3() {\n" +
                "        return kBase3;\n" +
                "    }\n" +
                "    private @Inject @KSession(\"" + namespace + ".test1.KSession1\") StatelessKnowledgeSession kBase1kSession1; \n" +
                "    public StatelessKnowledgeSession getKBase1KSession1() {\n" +
                "        return kBase1kSession1;\n" +
                "    }\n" +
                "    private @Inject @KSession(\"" + namespace + ".test1.KSession2\") StatefulKnowledgeSession kBase1kSession2; \n" +
                "    public StatefulKnowledgeSession getKBase1KSession2() {\n" +
                "        return kBase1kSession2;\n" +
                "    }\n" +
                "    private @Inject @KSession(\"" + namespace + ".test2.KSession3\") StatefulKnowledgeSession kBase2kSession3; \n" +
                "    public StatefulKnowledgeSession getKBase2KSession3() {\n" +
                "        return kBase2kSession3;\n" +
                "    }\n" +
                "    private @Inject @KSession(\"" + namespace + ".test3.KSession4\") StatelessKnowledgeSession kBase3kSession4; \n" +
                "    public StatelessKnowledgeSession getKBase3KSession4() {\n" +
                "        return kBase3kSession4;\n" +
                "    }\n" +
                "}\n";
    }

    public List<String> compile(KProject kproj,
                                MemoryFileSystem srcMfs,
                                MemoryFileSystem trgMfs,
                                List<String> classes) {
        for ( KBase kbase : kproj.getKBases().values() ) {
            Folder srcFolder = srcMfs.getFolder( kproj.getKBasesPath() + "/" + kbase.getQName() );
            Folder trgFolder = trgMfs.getRootFolder();

            copyFolder( srcMfs, srcFolder, trgMfs, trgFolder, kproj );
        }

        Folder srcFolder = srcMfs.getFolder( "META-INF" );
        Folder trgFolder = trgMfs.getFolder( "META-INF" );
        trgFolder.create();

        copyFolder( srcMfs, srcFolder, trgMfs, trgFolder, kproj );

        //printFs(trgMfs, trgMfs.getRootFolder());
        // populateClasses(kproj, classes);

        System.out.println( classes );

        EclipseJavaCompilerSettings settings = new EclipseJavaCompilerSettings();
        settings.setSourceVersion( "1.5" );
        settings.setTargetVersion( "1.5" );
        EclipseJavaCompiler compiler = new EclipseJavaCompiler( settings );
        CompilationResult res = compiler.compile( classes.toArray( new String[classes.size()] ), trgMfs, trgMfs );

        if ( res.getErrors().length > 0 ) {
            fail( res.getErrors()[0].getMessage() );
            //fail(res.getErrors().toString());
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
                           KProject kproj) {
        if ( !trgFolder.exists() ) {
            trgMfs.getFolder( trgFolder.getPath() ).create();
        }

        Collection<Resource> col = (Collection<Resource>) srcFolder.getMembers();

        for ( Resource rs : srcFolder.getMembers() ) {
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
