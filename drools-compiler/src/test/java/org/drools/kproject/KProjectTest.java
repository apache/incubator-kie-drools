package org.drools.kproject;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.enterprise.inject.spi.Bean;
import javax.enterprise.util.AnnotationLiteral;

import org.drools.KnowledgeBase;
import org.drools.commons.jci.compilers.CompilationResult;
import org.drools.commons.jci.compilers.EclipseJavaCompiler;
import org.drools.commons.jci.compilers.EclipseJavaCompilerSettings;
import org.drools.conf.AssertBehaviorOption;
import org.drools.conf.EventProcessingOption;
import org.drools.core.util.FileManager;
import org.drools.kproject.memory.MemoryFile;
import org.drools.kproject.memory.MemoryFileSystem;
import org.drools.kproject.memory.MemorytURLStreamHandler;
import org.drools.rule.JavaDialectRuntimeData;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.StatelessKnowledgeSession;
import org.drools.runtime.conf.ClockTypeOption;
import org.jboss.weld.bootstrap.api.Bootstrap;
import org.jboss.weld.bootstrap.spi.BeanDeploymentArchive;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.jboss.weld.environment.se.discovery.AbstractWeldSEDeployment;
import org.jboss.weld.environment.se.discovery.ImmutableBeanDeploymentArchive;
import org.jboss.weld.resources.spi.ResourceLoader;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.thoughtworks.xstream.XStream;

public class KProjectTest {
    private static final ProtectionDomain PROTECTION_DOMAIN;

    static {
        PROTECTION_DOMAIN = (ProtectionDomain) AccessController.doPrivileged( new PrivilegedAction() {

            public Object run() {
                return JavaDialectRuntimeData.class.getProtectionDomain();
            }
        } );
    }

    FileManager                           fileManager;

    @Before
    public void setUp() throws Exception {
        this.fileManager = new FileManager();
        this.fileManager.setUp();
    }

    @After
    public void tearDown() throws Exception {
        this.fileManager.tearDown();
    }

    public static class KPTestLiteral extends AnnotationLiteral<KPTest>
            implements
            KPTest {
        private String value;

        public KPTestLiteral(String value) {
            this.value = value;
        }

        public String value() {
            return this.value;
        }

    }

    @Test
    public void treatCreateMultpleJarAndFileResources() throws IOException,
                       ClassNotFoundException,
                       InterruptedException {
        createKProjectJar( "jar1", true );
        createKProjectJar( "jar2", true );
        createKProjectJar( "jar3", true );
        createKProjectJar( "fol4", false );

        ClassLoader origCl = Thread.currentThread().getContextClassLoader();
        try {
            java.io.File file1 = fileManager.newFile( "jar1.jar" );
            java.io.File file2 = fileManager.newFile( "jar2.jar" );
            java.io.File file3 = fileManager.newFile( "jar3.jar" );
            java.io.File fol4 = fileManager.newFile( "fol4" );
            URLClassLoader urlClassLoader = new URLClassLoader( new URL[]{file1.toURL(), file2.toURL(), file3.toURL(), fol4.toURL() } );
            Thread.currentThread().setContextClassLoader( urlClassLoader );

            Enumeration<URL> e = urlClassLoader.getResources( "META-INF/kproject.xml" );
            while ( e.hasMoreElements() ) {
                URL url = e.nextElement();
                System.out.println( url );
            }

            Class cls = Thread.currentThread().getContextClassLoader().loadClass( "org.drools.cdi.test.KProjectTestClassjar1" );
            assertNotNull( cls );
            cls = Thread.currentThread().getContextClassLoader().loadClass( "org.drools.cdi.test.KProjectTestClassjar2" );
            assertNotNull( cls );
            cls = Thread.currentThread().getContextClassLoader().loadClass( "org.drools.cdi.test.KProjectTestClassjar3" );
            assertNotNull( cls );

            Weld weldContainer = new Weld();
            WeldContainer weld = weldContainer.initialize();

            
            Set<Bean< ? >> beans = weld.getBeanManager().getBeans( KProjectTestClass.class, new KPTestLiteral( "jar1" ) );
            Bean bean = (Bean) beans.toArray()[0];
            KProjectTestClass o1 = (KProjectTestClass) bean.create( weld.getBeanManager().createCreationalContext( null ) );
            assertNotNull( o1 );            
            testEntry(o1, "jar1");
            
            beans = weld.getBeanManager().getBeans( KProjectTestClass.class, new KPTestLiteral( "jar2" ) );
            bean = (Bean) beans.toArray()[0];
            KProjectTestClass o2 = (KProjectTestClass) bean.create( weld.getBeanManager().createCreationalContext( null ) );
            assertNotNull( o2 );            
            testEntry(o2, "jar2");
            
            beans = weld.getBeanManager().getBeans( KProjectTestClass.class, new KPTestLiteral( "jar3" ) );
            bean = (Bean) beans.toArray()[0];
            KProjectTestClass o3 = (KProjectTestClass) bean.create( weld.getBeanManager().createCreationalContext( null ) );
            assertNotNull( o3 );            
            testEntry(o3, "jar3");

            beans = weld.getBeanManager().getBeans( KProjectTestClass.class, new KPTestLiteral( "fol4" ) );
            bean = (Bean) beans.toArray()[0];
            KProjectTestClass o4 = (KProjectTestClass) bean.create( weld.getBeanManager().createCreationalContext( null ) );
            assertNotNull( o4 );            
            testEntry(o4, "fol4");
            
            weldContainer.shutdown();
        } finally {
            Thread.currentThread().setContextClassLoader( origCl );
        }
    }

