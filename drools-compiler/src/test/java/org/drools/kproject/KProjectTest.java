package org.drools.kproject;

import org.drools.kproject.memory.MemoryFile;
import org.drools.kproject.memory.MemoryFileSystem;
import org.drools.kproject.memory.MemorytURLStreamHandler;
import org.drools.rule.JavaDialectRuntimeData;
import org.jboss.weld.bootstrap.api.Bootstrap;
import org.jboss.weld.bootstrap.spi.BeanDeploymentArchive;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.jboss.weld.environment.se.discovery.AbstractWeldSEDeployment;
import org.jboss.weld.environment.se.discovery.ImmutableBeanDeploymentArchive;
import org.jboss.weld.resources.spi.ResourceLoader;
import org.junit.Test;
import org.kie.builder.KieBaseDescr;
import org.kie.builder.KieProject;
import org.kie.builder.KieSessionDescr;
import org.kie.runtime.StatefulKnowledgeSession;
import org.kie.runtime.StatelessKnowledgeSession;

import javax.enterprise.inject.spi.Bean;
import javax.enterprise.util.AnnotationLiteral;
import java.io.ByteArrayInputStream;
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class KProjectTest extends AbstractKnowledgeTest {
    private static final ProtectionDomain PROTECTION_DOMAIN;

    static {
        PROTECTION_DOMAIN = (ProtectionDomain) AccessController.doPrivileged( new PrivilegedAction() {

            public Object run() {
                return JavaDialectRuntimeData.class.getProtectionDomain();
            }
        } );
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
    public void createMultpleJarAndFileResources() throws IOException,
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
            URLClassLoader urlClassLoader = new URLClassLoader( new URL[]{file1.toURI().toURL(), file2.toURI().toURL(), file3.toURI().toURL(), fol4.toURI().toURL() } );
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

    public void populateClasses(KieProject kproject,
                                List<String> classes) {
        for ( KieBaseDescr kieBaseDescr : kproject.getKieBaseDescrs().values() ) {
            classes.add(kieBaseDescr.getName() + "Producer.java");
            classes.add(kieBaseDescr.getName() + ".java" );
            for ( KieSessionDescr kieSessionDescr : kieBaseDescr.getKieSessionDescrs().values() ) {
                classes.add( kieSessionDescr.getName() + "Producer.java" );
                classes.add( kieSessionDescr.getName() + ".java" );
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
}
