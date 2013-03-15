package org.drools.compiler.kproject;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;

import org.junit.Test;
import org.drools.compiler.kie.builder.impl.ClasspathKieProject;
import org.drools.compiler.kie.builder.impl.KieContainerImpl;
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
            java.io.File file1 = fileManager.newFile( "jar1-1.0-SNAPSHOT.jar" );
            java.io.File file2 = fileManager.newFile( "jar2-1.0-SNAPSHOT.jar" );
            java.io.File file3 = fileManager.newFile( "jar3-1.0-SNAPSHOT.jar" );
            java.io.File fol4 = fileManager.newFile( "fol4-1.0-SNAPSHOT" );
        
            URLClassLoader urlClassLoader = new URLClassLoader( new URL[]{file1.toURI().toURL(), file2.toURI().toURL(), file3.toURI().toURL(), fol4.toURI().toURL() } );
            Thread.currentThread().setContextClassLoader( urlClassLoader );


            Class cls = Thread.currentThread().getContextClassLoader().loadClass( "org.drools.compiler.cdi.test.KProjectTestClassjar1" );
            assertNotNull( cls );
            cls = Thread.currentThread().getContextClassLoader().loadClass( "org.drools.compiler.cdi.test.KProjectTestClassjar2" );
            assertNotNull( cls );
            cls = Thread.currentThread().getContextClassLoader().loadClass( "org.drools.compiler.cdi.test.KProjectTestClassjar3" );
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
