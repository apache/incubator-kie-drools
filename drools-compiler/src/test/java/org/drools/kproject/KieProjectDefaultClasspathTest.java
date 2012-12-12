package org.drools.kproject;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.Set;

import javax.enterprise.inject.spi.Bean;

import org.drools.kproject.KieProjectCDITest.KPTestLiteral;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.junit.Test;
import org.kie.builder.impl.ClasspathKieProject;
import org.kie.builder.impl.KieContainerImpl;
import org.kie.runtime.KieContainer;

public class KieProjectDefaultClasspathTest extends AbstractKnowledgeTest {

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


            Class cls = Thread.currentThread().getContextClassLoader().loadClass( "org.drools.cdi.test.KProjectTestClassjar1" );
            assertNotNull( cls );
            cls = Thread.currentThread().getContextClassLoader().loadClass( "org.drools.cdi.test.KProjectTestClassjar2" );
            assertNotNull( cls );
            cls = Thread.currentThread().getContextClassLoader().loadClass( "org.drools.cdi.test.KProjectTestClassjar3" );
            assertNotNull( cls );
            
            ClasspathKieProject kProject =  new ClasspathKieProject();
            
            KieContainer kContainer = new KieContainerImpl(kProject, null);
            
             testEntry(new KProjectTestClassImpl( "jar1", kContainer ), "jar1");
             testEntry(new KProjectTestClassImpl( "jar2", kContainer ), "jar2");
             testEntry(new KProjectTestClassImpl( "jar3", kContainer ), "jar3");
             testEntry(new KProjectTestClassImpl( "fol4", kContainer ), "fol4");

        } finally {
            Thread.currentThread().setContextClassLoader( origCl );
        }
    }
    
}
