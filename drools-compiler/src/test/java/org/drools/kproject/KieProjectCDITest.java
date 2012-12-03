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
import org.kie.builder.KieBaseModel;
import org.kie.builder.KieProjectModel;
import org.kie.builder.KieSessionModel;
import org.kie.runtime.KieSession;
import org.kie.runtime.StatefulKnowledgeSession;
import org.kie.runtime.StatelessKieSession;
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

public class KieProjectCDITest extends AbstractKnowledgeTest {
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
        createKieModule( "jar1", true );
        createKieModule( "jar2", true );
        createKieModule( "jar3", true );
        createKieModule( "fol4", false );

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

}
