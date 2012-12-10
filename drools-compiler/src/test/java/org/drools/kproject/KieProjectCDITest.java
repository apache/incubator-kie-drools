package org.drools.kproject;

import org.drools.cdi.CDITestRunner;
import org.drools.cdi.KieCDIExtension;
import org.drools.cdi.CDITestRunner.TestWeldSEDeployment;
import org.drools.kproject.models.KieModuleModelImpl;
import org.drools.rule.JavaDialectRuntimeData;
import org.jboss.weld.bootstrap.api.Bootstrap;
import org.jboss.weld.bootstrap.spi.Deployment;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.jboss.weld.resources.spi.ResourceLoader;
import org.junit.AfterClass;
import org.junit.Test;
import org.kie.builder.impl.AbstractKieModule;
import org.kie.cdi.KBase;
import org.kie.cdi.KGAV;
import org.kie.cdi.KSession;

import javax.enterprise.inject.spi.Bean;
import javax.enterprise.util.AnnotationLiteral;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertNotNull;

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
            
            Enumeration<URL> e = urlClassLoader.getResources( KieModuleModelImpl.KMODULE_JAR_PATH );
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
            
            Weld weld = CDITestRunner.createWeld(KProjectTestClass.class.getName(), 
                                                 KPTestLiteral.class.getName(),
                                                 "org.drools.cdi.test.KProjectTestClassjar1",
                                                 "org.drools.cdi.test.KProjectTestClassjar2",
                                                 "org.drools.cdi.test.KProjectTestClassjar3",
                                                 "org.drools.cdi.test.KProjectTestClassfol4");
            
            WeldContainer container = weld.initialize();            
            
            Set<Bean< ? >> beans = container.getBeanManager().getBeans( KProjectTestClass.class, new KPTestLiteral( "jar1" ) );
            Bean bean = (Bean) beans.toArray()[0];
            KProjectTestClass o1 = (KProjectTestClass) bean.create( container.getBeanManager().createCreationalContext( null ) );
            assertNotNull( o1 );            
            testEntry(o1, "jar1");
            
            beans = container.getBeanManager().getBeans( KProjectTestClass.class, new KPTestLiteral( "jar2" ) );
            bean = (Bean) beans.toArray()[0];
            KProjectTestClass o2 = (KProjectTestClass) bean.create( container.getBeanManager().createCreationalContext( null ) );
            assertNotNull( o2 );            
            testEntry(o2, "jar2");
            
            beans = container.getBeanManager().getBeans( KProjectTestClass.class, new KPTestLiteral( "jar3" ) );
            bean = (Bean) beans.toArray()[0];
            KProjectTestClass o3 = (KProjectTestClass) bean.create( container.getBeanManager().createCreationalContext( null ) );
            assertNotNull( o3 );            
            testEntry(o3, "jar3");

            beans = container.getBeanManager().getBeans( KProjectTestClass.class, new KPTestLiteral( "fol4" ) );
            bean = (Bean) beans.toArray()[0];
            KProjectTestClass o4 = (KProjectTestClass) bean.create( container.getBeanManager().createCreationalContext( null ) );
            assertNotNull( o4 );            
            testEntry(o4, "fol4");
            
            weld.shutdown();
        } finally {
            Thread.currentThread().setContextClassLoader( origCl );
        }
    }

}