    public void testEntry(KProjectTestClass testClass, String jarName) {
        List<String> list = new ArrayList<String>();

        StatelessKnowledgeSession stlsKsession = testClass.getKBase1KSession1();
        stlsKsession.setGlobal( "list", list );
        stlsKsession.execute( "dummy" );
        assertEquals( 2, list.size() );
        assertTrue( list.contains( jarName + ".test1:rule1" ) );
        assertTrue( list.contains( jarName + ".test1:rule2" ) );

        list.clear();
        StatefulKnowledgeSession stflKsession = testClass.getKBase1KSession2();
        stflKsession.setGlobal( "list", list );
        stflKsession.fireAllRules();
        assertEquals( 2, list.size() );
        assertTrue( list.contains( jarName + ".test1:rule1" ) );
        assertTrue( list.contains( jarName + ".test1:rule2" ) );

        list.clear();
        stflKsession = testClass.getKBase2KSession3();
        stflKsession.setGlobal( "list", list );
        stflKsession.fireAllRules();
        assertEquals( 2, list.size() );

        assertTrue( list.contains( jarName + ".test2:rule1" ) );
        assertTrue( list.contains( jarName + ".test2:rule2" ) );

        list.clear();
        stlsKsession = testClass.getKBase3KSession4();
        stlsKsession.setGlobal( "list", list );
        stlsKsession.execute( "dummy" );
        assertEquals( 4, list.size() );
        assertTrue( list.contains( jarName + ".test1:rule1" ) );
        assertTrue( list.contains( jarName + ".test1:rule2" ) );
        assertTrue( list.contains( jarName + ".test2:rule1" ) );
        assertTrue( list.contains( jarName + ".test2:rule2" ) );
    }

    public void createKProjectJar(String namespace, 
                                  boolean createJar) throws IOException,
                                                       ClassNotFoundException,
                                                       InterruptedException {
        KProject kproj = new KProjectImpl();

        kproj.setGroupArtifactVersion( new GroupArtifactVersion( "org.test", namespace, "0.1" ) );

        kproj.setKProjectPath( "src/main/resources/" );
        kproj.setKBasesPath( "src/kbases" );

        List<String> files = asList( new String[]{namespace + "/test1/rule1.drl", namespace + "/test1/rule2.drl"} );

        KBase kBase1 = kproj.newKBase( namespace + ".test1", "KBase1" )
                .setFiles( files )
                .setAnnotations( asList( "@ApplicationScoped; @Inject" ) )
                .setEqualsBehavior( AssertBehaviorOption.EQUALITY )
                .setEventProcessingMode( EventProcessingOption.STREAM );

        KSession ksession1 = kBase1.newKSession( namespace + ".test1", "KSession1" )
                .setType( "stateless" )
                .setAnnotations( asList( "@ApplicationScoped; @Inject" ) )
                .setClockType( ClockTypeOption.get( "realtime" ) );

        KSession ksession2 = kBase1.newKSession( namespace + ".test1", "KSession2" )
                .setType( "stateful" )
                .setAnnotations( asList( "@ApplicationScoped; @Inject" ) )
                .setClockType( ClockTypeOption.get( "pseudo" ) );

        files = asList( new String[]{namespace + "/test2/rule1.drl", namespace + "/test2/rule2.drl"} );
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
        String string = xstream.toXML( kproj );
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
            writeJar( namespace, trgMfs );
        } else {
            writeFs( namespace, trgMfs );
        }
    }

    public List<String> compile(KProject kproj,
                                MemoryFileSystem srcMfs,
                                MemoryFileSystem trgMfs,
                                List<String> classes) {
        for ( KBase kbase : kproj.getKBases().values() ) {
            Folder srcFolder = srcMfs.getFolder( kproj.getKBasesPath() + "/" + kbase.getQName() );
            Folder trgFolder = trgMfs.getProjectFolder();

            copyFolder( srcMfs, srcFolder, trgMfs, trgFolder, kproj );
        }

        Folder srcFolder = srcMfs.getFolder( "META-INF" );
        Folder trgFolder = trgMfs.getFolder( "META-INF" );
        trgFolder.create();

        copyFolder( srcMfs, srcFolder, trgMfs, trgFolder, kproj );

        //printFs(trgMfs, trgMfs.getProjectFolder());
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

    public void populateClasses(KProject kproject,
                                List<String> classes) {
        for ( KBase kBase : kproject.getKBases().values() ) {
            classes.add( kBase.getNamespace().replace( '.', '/' ) + "/" + kBase.getName() + "Producer.java" );
            classes.add( kBase.getNamespace().replace( '.', '/' ) + "/" + kBase.getName() + ".java" );
            for ( KSession kSession : kBase.getKSessions().values() ) {
                classes.add( kSession.getNamespace().replace( '.', '/' ) + "/" + kSession.getName() + "Producer.java" );
                classes.add( kSession.getNamespace().replace( '.', '/' ) + "/" + kSession.getName() + ".java" );
            }
        }
    }

    public void printFs(MemoryFileSystem mfs,
                        Folder f) {
        for ( Resource rs : f.getMembers() ) {
            System.out.println( rs );
            if ( rs instanceof Folder ) {
                printFs( mfs, (Folder) rs );
            } else {
                System.out.println( new String( mfs.getFileContents( (MemoryFile) rs ) ) );
            }
        }
    }

    public String generateKProjectTestClass(KProject kproject,
                                            String namespace) {
        String s = "package org.drools.cdi.test;\n" +
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
                   "public class KProjectTestClass" + namespace + " implements org.drools.kproject.KProjectTestClass {\n" +
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

        return s;
    }

    public static String filenameToClassname(String filename) {
        return filename.substring( 0, filename.lastIndexOf( ".java" ) ).replace( '/', '.' ).replace( '\\', '.' );
    }

    public String generateBeansXML(KProject kproject) {
        String s = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                   "<beans xmlns=\"http://java.sun.com/xml/ns/javaee\"  xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"  xsi:schemaLocation=\"http://java.sun.com/xml/ns/javaee http://java.sun.com/xml/ns/javaee/beans_1_0.xsd\">\n" +
                   "</beans>";
        return s;
    }

    /**
     * This is an Internal Drools Class
     */
    public static class MemoryFileSystemClassLoader extends ClassLoader {

        MemoryFileSystem mfs;

        public MemoryFileSystemClassLoader(MemoryFileSystem mfs) {
            super( MemoryFileSystemClassLoader.class.getClassLoader() );
            this.mfs = mfs;
        }

        public Class< ? > loadClass(final String name,
                                    final boolean resolve) throws ClassNotFoundException {
            Class< ? > cls = fastFindClass( name );

            if ( cls == null ) {
                cls = getParent().loadClass( name );
            }

            if ( cls == null ) {
                throw new ClassNotFoundException( "Unable to load class: " + name );
            }

            return cls;
        }

        public Class< ? > fastFindClass(final String name) {
            Class< ? > cls = findLoadedClass( name );

            if ( cls == null ) {
                final byte[] clazzBytes = this.mfs.read( convertClassToResourcePath( name ) );
                if ( clazzBytes != null ) {
                    String pkgName = name.substring( 0,
                                                     name.lastIndexOf( '.' ) );
                    if ( getPackage( pkgName ) == null ) {
                        definePackage( pkgName,
                                       "",
                                       "",
                                       "",
                                       "",
                                       "",
                                       "",
                                       null );
                    }

                    cls = defineClass( name,
                                       clazzBytes,
                                       0,
                                       clazzBytes.length,
                                       PROTECTION_DOMAIN );
                }

                if ( cls != null ) {
                    resolveClass( cls );
                }
            }

            return cls;
        }

        public InputStream getResourceAsStream(final String name) {
            final byte[] clsBytes = this.mfs.read( name );
            if ( clsBytes != null ) {
                return new ByteArrayInputStream( clsBytes );
            }

            return getParent().getResourceAsStream( name );
        }

        public URL getResource(String name) {
            final byte[] clsBytes = this.mfs.read( name );
            if ( clsBytes != null ) {
                try {
                    return new URL( null, "memory://" + name, new MemorytURLStreamHandler( clsBytes ) );
                } catch ( MalformedURLException e ) {
                    throw new RuntimeException( "Unable to create URL for: " + name );
                }
            }
            return getParent().getResource( name );
        }

        public Enumeration<URL> getResources(String name) throws IOException {
            return getParent().getResources( name );
        }

        public static String convertClassToResourcePath(final String pName) {
            return pName.replace( '.',
                                  '/' ) + ".class";
        }

    }

    public static class TestWeldSEDeployment extends AbstractWeldSEDeployment {
        private final BeanDeploymentArchive beanDeploymentArchive;

        public TestWeldSEDeployment(ResourceLoader resourceLoader,
                                    Bootstrap bootstrap,
                                    List<String> classes) {
            super( bootstrap );
            beanDeploymentArchive = new ImmutableBeanDeploymentArchive( "classpath", classes, null );

        }

        public Collection<BeanDeploymentArchive> getBeanDeploymentArchives() {
            return Collections.singletonList( beanDeploymentArchive );
        }

        public BeanDeploymentArchive loadBeanDeploymentArchive(Class< ? > beanClass) {
            return beanDeploymentArchive;
        }

    }
    
    public void writeFs(String outFilename, MemoryFileSystem mfs) {
        java.io.File file = fileManager.newFile( outFilename );
        file.mkdir();
        writeFs(mfs, mfs.getProjectFolder(), file);
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

    public void writeJar(String outFilename,
                         MemoryFileSystem mfs) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ZipOutputStream out = new ZipOutputStream( baos );

            writeJarEntries( mfs,
                             mfs.getProjectFolder(),
                             out );
            out.close();

            FileManager.write( fileManager.newFile( outFilename + ".jar" ), baos.toByteArray() );
        } catch ( IOException e ) {
            e.printStackTrace();
        }
    }

    public void writeJarEntries(MemoryFileSystem mfs,
                                Folder f,
                                ZipOutputStream out) throws IOException {
        byte[] buf = new byte[1024];
        for ( Resource rs : f.getMembers() ) {
            if ( rs instanceof Folder ) {
                writeJarEntries( mfs, (Folder) rs, out );
            } else {
                out.putNextEntry( new ZipEntry( rs.getPath().toPortableString() ) );

                byte[] contents = mfs.getFileContents( (MemoryFile) rs );

                ByteArrayInputStream bais = new ByteArrayInputStream( contents );

                int len;
                while ( (len = bais.read( buf )) > 0 ) {
                    out.write( buf, 0, len );
                }

                out.closeEntry();
                bais.close();
            }
        }
    }
}
